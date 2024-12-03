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
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SandboxPdfConversionResponse {
    private final File resultingPdf;
    private final int pdfPageCount;

    public SandboxPdfConversionResponse(File resultingPdf, int pageCount) {
        this.resultingPdf = resultingPdf;
        this.pdfPageCount = pageCount;
    }

    public File getResultingPdf() {
        return this.resultingPdf;
    }

    public int getPdfPageCount() {
        return this.pdfPageCount;
    }

    static Serializer serializer() {
        return Serializer.instance;
    }

    static final class Serializer
    implements SandboxSerializer<SandboxPdfConversionResponse> {
        static final Serializer instance = new Serializer();

        private Serializer() {
        }

        public byte[] serialize(SandboxPdfConversionResponse conversionResponse) {
            ArrayList<byte[]> fields = new ArrayList<byte[]>();
            String filePathOrNull = Optional.ofNullable(conversionResponse.getResultingPdf()).map(File::getAbsolutePath).orElse(null);
            fields.add(SandboxSerializers.stringSerializer().serialize((Object)filePathOrNull));
            fields.add(SandboxSerializers.intSerializer().serialize((Object)conversionResponse.getPdfPageCount()));
            return SandboxSerializers.compositeByteArraySerializer().serialize(fields);
        }

        public SandboxPdfConversionResponse deserialize(byte[] bytes) {
            List fields = (List)SandboxSerializers.compositeByteArraySerializer().deserialize(bytes);
            return new SandboxPdfConversionResponse(new File((String)SandboxSerializers.stringSerializer().deserialize((byte[])fields.get(0))), (Integer)SandboxSerializers.intSerializer().deserialize((byte[])fields.get(1)));
        }
    }
}

