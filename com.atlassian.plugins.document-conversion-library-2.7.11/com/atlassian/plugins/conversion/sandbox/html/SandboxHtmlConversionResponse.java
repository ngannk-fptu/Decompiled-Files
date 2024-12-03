/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.sandbox.SandboxSerializer
 *  com.atlassian.confluence.util.sandbox.SandboxSerializers
 */
package com.atlassian.plugins.conversion.sandbox.html;

import com.atlassian.confluence.util.sandbox.SandboxSerializer;
import com.atlassian.confluence.util.sandbox.SandboxSerializers;
import com.atlassian.plugins.conversion.convert.html.HtmlConversionResult;
import com.atlassian.plugins.conversion.sandbox.SandboxConversionStatus;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SandboxHtmlConversionResponse {
    private final SandboxConversionStatus status;
    private final HtmlConversionResult htmlConversionResult;

    public SandboxHtmlConversionResponse(SandboxConversionStatus status, HtmlConversionResult htmlConversionResult) {
        this.status = status;
        this.htmlConversionResult = htmlConversionResult;
    }

    public HtmlConversionResult getHtmlConversionResult() {
        return this.htmlConversionResult;
    }

    public SandboxConversionStatus getStatus() {
        return this.status;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SandboxHtmlConversionResponse that = (SandboxHtmlConversionResponse)o;
        boolean isEquals = true;
        if (this.htmlConversionResult.getHtml() != null) {
            isEquals = this.htmlConversionResult.getHtml().equals(that.htmlConversionResult.getHtml());
        }
        return this.status == that.status && isEquals;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.status, this.htmlConversionResult});
    }

    static Serializer serializer() {
        return Serializer.instance;
    }

    static class Serializer
    implements SandboxSerializer<SandboxHtmlConversionResponse> {
        static final Serializer instance = new Serializer();

        private Serializer() {
        }

        public byte[] serialize(SandboxHtmlConversionResponse conversionResponse) {
            ArrayList<byte[]> bytesList = new ArrayList<byte[]>();
            bytesList.add(SandboxSerializers.stringSerializer().serialize((Object)conversionResponse.getStatus().name()));
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                objectOutputStream.writeObject(conversionResponse.getHtmlConversionResult());
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            bytesList.add(byteArrayOutputStream.toByteArray());
            return SandboxSerializers.compositeByteArraySerializer().serialize(bytesList);
        }

        public SandboxHtmlConversionResponse deserialize(byte[] bytes) {
            HtmlConversionResult htmlConversionResult;
            List bytesList = (List)SandboxSerializers.compositeByteArraySerializer().deserialize(bytes);
            String conversionResponseStatusName = new String((byte[])bytesList.get(0), StandardCharsets.UTF_8);
            SandboxConversionStatus status = SandboxConversionStatus.valueOf(conversionResponseStatusName);
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream((byte[])bytesList.get(1)));
                htmlConversionResult = (HtmlConversionResult)objectInputStream.readObject();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
            return new SandboxHtmlConversionResponse(status, htmlConversionResult);
        }
    }
}

