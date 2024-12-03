/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.groovydoc;

import java.util.List;
import java.util.Map;
import org.codehaus.groovy.groovydoc.GroovyClassDoc;
import org.codehaus.groovy.groovydoc.GroovyDoc;
import org.codehaus.groovy.groovydoc.GroovyDocErrorReporter;
import org.codehaus.groovy.groovydoc.GroovyPackageDoc;

public interface GroovyRootDoc
extends GroovyDoc,
GroovyDocErrorReporter {
    public GroovyClassDoc classNamed(GroovyClassDoc var1, String var2);

    public GroovyClassDoc[] classes();

    public String[][] options();

    public GroovyPackageDoc packageNamed(String var1);

    public GroovyClassDoc[] specifiedClasses();

    public GroovyPackageDoc[] specifiedPackages();

    public Map<String, GroovyClassDoc> getVisibleClasses(List var1);
}

