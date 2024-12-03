/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.dev;

import java.io.IOException;
import org.apache.poi.hslf.record.Notes;
import org.apache.poi.hslf.record.NotesAtom;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.Slide;
import org.apache.poi.hslf.record.SlideAtom;
import org.apache.poi.hslf.usermodel.HSLFSlideShowImpl;

public final class SlideAndNotesAtomListing {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Need to give a filename");
            System.exit(1);
        }
        HSLFSlideShowImpl ss = new HSLFSlideShowImpl(args[0]);
        System.out.println();
        Record[] records = ss.getRecords();
        for (int i = 0; i < records.length; ++i) {
            Record r = records[i];
            if (r instanceof Slide) {
                Slide s = (Slide)r;
                SlideAtom sa = s.getSlideAtom();
                System.out.println("Found Slide at " + i);
                System.out.println("  Slide's master ID is " + sa.getMasterID());
                System.out.println("  Slide's notes ID is  " + sa.getNotesID());
                System.out.println();
            }
            if (!(r instanceof Notes)) continue;
            Notes n = (Notes)r;
            NotesAtom na = n.getNotesAtom();
            System.out.println("Found Notes at " + i);
            System.out.println("  Notes ID is " + na.getSlideID());
            System.out.println();
        }
        ss.close();
    }
}

