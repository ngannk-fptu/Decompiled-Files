/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.groovydoc;

import org.codehaus.groovy.groovydoc.GroovyAnnotationRef;
import org.codehaus.groovy.groovydoc.GroovyClassDoc;

public class SimpleGroovyAnnotationRef
implements GroovyAnnotationRef {
    private GroovyClassDoc type;
    private final String desc;
    private String name;

    public SimpleGroovyAnnotationRef(String name, String desc) {
        this.desc = desc;
        this.name = name;
    }

    public void setType(GroovyClassDoc type) {
        this.type = type;
    }

    @Override
    public GroovyClassDoc type() {
        return this.type;
    }

    public boolean isTypeAvailable() {
        return this.type != null;
    }

    @Override
    public String name() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String description() {
        return this.desc;
    }
}

