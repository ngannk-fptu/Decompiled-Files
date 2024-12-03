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

public class SandboxPdfJoinResponse {
    private final File pdf;
    private final int numberOfPages;

    SandboxPdfJoinResponse(File pdf, int pdfPagesNum) {
        this.pdf = pdf;
        this.numberOfPages = pdfPagesNum;
    }

    public File getPdf() {
        return this.pdf;
    }

    public int getNumberOfPages() {
        return this.numberOfPages;
    }

    static Serializer serializer() {
        return Serializer.instance;
    }

    static final class Serializer
    implements SandboxSerializer<SandboxPdfJoinResponse> {
        static final Serializer instance = new Serializer();

        private Serializer() {
        }

        public byte[] serialize(SandboxPdfJoinResponse conversionResponse) {
            ArrayList<byte[]> fields = new ArrayList<byte[]>();
            fields.add(SandboxSerializers.stringSerializer().serialize((Object)conversionResponse.getPdf().getAbsolutePath()));
            fields.add(SandboxSerializers.intSerializer().serialize((Object)conversionResponse.getNumberOfPages()));
            return SandboxSerializers.compositeByteArraySerializer().serialize(fields);
        }

        public SandboxPdfJoinResponse deserialize(byte[] bytes) {
            List fields = (List)SandboxSerializers.compositeByteArraySerializer().deserialize(bytes);
            return new SandboxPdfJoinResponse(new File((String)SandboxSerializers.stringSerializer().deserialize((byte[])fields.get(0))), (Integer)SandboxSerializers.intSerializer().deserialize((byte[])fields.get(1)));
        }
    }
}

