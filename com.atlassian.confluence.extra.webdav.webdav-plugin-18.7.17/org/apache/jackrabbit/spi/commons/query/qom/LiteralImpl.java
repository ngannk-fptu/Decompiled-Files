/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.qom;

import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.query.qom.Literal;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.query.qom.QOMTreeVisitor;
import org.apache.jackrabbit.spi.commons.query.qom.StaticOperandImpl;

public class LiteralImpl
extends StaticOperandImpl
implements Literal {
    private final Value value;

    public LiteralImpl(NamePathResolver resolver, Value value) {
        super(resolver);
        this.value = value;
    }

    @Override
    public Value getLiteralValue() {
        return this.value;
    }

    @Override
    public Object accept(QOMTreeVisitor visitor, Object data) throws Exception {
        return visitor.visit(this, data);
    }

    public String toString() {
        try {
            switch (this.value.getType()) {
                case 2: {
                    return this.cast("BINARY");
                }
                case 6: {
                    return this.cast("BOOLEAN");
                }
                case 5: {
                    return this.cast("DATE");
                }
                case 12: {
                    return this.cast("DECIMAL");
                }
                case 3: 
                case 4: {
                    return this.value.getString();
                }
                case 7: {
                    return this.cast("NAME");
                }
                case 8: {
                    return this.cast("PATH");
                }
                case 9: {
                    return this.cast("REFERENCE");
                }
                case 1: {
                    return this.escape();
                }
                case 11: {
                    return this.cast("URI");
                }
                case 10: {
                    return this.cast("WEAKREFERENCE");
                }
            }
            return this.escape();
        }
        catch (RepositoryException e) {
            return this.value.toString();
        }
    }

    private String cast(String type) throws RepositoryException {
        return "CAST(" + this.escape() + " AS " + type + ")";
    }

    private String escape() throws RepositoryException {
        return "'" + this.value.getString().replace("'", "''") + "'";
    }
}

