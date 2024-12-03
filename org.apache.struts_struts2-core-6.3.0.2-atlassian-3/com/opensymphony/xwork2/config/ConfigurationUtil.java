/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.config;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfigurationUtil {
    private static final Logger LOG = LogManager.getLogger(ConfigurationUtil.class);

    private ConfigurationUtil() {
    }

    public static List<PackageConfig> buildParentsFromString(Configuration configuration, String parent) {
        List<String> parentPackageNames = ConfigurationUtil.buildParentListFromString(parent);
        ArrayList<PackageConfig> parentPackageConfigs = new ArrayList<PackageConfig>();
        for (String parentPackageName : parentPackageNames) {
            PackageConfig parentPackageContext = configuration.getPackageConfig(parentPackageName);
            if (parentPackageContext == null) continue;
            parentPackageConfigs.add(parentPackageContext);
        }
        return parentPackageConfigs;
    }

    public static List<String> buildParentListFromString(String parent) {
        if (StringUtils.isEmpty((CharSequence)parent)) {
            return Collections.emptyList();
        }
        StringTokenizer tokenizer = new StringTokenizer(parent, ",");
        ArrayList<String> parents = new ArrayList<String>();
        while (tokenizer.hasMoreTokens()) {
            String parentName = tokenizer.nextToken().trim();
            if (!StringUtils.isNotEmpty((CharSequence)parentName)) continue;
            parents.add(parentName);
        }
        return parents;
    }
}

