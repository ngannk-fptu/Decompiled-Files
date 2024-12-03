/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import java.math.BigInteger;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMarkupRange;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDecimalNumber;

public interface CTBookmarkRange
extends CTMarkupRange {
    public static final DocumentFactory<CTBookmarkRange> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctbookmarkranged88btype");
    public static final SchemaType type = Factory.getType();

    public BigInteger getColFirst();

    public STDecimalNumber xgetColFirst();

    public boolean isSetColFirst();

    public void setColFirst(BigInteger var1);

    public void xsetColFirst(STDecimalNumber var1);

    public void unsetColFirst();

    public BigInteger getColLast();

    public STDecimalNumber xgetColLast();

    public boolean isSetColLast();

    public void setColLast(BigInteger var1);

    public void xsetColLast(STDecimalNumber var1);

    public void unsetColLast();
}

