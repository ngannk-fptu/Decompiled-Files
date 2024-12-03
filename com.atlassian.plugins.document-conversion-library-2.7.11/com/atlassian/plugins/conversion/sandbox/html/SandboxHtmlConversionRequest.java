/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.sandbox.SandboxSerializer
 *  com.atlassian.confluence.util.sandbox.SandboxSerializers
 *  org.springframework.util.SerializationUtils
 */
package com.atlassian.plugins.conversion.sandbox.html;

import com.atlassian.confluence.util.sandbox.SandboxSerializer;
import com.atlassian.confluence.util.sandbox.SandboxSerializers;
import com.atlassian.plugins.conversion.sandbox.html.SandboxHtmlConversionType;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.util.SerializationUtils;

public class SandboxHtmlConversionRequest {
    private final Map<String, Object> args;
    private final String imgPath;
    private final byte[] inputFile;
    private final SandboxHtmlConversionType sandboxHtmlConversionType;

    public SandboxHtmlConversionRequest(Map<String, Object> args, String imgPath, byte[] inputFile, SandboxHtmlConversionType sandboxHtmlConversionType) {
        this.args = args;
        this.imgPath = imgPath;
        this.inputFile = inputFile;
        this.sandboxHtmlConversionType = sandboxHtmlConversionType;
    }

    public Map<String, Object> getArgs() {
        return this.args;
    }

    public String getImgPath() {
        return this.imgPath;
    }

    public byte[] getInputFile() {
        return this.inputFile;
    }

    public SandboxHtmlConversionType getSandboxHtmlConversionType() {
        return this.sandboxHtmlConversionType;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SandboxHtmlConversionRequest that = (SandboxHtmlConversionRequest)o;
        return Objects.equals(this.args, that.args) && Objects.equals(this.imgPath, that.imgPath) && Arrays.equals(this.inputFile, that.inputFile) && this.sandboxHtmlConversionType == that.sandboxHtmlConversionType;
    }

    public int hashCode() {
        int result = Objects.hash(new Object[]{this.args, this.imgPath, this.sandboxHtmlConversionType});
        result = 31 * result + Arrays.hashCode(this.inputFile);
        return result;
    }

    static Serializer serializer() {
        return Serializer.instance;
    }

    static final class Serializer
    implements SandboxSerializer<SandboxHtmlConversionRequest> {
        static final Serializer instance = new Serializer();

        Serializer() {
        }

        public byte[] serialize(SandboxHtmlConversionRequest sandboxHtmlConversionRequest) {
            ArrayList<byte[]> bytesList = new ArrayList<byte[]>();
            try {
                bytesList.add(SerializationUtils.serialize(sandboxHtmlConversionRequest.getArgs()));
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
            bytesList.add(sandboxHtmlConversionRequest.getImgPath().getBytes(StandardCharsets.UTF_8));
            bytesList.add(sandboxHtmlConversionRequest.getInputFile());
            bytesList.add(sandboxHtmlConversionRequest.getSandboxHtmlConversionType().name().getBytes(StandardCharsets.UTF_8));
            return SandboxSerializers.compositeByteArraySerializer().serialize(bytesList);
        }

        public SandboxHtmlConversionRequest deserialize(byte[] bytes) {
            List bytesList = (List)SandboxSerializers.compositeByteArraySerializer().deserialize(bytes);
            Map args = (Map)SerializationUtils.deserialize((byte[])((byte[])bytesList.get(0)));
            String imgPath = new String((byte[])bytesList.get(1), StandardCharsets.UTF_8);
            byte[] fileContent = (byte[])bytesList.get(2);
            String conversionTypeName = new String((byte[])bytesList.get(3), StandardCharsets.UTF_8);
            SandboxHtmlConversionType type = SandboxHtmlConversionType.valueOf(conversionTypeName);
            return new SandboxHtmlConversionRequest(args, imgPath, fileContent, type);
        }
    }
}

