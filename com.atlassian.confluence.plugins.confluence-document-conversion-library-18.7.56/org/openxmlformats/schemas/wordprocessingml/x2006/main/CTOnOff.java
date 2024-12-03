/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STOnOff;

public interface CTOnOff
extends XmlObject {
    public static final DocumentFactory<CTOnOff> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctonoff04c2type");
    public static final SchemaType type = Factory.getType();

    public Object getVal();

    public STOnOff xgetVal();

    public boolean isSetVal();

    public void setVal(Object var1);

    public void xsetVal(STOnOff var1);

    public void unsetVal();
}

