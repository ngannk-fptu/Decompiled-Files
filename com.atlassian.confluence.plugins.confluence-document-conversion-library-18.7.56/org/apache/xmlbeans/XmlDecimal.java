/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import java.math.BigDecimal;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.impl.schema.XmlObjectFactory;

public interface XmlDecimal
extends XmlAnySimpleType {
    public static final XmlObjectFactory<XmlDecimal> Factory = new XmlObjectFactory("_BI_decimal");
    public static final SchemaType type = Factory.getType();

    public BigDecimal getBigDecimalValue();

    public void setBigDecimalValue(BigDecimal var1);
}

