/*
 * Decompiled with CFR 0.152.
 */
package groovy.util.slurpersupport;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyRuntimeException;
import groovy.util.slurpersupport.GPathResult;
import groovy.util.slurpersupport.NoChildren;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class Attribute
extends GPathResult {
    private final String value;

    public Attribute(String name, String value, GPathResult parent, String namespacePrefix, Map<String, String> namespaceTagHints) {
        super(parent, name, namespacePrefix, namespaceTagHints);
        this.value = value;
    }

    @Override
    public String name() {
        return this.name.substring(1);
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public String text() {
        return this.value;
    }

    public String namespaceURI() {
        if (this.namespacePrefix == null || this.namespacePrefix.isEmpty()) {
            return "";
        }
        String uri = (String)this.namespaceTagHints.get(this.namespacePrefix);
        return uri == null ? "" : uri;
    }

    @Override
    public GPathResult parents() {
        throw new GroovyRuntimeException("parents() not implemented yet");
    }

    @Override
    public Iterator childNodes() {
        throw new GroovyRuntimeException("can't call childNodes() in the attribute " + this.name);
    }

    @Override
    public Iterator iterator() {
        return this.nodeIterator();
    }

    @Override
    public GPathResult find(Closure closure) {
        if (DefaultTypeTransformation.castToBoolean(closure.call(new Object[]{this}))) {
            return this;
        }
        return new NoChildren(this, "", this.namespaceTagHints);
    }

    @Override
    public GPathResult findAll(Closure closure) {
        return this.find(closure);
    }

    @Override
    public Iterator nodeIterator() {
        return new Iterator(){
            private boolean hasNext = true;

            @Override
            public boolean hasNext() {
                return this.hasNext;
            }

            public Object next() {
                try {
                    Attribute attribute = this.hasNext ? Attribute.this : null;
                    return attribute;
                }
                finally {
                    this.hasNext = false;
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public Writer writeTo(Writer out) throws IOException {
        out.write(this.value);
        return out;
    }

    @Override
    public void build(GroovyObject builder) {
        builder.getProperty("mkp");
        builder.invokeMethod("yield", new Object[]{this.value});
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
}

