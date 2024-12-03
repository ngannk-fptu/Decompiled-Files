/*
 * Decompiled with CFR 0.152.
 */
package org.jdom;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jdom.Attribute;
import org.jdom.AttributeList;
import org.jdom.Content;
import org.jdom.ContentList;
import org.jdom.DescendantIterator;
import org.jdom.Document;
import org.jdom.FilterIterator;
import org.jdom.IllegalAddException;
import org.jdom.IllegalNameException;
import org.jdom.Namespace;
import org.jdom.Parent;
import org.jdom.Text;
import org.jdom.Verifier;
import org.jdom.filter.ElementFilter;
import org.jdom.filter.Filter;

public class Element
extends Content
implements Parent {
    private static final String CVS_ID = "@(#) $RCSfile: Element.java,v $ $Revision: 1.159 $ $Date: 2007/11/14 05:02:08 $ $Name:  $";
    private static final int INITIAL_ARRAY_SIZE = 5;
    protected String name;
    protected transient Namespace namespace;
    protected transient List additionalNamespaces;
    AttributeList attributes = new AttributeList(this);
    ContentList content = new ContentList(this);

    protected Element() {
    }

    public Element(String name, Namespace namespace) {
        this.setName(name);
        this.setNamespace(namespace);
    }

    public Element(String name) {
        this(name, (Namespace)null);
    }

    public Element(String name, String uri) {
        this(name, Namespace.getNamespace("", uri));
    }

    public Element(String name, String prefix, String uri) {
        this(name, Namespace.getNamespace(prefix, uri));
    }

    public String getName() {
        return this.name;
    }

    public Element setName(String name) {
        String reason = Verifier.checkElementName(name);
        if (reason != null) {
            throw new IllegalNameException(name, "element", reason);
        }
        this.name = name;
        return this;
    }

    public Namespace getNamespace() {
        return this.namespace;
    }

    public Element setNamespace(Namespace namespace) {
        String reason;
        if (namespace == null) {
            namespace = Namespace.NO_NAMESPACE;
        }
        if ((reason = Verifier.checkNamespaceCollision(namespace, this.getAdditionalNamespaces())) != null) {
            throw new IllegalAddException(this, namespace, reason);
        }
        for (Attribute a : this.getAttributes()) {
            reason = Verifier.checkNamespaceCollision(namespace, a);
            if (reason == null) continue;
            throw new IllegalAddException(this, namespace, reason);
        }
        this.namespace = namespace;
        return this;
    }

    public String getNamespacePrefix() {
        return this.namespace.getPrefix();
    }

    public String getNamespaceURI() {
        return this.namespace.getURI();
    }

    public Namespace getNamespace(String prefix) {
        if (prefix == null) {
            return null;
        }
        if ("xml".equals(prefix)) {
            return Namespace.XML_NAMESPACE;
        }
        if (prefix.equals(this.getNamespacePrefix())) {
            return this.getNamespace();
        }
        if (this.additionalNamespaces != null) {
            for (int i = 0; i < this.additionalNamespaces.size(); ++i) {
                Namespace ns = (Namespace)this.additionalNamespaces.get(i);
                if (!prefix.equals(ns.getPrefix())) continue;
                return ns;
            }
        }
        if (this.attributes != null) {
            for (Attribute a : this.attributes) {
                if (!prefix.equals(a.getNamespacePrefix())) continue;
                return a.getNamespace();
            }
        }
        if (this.parent instanceof Element) {
            return ((Element)this.parent).getNamespace(prefix);
        }
        return null;
    }

    public String getQualifiedName() {
        if ("".equals(this.namespace.getPrefix())) {
            return this.getName();
        }
        return new StringBuffer(this.namespace.getPrefix()).append(':').append(this.name).toString();
    }

    public void addNamespaceDeclaration(Namespace additionalNamespace) {
        String reason = Verifier.checkNamespaceCollision(additionalNamespace, this);
        if (reason != null) {
            throw new IllegalAddException(this, additionalNamespace, reason);
        }
        if (this.additionalNamespaces == null) {
            this.additionalNamespaces = new ArrayList(5);
        }
        this.additionalNamespaces.add(additionalNamespace);
    }

    public void removeNamespaceDeclaration(Namespace additionalNamespace) {
        if (this.additionalNamespaces == null) {
            return;
        }
        this.additionalNamespaces.remove(additionalNamespace);
    }

    public List getAdditionalNamespaces() {
        if (this.additionalNamespaces == null) {
            return Collections.EMPTY_LIST;
        }
        return Collections.unmodifiableList(this.additionalNamespaces);
    }

    @Override
    public String getValue() {
        StringBuffer buffer = new StringBuffer();
        for (Content child : this.getContent()) {
            if (!(child instanceof Element) && !(child instanceof Text)) continue;
            buffer.append(child.getValue());
        }
        return buffer.toString();
    }

    public boolean isRootElement() {
        return this.parent instanceof Document;
    }

    @Override
    public int getContentSize() {
        return this.content.size();
    }

    @Override
    public int indexOf(Content child) {
        return this.content.indexOf(child);
    }

    public String getText() {
        if (this.content.size() == 0) {
            return "";
        }
        if (this.content.size() == 1) {
            Object obj = this.content.get(0);
            if (obj instanceof Text) {
                return ((Text)obj).getText();
            }
            return "";
        }
        StringBuffer textContent = new StringBuffer();
        boolean hasText = false;
        for (int i = 0; i < this.content.size(); ++i) {
            Object obj = this.content.get(i);
            if (!(obj instanceof Text)) continue;
            textContent.append(((Text)obj).getText());
            hasText = true;
        }
        if (!hasText) {
            return "";
        }
        return textContent.toString();
    }

    public String getTextTrim() {
        return this.getText().trim();
    }

    public String getTextNormalize() {
        return Text.normalizeString(this.getText());
    }

    public String getChildText(String name) {
        Element child = this.getChild(name);
        if (child == null) {
            return null;
        }
        return child.getText();
    }

    public String getChildTextTrim(String name) {
        Element child = this.getChild(name);
        if (child == null) {
            return null;
        }
        return child.getTextTrim();
    }

    public String getChildTextNormalize(String name) {
        Element child = this.getChild(name);
        if (child == null) {
            return null;
        }
        return child.getTextNormalize();
    }

    public String getChildText(String name, Namespace ns) {
        Element child = this.getChild(name, ns);
        if (child == null) {
            return null;
        }
        return child.getText();
    }

    public String getChildTextTrim(String name, Namespace ns) {
        Element child = this.getChild(name, ns);
        if (child == null) {
            return null;
        }
        return child.getTextTrim();
    }

    public String getChildTextNormalize(String name, Namespace ns) {
        Element child = this.getChild(name, ns);
        if (child == null) {
            return null;
        }
        return child.getTextNormalize();
    }

    public Element setText(String text) {
        this.content.clear();
        if (text != null) {
            this.addContent(new Text(text));
        }
        return this;
    }

    @Override
    public List getContent() {
        return this.content;
    }

    @Override
    public List getContent(Filter filter) {
        return this.content.getView(filter);
    }

    @Override
    public List removeContent() {
        ArrayList old = new ArrayList(this.content);
        this.content.clear();
        return old;
    }

    @Override
    public List removeContent(Filter filter) {
        ArrayList<Content> old = new ArrayList<Content>();
        Iterator iter = this.content.getView(filter).iterator();
        while (iter.hasNext()) {
            Content child = (Content)iter.next();
            old.add(child);
            iter.remove();
        }
        return old;
    }

    public Element setContent(Collection newContent) {
        this.content.clearAndSet(newContent);
        return this;
    }

    public Element setContent(int index, Content child) {
        this.content.set(index, child);
        return this;
    }

    public Parent setContent(int index, Collection newContent) {
        this.content.remove(index);
        this.content.addAll(index, newContent);
        return this;
    }

    public Element addContent(String str) {
        return this.addContent(new Text(str));
    }

    public Element addContent(Content child) {
        this.content.add(child);
        return this;
    }

    public Element addContent(Collection newContent) {
        this.content.addAll(newContent);
        return this;
    }

    public Element addContent(int index, Content child) {
        this.content.add(index, child);
        return this;
    }

    public Element addContent(int index, Collection newContent) {
        this.content.addAll(index, newContent);
        return this;
    }

    @Override
    public List cloneContent() {
        int size = this.getContentSize();
        ArrayList<Object> list = new ArrayList<Object>(size);
        for (int i = 0; i < size; ++i) {
            Content child = this.getContent(i);
            list.add(child.clone());
        }
        return list;
    }

    @Override
    public Content getContent(int index) {
        return (Content)this.content.get(index);
    }

    @Override
    public boolean removeContent(Content child) {
        return this.content.remove(child);
    }

    @Override
    public Content removeContent(int index) {
        return (Content)this.content.remove(index);
    }

    public Element setContent(Content child) {
        this.content.clear();
        this.content.add(child);
        return this;
    }

    public boolean isAncestor(Element element) {
        Parent p = element.getParent();
        while (p instanceof Element) {
            if (p == this) {
                return true;
            }
            p = p.getParent();
        }
        return false;
    }

    public List getAttributes() {
        return this.attributes;
    }

    public Attribute getAttribute(String name) {
        return this.getAttribute(name, Namespace.NO_NAMESPACE);
    }

    public Attribute getAttribute(String name, Namespace ns) {
        return (Attribute)this.attributes.get(name, ns);
    }

    public String getAttributeValue(String name) {
        return this.getAttributeValue(name, Namespace.NO_NAMESPACE);
    }

    public String getAttributeValue(String name, String def) {
        return this.getAttributeValue(name, Namespace.NO_NAMESPACE, def);
    }

    public String getAttributeValue(String name, Namespace ns) {
        return this.getAttributeValue(name, ns, null);
    }

    public String getAttributeValue(String name, Namespace ns, String def) {
        Attribute attribute = (Attribute)this.attributes.get(name, ns);
        if (attribute == null) {
            return def;
        }
        return attribute.getValue();
    }

    public Element setAttributes(Collection newAttributes) {
        this.attributes.clearAndSet(newAttributes);
        return this;
    }

    public Element setAttributes(List newAttributes) {
        return this.setAttributes((Collection)newAttributes);
    }

    public Element setAttribute(String name, String value) {
        Attribute attribute = this.getAttribute(name);
        if (attribute == null) {
            Attribute newAttribute = new Attribute(name, value);
            this.setAttribute(newAttribute);
        } else {
            attribute.setValue(value);
        }
        return this;
    }

    public Element setAttribute(String name, String value, Namespace ns) {
        Attribute attribute = this.getAttribute(name, ns);
        if (attribute == null) {
            Attribute newAttribute = new Attribute(name, value, ns);
            this.setAttribute(newAttribute);
        } else {
            attribute.setValue(value);
        }
        return this;
    }

    public Element setAttribute(Attribute attribute) {
        this.attributes.add(attribute);
        return this;
    }

    public boolean removeAttribute(String name) {
        return this.removeAttribute(name, Namespace.NO_NAMESPACE);
    }

    public boolean removeAttribute(String name, Namespace ns) {
        return this.attributes.remove(name, ns);
    }

    public boolean removeAttribute(Attribute attribute) {
        return this.attributes.remove(attribute);
    }

    public String toString() {
        StringBuffer stringForm = new StringBuffer(64).append("[Element: <").append(this.getQualifiedName());
        String nsuri = this.getNamespaceURI();
        if (!"".equals(nsuri)) {
            stringForm.append(" [Namespace: ").append(nsuri).append("]");
        }
        stringForm.append("/>]");
        return stringForm.toString();
    }

    @Override
    public Object clone() {
        int i;
        Element element = (Element)super.clone();
        element.content = new ContentList(element);
        element.attributes = new AttributeList(element);
        if (this.attributes != null) {
            for (i = 0; i < this.attributes.size(); ++i) {
                Attribute attribute = (Attribute)this.attributes.get(i);
                element.attributes.add(attribute.clone());
            }
        }
        if (this.additionalNamespaces != null) {
            element.additionalNamespaces = new ArrayList(this.additionalNamespaces);
        }
        if (this.content != null) {
            for (i = 0; i < this.content.size(); ++i) {
                Content c = (Content)this.content.get(i);
                element.content.add(c.clone());
            }
        }
        return element;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.namespace.getPrefix());
        out.writeObject(this.namespace.getURI());
        if (this.additionalNamespaces == null) {
            out.write(0);
        } else {
            int size = this.additionalNamespaces.size();
            out.write(size);
            for (int i = 0; i < size; ++i) {
                Namespace additional = (Namespace)this.additionalNamespaces.get(i);
                out.writeObject(additional.getPrefix());
                out.writeObject(additional.getURI());
            }
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.namespace = Namespace.getNamespace((String)in.readObject(), (String)in.readObject());
        int size = in.read();
        if (size != 0) {
            this.additionalNamespaces = new ArrayList(size);
            for (int i = 0; i < size; ++i) {
                Namespace additional = Namespace.getNamespace((String)in.readObject(), (String)in.readObject());
                this.additionalNamespaces.add(additional);
            }
        }
    }

    @Override
    public Iterator getDescendants() {
        return new DescendantIterator(this);
    }

    @Override
    public Iterator getDescendants(Filter filter) {
        DescendantIterator iterator = new DescendantIterator(this);
        return new FilterIterator(iterator, filter);
    }

    public List getChildren() {
        return this.content.getView(new ElementFilter());
    }

    public List getChildren(String name) {
        return this.getChildren(name, Namespace.NO_NAMESPACE);
    }

    public List getChildren(String name, Namespace ns) {
        return this.content.getView(new ElementFilter(name, ns));
    }

    public Element getChild(String name, Namespace ns) {
        List elements = this.content.getView(new ElementFilter(name, ns));
        Iterator iter = elements.iterator();
        if (iter.hasNext()) {
            return (Element)iter.next();
        }
        return null;
    }

    public Element getChild(String name) {
        return this.getChild(name, Namespace.NO_NAMESPACE);
    }

    public boolean removeChild(String name) {
        return this.removeChild(name, Namespace.NO_NAMESPACE);
    }

    public boolean removeChild(String name, Namespace ns) {
        ElementFilter filter = new ElementFilter(name, ns);
        List old = this.content.getView(filter);
        Iterator iter = old.iterator();
        if (iter.hasNext()) {
            iter.next();
            iter.remove();
            return true;
        }
        return false;
    }

    public boolean removeChildren(String name) {
        return this.removeChildren(name, Namespace.NO_NAMESPACE);
    }

    public boolean removeChildren(String name, Namespace ns) {
        boolean deletedSome = false;
        ElementFilter filter = new ElementFilter(name, ns);
        List old = this.content.getView(filter);
        Iterator iter = old.iterator();
        while (iter.hasNext()) {
            iter.next();
            iter.remove();
            deletedSome = true;
        }
        return deletedSome;
    }
}

