package com.gradle.develocity.teamcity.agent.bootstrapping.restore;

import com.google.common.annotations.VisibleForTesting;
import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.agent.BuildRunnerContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Comparator.naturalOrder;
import static java.util.stream.Collectors.toList;

class JdkProvider {

    private static final Logger LOG = Logger.getInstance("jetbrains.buildServer.AGENT");
    private static final Pattern JDKS_WITH_MAJOR_AND_MINOR_VERSION_ENV_VAR_PATTERN = Pattern.compile("^JDK_(\\d+)_(\\d+)$");

    private final BuildRunnerContext context;
    private final JavaExecutableFinder javaExecutableFinder;

    JdkProvider(BuildRunnerContext context) {
        this.context = context;
        this.javaExecutableFinder = new DefaultJavaExecutableFinder();
    }

    @VisibleForTesting
    JdkProvider(BuildRunnerContext context, JavaExecutableFinder javaExecutableFinder) {
        this.context = context;
        this.javaExecutableFinder = javaExecutableFinder;
    }

    /**
     * Returns a JDK location for the specified major version - or higher.
     * </p>
     */
    Optional<JdkLocation> jdkForMajorVersion(@SuppressWarnings("SameParameterValue") int majorVersion) {

        // TeamCity sets an environment variable for detected JDKs in the following format:
        //      JDK_21_0
        //      JDK_21_0_x64
        //      JDK_21_0_ARM64
        // The "x64" means that it's a 64bit JDK. If that part is missing, it could either be 32bit or 64bit.
        //
        // Our assumption is, that when JDKs are found, there's always an env variable present which does
        // not include the 32/64 bit information and/or architecture.
        //
        // We want to return the highest JDK location that meets the major version requirement.

        List<String> jdkCandidateEnvVars = findCandidateEnvVarsFor(majorVersion);
        if (jdkCandidateEnvVars.isEmpty()) {
            LOG.warn(format("Could not find candidate env vars for major version %d", majorVersion));
            return Optional.empty();
        } else {
            LOG.info(format("Found JDK candidates: %s", stringified(jdkCandidateEnvVars)));
        }

        String chosenJdkEnvVar = chooseJdkEnvVar(jdkCandidateEnvVars);
        String chosenJdk = valueForEnvVar(chosenJdkEnvVar);
        LOG.info(format("Chosen JDK %s is located at '%s'", chosenJdkEnvVar, chosenJdk));

        Optional<Path> javaBinaryPath = javaExecutableFinder.executablePathFor(Paths.get(chosenJdk));
        if (javaBinaryPath.isPresent()) {
            return Optional.of(new JdkLocation(chosenJdkEnvVar, javaBinaryPath.get()));
        } else {
            LOG.warn("Cannot use chosen JDK as it is not executable");
            return Optional.empty();
        }
    }

    private List<String> findCandidateEnvVarsFor(int majorVersion) {
        Map<String, String> environment = context.getBuildParameters().getEnvironmentVariables();

        return environment.keySet().stream()
                .map(JDKS_WITH_MAJOR_AND_MINOR_VERSION_ENV_VAR_PATTERN::matcher)
                .filter(jdk -> jdk.matches() &&  toIntOrZero(jdk.group(1)) >= majorVersion)
                .map(jdk -> jdk.group(0))
                .collect(toList());
    }

    private String chooseJdkEnvVar(List<String> jdkCandidateEnvVars) {
        return jdkCandidateEnvVars.stream()
                .max(naturalOrder())
                .orElseThrow(() -> new IllegalStateException("There must be at least one JDK candidate"));
    }

    private String valueForEnvVar(String envVar) {
        return context.getBuildParameters().getEnvironmentVariables().get(envVar);
    }

    private static int toIntOrZero(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static String stringified(Collection<?> items) {
        return items.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", ", "[", "]"));
    }

    private static Optional<Path> pathIfIsExecutable(Path javaBinary) {
        if (Files.isExecutable(javaBinary)) {
            return Optional.of(javaBinary);
        } else {
            LOG.warn(format("Java binary at '%s' is not executable.", javaBinary));
            return Optional.empty();
        }
    }


    static final class JdkLocation {
        private final String envVariableName;
        private final int majorVersion;
        private final int minorVersion;
        private final Path executablePath;

        public JdkLocation(String envVariableName, Path executablePath) {
            Matcher versionMatcher = JDKS_WITH_MAJOR_AND_MINOR_VERSION_ENV_VAR_PATTERN.matcher(envVariableName);
            boolean matches = versionMatcher.matches();
            assert matches;

            this.envVariableName = envVariableName;
            this.majorVersion = toIntOrZero(versionMatcher.group(1));
            this.minorVersion = toIntOrZero(versionMatcher.group(2));
            this.executablePath = executablePath;
        }

        public String envVariableName() {
            return envVariableName;
        }

        public int majorVersion() {
            return majorVersion;
        }

        public int minorVersion() {
            return minorVersion;
        }

        public Path executablePath() {
            return executablePath;
        }
    }

    interface JavaExecutableFinder {

        Optional<Path> executablePathFor(Path jdkBasePath);

    }

    class DefaultJavaExecutableFinder implements JavaExecutableFinder {

        @Override
        public Optional<Path> executablePathFor(Path jdkBasePath) {
            switch (OperatingSystem.current(context)) {
                case LINUX:
                case MACOS: {
                    Path javaBinary = jdkBasePath.resolve("bin").resolve("java");
                    return pathIfIsExecutable(javaBinary);
                }

                case WINDOWS: {
                    Path javaBinary = jdkBasePath.resolve("bin").resolve("java.exe");
                    return pathIfIsExecutable(javaBinary);
                }

                case UNKNOWN:
                    LOG.warn("Unsupported operating system: " + OperatingSystem.current(context));
                    return Optional.empty();

                default:
                    throw new IllegalStateException("Unsupported operating system: " + OperatingSystem.current(context));
            }
        }
    }
}
