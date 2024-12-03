/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STTwipsMeasure;

public interface CTTblGridCol
extends XmlObject {
    public static final DocumentFactory<CTTblGridCol> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttblgridcolbfectype");
    public static final SchemaType type = Factory.getType();

    public Object getW();

    public STTwipsMeasure xgetW();

    public boolean isSetW();

    public void setW(Object var1);

    public void xsetW(STTwipsMeasure var1);

    public void unsetW();
}

