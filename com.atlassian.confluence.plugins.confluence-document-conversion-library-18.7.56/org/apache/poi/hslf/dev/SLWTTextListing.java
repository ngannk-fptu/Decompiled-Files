/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.dev;

import java.io.IOException;
import org.apache.poi.hslf.record.Document;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.SlideListWithText;
import org.apache.poi.hslf.record.SlidePersistAtom;
import org.apache.poi.hslf.record.TextBytesAtom;
import org.apache.poi.hslf.record.TextCharsAtom;
import org.apache.poi.hslf.usermodel.HSLFSlideShowImpl;

public final class SLWTTextListing {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Need to give a filename");
            System.exit(1);
        }
        HSLFSlideShowImpl ss = new HSLFSlideShowImpl(args[0]);
        Record[] records = ss.getRecords();
        for (int i = 0; i < records.length; ++i) {
            if (!(records[i] instanceof Document)) continue;
            Record docRecord = records[i];
            Record[] docChildren = docRecord.getChildRecords();
            for (int j = 0; j < docChildren.length; ++j) {
                if (!(docChildren[j] instanceof SlideListWithText)) continue;
                System.out.println("Found SLWT at pos " + j + " in the Document at " + i);
                System.out.println("  Has " + docChildren[j].getChildRecords().length + " children");
                SlideListWithText slwt = (SlideListWithText)docChildren[j];
                SlideListWithText.SlideAtomsSet[] thisSets = slwt.getSlideAtomsSets();
                System.out.println("  Has " + thisSets.length + " AtomSets in it");
                for (int k = 0; k < thisSets.length; ++k) {
                    Record[] slwtc;
                    SlidePersistAtom spa = thisSets[k].getSlidePersistAtom();
                    System.out.println("    " + k + " has slide id " + spa.getSlideIdentifier());
                    System.out.println("    " + k + " has ref id " + spa.getRefID());
                    for (Record record : slwtc = thisSets[k].getSlideRecords()) {
                        String text = null;
                        if (record instanceof TextBytesAtom) {
                            TextBytesAtom tba = (TextBytesAtom)record;
                            text = tba.getText();
                        }
                        if (record instanceof TextCharsAtom) {
                            TextCharsAtom tca = (TextCharsAtom)record;
                            text = tca.getText();
                        }
                        if (text == null) continue;
                        text = text.replace('\r', '\n');
                        System.out.println("        ''" + text + "''");
                    }
                }
            }
        }
        ss.close();
    }
}

