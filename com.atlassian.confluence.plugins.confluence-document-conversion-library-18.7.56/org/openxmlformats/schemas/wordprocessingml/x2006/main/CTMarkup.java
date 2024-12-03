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

public interface CTMarkup
extends XmlObject {
    public static final DocumentFactory<CTMarkup> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctmarkup2d80type");
    public static final SchemaType type = Factory.getType();

    public BigInteger getId();

    public STDecimalNumber xgetId();

    public void setId(BigInteger var1);

    public void xsetId(STDecimalNumber var1);
}

