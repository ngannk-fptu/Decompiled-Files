/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STXstring;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STNumFmtId;

public interface CTNumFmt
extends XmlObject {
    public static final DocumentFactory<CTNumFmt> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctnumfmt3870type");
    public static final SchemaType type = Factory.getType();

    public long getNumFmtId();

    public STNumFmtId xgetNumFmtId();

    public void setNumFmtId(long var1);

    public void xsetNumFmtId(STNumFmtId var1);

    public String getFormatCode();

    public STXstring xgetFormatCode();

    public void setFormatCode(String var1);

    public void xsetFormatCode(STXstring var1);
}

