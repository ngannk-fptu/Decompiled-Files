/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.font.encoding;

import java.util.Map;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.pdmodel.font.encoding.Encoding;

public class BuiltInEncoding
extends Encoding {
    public BuiltInEncoding(Map<Integer, String> codeToName) {
        for (Map.Entry<Integer, String> entry : codeToName.entrySet()) {
            this.add(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public COSBase getCOSObject() {
        throw new UnsupportedOperationException("Built-in encodings cannot be serialized");
    }

    @Override
    public String getEncodingName() {
        return "built-in (TTF)";
    }
}

