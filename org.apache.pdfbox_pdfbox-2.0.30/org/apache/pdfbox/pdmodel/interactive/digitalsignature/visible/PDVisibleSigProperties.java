/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible;

import java.io.IOException;
import java.io.InputStream;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible.PDFTemplateCreator;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible.PDVisibleSigBuilder;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible.PDVisibleSignDesigner;

public class PDVisibleSigProperties {
    private String signerName;
    private String signerLocation;
    private String signatureReason;
    private boolean visualSignEnabled;
    private int page;
    private int preferredSize;
    private InputStream visibleSignature;
    private PDVisibleSignDesigner pdVisibleSignature;

    public void buildSignature() throws IOException {
        PDVisibleSigBuilder builder = new PDVisibleSigBuilder();
        PDFTemplateCreator creator = new PDFTemplateCreator(builder);
        this.setVisibleSignature(creator.buildPDF(this.getPdVisibleSignature()));
    }

    public String getSignerName() {
        return this.signerName;
    }

    public PDVisibleSigProperties signerName(String signerName) {
        this.signerName = signerName;
        return this;
    }

    public String getSignerLocation() {
        return this.signerLocation;
    }

    public PDVisibleSigProperties signerLocation(String signerLocation) {
        this.signerLocation = signerLocation;
        return this;
    }

    public String getSignatureReason() {
        return this.signatureReason;
    }

    public PDVisibleSigProperties signatureReason(String signatureReason) {
        this.signatureReason = signatureReason;
        return this;
    }

    public int getPage() {
        return this.page;
    }

    public PDVisibleSigProperties page(int page) {
        this.page = page;
        return this;
    }

    public int getPreferredSize() {
        return this.preferredSize;
    }

    public PDVisibleSigProperties preferredSize(int preferredSize) {
        this.preferredSize = preferredSize;
        return this;
    }

    public boolean isVisualSignEnabled() {
        return this.visualSignEnabled;
    }

    public PDVisibleSigProperties visualSignEnabled(boolean visualSignEnabled) {
        this.visualSignEnabled = visualSignEnabled;
        return this;
    }

    public PDVisibleSignDesigner getPdVisibleSignature() {
        return this.pdVisibleSignature;
    }

    public PDVisibleSigProperties setPdVisibleSignature(PDVisibleSignDesigner pdVisibleSignature) {
        this.pdVisibleSignature = pdVisibleSignature;
        return this;
    }

    public InputStream getVisibleSignature() {
        return this.visibleSignature;
    }

    public void setVisibleSignature(InputStream visibleSignature) {
        this.visibleSignature = visibleSignature;
    }
}

