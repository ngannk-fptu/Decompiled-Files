/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.qom;

import javax.jcr.query.qom.PropertyExistence;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.query.qom.ConstraintImpl;
import org.apache.jackrabbit.spi.commons.query.qom.QOMTreeVisitor;

public class PropertyExistenceImpl
extends ConstraintImpl
implements PropertyExistence {
    private final Name selectorName;
    private final Name propertyName;

    PropertyExistenceImpl(NamePathResolver resolver, Name selectorName, Name propertyName) {
        super(resolver);
        this.selectorName = selectorName;
        this.propertyName = propertyName;
    }

    public Name getSelectorQName() {
        return this.selectorName;
    }

    public Name getPropertyQName() {
        return this.propertyName;
    }

    @Override
    public String getSelectorName() {
        return this.getJCRName(this.selectorName);
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
        return this.getSelectorName() + "." + this.quote(this.propertyName) + " IS NOT NULL";
    }
}

