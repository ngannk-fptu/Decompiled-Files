/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xslf.usermodel;

import com.microsoft.schemas.office.drawing.x2008.diagram.DrawingDocument;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.xmlbeans.XmlException;

public class XSLFDiagramDrawing
extends POIXMLDocumentPart {
    private final DrawingDocument _drawingDoc;

    XSLFDiagramDrawing() {
        this._drawingDoc = DrawingDocument.Factory.newInstance();
    }

    XSLFDiagramDrawing(PackagePart part) throws XmlException, IOException {
        super(part);
        this._drawingDoc = XSLFDiagramDrawing.readPackagePart(part);
    }

    private static DrawingDocument readPackagePart(PackagePart part) throws IOException, XmlException {
        try (InputStream is = part.getInputStream();){
            DrawingDocument drawingDocument = (DrawingDocument)DrawingDocument.Factory.parse(is);
            return drawingDocument;
        }
    }

    public DrawingDocument getDrawingDocument() {
        return this._drawingDoc;
    }
}

