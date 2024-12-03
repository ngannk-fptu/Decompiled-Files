/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel;

import java.io.IOException;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.PDNameTreeNode;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;

public class PDDestinationNameTreeNode
extends PDNameTreeNode<PDPageDestination> {
    public PDDestinationNameTreeNode() {
    }

    public PDDestinationNameTreeNode(COSDictionary dic) {
        super(dic);
    }

    @Override
    protected PDPageDestination convertCOSToPD(COSBase base) throws IOException {
        COSBase destination = base;
        if (base instanceof COSDictionary) {
            destination = ((COSDictionary)base).getDictionaryObject(COSName.D);
        }
        return (PDPageDestination)PDDestination.create(destination);
    }

    @Override
    protected PDNameTreeNode<PDPageDestination> createChildNode(COSDictionary dic) {
        return new PDDestinationNameTreeNode(dic);
    }
}

