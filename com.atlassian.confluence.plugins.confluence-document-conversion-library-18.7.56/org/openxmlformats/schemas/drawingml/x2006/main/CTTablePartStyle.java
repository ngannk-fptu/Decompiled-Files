/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableStyleCellStyle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableStyleTextStyle;

public interface CTTablePartStyle
extends XmlObject {
    public static final DocumentFactory<CTTablePartStyle> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttablepartstylef22btype");
    public static final SchemaType type = Factory.getType();

    public CTTableStyleTextStyle getTcTxStyle();

    public boolean isSetTcTxStyle();

    public void setTcTxStyle(CTTableStyleTextStyle var1);

    public CTTableStyleTextStyle addNewTcTxStyle();

    public void unsetTcTxStyle();

    public CTTableStyleCellStyle getTcStyle();

    public boolean isSetTcStyle();

    public void setTcStyle(CTTableStyleCellStyle var1);

    public CTTableStyleCellStyle addNewTcStyle();

    public void unsetTcStyle();
}

