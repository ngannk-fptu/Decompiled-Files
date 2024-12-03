/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.qom;

import javax.jcr.query.qom.PropertyValue;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.query.qom.DynamicOperandImpl;
import org.apache.jackrabbit.spi.commons.query.qom.QOMTreeVisitor;

public class PropertyValueImpl
extends DynamicOperandImpl
implements PropertyValue {
    private final Name propertyName;

    PropertyValueImpl(NamePathResolver resolver, Name selectorName, Name propertyName) {
        super(resolver, selectorName);
        this.propertyName = propertyName;
    }

    public Name getPropertyQName() {
        return this.propertyName;
    }

    @Override
    public String getPropertyName() {
        return this.getJCRName(this.propertyName);
    }

    @Override
    public Object accept(QOMTreeVisitor visitor, Object data) throws Exception {
        return visitor.visit(this, data);
    }

    public String toString() {
        return this.getSelectorName() + "." + this.quote(this.propertyName);
    }
}

