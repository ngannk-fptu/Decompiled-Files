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

public interface CTPaperSource
extends XmlObject {
    public static final DocumentFactory<CTPaperSource> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpapersource8aabtype");
    public static final SchemaType type = Factory.getType();

    public BigInteger getFirst();

    public STDecimalNumber xgetFirst();

    public boolean isSetFirst();

    public void setFirst(BigInteger var1);

    public void xsetFirst(STDecimalNumber var1);

    public void unsetFirst();

    public BigInteger getOther();

    public STDecimalNumber xgetOther();

    public boolean isSetOther();

    public void setOther(BigInteger var1);

    public void xsetOther(STDecimalNumber var1);

    public void unsetOther();
}

