/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STXstring;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRPrElt;

public interface CTRElt
extends XmlObject {
    public static final DocumentFactory<CTRElt> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctrelt6464type");
    public static final SchemaType type = Factory.getType();

    public CTRPrElt getRPr();

    public boolean isSetRPr();

    public void setRPr(CTRPrElt var1);

    public CTRPrElt addNewRPr();

    public void unsetRPr();

    public String getT();

    public STXstring xgetT();

    public void setT(String var1);

    public void xsetT(STXstring var1);
}

