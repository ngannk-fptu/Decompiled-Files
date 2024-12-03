/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.groovydoc;

import org.codehaus.groovy.groovydoc.GroovyClassDoc;
import org.codehaus.groovy.groovydoc.GroovyMemberDoc;
import org.codehaus.groovy.groovydoc.GroovyParameter;
import org.codehaus.groovy.groovydoc.GroovyType;

public interface GroovyExecutableMemberDoc
extends GroovyMemberDoc {
    public String flatSignature();

    public boolean isNative();

    public boolean isSynchronized();

    public boolean isVarArgs();

    public GroovyParameter[] parameters();

    public String signature();

    public GroovyClassDoc[] thrownExceptions();

    public GroovyType[] thrownExceptionTypes();
}

