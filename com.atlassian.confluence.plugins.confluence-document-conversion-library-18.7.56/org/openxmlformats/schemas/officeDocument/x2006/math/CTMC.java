/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.officeDocument.x2006.math;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTMCPr;

public interface CTMC
extends XmlObject {
    public static final DocumentFactory<CTMC> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctmc923ctype");
    public static final SchemaType type = Factory.getType();

    public CTMCPr getMcPr();

    public boolean isSetMcPr();

    public void setMcPr(CTMCPr var1);

    public CTMCPr addNewMcPr();

    public void unsetMcPr();
}

