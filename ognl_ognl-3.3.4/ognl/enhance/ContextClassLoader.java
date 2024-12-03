/*
 * Decompiled with CFR 0.152.
 */
package ognl.enhance;

import ognl.OgnlContext;

public class ContextClassLoader
extends ClassLoader {
    private OgnlContext context;

    public ContextClassLoader(ClassLoader parentClassLoader, OgnlContext context) {
        super(parentClassLoader);
        this.context = context;
    }

    protected Class findClass(String name) throws ClassNotFoundException {
        if (this.context != null && this.context.getClassResolver() != null) {
            return this.context.getClassResolver().classForName(name, this.context);
        }
        return super.findClass(name);
    }
}

