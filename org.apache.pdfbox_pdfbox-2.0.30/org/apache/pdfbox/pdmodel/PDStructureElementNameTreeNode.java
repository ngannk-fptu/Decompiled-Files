/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel;

import java.io.IOException;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.common.PDNameTreeNode;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureElement;

public class PDStructureElementNameTreeNode
extends PDNameTreeNode<PDStructureElement> {
    public PDStructureElementNameTreeNode() {
    }

    public PDStructureElementNameTreeNode(COSDictionary dic) {
        super(dic);
    }

    @Override
    protected PDStructureElement convertCOSToPD(COSBase base) throws IOException {
        return new PDStructureElement((COSDictionary)base);
    }

    @Override
    protected PDNameTreeNode<PDStructureElement> createChildNode(COSDictionary dic) {
        return new PDStructureElementNameTreeNode(dic);
    }
}

