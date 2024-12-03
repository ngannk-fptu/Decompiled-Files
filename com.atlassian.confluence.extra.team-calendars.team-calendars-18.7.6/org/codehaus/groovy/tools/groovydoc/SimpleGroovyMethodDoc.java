/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.groovydoc;

import org.codehaus.groovy.groovydoc.GroovyClassDoc;
import org.codehaus.groovy.groovydoc.GroovyMethodDoc;
import org.codehaus.groovy.groovydoc.GroovyType;
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyExecutableMemberDoc;

public class SimpleGroovyMethodDoc
extends SimpleGroovyExecutableMemberDoc
implements GroovyMethodDoc {
    private GroovyType returnType;
    private String typeParameters;

    public SimpleGroovyMethodDoc(String name, GroovyClassDoc belongsToClass) {
        super(name, belongsToClass);
    }

    @Override
    public GroovyType returnType() {
        return this.returnType;
    }

    @Override
    public void setReturnType(GroovyType returnType) {
        this.returnType = returnType;
    }

    @Override
    public boolean isAbstract() {
        return false;
    }

    @Override
    public GroovyClassDoc overriddenClass() {
        return null;
    }

    @Override
    public GroovyMethodDoc overriddenMethod() {
        return null;
    }

    @Override
    public GroovyType overriddenType() {
        return null;
    }

    @Override
    public boolean overrides(GroovyMethodDoc arg0) {
        return false;
    }

    public String typeParameters() {
        return this.typeParameters;
    }

    public void setTypeParameters(String typeParameters) {
        this.typeParameters = typeParameters;
    }
}

