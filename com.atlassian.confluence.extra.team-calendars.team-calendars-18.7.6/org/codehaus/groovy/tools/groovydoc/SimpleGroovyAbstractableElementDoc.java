/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.groovydoc;

import org.codehaus.groovy.tools.groovydoc.SimpleGroovyProgramElementDoc;

public class SimpleGroovyAbstractableElementDoc
extends SimpleGroovyProgramElementDoc {
    private boolean abstractElement;

    public SimpleGroovyAbstractableElementDoc(String name) {
        super(name);
    }

    public void setAbstract(boolean b) {
        this.abstractElement = b;
    }

    public boolean isAbstract() {
        return this.abstractElement;
    }
}

