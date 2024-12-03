/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.eclipse.gemini.blueprint.io.OsgiBundleResourcePatternResolver
 *  org.eclipse.gemini.blueprint.util.OsgiStringUtils
 *  org.osgi.framework.Bundle
 *  org.springframework.util.ObjectUtils
 */
package org.eclipse.gemini.blueprint.extender.internal.blueprint.activator.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.extender.internal.blueprint.activator.support.BlueprintConfigUtils;
import org.eclipse.gemini.blueprint.extender.support.scanning.ConfigurationScanner;
import org.eclipse.gemini.blueprint.io.OsgiBundleResourcePatternResolver;
import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.osgi.framework.Bundle;
import org.springframework.util.ObjectUtils;

public class BlueprintConfigurationScanner
implements ConfigurationScanner {
    private static final Log log = LogFactory.getLog(BlueprintConfigurationScanner.class);
    private static final String CONTEXT_DIR = "OSGI-INF/blueprint/";
    private static final String CONTEXT_FILES = "*.xml";
    public static final String DEFAULT_CONFIG = "osgibundle:OSGI-INF/blueprint/*.xml";

    @Override
    public String[] getConfigurations(Bundle bundle) {
        Object[] locations;
        String bundleName = OsgiStringUtils.nullSafeName((Bundle)bundle);
        boolean trace = log.isTraceEnabled();
        boolean debug = log.isDebugEnabled();
        if (debug) {
            log.debug((Object)("Scanning bundle '" + bundleName + "' for blueprint configurations..."));
        }
        if ((locations = BlueprintConfigUtils.getBlueprintHeaderLocations(bundle.getHeaders())) == null) {
            if (trace) {
                log.trace((Object)("Bundle '" + bundleName + "' has no declared locations; trying default " + DEFAULT_CONFIG));
            }
            locations = new String[]{DEFAULT_CONFIG};
        } else if (ObjectUtils.isEmpty((Object[])locations)) {
            log.info((Object)("Bundle '" + bundleName + "' has an empty blueprint header - ignoring bundle..."));
            return new String[0];
        }
        Object[] configs = this.findValidBlueprintConfigs(bundle, (String[])locations);
        if (debug) {
            log.debug((Object)("Discovered in bundle '" + bundleName + "' blueprint configurations=" + Arrays.toString(configs)));
        }
        return configs;
    }

    private String[] findValidBlueprintConfigs(Bundle bundle, String[] locations) {
        ArrayList<String> configs = new ArrayList<String>(locations.length);
        OsgiBundleResourcePatternResolver loader = new OsgiBundleResourcePatternResolver(bundle);
        boolean debug = log.isDebugEnabled();
        for (String location : locations) {
            if (this.isAbsolute(location)) {
                configs.add(location);
                continue;
            }
            try {
                Object[] resources;
                String loc = location;
                if (loc.endsWith("/")) {
                    loc = loc + CONTEXT_FILES;
                }
                if (ObjectUtils.isEmpty((Object[])(resources = loader.getResources(loc)))) continue;
                for (Object resource : resources) {
                    if (!resource.exists()) continue;
                    String value = resource.getURL().toString();
                    if (debug) {
                        log.debug((Object)("Found location " + value));
                    }
                    configs.add(value);
                }
            }
            catch (IOException ex) {
                if (!debug) continue;
                log.debug((Object)("Cannot resolve location " + location), (Throwable)ex);
            }
        }
        return configs.toArray(new String[configs.size()]);
    }

    private boolean isAbsolute(String location) {
        return !location.endsWith("/") && !location.contains("*");
    }
}

