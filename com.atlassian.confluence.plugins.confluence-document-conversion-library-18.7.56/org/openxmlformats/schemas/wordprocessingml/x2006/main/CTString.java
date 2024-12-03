/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STString;

public interface CTString
extends XmlObject {
    public static final DocumentFactory<CTString> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctstring9c37type");
    public static final SchemaType type = Factory.getType();

    public String getVal();

    public STString xgetVal();

    public void setVal(String var1);

    public void xsetVal(STString var1);
}

