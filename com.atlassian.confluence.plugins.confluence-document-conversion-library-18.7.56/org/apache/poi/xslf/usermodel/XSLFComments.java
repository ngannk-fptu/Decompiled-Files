/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xslf.usermodel;

import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.presentationml.x2006.main.CTComment;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCommentList;
import org.openxmlformats.schemas.presentationml.x2006.main.CmLstDocument;

public class XSLFComments
extends POIXMLDocumentPart {
    private final CmLstDocument doc;

    XSLFComments() {
        this.doc = CmLstDocument.Factory.newInstance();
    }

    XSLFComments(PackagePart part) throws IOException, XmlException {
        super(part);
        try (InputStream stream = this.getPackagePart().getInputStream();){
            this.doc = (CmLstDocument)CmLstDocument.Factory.parse(stream, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        }
    }

    public CTCommentList getCTCommentsList() {
        return this.doc.getCmLst();
    }

    public int getNumberOfComments() {
        return this.doc.getCmLst().sizeOfCmArray();
    }

    public CTComment getCommentAt(int pos) {
        return this.doc.getCmLst().getCmArray(pos);
    }
}

