/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.vml;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface CTF
extends XmlObject {
    public static final DocumentFactory<CTF> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctfbc3atype");
    public static final SchemaType type = Factory.getType();

    public String getEqn();

    public XmlString xgetEqn();

    public boolean isSetEqn();

    public void setEqn(String var1);

    public void xsetEqn(XmlString var1);

    public void unsetEqn();
}

