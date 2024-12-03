/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STSignedHpsMeasure;

public interface CTSignedHpsMeasure
extends XmlObject {
    public static final DocumentFactory<CTSignedHpsMeasure> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctsignedhpsmeasure3099type");
    public static final SchemaType type = Factory.getType();

    public Object getVal();

    public STSignedHpsMeasure xgetVal();

    public void setVal(Object var1);

    public void xsetVal(STSignedHpsMeasure var1);
}

