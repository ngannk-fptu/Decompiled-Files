/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview;

import com.sun.pdfview.action.PDFAction;
import javax.swing.tree.DefaultMutableTreeNode;

public class OutlineNode
extends DefaultMutableTreeNode {
    private String title;

    public OutlineNode(String title) {
        this.title = title;
    }

    public PDFAction getAction() {
        return (PDFAction)this.getUserObject();
    }

    public void setAction(PDFAction action) {
        this.setUserObject(action);
    }

    @Override
    public String toString() {
        return this.title;
    }
}

