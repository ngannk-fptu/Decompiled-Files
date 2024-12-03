/*
 * Decompiled with CFR 0.152.
 */
package groovy.util.slurpersupport;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyRuntimeException;
import groovy.util.slurpersupport.Attribute;
import groovy.util.slurpersupport.FilteredAttributes;
import groovy.util.slurpersupport.GPathResult;
import groovy.util.slurpersupport.Node;
import groovy.util.slurpersupport.NodeChild;
import groovy.util.slurpersupport.NodeChildren;
import groovy.util.slurpersupport.NodeIterator;
import groovy.xml.QName;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Attributes
extends NodeChildren {
    final String attributeName;

    public Attributes(GPathResult parent, String name, String namespacePrefix, Map<String, String> namespaceTagHints) {
        super(parent, name, namespacePrefix, namespaceTagHints);
        this.attributeName = this.name.substring(1);
    }

    public Attributes(GPathResult parent, String name, Map<String, String> namespaceTagHints) {
        this(parent, name, "*", namespaceTagHints);
    }

    @Override
    public String name() {
        return this.name.substring(1);
    }

    @Override
    public Iterator childNodes() {
        throw new GroovyRuntimeException("Can't get the child nodes on a GPath expression selecting attributes: ...." + this.parent.name() + "." + this.name() + ".childNodes()");
    }

    @Override
    public Iterator iterator() {
        return new NodeIterator(this.nodeIterator()){

            @Override
            protected Object getNextNode(Iterator iter) {
                while (iter.hasNext()) {
                    String value;
                    Object next = iter.next();
                    if (next instanceof Attribute) {
                        return next;
                    }
                    String attributeKey = Attributes.this.attributeName;
                    if (Attributes.this.namespacePrefix != null && !"*".equals(Attributes.this.namespacePrefix) && Attributes.this.namespacePrefix.length() > 0) {
                        attributeKey = new QName(Attributes.this.lookupNamespace(Attributes.this.namespacePrefix), Attributes.this.attributeName).toString();
                    }
                    if ((value = (String)((Node)next).attributes().get(attributeKey)) == null) continue;
                    return new Attribute(Attributes.this.name, value, new NodeChild((Node)next, Attributes.this.parent.parent, "", (Map<String, String>)Attributes.this.namespaceTagHints), Attributes.this.namespacePrefix == null || "*".equals(Attributes.this.namespacePrefix) ? "" : Attributes.this.namespacePrefix, Attributes.this.namespaceTagHints);
                }
                return null;
            }
        };
    }

    @Override
    public Iterator nodeIterator() {
        return this.parent.nodeIterator();
    }

    @Override
    public GPathResult parents() {
        return super.parents();
    }

    @Override
    public String text() {
        StringBuilder sb = new StringBuilder();
        Iterator iter = this.iterator();
        while (iter.hasNext()) {
            sb.append(iter.next());
        }
        return sb.toString();
    }

    @Override
    public List list() {
        Iterator iter = this.iterator();
        ArrayList result = new ArrayList();
        while (iter.hasNext()) {
            result.add(iter.next());
        }
        return result;
    }

    @Override
    public GPathResult findAll(Closure closure) {
        return new FilteredAttributes((GPathResult)this, closure, (Map<String, String>)this.namespaceTagHints);
    }

    @Override
    public Writer writeTo(Writer out) throws IOException {
        out.write(this.text());
        return out;
    }

    @Override
    public void build(GroovyObject builder) {
        builder.getProperty("mkp");
        builder.invokeMethod("yield", new Object[]{this.text()});
    }
}

