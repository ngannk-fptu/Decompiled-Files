/*
 * Decompiled with CFR 0.152.
 */
package groovy.util.slurpersupport;

import groovy.lang.Buildable;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.Writable;
import groovy.util.slurpersupport.GPathResult;
import groovy.util.slurpersupport.ReplacementNode;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Node
implements Writable {
    private final String name;
    private final Map attributes;
    private final Map attributeNamespaces;
    private final String namespaceURI;
    private final List children = new LinkedList();
    private final Stack replacementNodeStack = new Stack();
    private final Node parent;

    public Node(Node parent, String name, Map attributes, Map attributeNamespaces, String namespaceURI) {
        this.name = name;
        this.attributes = attributes;
        this.attributeNamespaces = attributeNamespaces;
        this.namespaceURI = namespaceURI;
        this.parent = parent;
    }

    public String name() {
        return this.name;
    }

    public Node parent() {
        return this.parent;
    }

    public String namespaceURI() {
        return this.namespaceURI;
    }

    public Map attributes() {
        return this.attributes;
    }

    public List children() {
        return this.children;
    }

    public void addChild(Object child) {
        this.children.add(child);
    }

    public void replaceNode(final Closure replacementClosure, final GPathResult result) {
        this.replacementNodeStack.push(new ReplacementNode(){

            @Override
            public void build(GroovyObject builder, Map namespaceMap, Map<String, String> namespaceTagHints) {
                Closure c = (Closure)replacementClosure.clone();
                Node.this.replacementNodeStack.pop();
                c.setDelegate(builder);
                c.call(new Object[]{result});
                Node.this.replacementNodeStack.push(this);
            }
        });
    }

    protected void replaceBody(Object newValue) {
        this.children.clear();
        this.children.add(newValue);
    }

    protected void appendNode(final Object newValue, final GPathResult result) {
        if (newValue instanceof Closure) {
            this.children.add(new ReplacementNode(){

                @Override
                public void build(GroovyObject builder, Map namespaceMap, Map<String, String> namespaceTagHints) {
                    Closure c = (Closure)((Closure)newValue).clone();
                    c.setDelegate(builder);
                    c.call(new Object[]{result});
                }
            });
        } else {
            this.children.add(newValue);
        }
    }

    public String text() {
        StringBuilder sb = new StringBuilder();
        for (Object child : this.children) {
            if (child instanceof Node) {
                sb.append(((Node)child).text());
                continue;
            }
            sb.append(child);
        }
        return sb.toString();
    }

    public List<String> localText() {
        ArrayList<String> result = new ArrayList<String>();
        for (Object child : this.children) {
            if (child instanceof Node) continue;
            result.add(child.toString());
        }
        return result;
    }

    public Iterator childNodes() {
        return new Iterator(){
            private final Iterator iter;
            private Object nextElementNodes;
            {
                this.iter = Node.this.children.iterator();
                this.nextElementNodes = this.getNextElementNodes();
            }

            @Override
            public boolean hasNext() {
                return this.nextElementNodes != null;
            }

            public Object next() {
                try {
                    Object object = this.nextElementNodes;
                    return object;
                }
                finally {
                    this.nextElementNodes = this.getNextElementNodes();
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            private Object getNextElementNodes() {
                while (this.iter.hasNext()) {
                    Object node = this.iter.next();
                    if (!(node instanceof Node)) continue;
                    return node;
                }
                return null;
            }
        };
    }

    @Override
    public Writer writeTo(Writer out) throws IOException {
        if (this.replacementNodeStack.empty()) {
            for (Object child : this.children) {
                if (child instanceof Writable) {
                    ((Writable)child).writeTo(out);
                    continue;
                }
                out.write(child.toString());
            }
            return out;
        }
        return ((Writable)this.replacementNodeStack.peek()).writeTo(out);
    }

    public void build(final GroovyObject builder, final Map namespaceMap, final Map<String, String> namespaceTagHints) {
        if (this.replacementNodeStack.empty()) {
            Closure rest = new Closure(null){

                public Object doCall(Object o) {
                    Node.this.buildChildren(builder, namespaceMap, namespaceTagHints);
                    return null;
                }
            };
            if (this.namespaceURI.length() == 0 && this.attributeNamespaces.isEmpty()) {
                builder.invokeMethod(this.name, new Object[]{this.attributes, rest});
            } else {
                LinkedList newTags = new LinkedList();
                builder.getProperty("mkp");
                List namespaces = (List)builder.invokeMethod("getNamespaces", new Object[0]);
                Map current = (Map)namespaces.get(0);
                Map pending = (Map)namespaces.get(1);
                if (this.attributeNamespaces.isEmpty()) {
                    builder.getProperty(Node.getTagFor(this.namespaceURI, current, pending, namespaceMap, namespaceTagHints, newTags, builder));
                    builder.invokeMethod(this.name, new Object[]{this.attributes, rest});
                } else {
                    HashMap attributesWithNamespaces = new HashMap(this.attributes);
                    for (Object key : this.attributes.keySet()) {
                        Object attributeNamespaceURI = this.attributeNamespaces.get(key);
                        if (attributeNamespaceURI == null) continue;
                        attributesWithNamespaces.put(Node.getTagFor(attributeNamespaceURI, current, pending, namespaceMap, namespaceTagHints, newTags, builder) + "$" + key, attributesWithNamespaces.remove(key));
                    }
                    builder.getProperty(Node.getTagFor(this.namespaceURI, current, pending, namespaceMap, namespaceTagHints, newTags, builder));
                    builder.invokeMethod(this.name, new Object[]{attributesWithNamespaces, rest});
                }
                if (!newTags.isEmpty()) {
                    Iterator iter = newTags.iterator();
                    do {
                        pending.remove(iter.next());
                    } while (iter.hasNext());
                }
            }
        } else {
            ((ReplacementNode)this.replacementNodeStack.peek()).build(builder, namespaceMap, namespaceTagHints);
        }
    }

    private static String getTagFor(Object namespaceURI, Map current, Map pending, Map local, Map tagHints, List newTags, GroovyObject builder) {
        String tag = Node.findNamespaceTag(pending, namespaceURI);
        if (tag == null && (tag = Node.findNamespaceTag(current, namespaceURI)) == null) {
            tag = Node.findNamespaceTag(local, namespaceURI);
            if (tag == null || tag.length() == 0) {
                tag = Node.findNamespaceTag(tagHints, namespaceURI);
            }
            if (tag == null || tag.length() == 0) {
                int suffix = 0;
                do {
                    String possibleTag;
                    if (pending.containsKey(possibleTag = "tag" + suffix++) || current.containsKey(possibleTag) || local.containsKey(possibleTag)) continue;
                    tag = possibleTag;
                } while (tag == null);
            }
            HashMap<String, Object> newNamespace = new HashMap<String, Object>();
            newNamespace.put(tag, namespaceURI);
            builder.getProperty("mkp");
            builder.invokeMethod("declareNamespace", new Object[]{newNamespace});
            newTags.add(tag);
        }
        return tag;
    }

    private static String findNamespaceTag(Map tagMap, Object namespaceURI) {
        if (tagMap.containsValue(namespaceURI)) {
            for (Map.Entry o : tagMap.entrySet()) {
                Map.Entry entry = o;
                if (!namespaceURI.equals(entry.getValue())) continue;
                return (String)entry.getKey();
            }
        }
        return null;
    }

    private void buildChildren(GroovyObject builder, Map namespaceMap, Map<String, String> namespaceTagHints) {
        for (Object child : this.children) {
            if (child instanceof Node) {
                ((Node)child).build(builder, namespaceMap, namespaceTagHints);
                continue;
            }
            if (child instanceof Buildable) {
                ((Buildable)child).build(builder);
                continue;
            }
            builder.getProperty("mkp");
            builder.invokeMethod("yield", new Object[]{child});
        }
    }
}

