/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.util.ClassLoaderUtils
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Maps
 *  org.apache.commons.lang3.StringUtils
 *  org.osgi.framework.Version
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.twdata.pkgscanner.DefaultOsgiVersionConverter
 *  org.twdata.pkgscanner.ExportPackage
 */
package com.atlassian.plugin.osgi.container.felix;

import com.atlassian.plugin.util.ClassLoaderUtils;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.twdata.pkgscanner.DefaultOsgiVersionConverter;
import org.twdata.pkgscanner.ExportPackage;

final class ExportBuilderUtils {
    private static Logger LOG = LoggerFactory.getLogger(ExportBuilderUtils.class);
    private static final DefaultOsgiVersionConverter converter = new DefaultOsgiVersionConverter();
    private static final String EMPTY_OSGI_VERSION = Version.emptyVersion.toString();
    private static final Function<String, String> CONVERT_VERSION = from -> {
        if (from != null && from.trim().length() > 0) {
            return converter.getVersion(from);
        }
        return EMPTY_OSGI_VERSION;
    };

    private ExportBuilderUtils() {
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    static Map<String, String> parseExportFile(String exportFilePath) {
        Properties props = new Properties();
        try (InputStream in = ClassLoaderUtils.getResourceAsStream((String)exportFilePath, ExportBuilderUtils.class);){
            if (in == null) {
                LOG.warn("Unable to find properties for package export: {}", (Object)exportFilePath);
                ImmutableMap immutableMap = ImmutableMap.of();
                return immutableMap;
            }
            props.load(in);
            return Maps.transformValues((Map)Maps.fromProperties((Properties)props), CONVERT_VERSION);
        }
        catch (IOException e) {
            LOG.warn("Problem occurred while processing package export: {}", (Object)exportFilePath, (Object)e);
            return ImmutableMap.of();
        }
    }

    static void copyUnlessExist(Map<String, String> dest, Map<String, String> src) {
        dest.putAll(Maps.filterKeys(src, key -> !dest.containsKey(key)));
    }

    static Map<String, String> toMap(Iterable<ExportPackage> exportPackages) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        for (ExportPackage pkg : exportPackages) {
            builder.put((Object)pkg.getPackageName(), (Object)StringUtils.defaultString((String)pkg.getVersion(), (String)EMPTY_OSGI_VERSION));
        }
        return builder.build();
    }
}

