/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.picture;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.picture.CTPictureNonVisual;

public interface CTPicture
extends XmlObject {
    public static final DocumentFactory<CTPicture> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpicture1d48type");
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
}

