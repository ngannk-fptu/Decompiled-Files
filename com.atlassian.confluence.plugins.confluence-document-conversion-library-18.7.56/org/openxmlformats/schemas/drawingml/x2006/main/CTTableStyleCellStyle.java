/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTCell3D
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTTableCellBorderStyle
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTCell3D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTStyleMatrixReference;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableCellBorderStyle;

public interface CTTableStyleCellStyle
extends XmlObject {
    public static final DocumentFactory<CTTableStyleCellStyle> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttablestylecellstyle1fddtype");
    public static final SchemaType type = Factory.getType();

    public CTTableCellBorderStyle getTcBdr();

    public boolean isSetTcBdr();

    public void setTcBdr(CTTableCellBorderStyle var1);

    public CTTableCellBorderStyle addNewTcBdr();

    public void unsetTcBdr();

    public CTFillProperties getFill();

    public boolean isSetFill();

    public void setFill(CTFillProperties var1);

    public CTFillProperties addNewFill();

    public void unsetFill();

    public CTStyleMatrixReference getFillRef();

    public boolean isSetFillRef();

    public void setFillRef(CTStyleMatrixReference var1);

    public CTStyleMatrixReference addNewFillRef();

    public void unsetFillRef();

    public CTCell3D getCell3D();

    public boolean isSetCell3D();

    public void setCell3D(CTCell3D var1);

    public CTCell3D addNewCell3D();

    public void unsetCell3D();
}

