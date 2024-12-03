/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.groovydoc;

import org.codehaus.groovy.groovydoc.GroovyAnnotationRef;
import org.codehaus.groovy.groovydoc.GroovyType;

public interface GroovyParameter {
    public GroovyAnnotationRef[] annotations();

    public String name();

    public GroovyType type();

    public String typeName();

    public String defaultValue();
}

