/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionListModify
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeStyle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionListModify;
import org.openxmlformats.schemas.presentationml.x2006.main.CTShapeNonVisual;

public interface CTShape
extends XmlObject {
    public static final DocumentFactory<CTShape> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctshapecfcetype");
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

    public CTExtensionListModify getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionListModify var1);

    public CTExtensionListModify addNewExtLst();

    public void unsetExtLst();

    public boolean getUseBgFill();

    public XmlBoolean xgetUseBgFill();

    public boolean isSetUseBgFill();

    public void setUseBgFill(boolean var1);

    public void xsetUseBgFill(XmlBoolean var1);

    public void unsetUseBgFill();
}

