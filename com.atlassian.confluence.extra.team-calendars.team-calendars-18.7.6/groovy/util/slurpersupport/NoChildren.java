/*
 * Decompiled with CFR 0.152.
 */
package groovy.util.slurpersupport;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyRuntimeException;
import groovy.util.slurpersupport.GPathResult;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

public class NoChildren
extends GPathResult {
    public NoChildren(GPathResult parent, String name, Map<String, String> namespaceTagHints) {
        super(parent, name, "*", namespaceTagHints);
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public String text() {
        return "";
    }

    @Override
    public GPathResult parents() {
        throw new GroovyRuntimeException("parents() not implemented yet");
    }

    @Override
    public Iterator childNodes() {
        return this.iterator();
    }

    @Override
    public Iterator iterator() {
        return new Iterator(){

            @Override
            public boolean hasNext() {
                return false;
            }

            public Object next() {
                return null;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public GPathResult find(Closure closure) {
        return this;
    }

    @Override
    public GPathResult findAll(Closure closure) {
        return this;
    }

    @Override
    public Iterator nodeIterator() {
        return this.iterator();
    }

    @Override
    public Writer writeTo(Writer out) throws IOException {
        return out;
    }

    @Override
    public void build(GroovyObject builder) {
    }

    @Override
    protected void replaceNode(Closure newValue) {
    }

    @Override
    protected void replaceBody(Object newValue) {
    }

    @Override
    protected void appendNode(Object newValue) {
    }

    public boolean asBoolean() {
        return false;
    }
}

