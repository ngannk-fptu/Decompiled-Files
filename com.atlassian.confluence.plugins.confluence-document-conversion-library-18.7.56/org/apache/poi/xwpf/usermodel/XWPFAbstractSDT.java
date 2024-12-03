/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xwpf.usermodel;

import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.BodyType;
import org.apache.poi.xwpf.usermodel.IBody;
import org.apache.poi.xwpf.usermodel.ISDTContent;
import org.apache.poi.xwpf.usermodel.ISDTContents;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtPr;

public abstract class XWPFAbstractSDT
implements ISDTContents {
    private final String title;
    private final String tag;
    private final IBody part;

    public XWPFAbstractSDT(CTSdtPr pr, IBody part) {
        this.title = pr != null && pr.isSetAlias() ? pr.getAlias().getVal() : "";
        this.tag = pr != null && pr.isSetTag() ? pr.getTag().getVal() : "";
        this.part = part;
    }

    public String getTitle() {
        return this.title;
    }

    public String getTag() {
        return this.tag;
    }

    public abstract ISDTContent getContent();

    public IBody getBody() {
        return null;
    }

    public POIXMLDocumentPart getPart() {
        return this.part.getPart();
    }

    public BodyType getPartType() {
        return BodyType.CONTENTCONTROL;
    }

    public BodyElementType getElementType() {
        return BodyElementType.CONTENTCONTROL;
    }

    public XWPFDocument getDocument() {
        return this.part.getXWPFDocument();
    }
}

