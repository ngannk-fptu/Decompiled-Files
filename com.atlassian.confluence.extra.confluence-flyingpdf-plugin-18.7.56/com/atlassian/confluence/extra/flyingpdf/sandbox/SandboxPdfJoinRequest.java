/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.sandbox.SandboxSerializer
 *  com.atlassian.confluence.util.sandbox.SandboxSerializers
 *  org.apache.commons.lang3.SerializationUtils
 */
package com.atlassian.confluence.extra.flyingpdf.sandbox;

import com.atlassian.confluence.extra.flyingpdf.html.DecorationPolicy;
import com.atlassian.confluence.extra.flyingpdf.util.ExportedSpaceStructure;
import com.atlassian.confluence.util.sandbox.SandboxSerializer;
import com.atlassian.confluence.util.sandbox.SandboxSerializers;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.SerializationUtils;

public class SandboxPdfJoinRequest {
    private final String spaceKey;
    private final ExportedSpaceStructure exportedSpaceStructure;
    private final String outputFile;
    private final String baseUrl;
    private final DecorationPolicy decorationPolicy;

    SandboxPdfJoinRequest(String spaceKey, ExportedSpaceStructure exportedSpaceStructure, String outputFile, String baseUrl, DecorationPolicy decorationPolicy) {
        this.spaceKey = spaceKey;
        this.exportedSpaceStructure = exportedSpaceStructure;
        this.outputFile = outputFile;
        this.baseUrl = baseUrl;
        this.decorationPolicy = decorationPolicy;
    }

    static Serializer serializer() {
        return Serializer.instance;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    ExportedSpaceStructure getExportedSpaceStructure() {
        return this.exportedSpaceStructure;
    }

    String getOutputFile() {
        return this.outputFile;
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public DecorationPolicy getDecorationPolicy() {
        return this.decorationPolicy;
    }

    static final class Serializer
    implements SandboxSerializer<SandboxPdfJoinRequest> {
        static final Serializer instance = new Serializer();

        private Serializer() {
        }

        public byte[] serialize(SandboxPdfJoinRequest request) {
            ArrayList<byte[]> fields = new ArrayList<byte[]>();
            fields.add(SandboxSerializers.stringSerializer().serialize((Object)request.getSpaceKey()));
            fields.add(Serializer.structureToBytes(request.getExportedSpaceStructure()));
            fields.add(SandboxSerializers.stringSerializer().serialize((Object)request.getOutputFile()));
            fields.add(SandboxSerializers.stringSerializer().serialize((Object)request.getBaseUrl()));
            fields.add(Serializer.decorationPolicyToBytes(request.getDecorationPolicy()));
            return SandboxSerializers.compositeByteArraySerializer().serialize(fields);
        }

        public SandboxPdfJoinRequest deserialize(byte[] bytes) {
            List fields = (List)SandboxSerializers.compositeByteArraySerializer().deserialize(bytes);
            String spaceKey = (String)SandboxSerializers.stringSerializer().deserialize((byte[])fields.get(0));
            ExportedSpaceStructure exportedSpaceStructure = Serializer.structureFromBytes((byte[])fields.get(1));
            String outputFile = (String)SandboxSerializers.stringSerializer().deserialize((byte[])fields.get(2));
            String baseUrl = (String)SandboxSerializers.stringSerializer().deserialize((byte[])fields.get(3));
            DecorationPolicy decorationPolicy = Serializer.decorationPolicyFromBytes((byte[])fields.get(4));
            return new SandboxPdfJoinRequest(spaceKey, exportedSpaceStructure, outputFile, baseUrl, decorationPolicy);
        }

        private static byte[] structureToBytes(ExportedSpaceStructure exportedSpaceStructure) {
            return SerializationUtils.serialize((Serializable)exportedSpaceStructure);
        }

        private static ExportedSpaceStructure structureFromBytes(byte[] bytes) {
            return (ExportedSpaceStructure)SerializationUtils.deserialize((byte[])bytes);
        }

        private static byte[] decorationPolicyToBytes(DecorationPolicy decorationPolicy) {
            return SerializationUtils.serialize((Serializable)decorationPolicy);
        }

        private static DecorationPolicy decorationPolicyFromBytes(byte[] bytes) {
            return (DecorationPolicy)SerializationUtils.deserialize((byte[])bytes);
        }
    }
}

