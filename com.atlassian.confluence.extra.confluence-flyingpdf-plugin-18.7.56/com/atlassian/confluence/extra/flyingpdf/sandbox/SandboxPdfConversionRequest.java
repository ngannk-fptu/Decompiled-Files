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

public class SandboxPdfConversionRequest {
    private final String baseUrl;
    private final String contextPath;
    private final String cdnUrl;
    private final String fontPath;
    private final String username;
    private final String exportFile;
    private final byte[] document;

    public SandboxPdfConversionRequest(String baseUrl, String contextPath, String cdnUrl, String fontPath, String username, String exportFile, byte[] document) {
        this.baseUrl = baseUrl;
        this.contextPath = contextPath;
        this.cdnUrl = cdnUrl;
        this.fontPath = fontPath;
        this.username = username;
        this.exportFile = exportFile;
        this.document = document;
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public String getContextPath() {
        return this.contextPath;
    }

    public String getCdnUrl() {
        return this.cdnUrl;
    }

    public String getFontPath() {
        return this.fontPath;
    }

    public String getUsername() {
        return this.username;
    }

    public String getExportFile() {
        return this.exportFile;
    }

    public byte[] getDocument() {
        return this.document;
    }

    static Serializer serializer() {
        return Serializer.instance;
    }

    static final class Serializer
    implements SandboxSerializer<SandboxPdfConversionRequest> {
        static final Serializer instance = new Serializer();

        private Serializer() {
        }

        public byte[] serialize(SandboxPdfConversionRequest request) {
            ArrayList<byte[]> fields = new ArrayList<byte[]>();
            fields.add(SandboxSerializers.stringSerializer().serialize((Object)request.getBaseUrl()));
            fields.add(SandboxSerializers.stringSerializer().serialize((Object)request.getContextPath()));
            fields.add(SandboxSerializers.stringSerializer().serialize((Object)request.getCdnUrl()));
            fields.add(SandboxSerializers.stringSerializer().serialize((Object)request.getFontPath()));
            fields.add(SandboxSerializers.stringSerializer().serialize((Object)request.getUsername()));
            fields.add(SandboxSerializers.stringSerializer().serialize((Object)request.getExportFile()));
            fields.add(request.getDocument());
            return SandboxSerializers.compositeByteArraySerializer().serialize(fields);
        }

        public SandboxPdfConversionRequest deserialize(byte[] bytes) {
            List fields = (List)SandboxSerializers.compositeByteArraySerializer().deserialize(bytes);
            String baseUrl = (String)SandboxSerializers.stringSerializer().deserialize((byte[])fields.get(0));
            String contextPath = (String)SandboxSerializers.stringSerializer().deserialize((byte[])fields.get(1));
            String cdnUrl = (String)SandboxSerializers.stringSerializer().deserialize((byte[])fields.get(2));
            String fontPath = (String)SandboxSerializers.stringSerializer().deserialize((byte[])fields.get(3));
            String username = (String)SandboxSerializers.stringSerializer().deserialize((byte[])fields.get(4));
            String exportFile = (String)SandboxSerializers.stringSerializer().deserialize((byte[])fields.get(5));
            byte[] document = (byte[])fields.get(6);
            return new SandboxPdfConversionRequest(baseUrl, contextPath, cdnUrl, fontPath, username, exportFile, document);
        }
    }
}

