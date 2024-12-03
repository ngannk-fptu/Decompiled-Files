/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.sandbox.SandboxSerializer
 *  com.atlassian.confluence.util.sandbox.SandboxSerializers
 *  com.atlassian.plugins.conversion.sandbox.SandboxConversionStatus
 */
package com.atlassian.confluence.plugins.conversion.convert.image;

import com.atlassian.confluence.util.sandbox.SandboxSerializer;
import com.atlassian.confluence.util.sandbox.SandboxSerializers;
import com.atlassian.plugins.conversion.sandbox.SandboxConversionStatus;
import java.util.Objects;

public class SandboxPdfThumbnailResponse {
    private SandboxConversionStatus status;

    public SandboxPdfThumbnailResponse(SandboxConversionStatus status) {
        this.status = status;
    }

    public SandboxConversionStatus getStatus() {
        return this.status;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SandboxPdfThumbnailResponse)) {
            return false;
        }
        SandboxPdfThumbnailResponse that = (SandboxPdfThumbnailResponse)o;
        return this.getStatus() == that.getStatus();
    }

    public int hashCode() {
        return Objects.hash(this.getStatus());
    }

    static Serializer serializer() {
        return Serializer.instance;
    }

    static class Serializer
    implements SandboxSerializer<SandboxPdfThumbnailResponse> {
        static final Serializer instance = new Serializer();

        private Serializer() {
        }

        public byte[] serialize(SandboxPdfThumbnailResponse conversionResponse) {
            return SandboxSerializers.stringSerializer().serialize((Object)conversionResponse.getStatus().name());
        }

        public SandboxPdfThumbnailResponse deserialize(byte[] bytes) {
            return new SandboxPdfThumbnailResponse(SandboxConversionStatus.valueOf((String)((String)SandboxSerializers.stringSerializer().deserialize(bytes))));
        }
    }
}

