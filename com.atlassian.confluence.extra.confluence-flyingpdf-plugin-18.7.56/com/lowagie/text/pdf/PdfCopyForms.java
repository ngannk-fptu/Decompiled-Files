/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocWriter;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopyFormsImp;
import com.lowagie.text.pdf.PdfEncodings;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.SequenceList;
import com.lowagie.text.pdf.interfaces.PdfEncryptionSettings;
import com.lowagie.text.pdf.interfaces.PdfViewerPreferences;
import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.Certificate;
import java.util.List;

public class PdfCopyForms
implements PdfViewerPreferences,
PdfEncryptionSettings {
    private PdfCopyFormsImp fc;

    public PdfCopyForms(OutputStream os) throws DocumentException {
        this.fc = new PdfCopyFormsImp(os);
    }

    public void addDocument(PdfReader reader) throws DocumentException, IOException {
        this.fc.addDocument(reader);
    }

    public void addDocument(PdfReader reader, List<Integer> pagesToKeep) throws DocumentException, IOException {
        this.fc.addDocument(reader, pagesToKeep);
    }

    public void addDocument(PdfReader reader, String ranges) throws DocumentException, IOException {
        this.fc.addDocument(reader, SequenceList.expand(ranges, reader.getNumberOfPages()));
    }

    public void copyDocumentFields(PdfReader reader) throws DocumentException {
        this.fc.copyDocumentFields(reader);
    }

    public void setEncryption(byte[] userPassword, byte[] ownerPassword, int permissions, boolean strength128Bits) throws DocumentException {
        this.fc.setEncryption(userPassword, ownerPassword, permissions, strength128Bits ? 1 : 0);
    }

    public void setEncryption(boolean strength, String userPassword, String ownerPassword, int permissions) throws DocumentException {
        this.setEncryption(DocWriter.getISOBytes(userPassword), DocWriter.getISOBytes(ownerPassword), permissions, strength);
    }

    public void close() {
        this.fc.close();
    }

    public void open() {
        this.fc.openDoc();
    }

    public void addJavaScript(String js) {
        this.fc.addJavaScript(js, !PdfEncodings.isPdfDocEncoding(js));
    }

    public void setOutlines(List outlines) {
        this.fc.setOutlines(outlines);
    }

    public PdfWriter getWriter() {
        return this.fc;
    }

    public boolean isFullCompression() {
        return this.fc.isFullCompression();
    }

    public void setFullCompression() {
        this.fc.setFullCompression();
    }

    @Override
    public void setEncryption(byte[] userPassword, byte[] ownerPassword, int permissions, int encryptionType) throws DocumentException {
        this.fc.setEncryption(userPassword, ownerPassword, permissions, encryptionType);
    }

    @Override
    public void addViewerPreference(PdfName key, PdfObject value) {
        this.fc.addViewerPreference(key, value);
    }

    @Override
    public void setViewerPreferences(int preferences) {
        this.fc.setViewerPreferences(preferences);
    }

    @Override
    public void setEncryption(Certificate[] certs, int[] permissions, int encryptionType) throws DocumentException {
        this.fc.setEncryption(certs, permissions, encryptionType);
    }
}

