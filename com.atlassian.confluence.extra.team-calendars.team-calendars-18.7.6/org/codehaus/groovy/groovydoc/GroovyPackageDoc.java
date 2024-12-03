/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.groovydoc;

import org.codehaus.groovy.groovydoc.GroovyClassDoc;
import org.codehaus.groovy.groovydoc.GroovyDoc;

public interface GroovyPackageDoc
extends GroovyDoc {
    public GroovyClassDoc[] allClasses();

    public GroovyClassDoc[] allClasses(boolean var1);

    public GroovyClassDoc[] enums();

    public GroovyClassDoc[] errors();

    public GroovyClassDoc[] exceptions();

    public GroovyClassDoc findClass(String var1);

    public GroovyClassDoc[] interfaces();

    public GroovyClassDoc[] ordinaryClasses();

    public String summary();

    public String description();

    public String nameWithDots();

    public String getRelativeRootPath();
}

