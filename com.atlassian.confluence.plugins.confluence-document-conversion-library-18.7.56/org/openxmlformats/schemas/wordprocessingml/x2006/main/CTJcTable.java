/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STJcTable;

public interface CTJcTable
extends XmlObject {
    public static final DocumentFactory<CTJcTable> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctjctablefa9dtype");
    public static final SchemaType type = Factory.getType();

    public STJcTable.Enum getVal();

    public STJcTable xgetVal();

    public void setVal(STJcTable.Enum var1);

    public void xsetVal(STJcTable var1);
}

