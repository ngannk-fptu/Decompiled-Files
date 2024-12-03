/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.STPositiveCoordinate;

public interface CTPositiveSize2D
extends XmlObject {
    public static final DocumentFactory<CTPositiveSize2D> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpositivesize2d0147type");
    public static final SchemaType type = Factory.getType();

    public long getCx();

    public STPositiveCoordinate xgetCx();

    public void setCx(long var1);

    public void xsetCx(STPositiveCoordinate var1);

    public long getCy();

    public STPositiveCoordinate xgetCy();

    public void setCy(long var1);

    public void xsetCy(STPositiveCoordinate var1);
}

