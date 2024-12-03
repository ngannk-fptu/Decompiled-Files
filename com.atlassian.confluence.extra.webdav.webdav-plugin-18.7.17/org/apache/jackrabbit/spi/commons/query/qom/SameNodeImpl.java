/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.qom;

import javax.jcr.RepositoryException;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.qom.SameNode;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.query.qom.ConstraintImpl;
import org.apache.jackrabbit.spi.commons.query.qom.QOMTreeVisitor;

public class SameNodeImpl
extends ConstraintImpl
implements SameNode {
    private final Name selectorName;
    private final Path path;

    SameNodeImpl(NamePathResolver resolver, Name selectorName, Path path) throws InvalidQueryException, RepositoryException {
        super(resolver);
        this.selectorName = selectorName;
        this.path = path;
        if (!path.isAbsolute()) {
            throw new InvalidQueryException(resolver.getJCRPath(path) + " is not an absolute path");
        }
    }

    @Override
    public String getSelectorName() {
        return this.getJCRName(this.selectorName);
    }

    @Override
    public String getPath() {
        return this.getJCRPath(this.path);
    }

    public Name getSelectorQName() {
        return this.selectorName;
    }

    @Override
    public Object accept(QOMTreeVisitor visitor, Object data) throws Exception {
        return visitor.visit(this, data);
    }

    public String toString() {
        return "ISSAMENODE(" + this.getSelectorName() + ", " + this.quote(this.path) + ")";
    }
}

