package com.gradle.develocity;

import com.intellij.openapi.diagnostic.Logger;

public interface ExtensionApplicationListener {

    void geExtensionApplied(String version);

    void ccudExtensionApplied(String version);

    class LoggingExtensionApplicationListener implements ExtensionApplicationListener {

        private static final Logger LOG = Logger.getInstance(LoggingExtensionApplicationListener.class.getName());

        @Override
        public void geExtensionApplied(String version) {
            LOG.info("Adding develocity-maven-extension to Maven extensions classpath");
        }

        @Override
        public void ccudExtensionApplied(String version) {
            LOG.info("Adding common-custom-user-data-maven-extension to Maven extensions classpath");
        }

    }

}
