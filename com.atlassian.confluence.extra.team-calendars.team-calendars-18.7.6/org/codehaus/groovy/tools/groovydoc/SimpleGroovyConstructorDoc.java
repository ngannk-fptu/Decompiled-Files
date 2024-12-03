/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.groovydoc;

import org.codehaus.groovy.groovydoc.GroovyClassDoc;
import org.codehaus.groovy.groovydoc.GroovyConstructorDoc;
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyExecutableMemberDoc;

public class SimpleGroovyConstructorDoc
extends SimpleGroovyExecutableMemberDoc
implements GroovyConstructorDoc {
    public SimpleGroovyConstructorDoc(String name, GroovyClassDoc belongsToClass) {
        super(name, belongsToClass);
    }
}

