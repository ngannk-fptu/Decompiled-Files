/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.regions;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.SdkClientException;
import com.amazonaws.regions.LegacyRegionXmlLoadUtils;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionMetadata;
import com.amazonaws.regions.RegionMetadataFactory;
import com.amazonaws.util.SdkHttpUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

public class RegionUtils {
    private static volatile RegionMetadata regionMetadata;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static RegionMetadata getRegionMetadata() {
        RegionMetadata rval = regionMetadata;
        if (rval != null) {
            return rval;
        }
        Class<RegionUtils> clazz = RegionUtils.class;
        synchronized (RegionUtils.class) {
            if (regionMetadata == null) {
                RegionUtils.initialize();
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return regionMetadata;
        }
    }

    public static void initialize() {
        regionMetadata = RegionMetadataFactory.create();
    }

    public static void initializeWithMetadata(RegionMetadata metadata) {
        if (metadata == null) {
            throw new IllegalArgumentException("metadata cannot be null");
        }
        regionMetadata = metadata;
    }

    public static List<Region> getRegions() {
        return RegionUtils.getRegionMetadata().getRegions();
    }

    public static List<Region> getRegionsForService(String serviceAbbreviation) {
        return RegionUtils.getRegionMetadata().getRegionsForService(serviceAbbreviation);
    }

    public static Region getRegion(String regionName) {
        String urlEncodedRegionName = regionName == null ? null : SdkHttpUtils.urlEncode(regionName, false);
        return RegionUtils.getRegionMetadata().getRegion(urlEncodedRegionName);
    }

    @Deprecated
    public static RegionMetadata loadMetadataFromURI(URI uri) throws IOException {
        return RegionUtils.loadMetadataFromURI(uri, null);
    }

    @Deprecated
    public static RegionMetadata loadMetadataFromURI(URI uri, ClientConfiguration config) throws IOException {
        return LegacyRegionXmlLoadUtils.load(uri, config);
    }

    @Deprecated
    public static RegionMetadata loadMetadataFromFile(File file) throws IOException {
        return LegacyRegionXmlLoadUtils.load(file);
    }

    @Deprecated
    public static RegionMetadata loadMetadataFromResource(String name) throws IOException {
        return LegacyRegionXmlLoadUtils.load(RegionUtils.class, name);
    }

    @Deprecated
    public static RegionMetadata loadMetadataFromResource(Class<?> clazz, String name) throws IOException {
        return LegacyRegionXmlLoadUtils.load(clazz, name);
    }

    @Deprecated
    public static RegionMetadata loadMetadataFromResource(ClassLoader classLoader, String name) throws IOException {
        return LegacyRegionXmlLoadUtils.load(classLoader, name);
    }

    @Deprecated
    public static RegionMetadata loadMetadataFromInputStream(InputStream stream) throws IOException {
        return LegacyRegionXmlLoadUtils.load(stream);
    }

    @Deprecated
    public static void init() {
        RegionUtils.initialize();
    }

    @Deprecated
    public static synchronized void initializeFromURI(URI uri) {
        RegionUtils.initializeFromURI(uri, null);
    }

    @Deprecated
    public static synchronized void initializeFromURI(URI uri, ClientConfiguration config) {
        try {
            regionMetadata = RegionUtils.loadMetadataFromURI(uri, config);
        }
        catch (IOException exception) {
            throw new SdkClientException("Error parsing region metadata from " + uri, exception);
        }
    }

    @Deprecated
    public static synchronized void initializeFromFile(File file) {
        try {
            regionMetadata = RegionUtils.loadMetadataFromFile(file);
        }
        catch (IOException exception) {
            throw new SdkClientException("Error parsing region metadata from " + file, exception);
        }
    }

    @Deprecated
    public static synchronized void initializeFromResource(String name) {
        RegionUtils.initializeFromResource(RegionUtils.class, name);
    }

    @Deprecated
    public static synchronized void initializeFromResource(Class<?> clazz, String name) {
        try {
            regionMetadata = RegionUtils.loadMetadataFromResource(clazz, name);
        }
        catch (IOException exception) {
            throw new SdkClientException("Error parsing region metadata from resource " + name, exception);
        }
    }

    @Deprecated
    public static synchronized void initializeFromResource(ClassLoader classLoader, String name) {
        try {
            regionMetadata = RegionUtils.loadMetadataFromResource(classLoader, name);
        }
        catch (IOException exception) {
            throw new SdkClientException("Error parsing region metadata from resource " + name, exception);
        }
    }
}

