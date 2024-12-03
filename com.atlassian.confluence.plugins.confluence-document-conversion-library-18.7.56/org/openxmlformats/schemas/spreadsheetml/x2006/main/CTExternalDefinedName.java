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

public interface CTExternalDefinedName
extends XmlObject {
    public static final DocumentFactory<CTExternalDefinedName> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctexternaldefinedname9408type");
    public static final SchemaType type = Factory.getType();

    public String getName();

    public STXstring xgetName();

    public void setName(String var1);

    public void xsetName(STXstring var1);

    public String getRefersTo();

    public STXstring xgetRefersTo();

    public boolean isSetRefersTo();

    public void setRefersTo(String var1);

    public void xsetRefersTo(STXstring var1);

    public void unsetRefersTo();

    public long getSheetId();

    public XmlUnsignedInt xgetSheetId();

    public boolean isSetSheetId();

    public void setSheetId(long var1);

    public void xsetSheetId(XmlUnsignedInt var1);

    public void unsetSheetId();
}

