/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableStyleList;

public interface TblStyleLstDocument
extends XmlObject {
    public static final DocumentFactory<TblStyleLstDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "tblstylelst4997doctype");
    public static final SchemaType type = Factory.getType();

    public CTTableStyleList getTblStyleLst();

    public void setTblStyleLst(CTTableStyleList var1);

    public CTTableStyleList addNewTblStyleLst();
}

