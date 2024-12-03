/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionListModify
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeStyle;
import org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionListModify;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPictureNonVisual;

public interface CTPicture
extends XmlObject {
    public static final DocumentFactory<CTPicture> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpicture4f11type");
    public static final SchemaType type = Factory.getType();

    public CTPictureNonVisual getNvPicPr();

    public void setNvPicPr(CTPictureNonVisual var1);

    public CTPictureNonVisual addNewNvPicPr();

    public CTBlipFillProperties getBlipFill();

    public void setBlipFill(CTBlipFillProperties var1);

    public CTBlipFillProperties addNewBlipFill();

    public CTShapeProperties getSpPr();

    public void setSpPr(CTShapeProperties var1);

    public CTShapeProperties addNewSpPr();

    public CTShapeStyle getStyle();

    public boolean isSetStyle();

    public void setStyle(CTShapeStyle var1);

    public CTShapeStyle addNewStyle();

    public void unsetStyle();

    public CTExtensionListModify getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionListModify var1);

    public CTExtensionListModify addNewExtLst();

    public void unsetExtLst();
}

