/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.groovydoc;

import org.codehaus.groovy.groovydoc.GroovyMemberDoc;
import org.codehaus.groovy.groovydoc.GroovyType;

public interface GroovyFieldDoc
extends GroovyMemberDoc {
    public Object constantValue();

    public String constantValueExpression();

    public boolean isTransient();

    public boolean isVolatile();

    public GroovyType type();
}

