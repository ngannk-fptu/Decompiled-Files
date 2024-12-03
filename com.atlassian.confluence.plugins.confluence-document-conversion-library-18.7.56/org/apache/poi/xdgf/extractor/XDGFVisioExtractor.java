/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xdgf.extractor;

import java.io.IOException;
import org.apache.poi.ooxml.extractor.POIXMLTextExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xdgf.usermodel.XDGFPage;
import org.apache.poi.xdgf.usermodel.XmlVisioDocument;
import org.apache.poi.xdgf.usermodel.shape.ShapeTextVisitor;

public class XDGFVisioExtractor
implements POIXMLTextExtractor {
    protected final XmlVisioDocument document;
    private boolean doCloseFilesystem = true;

    public XDGFVisioExtractor(XmlVisioDocument document) {
        this.document = document;
    }

    public XDGFVisioExtractor(OPCPackage openPackage) throws IOException {
        this(new XmlVisioDocument(openPackage));
    }

    @Override
    public String getText() {
        ShapeTextVisitor visitor = new ShapeTextVisitor();
        for (XDGFPage page : this.document.getPages()) {
            page.getContent().visitShapes(visitor);
        }
        return visitor.getText();
    }

    @Override
    public XmlVisioDocument getDocument() {
        return this.document;
    }

    @Override
    public void setCloseFilesystem(boolean doCloseFilesystem) {
        this.doCloseFilesystem = doCloseFilesystem;
    }

    @Override
    public boolean isCloseFilesystem() {
        return this.doCloseFilesystem;
    }

    @Override
    public XmlVisioDocument getFilesystem() {
        return this.document;
    }
}

