/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import java.math.BigInteger;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDocGrid;

public interface CTDocGrid
extends XmlObject {
    public static final DocumentFactory<CTDocGrid> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctdocgride8b4type");
    public static final SchemaType type = Factory.getType();

    public STDocGrid.Enum getType();

    public STDocGrid xgetType();

    public boolean isSetType();

    public void setType(STDocGrid.Enum var1);

    public void xsetType(STDocGrid var1);

    public void unsetType();

    public BigInteger getLinePitch();

    public STDecimalNumber xgetLinePitch();

    public boolean isSetLinePitch();

    public void setLinePitch(BigInteger var1);

    public void xsetLinePitch(STDecimalNumber var1);

    public void unsetLinePitch();

    public BigInteger getCharSpace();

    public STDecimalNumber xgetCharSpace();

    public boolean isSetCharSpace();

    public void setCharSpace(BigInteger var1);

    public void xsetCharSpace(STDecimalNumber var1);

    public void unsetCharSpace();
}

