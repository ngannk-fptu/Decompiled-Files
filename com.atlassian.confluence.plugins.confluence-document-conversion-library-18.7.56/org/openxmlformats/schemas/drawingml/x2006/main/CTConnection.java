/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.STDrawingElementId;

public interface CTConnection
extends XmlObject {
    public static final DocumentFactory<CTConnection> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctconnection7fb9type");
    public static final SchemaType type = Factory.getType();

    public long getId();

    public STDrawingElementId xgetId();

    public void setId(long var1);

    public void xsetId(STDrawingElementId var1);

    public long getIdx();

    public XmlUnsignedInt xgetIdx();

    public void setIdx(long var1);

    public void xsetIdx(XmlUnsignedInt var1);
}

