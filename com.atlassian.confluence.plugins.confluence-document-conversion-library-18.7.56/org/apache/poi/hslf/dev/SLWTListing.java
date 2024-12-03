/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.dev;

import java.io.IOException;
import org.apache.poi.hslf.record.Document;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.hslf.record.SlideListWithText;
import org.apache.poi.hslf.usermodel.HSLFSlideShowImpl;

public final class SLWTListing {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Need to give a filename");
            System.exit(1);
        }
        HSLFSlideShowImpl ss = new HSLFSlideShowImpl(args[0]);
        Record[] records = ss.getRecords();
        for (int i = 0; i < records.length; ++i) {
            if (!(records[i] instanceof Document)) continue;
            Document doc = (Document)records[i];
            SlideListWithText[] slwts = doc.getSlideListWithTexts();
            System.out.println("Document at " + i + " had " + slwts.length + " SlideListWithTexts");
            if (slwts.length == 0) {
                System.err.println("** Warning: Should have had at least 1! **");
            }
            if (slwts.length > 3) {
                System.err.println("** Warning: Shouldn't have more than 3!");
            }
            for (int j = 0; j < slwts.length; ++j) {
                SlideListWithText slwt = slwts[j];
                Record[] children = slwt.getChildRecords();
                System.out.println(" - SLWT at " + j + " had " + children.length + " children:");
                int numSAS = slwt.getSlideAtomsSets().length;
                if (j == 1) {
                    if (numSAS == 0) {
                        System.err.println("  ** 2nd SLWT didn't have any SlideAtomSets!");
                    } else {
                        System.out.println("  - Contains " + numSAS + " SlideAtomSets");
                    }
                } else if (numSAS > 0) {
                    System.err.println("  ** SLWT " + j + " had " + numSAS + " SlideAtomSets! (expected 0)");
                }
                int upTo = Math.min(children.length, 5);
                for (int k = 0; k < upTo; ++k) {
                    Record r = children[k];
                    int typeID = (int)r.getRecordType();
                    String typeName = RecordTypes.forTypeID(typeID).name();
                    System.out.println("   - " + typeID + " (" + typeName + ")");
                }
            }
        }
        ss.close();
    }
}

