package com.gradle.develocity.teamcity;

import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.controllers.BuildDataExtensionUtil;
import jetbrains.buildServer.serverSide.BuildsManager;
import jetbrains.buildServer.serverSide.SBuild;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PlaceId;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.PluginUIContext;
import jetbrains.buildServer.web.openapi.PositionConstraint;
import jetbrains.buildServer.web.openapi.SimplePageExtension;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import jetbrains.buildServer.web.util.WebUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BuildScanController extends BaseController {
    private static final String PLUGIN_NAME = "Build Scans";
    private final PluginDescriptor descriptor;
    private final BuildsManager buildsManager;
    private final BuildScanLookup buildScanLookup;
    private final SBuildServer buildServer;
    private final BuildScanDisplayArbiter buildScanDisplayArbiter;

    public BuildScanController(
        @NotNull PluginDescriptor descriptor,
        @NotNull PagePlaces places,
        @NotNull BuildsManager buildsManager,
        @NotNull WebControllerManager controllerManager,
        @NotNull BuildScanLookup buildScanLookup,
        @NotNull SBuildServer buildServer,
        @NotNull BuildScanDisplayArbiter buildScanDisplayArbiter
    ) {
        this.descriptor = descriptor;
        this.buildsManager = buildsManager;
        this.buildScanLookup = buildScanLookup;
        this.buildServer = buildServer;
        this.buildScanDisplayArbiter = buildScanDisplayArbiter;
        String url = "/buildScanLinks.html";
        // For the Sakura UI we register the Plugin using the SAKURA-prefixed PlaceIDs.
        SimplePageExtension buildOverview = new SimplePageExtension(places);
        buildOverview.setPlaceId(new PlaceId("SAKURA_BUILD_OVERVIEW"));
        buildOverview.setPluginName(PLUGIN_NAME);
        buildOverview.setIncludeUrl(url);
        buildOverview.register();

        // For the Classic UI we continue using the regular PlaceIds. Those plugins are rendered on the Server
        // and, generally speaking, stay the same, as they were the last decade
        SimplePageExtension classicBuildOverview = new SimplePageExtension(places);
        classicBuildOverview.setPlaceId(PlaceId.BUILD_SUMMARY);
        classicBuildOverview.setPluginName(PLUGIN_NAME);
        classicBuildOverview.setPosition(PositionConstraint.last());
        classicBuildOverview.setIncludeUrl(url);
        classicBuildOverview.register();

        controllerManager.registerController(url, this);
    }

    @Nullable
    @Override
    protected ModelAndView doHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws Exception {
        final ModelAndView mv = new ModelAndView(descriptor.getPluginResourcesPath("buildScanCrumbSummary.jsp"));
        boolean isSakuraUI = WebUtil.sakuraUIOpened(request);
        SBuild build = null;
        if (isSakuraUI) {
            PluginUIContext pluginUIContext = PluginUIContext.getFromRequest(request);
            Long btId = pluginUIContext.getBuildId();
            if (btId != null) {
                build = buildsManager.findBuildInstanceById(btId);
            }
        } else {
            build = BuildDataExtensionUtil.retrieveBuild(request, buildServer);
        }

        if (build != null) {
            BuildScanReferences buildScans = buildScanLookup.getBuildScansForBuild(build);
            mv.getModel().put("hasSupportedRunner", buildScanDisplayArbiter.hasSupportedRunner(build.getBuildType()));
            mv.getModel().put("buildScans", buildScans);
        }
        return mv;
    }
}
