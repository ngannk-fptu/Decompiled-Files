/*
 * Decompiled with CFR 0.152.
 */
package groovy.util;

import groovy.lang.Closure;
import groovy.lang.DelegatingMetaClass;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;
import groovy.util.Node;
import groovy.xml.QName;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class NodeList
extends ArrayList {
    public NodeList() {
    }

    public NodeList(Collection collection) {
        super(collection);
    }

    public NodeList(int size) {
        super(size);
    }

    @Override
    public Object clone() {
        NodeList result = new NodeList(this.size());
        for (int i = 0; i < this.size(); ++i) {
            Object next = this.get(i);
            if (next instanceof Node) {
                Node n = (Node)next;
                result.add(n.clone());
                continue;
            }
            result.add(next);
        }
        return result;
    }

    protected static void setMetaClass(Class nodelistClass, MetaClass metaClass) {
        DelegatingMetaClass newMetaClass = new DelegatingMetaClass(metaClass){

            @Override
            public Object getAttribute(Object object, String attribute) {
                NodeList nl = (NodeList)object;
                Iterator it = nl.iterator();
                ArrayList result = new ArrayList();
                while (it.hasNext()) {
                    Node node = (Node)it.next();
                    result.add(node.attributes().get(attribute));
                }
                return result;
            }

            @Override
            public void setAttribute(Object object, String attribute, Object newValue) {
                for (Object o : (NodeList)object) {
                    Node node = (Node)o;
                    node.attributes().put(attribute, newValue);
                }
            }

            @Override
            public Object getProperty(Object object, String property) {
                if (object instanceof NodeList) {
                    NodeList nl = (NodeList)object;
                    return nl.getAt(property);
                }
                return super.getProperty(object, property);
            }
        };
        GroovySystem.getMetaClassRegistry().setMetaClass(nodelistClass, newMetaClass);
    }

    public NodeList getAt(String name) {
        NodeList answer = new NodeList();
        for (Object child : this) {
            if (!(child instanceof Node)) continue;
            Node childNode = (Node)child;
            Object temp = childNode.get(name);
            if (temp instanceof Collection) {
                answer.addAll((Collection)temp);
                continue;
            }
            answer.add(temp);
        }
        return answer;
    }

    public NodeList getAt(QName name) {
        NodeList answer = new NodeList();
        for (Object child : this) {
            if (!(child instanceof Node)) continue;
            Node childNode = (Node)child;
            NodeList temp = childNode.getAt(name);
            answer.addAll(temp);
        }
        return answer;
    }

    public String text() {
        String previousText = null;
        StringBuilder buffer = null;
        for (Object child : this) {
            String text = null;
            if (child instanceof String) {
                text = (String)child;
            } else if (child instanceof Node) {
                text = ((Node)child).text();
            }
            if (text == null) continue;
            if (previousText == null) {
                previousText = text;
                continue;
            }
            if (buffer == null) {
                buffer = new StringBuilder();
                buffer.append(previousText);
            }
            buffer.append(text);
        }
        if (buffer != null) {
            return buffer.toString();
        }
        if (previousText != null) {
            return previousText;
        }
        return "";
    }

    public Node replaceNode(Closure c) {
        if (this.size() <= 0 || this.size() > 1) {
            throw new GroovyRuntimeException("replaceNode() can only be used to replace a single node, but was applied to " + this.size() + " nodes");
        }
        return ((Node)this.get(0)).replaceNode(c);
    }

    public void plus(Closure c) {
        for (Object o : this) {
            ((Node)o).plus(c);
        }
    }

    static {
        NodeList.setMetaClass(NodeList.class, GroovySystem.getMetaClassRegistry().getMetaClass(NodeList.class));
    }
}

