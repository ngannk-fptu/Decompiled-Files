/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf;

import java.io.FileInputStream;
import java.io.IOException;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Section;

public final class QuickTest {
    public static void main(String[] args) throws IOException {
        HWPFDocument doc = new HWPFDocument(new FileInputStream(args[0]));
        Range r = doc.getRange();
        System.out.println("Example you supplied:");
        System.out.println("---------------------");
        for (int x = 0; x < r.numSections(); ++x) {
            Section s = r.getSection(x);
            for (int y = 0; y < s.numParagraphs(); ++y) {
                Paragraph p = s.getParagraph(y);
                for (int z = 0; z < p.numCharacterRuns(); ++z) {
                    CharacterRun run = p.getCharacterRun(z);
                    String text = run.text();
                    System.out.print(text);
                }
                System.out.println();
            }
        }
        doc.close();
    }
}

