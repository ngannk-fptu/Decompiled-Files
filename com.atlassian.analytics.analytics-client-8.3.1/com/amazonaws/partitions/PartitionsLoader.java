/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.partitions;

import com.amazonaws.SdkClientException;
import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.partitions.PartitionMetadataProvider;
import com.amazonaws.partitions.model.Partitions;
import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;

@SdkInternalApi
public class PartitionsLoader {
    public static final String PARTITIONS_RESOURCE_PATH = "com/amazonaws/partitions/endpoints.json";
    public static final String PARTITIONS_OVERRIDE_RESOURCE_PATH = "com/amazonaws/partitions/override/endpoints.json";
    private static final ObjectMapper mapper = new ObjectMapper().disable(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS).disable(MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS).enable(JsonParser.Feature.ALLOW_COMMENTS).disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    private final ClassLoader classLoader = PartitionsLoader.class.getClassLoader();

    public PartitionMetadataProvider build() {
        InputStream stream = this.classLoader.getResourceAsStream(PARTITIONS_OVERRIDE_RESOURCE_PATH);
        if (stream != null) {
            return new PartitionMetadataProvider(this.loadPartitionFromStream(stream, PARTITIONS_OVERRIDE_RESOURCE_PATH).getPartitions());
        }
        stream = this.classLoader.getResourceAsStream(PARTITIONS_RESOURCE_PATH);
        if (stream == null) {
            throw new SdkClientException("Unable to load partition metadata from com/amazonaws/partitions/endpoints.json");
        }
        return new PartitionMetadataProvider(this.loadPartitionFromStream(stream, PARTITIONS_RESOURCE_PATH).getPartitions());
    }

    private Partitions loadPartitionFromStream(InputStream stream, String location) {
        try {
            Partitions partitions = mapper.readValue(stream, Partitions.class);
            return partitions;
        }
        catch (IOException e) {
            throw new SdkClientException("Error while loading partitions file from " + location, e);
        }
        finally {
            IOUtils.closeQuietly(stream, null);
        }
    }
}

