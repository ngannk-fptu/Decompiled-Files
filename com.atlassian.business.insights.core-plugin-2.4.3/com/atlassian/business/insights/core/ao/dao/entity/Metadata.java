/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.business.insights.core.ao.dao.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Metadata
implements Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Metadata.class);
    private static final String EMPTY_JSON_OBJECT = "{}";
    private String metadataVersion = "1.0";
    private String nodeId;

    private Metadata() {
    }

    private Metadata(@Nonnull String nodeId) {
        this.nodeId = Objects.requireNonNull(nodeId);
    }

    public static Metadata getInstance(@Nonnull String nodeId) {
        return new Metadata(nodeId);
    }

    @Nullable
    public static Metadata fromSerializedStr(@Nullable String str) {
        try {
            return (Metadata)new ObjectMapper().readValue(str, Metadata.class);
        }
        catch (Exception e) {
            LOGGER.warn(String.format("Could not deserialize metadata object; %s", str));
            return null;
        }
    }

    @Nonnull
    public String getNodeId() {
        return this.nodeId;
    }

    @Nonnull
    public String getMetadataVersion() {
        return this.metadataVersion;
    }

    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString((Object)this);
        }
        catch (Exception e) {
            return EMPTY_JSON_OBJECT;
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Metadata metadata = (Metadata)o;
        return Objects.equals(this.nodeId, metadata.nodeId);
    }

    public int hashCode() {
        return Objects.hash(this.nodeId);
    }
}

