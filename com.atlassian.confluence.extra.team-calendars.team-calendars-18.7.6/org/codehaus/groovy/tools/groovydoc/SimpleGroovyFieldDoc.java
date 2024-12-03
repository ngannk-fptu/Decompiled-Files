/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.groovydoc;

import org.codehaus.groovy.groovydoc.GroovyClassDoc;
import org.codehaus.groovy.groovydoc.GroovyFieldDoc;
import org.codehaus.groovy.groovydoc.GroovyType;
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyMemberDoc;

public class SimpleGroovyFieldDoc
extends SimpleGroovyMemberDoc
implements GroovyFieldDoc {
    private GroovyType type;
    private String constantValueExpression;

    public SimpleGroovyFieldDoc(String name, GroovyClassDoc belongsToClass) {
        super(name, belongsToClass);
    }

    @Override
    public Object constantValue() {
        return null;
    }

    public void setConstantValueExpression(String constantValueExpression) {
        this.constantValueExpression = constantValueExpression;
    }

    @Override
    public String constantValueExpression() {
        return this.constantValueExpression;
    }

    @Override
    public boolean isTransient() {
        return false;
    }

    @Override
    public boolean isVolatile() {
        return false;
    }

    @Override
    public GroovyType type() {
        return this.type;
    }

    public void setType(GroovyType type) {
        this.type = type;
    }
}

