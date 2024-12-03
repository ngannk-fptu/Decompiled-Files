/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.regions;

import com.amazonaws.SdkClientException;
import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.internal.config.Builder;
import com.amazonaws.regions.LegacyRegionXmlLoadUtils;
import com.amazonaws.regions.RegionMetadata;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.util.IOUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SdkInternalApi
public class LegacyRegionXmlMetadataBuilder
implements Builder<RegionMetadata> {
    private static final String REGIONS_FILE_OVERRIDE = "com.amazonaws.regions.RegionUtils.fileOverride";
    private static final String OVERRIDE_ENDPOINTS_RESOURCE_PATH = "/com/amazonaws/regions/override/regions.xml";
    private static final Log LOG = LogFactory.getLog(LegacyRegionXmlMetadataBuilder.class);

    @Override
    public RegionMetadata build() {
        return this.loadOverrideMetadataIfExists();
    }

    private RegionMetadata loadOverrideMetadataIfExists() {
        InputStream override;
        RegionMetadata metadata = this.loadFromSystemProperty();
        if (metadata == null && (override = RegionUtils.class.getResourceAsStream(OVERRIDE_ENDPOINTS_RESOURCE_PATH)) != null) {
            metadata = this.loadFromStream(override);
            IOUtils.closeQuietly(override, LOG);
        }
        return metadata;
    }

    private RegionMetadata loadFromSystemProperty() {
        String overrideFilePath = System.getProperty(REGIONS_FILE_OVERRIDE);
        if (overrideFilePath != null) {
            try {
                return LegacyRegionXmlLoadUtils.load(new File(overrideFilePath));
            }
            catch (IOException exception) {
                throw new SdkClientException("Error parsing region metadata from " + overrideFilePath, exception);
            }
        }
        return null;
    }

    private RegionMetadata loadFromStream(InputStream stream) {
        try {
            return LegacyRegionXmlLoadUtils.load(stream);
        }
        catch (IOException exception) {
            throw new SdkClientException("Error parsing region metadata from input stream", exception);
        }
    }
}

