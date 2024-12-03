/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel;

import java.io.IOException;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.common.PDNameTreeNode;
import org.apache.pdfbox.pdmodel.common.filespecification.PDComplexFileSpecification;

public class PDEmbeddedFilesNameTreeNode
extends PDNameTreeNode<PDComplexFileSpecification> {
    public PDEmbeddedFilesNameTreeNode() {
    }

    public PDEmbeddedFilesNameTreeNode(COSDictionary dic) {
        super(dic);
    }

    @Override
    protected PDComplexFileSpecification convertCOSToPD(COSBase base) throws IOException {
        return new PDComplexFileSpecification((COSDictionary)base);
    }

    @Override
    protected PDNameTreeNode<PDComplexFileSpecification> createChildNode(COSDictionary dic) {
        return new PDEmbeddedFilesNameTreeNode(dic);
    }
}

