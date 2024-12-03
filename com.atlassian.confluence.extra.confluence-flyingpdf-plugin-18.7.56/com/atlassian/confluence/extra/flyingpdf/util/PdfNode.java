/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.flyingpdf.util;

import com.atlassian.confluence.extra.flyingpdf.util.RenderedPdfFile;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PdfNode
implements Serializable {
    private final String pageTitle;
    private final RenderedPdfFile renderedPdfFile;
    private final List<PdfNode> children = new ArrayList<PdfNode>();

    public PdfNode(String pageTitle, RenderedPdfFile renderedPdfFile) {
        this.pageTitle = pageTitle;
        this.renderedPdfFile = renderedPdfFile;
    }

    public String getPageTitle() {
        return this.pageTitle;
    }

    public RenderedPdfFile getRenderedPdfFile() {
        return this.renderedPdfFile;
    }

    public List<PdfNode> getChildren() {
        return this.children;
    }

    public void addChild(PdfNode child) {
        this.children.add(child);
    }

    public String getFilename() {
        return this.renderedPdfFile.getFile().getAbsolutePath();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PdfNode pdfNode = (PdfNode)o;
        return Objects.equals(this.pageTitle, pdfNode.pageTitle) && Objects.equals(this.renderedPdfFile, pdfNode.renderedPdfFile) && Objects.equals(this.children, pdfNode.children);
    }

    public int hashCode() {
        return Objects.hash(this.pageTitle, this.renderedPdfFile, this.children);
    }
}

