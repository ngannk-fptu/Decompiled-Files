/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.qom;

import javax.jcr.query.qom.Length;
import javax.jcr.query.qom.PropertyValue;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.query.qom.DynamicOperandImpl;
import org.apache.jackrabbit.spi.commons.query.qom.PropertyValueImpl;
import org.apache.jackrabbit.spi.commons.query.qom.QOMTreeVisitor;

public class LengthImpl
extends DynamicOperandImpl
implements Length {
    private final PropertyValueImpl propertyValue;

    LengthImpl(NamePathResolver resolver, PropertyValueImpl propertyValue) {
        super(resolver, propertyValue.getSelectorQName());
        this.propertyValue = propertyValue;
    }

    @Override
    public PropertyValue getPropertyValue() {
        return this.propertyValue;
    }

    @Override
    public Object accept(QOMTreeVisitor visitor, Object data) throws Exception {
        return visitor.visit(this, data);
    }

    public String toString() {
        return "LENGTH(" + this.getPropertyValue() + ")";
    }
}

