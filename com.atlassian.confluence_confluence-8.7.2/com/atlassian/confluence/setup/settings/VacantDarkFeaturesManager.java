/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gzipfilter.util.IOUtils
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Sets
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.setup.settings;

import com.atlassian.confluence.setup.settings.DarkFeatures;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.confluence.setup.settings.FeatureService;
import com.atlassian.confluence.setup.settings.UnknownFeatureException;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.gzipfilter.util.IOUtils;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VacantDarkFeaturesManager
implements DarkFeaturesManager {
    private static final Logger log = LoggerFactory.getLogger(VacantDarkFeaturesManager.class);
    private static final String SYSTEM_DARKFEATURE_PROPERTY_CONFLUENCE_PREFIX = "atlassian.darkfeature.confluence.";
    private static final String SYSTEM_DARKFEATURE_PROPERTY_COMMON_PREFIX = "atlassian.darkfeature.";
    private static final boolean DARK_FEATURES_DISABLED_SYSTEM_WIDE = Boolean.getBoolean("atlassian.darkfeature.disabled");
    private static final String SYSTEM_DARKFEATURE_PROPERTIES_FILE = "darkfeature.properties";
    private static final Set<String> systemEnabledFeatures;
    public static final Set<String> SYSTEM_DISABLED_FEATURES;
    private static final String SHOULD_NOT_BE_CALLED = "should not be called on VacantDarkFeaturesManager";
    protected static final DarkFeatures NO_FEATURES;
    protected static final DarkFeatures ONLY_SYSTEM_FEATURES;

    VacantDarkFeaturesManager() {
    }

    @Deprecated
    public VacantDarkFeaturesManager(FeatureService ignored) {
    }

    @Override
    public DarkFeatures getDarkFeatures() {
        return this.getDarkFeatures(null);
    }

    @Override
    public DarkFeatures getDarkFeaturesAllUsers() {
        if (DARK_FEATURES_DISABLED_SYSTEM_WIDE) {
            return NO_FEATURES;
        }
        return ONLY_SYSTEM_FEATURES;
    }

    @Override
    public DarkFeatures getSiteDarkFeatures() {
        return this.getDarkFeatures(null);
    }

    @Override
    public DarkFeatures getDarkFeatures(ConfluenceUser user) {
        return ONLY_SYSTEM_FEATURES;
    }

    @Override
    public void enableUserFeature(String featureKey) throws UnknownFeatureException {
        throw new IllegalStateException("enableUserFeature(String) should not be called on VacantDarkFeaturesManager");
    }

    @Override
    public void enableUserFeature(ConfluenceUser user, String featureKey) {
        throw new IllegalStateException("enableUserFeature(ConfluenceUser, String) should not be called on VacantDarkFeaturesManager");
    }

    @Override
    public void disableUserFeature(String featureKey) throws UnknownFeatureException {
        throw new IllegalStateException("disableUserFeature(String) should not be called on VacantDarkFeaturesManager");
    }

    @Override
    public void disableUserFeature(ConfluenceUser user, String featureKey) {
        throw new IllegalStateException("disableUserFeature(ConfluenceUser, String) should not be called on VacantDarkFeaturesManager");
    }

    @Override
    public void enableSiteFeature(String featureKey) throws UnknownFeatureException {
        throw new IllegalStateException("enableSiteFeature(String) should not be called on VacantDarkFeaturesManager");
    }

    @Override
    public void disableSiteFeature(String featureKey) throws UnknownFeatureException {
        throw new IllegalStateException("disableSiteFeature(String) should not be called on VacantDarkFeaturesManager");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static {
        HashSet features = Sets.newHashSet();
        HashSet disabledFeatures = Sets.newHashSet();
        Properties props = new Properties();
        InputStream is = DarkFeaturesManager.class.getClassLoader().getResourceAsStream(SYSTEM_DARKFEATURE_PROPERTIES_FILE);
        if (is == null) {
            log.debug("No {} file found", (Object)SYSTEM_DARKFEATURE_PROPERTIES_FILE);
        } else {
            try {
                props.load(is);
                for (Map.Entry entry : props.entrySet()) {
                    if ("false".equalsIgnoreCase((String)entry.getValue())) continue;
                    features.add((String)entry.getKey());
                }
            }
            catch (IOException e) {
                log.debug("Error while reading {} file", (Object)SYSTEM_DARKFEATURE_PROPERTIES_FILE, (Object)e);
            }
            finally {
                IOUtils.closeQuietly((InputStream)is);
            }
        }
        for (Object object : System.getProperties().keySet()) {
            String[] systemPropertyPrefixes;
            String key = (String)object;
            for (String prefix : systemPropertyPrefixes = new String[]{SYSTEM_DARKFEATURE_PROPERTY_CONFLUENCE_PREFIX, SYSTEM_DARKFEATURE_PROPERTY_COMMON_PREFIX}) {
                if (!key.startsWith(prefix)) continue;
                String feature = key.substring(prefix.length());
                if ("false".equalsIgnoreCase(System.getProperty(key))) {
                    features.remove(feature);
                    disabledFeatures.add(feature);
                    continue;
                }
                features.add(feature);
            }
        }
        systemEnabledFeatures = ImmutableSet.copyOf((Collection)features);
        SYSTEM_DISABLED_FEATURES = ImmutableSet.copyOf((Collection)disabledFeatures);
        NO_FEATURES = new DarkFeatures(Collections.emptySet(), Collections.emptySet(), Collections.emptySet());
        ONLY_SYSTEM_FEATURES = new DarkFeatures(systemEnabledFeatures, Collections.emptySet(), Collections.emptySet());
    }
}

