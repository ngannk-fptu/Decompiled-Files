/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.groovydoc;

import java.util.ArrayList;
import java.util.List;
import org.codehaus.groovy.groovydoc.GroovyAnnotationRef;
import org.codehaus.groovy.groovydoc.GroovyParameter;
import org.codehaus.groovy.groovydoc.GroovyType;

public class SimpleGroovyParameter
implements GroovyParameter {
    private String name;
    private String typeName;
    private String defaultValue;
    private GroovyType type;
    private boolean vararg;
    private final List<GroovyAnnotationRef> annotationRefs;

    public SimpleGroovyParameter(String name) {
        this.name = name;
        this.annotationRefs = new ArrayList<GroovyAnnotationRef>();
    }

    @Override
    public String defaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String typeName() {
        if (this.type == null) {
            return this.typeName;
        }
        return this.type.simpleTypeName();
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public GroovyAnnotationRef[] annotations() {
        return this.annotationRefs.toArray(new GroovyAnnotationRef[this.annotationRefs.size()]);
    }

    public void addAnnotationRef(GroovyAnnotationRef ref) {
        this.annotationRefs.add(ref);
    }

    @Override
    public GroovyType type() {
        return this.type;
    }

    public void setType(GroovyType type) {
        this.type = type;
    }

    public boolean isTypeAvailable() {
        return this.type != null;
    }

    public boolean vararg() {
        return this.vararg;
    }

    public void setVararg(boolean vararg) {
        this.vararg = vararg;
    }
}

