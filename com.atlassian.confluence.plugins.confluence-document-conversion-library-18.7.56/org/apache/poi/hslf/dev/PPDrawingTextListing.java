/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.dev;

import java.io.IOException;
import org.apache.poi.hslf.record.EscherTextboxWrapper;
import org.apache.poi.hslf.record.PPDrawing;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.TextBytesAtom;
import org.apache.poi.hslf.record.TextCharsAtom;
import org.apache.poi.hslf.usermodel.HSLFSlideShowImpl;

public final class PPDrawingTextListing {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Need to give a filename");
            System.exit(1);
        }
        try (HSLFSlideShowImpl ss = new HSLFSlideShowImpl(args[0]);){
            Record[] records = ss.getRecords();
            for (int i = 0; i < records.length; ++i) {
                Record[] children = records[i].getChildRecords();
                if (children == null || children.length == 0) continue;
                for (int j = 0; j < children.length; ++j) {
                    if (!(children[j] instanceof PPDrawing)) continue;
                    System.out.println("Found PPDrawing at " + j + " in top level record " + i + " (" + records[i].getRecordType() + ")");
                    PPDrawing ppd = (PPDrawing)children[j];
                    EscherTextboxWrapper[] wrappers = ppd.getTextboxWrappers();
                    System.out.println("  Has " + wrappers.length + " textbox wrappers within");
                    for (int k = 0; k < wrappers.length; ++k) {
                        Record[] pptatoms;
                        EscherTextboxWrapper tbw = wrappers[k];
                        System.out.println("    " + k + " has " + tbw.getChildRecords().length + " PPT atoms within");
                        for (Record pptatom : pptatoms = tbw.getChildRecords()) {
                            String text = null;
                            if (pptatom instanceof TextBytesAtom) {
                                TextBytesAtom tba = (TextBytesAtom)pptatom;
                                text = tba.getText();
                            }
                            if (pptatom instanceof TextCharsAtom) {
                                TextCharsAtom tca = (TextCharsAtom)pptatom;
                                text = tca.getText();
                            }
                            if (text == null) continue;
                            text = text.replace('\r', '\n');
                            System.out.println("        ''" + text + "''");
                        }
                    }
                }
            }
        }
    }
}

