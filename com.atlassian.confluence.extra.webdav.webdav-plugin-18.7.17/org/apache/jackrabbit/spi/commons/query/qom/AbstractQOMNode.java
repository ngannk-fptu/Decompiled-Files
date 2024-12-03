/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.qom;

import javax.jcr.NamespaceException;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.query.qom.QOMTreeVisitor;

public abstract class AbstractQOMNode {
    protected final NamePathResolver resolver;

    public AbstractQOMNode(NamePathResolver resolver) {
        this.resolver = resolver;
    }

    public abstract Object accept(QOMTreeVisitor var1, Object var2) throws Exception;

    protected String getJCRName(Name name) {
        if (name == null) {
            return null;
        }
        try {
            return this.resolver.getJCRName(name);
        }
        catch (NamespaceException e) {
            return name.toString();
        }
    }

    protected String getJCRPath(Path path) {
        if (path == null) {
            return null;
        }
        try {
            return this.resolver.getJCRPath(path);
        }
        catch (NamespaceException e) {
            return path.toString();
        }
    }

    protected String quote(Name name) {
        String str = this.getJCRName(name);
        if (str.indexOf(58) != -1) {
            return "[" + str + "]";
        }
        return str;
    }

    protected String quote(Path path) {
        String str = this.getJCRPath(path);
        if (str.indexOf(58) != -1 || str.indexOf(47) != -1) {
            return "[" + str + "]";
        }
        return str;
    }

    protected String protect(Object expression) {
        String str = expression.toString();
        if (str.indexOf(" ") != -1) {
            return "(" + str + ")";
        }
        return str;
    }
}

