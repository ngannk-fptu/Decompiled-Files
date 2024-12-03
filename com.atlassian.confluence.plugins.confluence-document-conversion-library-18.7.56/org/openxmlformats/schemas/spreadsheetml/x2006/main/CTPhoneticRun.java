/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STXstring;

public interface CTPhoneticRun
extends XmlObject {
    public static final DocumentFactory<CTPhoneticRun> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctphoneticrun2b2atype");
    public static final SchemaType type = Factory.getType();

    public String getT();

    public STXstring xgetT();

    public void setT(String var1);

    public void xsetT(STXstring var1);

    public long getSb();

    public XmlUnsignedInt xgetSb();

    public void setSb(long var1);

    public void xsetSb(XmlUnsignedInt var1);

    public long getEb();

    public XmlUnsignedInt xgetEb();

    public void setEb(long var1);

    public void xsetEb(XmlUnsignedInt var1);
}

