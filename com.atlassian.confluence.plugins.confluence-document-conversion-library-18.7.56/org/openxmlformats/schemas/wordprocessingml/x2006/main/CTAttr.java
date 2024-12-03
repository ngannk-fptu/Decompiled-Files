/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STString;

public interface CTAttr
extends XmlObject {
    public static final DocumentFactory<CTAttr> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctattre117type");
    public static final SchemaType type = Factory.getType();

    public String getUri();

    public STString xgetUri();

    public boolean isSetUri();

    public void setUri(String var1);

    public void xsetUri(STString var1);

    public void unsetUri();

    public String getName();

    public STString xgetName();

    public void setName(String var1);

    public void xsetName(STString var1);

    public String getVal();

    public STString xgetVal();

    public void setVal(String var1);

    public void xsetVal(STString var1);
}

