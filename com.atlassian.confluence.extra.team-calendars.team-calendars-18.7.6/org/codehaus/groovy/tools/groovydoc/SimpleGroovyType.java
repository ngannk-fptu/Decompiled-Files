/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.groovydoc;

import org.codehaus.groovy.groovydoc.GroovyType;

public class SimpleGroovyType
implements GroovyType {
    private String typeName;

    public SimpleGroovyType(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String typeName() {
        return this.typeName;
    }

    @Override
    public boolean isPrimitive() {
        return false;
    }

    @Override
    public String qualifiedTypeName() {
        return this.typeName.startsWith("DefaultPackage.") ? this.typeName.substring("DefaultPackage.".length()) : this.typeName;
    }

    @Override
    public String simpleTypeName() {
        int lastDot = this.typeName.lastIndexOf(46);
        if (lastDot < 0) {
            return this.typeName;
        }
        return this.typeName.substring(lastDot + 1);
    }
}

