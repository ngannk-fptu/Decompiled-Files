/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.text;

import java.util.Comparator;
import org.apache.pdfbox.text.TextPosition;

public class TextPositionComparator
implements Comparator<TextPosition> {
    @Override
    public int compare(TextPosition pos1, TextPosition pos2) {
        int cmp1 = Float.compare(pos1.getDir(), pos2.getDir());
        if (cmp1 != 0) {
            return cmp1;
        }
        float x1 = pos1.getXDirAdj();
        float x2 = pos2.getXDirAdj();
        float pos1YBottom = pos1.getYDirAdj();
        float pos2YBottom = pos2.getYDirAdj();
        float pos1YTop = pos1YBottom - pos1.getHeightDir();
        float pos2YTop = pos2YBottom - pos2.getHeightDir();
        float yDifference = Math.abs(pos1YBottom - pos2YBottom);
        if ((double)yDifference < 0.1 || pos2YBottom >= pos1YTop && pos2YBottom <= pos1YBottom || pos1YBottom >= pos2YTop && pos1YBottom <= pos2YBottom) {
            return Float.compare(x1, x2);
        }
        if (pos1YBottom < pos2YBottom) {
            return -1;
        }
        return 1;
    }
}

