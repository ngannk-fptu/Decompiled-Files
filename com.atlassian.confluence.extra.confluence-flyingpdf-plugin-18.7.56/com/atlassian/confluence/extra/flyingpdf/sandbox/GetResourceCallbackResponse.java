/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.sandbox.SandboxSerializer
 *  com.atlassian.confluence.util.sandbox.SandboxSerializers
 */
package com.atlassian.confluence.extra.flyingpdf.sandbox;

import com.atlassian.confluence.util.sandbox.SandboxSerializer;
import com.atlassian.confluence.util.sandbox.SandboxSerializers;
import java.util.ArrayList;
import java.util.List;

public class GetResourceCallbackResponse {
    private final byte[] data;

    public GetResourceCallbackResponse(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return this.data;
    }

    static Serializer serializer() {
        return Serializer.instance;
    }

    static class Serializer
    implements SandboxSerializer<GetResourceCallbackResponse> {
        static final Serializer instance = new Serializer();

        private Serializer() {
        }

        public byte[] serialize(GetResourceCallbackResponse response) {
            ArrayList<byte[]> fields = new ArrayList<byte[]>();
            fields.add(response.getData());
            return SandboxSerializers.compositeByteArraySerializer().serialize(fields);
        }

        public GetResourceCallbackResponse deserialize(byte[] bytes) {
            List fields = (List)SandboxSerializers.compositeByteArraySerializer().deserialize(bytes);
            return new GetResourceCallbackResponse((byte[])fields.get(0));
        }
    }
}

