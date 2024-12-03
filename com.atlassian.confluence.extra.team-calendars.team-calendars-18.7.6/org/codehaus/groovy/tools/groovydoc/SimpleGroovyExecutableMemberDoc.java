/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.groovydoc;

import java.util.ArrayList;
import java.util.List;
import org.codehaus.groovy.groovydoc.GroovyClassDoc;
import org.codehaus.groovy.groovydoc.GroovyExecutableMemberDoc;
import org.codehaus.groovy.groovydoc.GroovyParameter;
import org.codehaus.groovy.groovydoc.GroovyType;
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyMemberDoc;

public class SimpleGroovyExecutableMemberDoc
extends SimpleGroovyMemberDoc
implements GroovyExecutableMemberDoc {
    List parameters = new ArrayList();

    public SimpleGroovyExecutableMemberDoc(String name, GroovyClassDoc belongsToClass) {
        super(name, belongsToClass);
    }

    @Override
    public GroovyParameter[] parameters() {
        return this.parameters.toArray(new GroovyParameter[this.parameters.size()]);
    }

    public void add(GroovyParameter parameter) {
        this.parameters.add(parameter);
    }

    @Override
    public String flatSignature() {
        return null;
    }

    @Override
    public boolean isNative() {
        return false;
    }

    @Override
    public boolean isSynchronized() {
        return false;
    }

    @Override
    public boolean isVarArgs() {
        return false;
    }

    @Override
    public String signature() {
        return null;
    }

    @Override
    public GroovyClassDoc[] thrownExceptions() {
        return null;
    }

    @Override
    public GroovyType[] thrownExceptionTypes() {
        return null;
    }
}

