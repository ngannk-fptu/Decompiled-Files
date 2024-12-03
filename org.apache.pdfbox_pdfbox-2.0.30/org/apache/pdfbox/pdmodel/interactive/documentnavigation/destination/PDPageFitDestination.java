/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;

public class PDPageFitDestination
extends PDPageDestination {
    protected static final String TYPE = "Fit";
    protected static final String TYPE_BOUNDED = "FitB";

    public PDPageFitDestination() {
        this.array.growToSize(2);
        this.array.setName(1, TYPE);
    }

    public PDPageFitDestination(COSArray arr) {
        super(arr);
    }

    public boolean fitBoundingBox() {
        return TYPE_BOUNDED.equals(this.array.getName(1));
    }

    public void setFitBoundingBox(boolean fitBoundingBox) {
        this.array.growToSize(2);
        if (fitBoundingBox) {
            this.array.setName(1, TYPE_BOUNDED);
        } else {
            this.array.setName(1, TYPE);
        }
    }
}

