/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.groovydoc;

import org.codehaus.groovy.groovydoc.GroovyTag;

public class SimpleGroovyTag
implements GroovyTag {
    private String name;
    private String param;
    private String text;

    public SimpleGroovyTag(String name, String param, String text) {
        this.name = name;
        this.param = param;
        this.text = text;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String param() {
        return this.param;
    }

    @Override
    public String text() {
        return this.text;
    }
}

