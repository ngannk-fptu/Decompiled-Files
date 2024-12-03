/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview;

import com.sun.pdfview.PDFObject;
import java.io.IOException;

public class NameTree {
    private PDFObject root;

    public NameTree(PDFObject root) {
        this.root = root;
    }

    public PDFObject find(String key) throws IOException {
        return this.find(this.root, key);
    }

    private PDFObject find(PDFObject root, String key) throws IOException {
        PDFObject names = root.getDictRef("Names");
        if (names != null) {
            return this.findInArray(names.getArray(), key);
        }
        PDFObject kidsObj = root.getDictRef("Kids");
        if (kidsObj != null) {
            PDFObject[] kids = kidsObj.getArray();
            for (int i = 0; i < kids.length; ++i) {
                PDFObject limitsObj = kids[i].getDictRef("Limits");
                if (limitsObj == null) continue;
                String lowerLimit = limitsObj.getAt(0).getStringValue();
                String upperLimit = limitsObj.getAt(1).getStringValue();
                if (key.compareTo(lowerLimit) < 0 || key.compareTo(upperLimit) > 0) continue;
                return this.find(kids[i], key);
            }
        }
        return null;
    }

    private PDFObject findInArray(PDFObject[] array, String key) throws IOException {
        int start = 0;
        int end = array.length / 2;
        while (end >= start && start >= 0 && end < array.length) {
            int pos = start + (end - start) / 2;
            String posKey = array[pos * 2].getStringValue();
            int comp = key.compareTo(posKey);
            if (comp == 0) {
                return array[pos * 2 + 1];
            }
            if (comp > 0) {
                start = pos + 1;
                continue;
            }
            if (comp >= 0) continue;
            end = pos - 1;
        }
        return null;
    }
}

