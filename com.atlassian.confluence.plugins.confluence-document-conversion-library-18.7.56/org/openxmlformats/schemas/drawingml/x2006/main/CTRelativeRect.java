/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.STPercentage;

public interface CTRelativeRect
extends XmlObject {
    public static final DocumentFactory<CTRelativeRect> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctrelativerecta4ebtype");
    public static final SchemaType type = Factory.getType();

    public Object getL();

    public STPercentage xgetL();

    public boolean isSetL();

    public void setL(Object var1);

    public void xsetL(STPercentage var1);

    public void unsetL();

    public Object getT();

    public STPercentage xgetT();

    public boolean isSetT();

    public void setT(Object var1);

    public void xsetT(STPercentage var1);

    public void unsetT();

    public Object getR();

    public STPercentage xgetR();

    public boolean isSetR();

    public void setR(Object var1);

    public void xsetR(STPercentage var1);

    public void unsetR();

    public Object getB();

    public STPercentage xgetB();

    public boolean isSetB();

    public void setB(Object var1);

    public void xsetB(STPercentage var1);

    public void unsetB();
}

