/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.annotation;

import java.io.IOException;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.filespecification.PDFileSpecification;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationMarkup;

public class PDAnnotationFileAttachment
extends PDAnnotationMarkup {
    public static final String ATTACHMENT_NAME_PUSH_PIN = "PushPin";
    public static final String ATTACHMENT_NAME_GRAPH = "Graph";
    public static final String ATTACHMENT_NAME_PAPERCLIP = "Paperclip";
    public static final String ATTACHMENT_NAME_TAG = "Tag";
    public static final String SUB_TYPE = "FileAttachment";

    public PDAnnotationFileAttachment() {
        this.getCOSObject().setName(COSName.SUBTYPE, SUB_TYPE);
    }

    public PDAnnotationFileAttachment(COSDictionary field) {
        super(field);
    }

    public PDFileSpecification getFile() throws IOException {
        return PDFileSpecification.createFS(this.getCOSObject().getDictionaryObject("FS"));
    }

    public void setFile(PDFileSpecification file) {
        this.getCOSObject().setItem("FS", (COSObjectable)file);
    }

    public String getAttachmentName() {
        return this.getCOSObject().getNameAsString(COSName.NAME, ATTACHMENT_NAME_PUSH_PIN);
    }

    @Deprecated
    public void setAttachementName(String name) {
        this.getCOSObject().setName(COSName.NAME, name);
    }

    public void setAttachmentName(String name) {
        this.getCOSObject().setName(COSName.NAME, name);
    }
}

