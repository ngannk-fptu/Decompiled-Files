/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.groovydoc;

import org.codehaus.groovy.groovydoc.GroovyClassDoc;
import org.codehaus.groovy.groovydoc.GroovyExecutableMemberDoc;
import org.codehaus.groovy.groovydoc.GroovyType;

public interface GroovyMethodDoc
extends GroovyExecutableMemberDoc {
    public boolean isAbstract();

    public GroovyClassDoc overriddenClass();

    public GroovyMethodDoc overriddenMethod();

    public GroovyType overriddenType();

    public boolean overrides(GroovyMethodDoc var1);

    public GroovyType returnType();

    public void setReturnType(GroovyType var1);
}

