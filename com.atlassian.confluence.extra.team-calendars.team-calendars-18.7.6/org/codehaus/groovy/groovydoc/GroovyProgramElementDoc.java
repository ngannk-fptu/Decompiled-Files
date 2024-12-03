/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.groovydoc;

import org.codehaus.groovy.groovydoc.GroovyAnnotationRef;
import org.codehaus.groovy.groovydoc.GroovyClassDoc;
import org.codehaus.groovy.groovydoc.GroovyDoc;
import org.codehaus.groovy.groovydoc.GroovyPackageDoc;

public interface GroovyProgramElementDoc
extends GroovyDoc {
    public GroovyAnnotationRef[] annotations();

    public GroovyClassDoc containingClass();

    public GroovyPackageDoc containingPackage();

    public boolean isFinal();

    public boolean isPackagePrivate();

    public boolean isPrivate();

    public boolean isProtected();

    public boolean isPublic();

    public boolean isStatic();

    public String modifiers();

    public int modifierSpecifier();

    public String qualifiedName();
}

