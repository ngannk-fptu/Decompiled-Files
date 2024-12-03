/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.decode;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PDFParseException;
import com.sun.pdfview.decode.ASCII85Decode;
import com.sun.pdfview.decode.ASCIIHexDecode;
import com.sun.pdfview.decode.CCITTFaxDecode;
import com.sun.pdfview.decode.DCTDecode;
import com.sun.pdfview.decode.FlateDecode;
import com.sun.pdfview.decode.LZWDecode;
import com.sun.pdfview.decode.RunLengthDecode;
import java.io.IOException;
import java.nio.ByteBuffer;

public class PDFDecoder {
    private PDFDecoder() {
    }

    public static ByteBuffer decodeStream(PDFObject dict, ByteBuffer streamBuf) throws IOException {
        boolean specificCryptFilter;
        PDFObject[] params;
        PDFObject[] ary;
        PDFObject filter = dict.getDictRef("Filter");
        if (filter == null) {
            return dict.getDecrypter().decryptBuffer(null, dict, streamBuf);
        }
        if (filter.getType() == 4) {
            ary = new PDFObject[]{filter};
            params = new PDFObject[]{dict.getDictRef("DecodeParms")};
        } else {
            ary = filter.getArray();
            PDFObject parmsobj = dict.getDictRef("DecodeParms");
            params = parmsobj != null ? parmsobj.getArray() : new PDFObject[ary.length];
        }
        boolean bl = specificCryptFilter = ary.length != 0 && ary[0].getStringValue().equals("Crypt");
        if (!specificCryptFilter) {
            streamBuf = dict.getDecrypter().decryptBuffer(null, dict, streamBuf);
        }
        for (int i = 0; i < ary.length; ++i) {
            String enctype = ary[i].getStringValue();
            if (enctype == null) continue;
            if (enctype.equals("FlateDecode") || enctype.equals("Fl")) {
                streamBuf = FlateDecode.decode(dict, streamBuf, params[i]);
                continue;
            }
            if (enctype.equals("LZWDecode") || enctype.equals("LZW")) {
                streamBuf = LZWDecode.decode(streamBuf, params[i]);
                continue;
            }
            if (enctype.equals("ASCII85Decode") || enctype.equals("A85")) {
                streamBuf = ASCII85Decode.decode(streamBuf, params[i]);
                continue;
            }
            if (enctype.equals("ASCIIHexDecode") || enctype.equals("AHx")) {
                streamBuf = ASCIIHexDecode.decode(streamBuf, params[i]);
                continue;
            }
            if (enctype.equals("RunLengthDecode") || enctype.equals("RL")) {
                streamBuf = RunLengthDecode.decode(streamBuf, params[i]);
                continue;
            }
            if (enctype.equals("DCTDecode") || enctype.equals("DCT")) {
                streamBuf = DCTDecode.decode(dict, streamBuf, params[i]);
                continue;
            }
            if (enctype.equals("CCITTFaxDecode") || enctype.equals("CCF")) {
                streamBuf = CCITTFaxDecode.decode(dict, streamBuf, params[i]);
                continue;
            }
            if (enctype.equals("Crypt")) {
                PDFObject nameObj;
                String cfName = "Identity";
                if (params[i] != null && (nameObj = params[i].getDictRef("Name")) != null && nameObj.getType() == 4) {
                    cfName = nameObj.getStringValue();
                }
                streamBuf = dict.getDecrypter().decryptBuffer(cfName, null, streamBuf);
                continue;
            }
            throw new PDFParseException("Unknown coding method:" + ary[i].getStringValue());
        }
        return streamBuf;
    }
}

