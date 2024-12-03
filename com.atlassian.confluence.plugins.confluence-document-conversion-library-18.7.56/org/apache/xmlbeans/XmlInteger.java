/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import java.math.BigInteger;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlDecimal;
import org.apache.xmlbeans.impl.schema.XmlObjectFactory;

public interface XmlInteger
extends XmlDecimal {
    public static final XmlObjectFactory<XmlInteger> Factory = new XmlObjectFactory("_BI_integer");
    public static final SchemaType type = Factory.getType();

    public BigInteger getBigIntegerValue();

    public void setBigIntegerValue(BigInteger var1);
}

