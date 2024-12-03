/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHpsMeasure;

public interface CTHpsMeasure
extends XmlObject {
    public static final DocumentFactory<CTHpsMeasure> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cthpsmeasure3795type");
    public static final SchemaType type = Factory.getType();

    public Object getVal();

    public STHpsMeasure xgetVal();

    public void setVal(Object var1);

    public void xsetVal(STHpsMeasure var1);
}

