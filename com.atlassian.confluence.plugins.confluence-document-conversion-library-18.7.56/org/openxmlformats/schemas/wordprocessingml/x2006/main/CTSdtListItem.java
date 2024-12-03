/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STString;

public interface CTSdtListItem
extends XmlObject {
    public static final DocumentFactory<CTSdtListItem> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctsdtlistitem705etype");
    public static final SchemaType type = Factory.getType();

    public String getDisplayText();

    public STString xgetDisplayText();

    public boolean isSetDisplayText();

    public void setDisplayText(String var1);

    public void xsetDisplayText(STString var1);

    public void unsetDisplayText();

    public String getValue();

    public STString xgetValue();

    public boolean isSetValue();

    public void setValue(String var1);

    public void xsetValue(STString var1);

    public void unsetValue();
}

