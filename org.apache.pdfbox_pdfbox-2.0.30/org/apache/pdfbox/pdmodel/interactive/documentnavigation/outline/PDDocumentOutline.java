/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineNode;

public final class PDDocumentOutline
extends PDOutlineNode {
    public PDDocumentOutline() {
        this.getCOSObject().setName(COSName.TYPE, COSName.OUTLINES.getName());
    }

    public PDDocumentOutline(COSDictionary dic) {
        super(dic);
        this.getCOSObject().setName(COSName.TYPE, COSName.OUTLINES.getName());
    }

    @Override
    public boolean isNodeOpen() {
        return true;
    }

    @Override
    public void openNode() {
    }

    @Override
    public void closeNode() {
    }
}

