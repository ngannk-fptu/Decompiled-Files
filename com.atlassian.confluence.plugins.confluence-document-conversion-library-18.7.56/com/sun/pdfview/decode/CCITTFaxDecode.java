/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.decode;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.decode.CCITTFaxDecoder;
import java.io.IOException;
import java.nio.ByteBuffer;

public class CCITTFaxDecode {
    protected static ByteBuffer decode(PDFObject dict, ByteBuffer buf, PDFObject params) throws IOException {
        byte[] bytes = new byte[buf.remaining()];
        buf.get(bytes, 0, bytes.length);
        return ByteBuffer.wrap(CCITTFaxDecode.decode(dict, bytes));
    }

    protected static byte[] decode(PDFObject dict, byte[] source) throws IOException {
        int width = 1728;
        PDFObject widthDef = dict.getDictRef("Width");
        if (widthDef == null) {
            widthDef = dict.getDictRef("W");
        }
        if (widthDef != null) {
            width = widthDef.getIntValue();
        }
        int height = 0;
        PDFObject heightDef = dict.getDictRef("Height");
        if (heightDef == null) {
            heightDef = dict.getDictRef("H");
        }
        if (heightDef != null) {
            height = heightDef.getIntValue();
        }
        int columns = CCITTFaxDecode.getOptionFieldInt(dict, "Columns", width);
        int rows = CCITTFaxDecode.getOptionFieldInt(dict, "Rows", height);
        int k = CCITTFaxDecode.getOptionFieldInt(dict, "K", 0);
        int size = rows * (columns + 7 >> 3);
        byte[] destination = new byte[size];
        boolean align = CCITTFaxDecode.getOptionFieldBoolean(dict, "EncodedByteAlign", false);
        CCITTFaxDecoder decoder = new CCITTFaxDecoder(1, columns, rows);
        decoder.setAlign(align);
        if (k == 0) {
            decoder.decodeT41D(destination, source, 0, rows);
        } else if (k > 0) {
            decoder.decodeT42D(destination, source, 0, rows);
        } else if (k < 0) {
            decoder.decodeT6(destination, source, 0, rows);
        }
        if (!CCITTFaxDecode.getOptionFieldBoolean(dict, "BlackIs1", false)) {
            for (int i = 0; i < destination.length; ++i) {
                destination[i] = ~destination[i];
            }
        }
        return destination;
    }

    public static int getOptionFieldInt(PDFObject dict, String name, int defaultValue) throws IOException {
        PDFObject dictParams = dict.getDictRef("DecodeParms");
        if (dictParams == null) {
            return defaultValue;
        }
        PDFObject value = dictParams.getDictRef(name);
        if (value == null) {
            return defaultValue;
        }
        return value.getIntValue();
    }

    public static boolean getOptionFieldBoolean(PDFObject dict, String name, boolean defaultValue) throws IOException {
        PDFObject dictParams = dict.getDictRef("DecodeParms");
        if (dictParams == null) {
            return defaultValue;
        }
        PDFObject value = dictParams.getDictRef(name);
        if (value == null) {
            return defaultValue;
        }
        return value.getBooleanValue();
    }
}

