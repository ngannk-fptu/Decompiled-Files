/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STFormula;

public interface CTTableFormula
extends STFormula {
    public static final DocumentFactory<CTTableFormula> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttableformulaf801type");
    public static final SchemaType type = Factory.getType();

    public boolean getArray();

    public XmlBoolean xgetArray();

    public boolean isSetArray();

    public void setArray(boolean var1);

    public void xsetArray(XmlBoolean var1);

    public void unsetArray();
}

