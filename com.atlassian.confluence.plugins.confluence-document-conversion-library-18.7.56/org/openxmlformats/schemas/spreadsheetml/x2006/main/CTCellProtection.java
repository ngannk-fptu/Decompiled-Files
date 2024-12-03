/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface CTCellProtection
extends XmlObject {
    public static final DocumentFactory<CTCellProtection> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcellprotectionf524type");
    public static final SchemaType type = Factory.getType();

    public boolean getLocked();

    public XmlBoolean xgetLocked();

    public boolean isSetLocked();

    public void setLocked(boolean var1);

    public void xsetLocked(XmlBoolean var1);

    public void unsetLocked();

    public boolean getHidden();

    public XmlBoolean xgetHidden();

    public boolean isSetHidden();

    public void setHidden(boolean var1);

    public void xsetHidden(XmlBoolean var1);

    public void unsetHidden();
}

