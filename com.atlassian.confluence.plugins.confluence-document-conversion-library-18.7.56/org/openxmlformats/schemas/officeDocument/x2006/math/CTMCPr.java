/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.officeDocument.x2006.math;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTInteger255;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTXAlign;

public interface CTMCPr
extends XmlObject {
    public static final DocumentFactory<CTMCPr> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctmcpr6d9atype");
    public static final SchemaType type = Factory.getType();

    public CTInteger255 getCount();

    public boolean isSetCount();

    public void setCount(CTInteger255 var1);

    public CTInteger255 addNewCount();

    public void unsetCount();

    public CTXAlign getMcJc();

    public boolean isSetMcJc();

    public void setMcJc(CTXAlign var1);

    public CTXAlign addNewMcJc();

    public void unsetMcJc();
}

