/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.STCoordinate;

public interface CTTableCol
extends XmlObject {
    public static final DocumentFactory<CTTableCol> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttablecol19edtype");
    public static final SchemaType type = Factory.getType();

    public CTOfficeArtExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTOfficeArtExtensionList var1);

    public CTOfficeArtExtensionList addNewExtLst();

    public void unsetExtLst();

    public Object getW();

    public STCoordinate xgetW();

    public void setW(Object var1);

    public void xsetW(STCoordinate var1);
}

