/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.officeDocument.x2006.math;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTCtrlPr;

public interface CTSSubPr
extends XmlObject {
    public static final DocumentFactory<CTSSubPr> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctssubpr1ae3type");
    public static final SchemaType type = Factory.getType();

    public CTCtrlPr getCtrlPr();

    public boolean isSetCtrlPr();

    public void setCtrlPr(CTCtrlPr var1);

    public CTCtrlPr addNewCtrlPr();

    public void unsetCtrlPr();
}

