/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.action;

import com.sun.pdfview.PDFDestination;
import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PDFParseException;
import java.io.IOException;

public class PdfObjectParseUtil {
    public static String parseStringFromDict(String key, PDFObject parent, boolean mandatory) throws IOException {
        PDFObject val = parent;
        while (val.getType() == 6) {
            if ((val = val.getDictRef(key)) != null) continue;
            if (mandatory) {
                throw new PDFParseException(key + "value could not be parsed : " + parent.toString());
            }
            return null;
        }
        return val.getStringValue();
    }

    public static boolean parseBooleanFromDict(String key, PDFObject parent, boolean mandatory) throws IOException {
        PDFObject val = parent.getDictRef(key);
        if (val == null) {
            if (mandatory) {
                throw new PDFParseException(key + "value could not be parsed : " + parent.toString());
            }
            return false;
        }
        return val.getBooleanValue();
    }

    public static int parseIntegerFromDict(String key, PDFObject parent, boolean mandatory) throws IOException {
        PDFObject val = parent.getDictRef(key);
        if (val == null) {
            if (mandatory) {
                throw new PDFParseException(key + "value could not be parsed : " + parent.toString());
            }
            return 0;
        }
        return val.getIntValue();
    }

    public static PDFDestination parseDestination(String key, PDFObject parent, PDFObject root, boolean mandatory) throws IOException {
        PDFObject destObj = parent.getDictRef(key);
        if (destObj == null) {
            if (mandatory) {
                throw new PDFParseException("Error parsing destination " + parent);
            }
            return null;
        }
        return PDFDestination.getDestination(destObj, root);
    }
}

