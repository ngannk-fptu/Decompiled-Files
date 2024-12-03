/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.office.office;

import com.microsoft.schemas.vml.STExt;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface CTIdMap
extends XmlObject {
    public static final DocumentFactory<CTIdMap> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctidmap63fatype");
    public static final SchemaType type = Factory.getType();

    public STExt.Enum getExt();

    public STExt xgetExt();

    public boolean isSetExt();

    public void setExt(STExt.Enum var1);

    public void xsetExt(STExt var1);

    public void unsetExt();

    public String getData();

    public XmlString xgetData();

    public boolean isSetData();

    public void setData(String var1);

    public void xsetData(XmlString var1);

    public void unsetData();
}

