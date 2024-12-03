/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination;

import java.io.IOException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.common.PDDestinationOrAction;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDNamedDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageFitDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageFitHeightDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageFitRectangleDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageFitWidthDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination;

public abstract class PDDestination
implements PDDestinationOrAction {
    /*
     * WARNING - void declaration
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static PDDestination create(COSBase base) throws IOException {
        void var1_10;
        Object var1_1 = null;
        if (base == null) return var1_10;
        if (base instanceof COSArray && ((COSArray)base).size() > 1 && ((COSArray)base).getObject(1) instanceof COSName) {
            COSArray array = (COSArray)base;
            COSName type = (COSName)array.getObject(1);
            String typeString = type.getName();
            if (typeString.equals("Fit") || typeString.equals("FitB")) {
                PDPageFitDestination pDPageFitDestination = new PDPageFitDestination(array);
                return var1_10;
            } else if (typeString.equals("FitV") || typeString.equals("FitBV")) {
                PDPageFitHeightDestination pDPageFitHeightDestination = new PDPageFitHeightDestination(array);
                return var1_10;
            } else if (typeString.equals("FitR")) {
                PDPageFitRectangleDestination pDPageFitRectangleDestination = new PDPageFitRectangleDestination(array);
                return var1_10;
            } else if (typeString.equals("FitH") || typeString.equals("FitBH")) {
                PDPageFitWidthDestination pDPageFitWidthDestination = new PDPageFitWidthDestination(array);
                return var1_10;
            } else {
                if (!typeString.equals("XYZ")) throw new IOException("Unknown destination type: " + type.getName());
                PDPageXYZDestination pDPageXYZDestination = new PDPageXYZDestination(array);
            }
            return var1_10;
        } else if (base instanceof COSString) {
            PDNamedDestination pDNamedDestination = new PDNamedDestination((COSString)base);
            return var1_10;
        } else {
            if (!(base instanceof COSName)) throw new IOException("Error: can't convert to Destination " + base);
            PDNamedDestination pDNamedDestination = new PDNamedDestination((COSName)base);
        }
        return var1_10;
    }
}

