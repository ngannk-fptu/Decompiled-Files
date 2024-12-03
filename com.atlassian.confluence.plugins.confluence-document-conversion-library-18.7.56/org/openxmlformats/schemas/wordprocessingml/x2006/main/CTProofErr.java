/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STProofErr;

public interface CTProofErr
extends XmlObject {
    public static final DocumentFactory<CTProofErr> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctprooferr1e07type");
    public static final SchemaType type = Factory.getType();

    public STProofErr.Enum getType();

    public STProofErr xgetType();

    public void setType(STProofErr.Enum var1);

    public void xsetType(STProofErr var1);
}

