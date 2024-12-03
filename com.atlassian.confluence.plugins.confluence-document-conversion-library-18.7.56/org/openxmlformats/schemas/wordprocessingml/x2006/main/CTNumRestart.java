/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STRestartNumber;

public interface CTNumRestart
extends XmlObject {
    public static final DocumentFactory<CTNumRestart> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctnumrestart261ftype");
    public static final SchemaType type = Factory.getType();

    public STRestartNumber.Enum getVal();

    public STRestartNumber xgetVal();

    public void setVal(STRestartNumber.Enum var1);

    public void xsetVal(STRestartNumber var1);
}

