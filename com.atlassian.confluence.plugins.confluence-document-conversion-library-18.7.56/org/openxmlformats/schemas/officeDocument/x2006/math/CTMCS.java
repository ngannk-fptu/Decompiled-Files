/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.officeDocument.x2006.math;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTMC;

public interface CTMCS
extends XmlObject {
    public static final DocumentFactory<CTMCS> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctmcs4b1ftype");
    public static final SchemaType type = Factory.getType();

    public List<CTMC> getMcList();

    public CTMC[] getMcArray();

    public CTMC getMcArray(int var1);

    public int sizeOfMcArray();

    public void setMcArray(CTMC[] var1);

    public void setMcArray(int var1, CTMC var2);

    public CTMC insertNewMc(int var1);

    public CTMC addNewMc();

    public void removeMc(int var1);
}

