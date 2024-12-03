/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xssf.usermodel;

import com.microsoft.schemas.office.excel.CTClientData;
import com.microsoft.schemas.office.excel.STObjectType;
import com.microsoft.schemas.office.office.CTIdMap;
import com.microsoft.schemas.office.office.CTShapeLayout;
import com.microsoft.schemas.office.office.STConnectType;
import com.microsoft.schemas.office.office.STInsetMode;
import com.microsoft.schemas.office.office.ShapelayoutDocument;
import com.microsoft.schemas.vml.CTGroup;
import com.microsoft.schemas.vml.CTPath;
import com.microsoft.schemas.vml.CTShadow;
import com.microsoft.schemas.vml.CTShape;
import com.microsoft.schemas.vml.CTShapetype;
import com.microsoft.schemas.vml.STExt;
import com.microsoft.schemas.vml.STStrokeJoinStyle;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.namespace.QName;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.schemas.vmldrawing.XmlDocument;
import org.apache.poi.util.Internal;
import org.apache.poi.util.ReplacingInputStream;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STTrueFalse;

public final class XSSFVMLDrawing
extends POIXMLDocumentPart {
    private static final String COMMENT_SHAPE_TYPE_ID = "_x0000_t202";
    public static final QName QNAME_VMLDRAWING = new QName("urn:schemas-poi-apache-org:vmldrawing", "xml");
    private static final Pattern ptrn_shapeId = Pattern.compile("_x0000_s(\\d+)");
    private XmlDocument root;
    private String _shapeTypeId;
    private int _shapeId = 1024;

    protected XSSFVMLDrawing() {
        this.newDrawing();
    }

    protected XSSFVMLDrawing(PackagePart part) throws IOException, XmlException {
        super(part);
        try (InputStream stream = this.getPackagePart().getInputStream();){
            this.read(stream);
        }
    }

    public XmlDocument getDocument() {
        return this.root;
    }

    protected void read(InputStream is) throws IOException, XmlException {
        XmlOptions xopt = new XmlOptions(POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        xopt.setLoadSubstituteNamespaces(Collections.singletonMap("", QNAME_VMLDRAWING.getNamespaceURI()));
        xopt.setDocumentType(XmlDocument.type);
        this.root = (XmlDocument)XmlDocument.Factory.parse(new ReplacingInputStream((InputStream)new ReplacingInputStream(is, "<br>", "<br/>"), " xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\"", ""), xopt);
        try (XmlCursor cur = this.root.getXml().newCursor();){
            boolean found = cur.toFirstChild();
            while (found) {
                Matcher m;
                CTShape shape;
                String id;
                XmlObject xo = cur.getObject();
                if (xo instanceof CTShapetype) {
                    this._shapeTypeId = ((CTShapetype)xo).getId();
                } else if (xo instanceof CTShape && (id = (shape = (CTShape)xo).getId()) != null && (m = ptrn_shapeId.matcher(id)).find()) {
                    this._shapeId = Math.max(this._shapeId, Integer.parseInt(m.group(1)));
                }
                found = cur.toNextSibling();
            }
        }
    }

    protected List<XmlObject> getItems() {
        ArrayList<XmlObject> items = new ArrayList<XmlObject>();
        try (XmlCursor cur = this.root.getXml().newCursor();){
            boolean found = cur.toFirstChild();
            while (found) {
                items.add(cur.getObject());
                found = cur.toNextSibling();
            }
        }
        return items;
    }

    protected void write(OutputStream out) throws IOException {
        XmlOptions xopt = new XmlOptions(POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        xopt.setSaveImplicitNamespaces(Collections.singletonMap("", QNAME_VMLDRAWING.getNamespaceURI()));
        this.root.save(out, xopt);
    }

    @Override
    protected void commit() throws IOException {
        PackagePart part = this.getPackagePart();
        try (OutputStream out = part.getOutputStream();){
            this.write(out);
        }
    }

    private void newDrawing() {
        this.root = XmlDocument.Factory.newInstance();
        try (XmlCursor xml = this.root.addNewXml().newCursor();){
            ShapelayoutDocument layDoc = ShapelayoutDocument.Factory.newInstance();
            CTShapeLayout layout = layDoc.addNewShapelayout();
            layout.setExt(STExt.EDIT);
            CTIdMap idmap = layout.addNewIdmap();
            idmap.setExt(STExt.EDIT);
            idmap.setData("1");
            xml.toEndToken();
            try (XmlCursor layCur = layDoc.newCursor();){
                layCur.copyXmlContents(xml);
            }
            CTGroup grp = CTGroup.Factory.newInstance();
            CTShapetype shapetype = grp.addNewShapetype();
            this._shapeTypeId = COMMENT_SHAPE_TYPE_ID;
            shapetype.setId(this._shapeTypeId);
            shapetype.setCoordsize("21600,21600");
            shapetype.setSpt(202.0f);
            shapetype.setPath2("m,l,21600r21600,l21600,xe");
            shapetype.addNewStroke().setJoinstyle(STStrokeJoinStyle.MITER);
            CTPath path = shapetype.addNewPath();
            path.setGradientshapeok(STTrueFalse.T);
            path.setConnecttype(STConnectType.RECT);
            xml.toEndToken();
            try (XmlCursor grpCur = grp.newCursor();){
                grpCur.copyXmlContents(xml);
            }
        }
    }

    @Internal
    public CTShape newCommentShape() {
        CTGroup grp = CTGroup.Factory.newInstance();
        CTShape shape = grp.addNewShape();
        shape.setId("_x0000_s" + ++this._shapeId);
        shape.setType("#" + this._shapeTypeId);
        shape.setStyle("position:absolute; visibility:hidden");
        shape.setFillcolor("#ffffe1");
        shape.setInsetmode(STInsetMode.AUTO);
        shape.addNewFill().setColor("#ffffe1");
        CTShadow shadow = shape.addNewShadow();
        shadow.setOn(STTrueFalse.T);
        shadow.setColor("black");
        shadow.setObscured(STTrueFalse.T);
        shape.addNewPath().setConnecttype(STConnectType.NONE);
        shape.addNewTextbox().setStyle("mso-direction-alt:auto");
        CTClientData cldata = shape.addNewClientData();
        cldata.setObjectType(STObjectType.NOTE);
        cldata.addNewMoveWithCells();
        cldata.addNewSizeWithCells();
        cldata.addNewAnchor().setStringValue("1, 15, 0, 2, 3, 15, 3, 16");
        cldata.addNewAutoFill().setStringValue("False");
        cldata.addNewRow().setBigIntegerValue(BigInteger.valueOf(0L));
        cldata.addNewColumn().setBigIntegerValue(BigInteger.valueOf(0L));
        try (XmlCursor xml = this.root.getXml().newCursor();){
            xml.toEndToken();
            try (XmlCursor grpCur = grp.newCursor();){
                grpCur.copyXmlContents(xml);
                xml.toPrevSibling();
                shape = (CTShape)xml.getObject();
            }
        }
        return shape;
    }

    public CTShape findCommentShape(int row, int col) {
        try (XmlCursor cur = this.root.getXml().newCursor();){
            boolean found = cur.toFirstChild();
            while (found) {
                XmlObject itm = cur.getObject();
                if (this.matchCommentShape(itm, row, col)) {
                    CTShape cTShape = (CTShape)itm;
                    return cTShape;
                }
                found = cur.toNextSibling();
            }
        }
        return null;
    }

    private boolean matchCommentShape(XmlObject itm, int row, int col) {
        if (!(itm instanceof CTShape)) {
            return false;
        }
        CTShape sh = (CTShape)itm;
        if (sh.sizeOfClientDataArray() == 0) {
            return false;
        }
        CTClientData cldata = sh.getClientDataArray(0);
        if (cldata.getObjectType() != STObjectType.NOTE) {
            return false;
        }
        int crow = cldata.getRowArray(0).intValue();
        int ccol = cldata.getColumnArray(0).intValue();
        return crow == row && ccol == col;
    }

    protected boolean removeCommentShape(int row, int col) {
        try (XmlCursor cur = this.root.getXml().newCursor();){
            boolean found = cur.toFirstChild();
            while (found) {
                XmlObject itm = cur.getObject();
                if (this.matchCommentShape(itm, row, col)) {
                    cur.removeXml();
                    boolean bl = true;
                    return bl;
                }
                found = cur.toNextSibling();
            }
        }
        return false;
    }
}

