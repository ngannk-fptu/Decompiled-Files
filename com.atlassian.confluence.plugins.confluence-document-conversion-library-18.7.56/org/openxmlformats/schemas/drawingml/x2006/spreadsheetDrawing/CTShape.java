/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeStyle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTShapeNonVisual;

public interface CTShape
extends XmlObject {
    public static final DocumentFactory<CTShape> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctshapee40btype");
    public static final SchemaType type = Factory.getType();

    public CTShapeNonVisual getNvSpPr();

    public void setNvSpPr(CTShapeNonVisual var1);

    public CTShapeNonVisual addNewNvSpPr();

    public CTShapeProperties getSpPr();

    public void setSpPr(CTShapeProperties var1);

    public CTShapeProperties addNewSpPr();

    public CTShapeStyle getStyle();

    public boolean isSetStyle();

    public void setStyle(CTShapeStyle var1);

    public CTShapeStyle addNewStyle();

    public void unsetStyle();

    public CTTextBody getTxBody();

    public boolean isSetTxBody();

    public void setTxBody(CTTextBody var1);

    public CTTextBody addNewTxBody();

    public void unsetTxBody();

    public String getMacro();

    public XmlString xgetMacro();

    public boolean isSetMacro();

    public void setMacro(String var1);

    public void xsetMacro(XmlString var1);

    public void unsetMacro();

    public String getTextlink();

    public XmlString xgetTextlink();

    public boolean isSetTextlink();

    public void setTextlink(String var1);

    public void xsetTextlink(XmlString var1);

    public void unsetTextlink();

    public boolean getFLocksText();

    public XmlBoolean xgetFLocksText();

    public boolean isSetFLocksText();

    public void setFLocksText(boolean var1);

    public void xsetFLocksText(XmlBoolean var1);

    public void unsetFLocksText();

    public boolean getFPublished();

    public XmlBoolean xgetFPublished();

    public boolean isSetFPublished();

    public void setFPublished(boolean var1);

    public void xsetFPublished(XmlBoolean var1);

    public void unsetFPublished();
}

