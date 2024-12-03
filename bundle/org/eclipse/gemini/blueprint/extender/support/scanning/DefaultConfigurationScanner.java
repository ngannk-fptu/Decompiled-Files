/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.springframework.util.ObjectUtils
 */
package org.eclipse.gemini.blueprint.extender.support.scanning;

import java.util.Enumeration;
import org.eclipse.gemini.blueprint.extender.support.internal.ConfigUtils;
import org.eclipse.gemini.blueprint.extender.support.scanning.ConfigurationScanner;
import org.osgi.framework.Bundle;
import org.springframework.util.ObjectUtils;

public class DefaultConfigurationScanner
implements ConfigurationScanner {
    private static final String CONTEXT_DIR = "/META-INF/spring/";
    private static final String CONTEXT_FILES = "*.xml";
    public static final String DEFAULT_CONFIG = "osgibundle:/META-INF/spring/*.xml";

    @Override
    public String[] getConfigurations(Bundle bundle) {
        Object[] locations = ConfigUtils.getHeaderLocations(bundle.getHeaders());
        if (ObjectUtils.isEmpty((Object[])locations)) {
            Enumeration defaultConfig = bundle.findEntries(CONTEXT_DIR, CONTEXT_FILES, false);
            if (defaultConfig != null && defaultConfig.hasMoreElements()) {
                return new String[]{DEFAULT_CONFIG};
            }
            return new String[0];
        }
        return locations;
    }
}

