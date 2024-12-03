/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.sandbox.SandboxSerializer
 *  com.atlassian.confluence.util.sandbox.SandboxSerializers
 */
package com.atlassian.plugins.conversion.sandbox;

import com.atlassian.confluence.util.sandbox.SandboxSerializer;
import com.atlassian.confluence.util.sandbox.SandboxSerializers;
import com.atlassian.plugins.conversion.sandbox.SandboxConversionStatus;
import java.util.Objects;

public class SandboxConversionResponse {
    private SandboxConversionStatus status;

    public SandboxConversionResponse(SandboxConversionStatus status) {
        this.status = status;
    }

    public SandboxConversionStatus getStatus() {
        return this.status;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SandboxConversionResponse)) {
            return false;
        }
        SandboxConversionResponse that = (SandboxConversionResponse)o;
        return this.getStatus() == that.getStatus();
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.getStatus()});
    }

    static Serializer serializer() {
        return Serializer.instance;
    }

    static class Serializer
    implements SandboxSerializer<SandboxConversionResponse> {
        static final Serializer instance = new Serializer();

        private Serializer() {
        }

        public byte[] serialize(SandboxConversionResponse conversionResponse) {
            return SandboxSerializers.stringSerializer().serialize((Object)conversionResponse.getStatus().name());
        }

        public SandboxConversionResponse deserialize(byte[] bytes) {
            return new SandboxConversionResponse(SandboxConversionStatus.valueOf((String)SandboxSerializers.stringSerializer().deserialize(bytes)));
        }
    }
}

