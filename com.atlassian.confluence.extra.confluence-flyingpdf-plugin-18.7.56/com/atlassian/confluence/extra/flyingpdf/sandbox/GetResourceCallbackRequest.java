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

public class GetResourceCallbackRequest {
    private final String uri;
    private final String username;

    public GetResourceCallbackRequest(String uri, String username) {
        this.uri = uri;
        this.username = username;
    }

    public String getUri() {
        return this.uri;
    }

    public String getUsername() {
        return this.username;
    }

    static Serializer serializer() {
        return Serializer.instance;
    }

    static class Serializer
    implements SandboxSerializer<GetResourceCallbackRequest> {
        static final Serializer instance = new Serializer();

        private Serializer() {
        }

        public byte[] serialize(GetResourceCallbackRequest request) {
            ArrayList<byte[]> fields = new ArrayList<byte[]>();
            fields.add(SandboxSerializers.stringSerializer().serialize((Object)request.getUri()));
            fields.add(SandboxSerializers.stringSerializer().serialize((Object)request.getUsername()));
            return SandboxSerializers.compositeByteArraySerializer().serialize(fields);
        }

        public GetResourceCallbackRequest deserialize(byte[] bytes) {
            List fields = (List)SandboxSerializers.compositeByteArraySerializer().deserialize(bytes);
            return new GetResourceCallbackRequest((String)SandboxSerializers.stringSerializer().deserialize((byte[])fields.get(0)), (String)SandboxSerializers.stringSerializer().deserialize((byte[])fields.get(1)));
        }
    }
}

