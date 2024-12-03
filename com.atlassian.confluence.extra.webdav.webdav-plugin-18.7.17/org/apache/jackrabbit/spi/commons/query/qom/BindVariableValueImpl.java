/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.qom;

import javax.jcr.query.qom.BindVariableValue;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.query.qom.QOMTreeVisitor;
import org.apache.jackrabbit.spi.commons.query.qom.StaticOperandImpl;

public class BindVariableValueImpl
extends StaticOperandImpl
implements BindVariableValue {
    private final Name variableName;

    BindVariableValueImpl(NamePathResolver resolver, Name variableName) {
        super(resolver);
        this.variableName = variableName;
    }

    public Name getBindVariableQName() {
        return this.variableName;
    }

    @Override
    public String getBindVariableName() {
        return this.getJCRName(this.variableName);
    }

    @Override
    public Object accept(QOMTreeVisitor visitor, Object data) throws Exception {
        return visitor.visit(this, data);
    }

    public String toString() {
        return "$" + this.getBindVariableName();
    }
}

