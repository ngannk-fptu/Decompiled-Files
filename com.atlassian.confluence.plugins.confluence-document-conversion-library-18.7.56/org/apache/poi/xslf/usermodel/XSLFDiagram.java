/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xslf.usermodel;

import com.microsoft.schemas.office.drawing.x2008.diagram.CTGroupShape;
import com.microsoft.schemas.office.drawing.x2008.diagram.CTShape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.xslf.usermodel.XSLFDiagramDrawing;
import org.apache.poi.xslf.usermodel.XSLFGraphicFrame;
import org.apache.poi.xslf.usermodel.XSLFGroupShape;
import org.apache.poi.xslf.usermodel.XSLFSheet;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.diagram.CTRelIds;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObjectData;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraph;
import org.openxmlformats.schemas.presentationml.x2006.main.CTApplicationNonVisualDrawingProps;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGraphicalObjectFrame;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGroupShapeNonVisual;
import org.openxmlformats.schemas.presentationml.x2006.main.CTShapeNonVisual;

public class XSLFDiagram
extends XSLFGraphicFrame {
    public static final String DRAWINGML_DIAGRAM_URI = "http://schemas.openxmlformats.org/drawingml/2006/diagram";
    private final XSLFDiagramDrawing _drawing;
    private final XSLFDiagramGroupShape _groupShape;

    XSLFDiagram(CTGraphicalObjectFrame shape, XSLFSheet sheet) {
        super(shape, sheet);
        this._drawing = XSLFDiagram.readDiagramDrawing(shape, sheet);
        this._groupShape = this.initGroupShape();
    }

    private static boolean hasTextContent(CTShape msShapeCt) {
        if (msShapeCt.getTxBody() == null || msShapeCt.getTxXfrm() == null) {
            return false;
        }
        List<CTTextParagraph> paragraphs = msShapeCt.getTxBody().getPList();
        return paragraphs.stream().flatMap(p -> p.getRList().stream()).anyMatch(run -> run.getT() != null && !run.getT().trim().isEmpty());
    }

    private static XSLFDiagramDrawing readDiagramDrawing(CTGraphicalObjectFrame shape, XSLFSheet sheet) {
        CTGraphicalObjectData graphicData = shape.getGraphic().getGraphicData();
        XmlObject[] children = graphicData.selectChildren(new QName(DRAWINGML_DIAGRAM_URI, "relIds"));
        if (children.length == 0) {
            return null;
        }
        CTRelIds relIds = (CTRelIds)children[0];
        POIXMLDocumentPart dataModelPart = sheet.getRelationById(relIds.getDm());
        if (dataModelPart == null) {
            return null;
        }
        String dataPartName = dataModelPart.getPackagePart().getPartName().getName();
        String drawingPartName = dataPartName.replace("data", "drawing");
        for (POIXMLDocumentPart.RelationPart rp : sheet.getRelationParts()) {
            if (!drawingPartName.equals(((POIXMLDocumentPart)rp.getDocumentPart()).getPackagePart().getPartName().getName()) || !(rp.getDocumentPart() instanceof XSLFDiagramDrawing)) continue;
            return (XSLFDiagramDrawing)rp.getDocumentPart();
        }
        return null;
    }

    public XSLFDiagramDrawing getDiagramDrawing() {
        return this._drawing;
    }

    public XSLFDiagramGroupShape getGroupShape() {
        return this._groupShape;
    }

    private List<org.openxmlformats.schemas.presentationml.x2006.main.CTShape> convertShape(CTShape msShapeCt) {
        org.openxmlformats.schemas.presentationml.x2006.main.CTShape shapeCt = org.openxmlformats.schemas.presentationml.x2006.main.CTShape.Factory.newInstance();
        shapeCt.setStyle(msShapeCt.getStyle());
        shapeCt.setSpPr(msShapeCt.getSpPr());
        CTShapeNonVisual nonVisualCt = shapeCt.addNewNvSpPr();
        nonVisualCt.setCNvPr(msShapeCt.getNvSpPr().getCNvPr());
        nonVisualCt.setCNvSpPr(msShapeCt.getNvSpPr().getCNvSpPr());
        nonVisualCt.setNvPr(CTApplicationNonVisualDrawingProps.Factory.newInstance());
        shapeCt.setNvSpPr(nonVisualCt);
        ArrayList<org.openxmlformats.schemas.presentationml.x2006.main.CTShape> shapes = new ArrayList<org.openxmlformats.schemas.presentationml.x2006.main.CTShape>();
        shapes.add(shapeCt);
        if (XSLFDiagram.hasTextContent(msShapeCt)) {
            org.openxmlformats.schemas.presentationml.x2006.main.CTShape textShapeCT = this.convertText(msShapeCt, nonVisualCt);
            shapes.add(textShapeCT);
        }
        return shapes;
    }

    private org.openxmlformats.schemas.presentationml.x2006.main.CTShape convertText(CTShape msShapeCt, CTShapeNonVisual nonVisualCt) {
        org.openxmlformats.schemas.presentationml.x2006.main.CTShape textShapeCT = org.openxmlformats.schemas.presentationml.x2006.main.CTShape.Factory.newInstance();
        CTShapeProperties textShapeProps = textShapeCT.addNewSpPr();
        textShapeCT.setTxBody(msShapeCt.getTxBody());
        textShapeCT.setStyle(msShapeCt.getStyle());
        textShapeCT.setNvSpPr((CTShapeNonVisual)nonVisualCt.copy());
        textShapeCT.getNvSpPr().getCNvSpPr().setTxBox(true);
        textShapeProps.setXfrm(msShapeCt.getTxXfrm());
        int shapeRotation = msShapeCt.getSpPr().getXfrm().getRot();
        int textRotation = msShapeCt.getTxXfrm().getRot();
        if (textRotation != 0) {
            int resolvedRotation = shapeRotation + textRotation;
            textShapeProps.getXfrm().setRot(resolvedRotation);
        }
        return textShapeCT;
    }

    private XSLFDiagramGroupShape initGroupShape() {
        XSLFDiagramDrawing drawing = this.getDiagramDrawing();
        if (drawing == null || drawing.getDrawingDocument() == null) {
            return null;
        }
        CTGroupShape msGroupShapeCt = drawing.getDrawingDocument().getDrawing().getSpTree();
        if (msGroupShapeCt == null || msGroupShapeCt.getSpList().isEmpty()) {
            return null;
        }
        return this.convertMsGroupToGroupShape(msGroupShapeCt, drawing);
    }

    private XSLFDiagramGroupShape convertMsGroupToGroupShape(CTGroupShape msGroupShapeCt, XSLFDiagramDrawing drawing) {
        org.openxmlformats.schemas.presentationml.x2006.main.CTGroupShape groupShapeCt = org.openxmlformats.schemas.presentationml.x2006.main.CTGroupShape.Factory.newInstance();
        CTGroupShapeProperties groupShapePropsCt = groupShapeCt.addNewGrpSpPr();
        CTGroupShapeNonVisual groupShapeNonVisualCt = groupShapeCt.addNewNvGrpSpPr();
        groupShapeNonVisualCt.setCNvPr(msGroupShapeCt.getNvGrpSpPr().getCNvPr());
        groupShapeNonVisualCt.setCNvGrpSpPr(msGroupShapeCt.getNvGrpSpPr().getCNvGrpSpPr());
        groupShapeNonVisualCt.setNvPr(CTApplicationNonVisualDrawingProps.Factory.newInstance());
        for (CTShape msShapeCt : msGroupShapeCt.getSpList()) {
            List<org.openxmlformats.schemas.presentationml.x2006.main.CTShape> shapes = this.convertShape(msShapeCt);
            groupShapeCt.getSpList().addAll(shapes);
        }
        Rectangle2D anchor = super.getAnchor();
        Rectangle2D.Double interiorAnchor = new Rectangle2D.Double(0.0, 0.0, anchor.getWidth(), anchor.getHeight());
        XSLFDiagramGroupShape groupShape = new XSLFDiagramGroupShape(groupShapeCt, this.getSheet(), drawing);
        groupShape.setAnchor(anchor);
        groupShape.setInteriorAnchor(interiorAnchor);
        groupShape.setRotation(super.getRotation());
        return groupShape;
    }

    static class XSLFDiagramGroupShape
    extends XSLFGroupShape {
        private XSLFDiagramDrawing diagramDrawing;

        protected XSLFDiagramGroupShape(org.openxmlformats.schemas.presentationml.x2006.main.CTGroupShape shape, XSLFSheet sheet) {
            super(shape, sheet);
        }

        private XSLFDiagramGroupShape(org.openxmlformats.schemas.presentationml.x2006.main.CTGroupShape shape, XSLFSheet sheet, XSLFDiagramDrawing diagramDrawing) {
            super(shape, sheet);
            this.diagramDrawing = diagramDrawing;
        }

        POIXMLDocumentPart getRelationById(String id) {
            return this.diagramDrawing.getRelationById(id);
        }
    }
}

