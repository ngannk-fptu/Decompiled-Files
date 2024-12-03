/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import org.jdom2.Attribute;
import org.jdom2.AttributeList;
import org.jdom2.Content;
import org.jdom2.ContentList;
import org.jdom2.DescendantIterator;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.FilterIterator;
import org.jdom2.IllegalAddException;
import org.jdom2.IllegalNameException;
import org.jdom2.Namespace;
import org.jdom2.Parent;
import org.jdom2.Text;
import org.jdom2.Verifier;
import org.jdom2.filter.ElementFilter;
import org.jdom2.filter.Filter;
import org.jdom2.util.IteratorIterable;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Element
extends Content
implements Parent {
    private static final int INITIAL_ARRAY_SIZE = 5;
    protected String name;
    protected Namespace namespace;
    transient List<Namespace> additionalNamespaces = null;
    transient AttributeList attributes = null;
    transient ContentList content = new ContentList(this);
    private static final long serialVersionUID = 200L;

    protected Element() {
        super(Content.CType.Element);
    }

    public Element(String name, Namespace namespace) {
        super(Content.CType.Element);
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
        if (this.additionalNamespaces != null && (reason = Verifier.checkNamespaceCollision(namespace, this.getAdditionalNamespaces())) != null) {
            throw new IllegalAddException(this, namespace, reason);
        }
        if (this.hasAttributes()) {
            for (Attribute a : this.getAttributes()) {
                String reason2 = Verifier.checkNamespaceCollision(namespace, a);
                if (reason2 == null) continue;
                throw new IllegalAddException(this, namespace, reason2);
            }
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
                Namespace ns = this.additionalNamespaces.get(i);
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
        return this.namespace.getPrefix() + ':' + this.name;
    }

    public boolean addNamespaceDeclaration(Namespace additionalNamespace) {
        if (this.additionalNamespaces == null) {
            this.additionalNamespaces = new ArrayList<Namespace>(5);
        }
        for (Namespace ns : this.additionalNamespaces) {
            if (ns != additionalNamespace) continue;
            return false;
        }
        String reason = Verifier.checkNamespaceCollision(additionalNamespace, this);
        if (reason != null) {
            throw new IllegalAddException(this, additionalNamespace, reason);
        }
        return this.additionalNamespaces.add(additionalNamespace);
    }

    public void removeNamespaceDeclaration(Namespace additionalNamespace) {
        if (this.additionalNamespaces == null) {
            return;
        }
        this.additionalNamespaces.remove(additionalNamespace);
    }

    public List<Namespace> getAdditionalNamespaces() {
        if (this.additionalNamespaces == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(this.additionalNamespaces);
    }

    @Override
    public String getValue() {
        StringBuilder buffer = new StringBuilder();
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
            Content obj = this.content.get(0);
            if (obj instanceof Text) {
                return ((Text)obj).getText();
            }
            return "";
        }
        StringBuilder textContent = new StringBuilder();
        boolean hasText = false;
        for (int i = 0; i < this.content.size(); ++i) {
            Content obj = this.content.get(i);
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

    public String getChildText(String cname) {
        Element child = this.getChild(cname);
        if (child == null) {
            return null;
        }
        return child.getText();
    }

    public String getChildTextTrim(String cname) {
        Element child = this.getChild(cname);
        if (child == null) {
            return null;
        }
        return child.getTextTrim();
    }

    public String getChildTextNormalize(String cname) {
        Element child = this.getChild(cname);
        if (child == null) {
            return null;
        }
        return child.getTextNormalize();
    }

    public String getChildText(String cname, Namespace ns) {
        Element child = this.getChild(cname, ns);
        if (child == null) {
            return null;
        }
        return child.getText();
    }

    public String getChildTextTrim(String cname, Namespace ns) {
        Element child = this.getChild(cname, ns);
        if (child == null) {
            return null;
        }
        return child.getTextTrim();
    }

    public String getChildTextNormalize(String cname, Namespace ns) {
        Element child = this.getChild(cname, ns);
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

    public boolean coalesceText(boolean recursively) {
        Iterator<Content> it = recursively ? this.getDescendants() : this.content.iterator();
        Text tfirst = null;
        boolean changed = false;
        while (it.hasNext()) {
            Content c = it.next();
            if (c.getCType() == Content.CType.Text) {
                Text text = (Text)c;
                if ("".equals(text.getValue())) {
                    it.remove();
                    changed = true;
                    continue;
                }
                if (tfirst == null || tfirst.getParent() != text.getParent()) {
                    tfirst = text;
                    continue;
                }
                tfirst.append(text.getValue());
                it.remove();
                changed = true;
                continue;
            }
            tfirst = null;
        }
        return changed;
    }

    @Override
    public List<Content> getContent() {
        return this.content;
    }

    @Override
    public <E extends Content> List<E> getContent(Filter<E> filter) {
        return this.content.getView(filter);
    }

    @Override
    public List<Content> removeContent() {
        ArrayList<Content> old = new ArrayList<Content>(this.content);
        this.content.clear();
        return old;
    }

    public <F extends Content> List<F> removeContent(Filter<F> filter) {
        ArrayList<Content> old = new ArrayList<Content>();
        Iterator<F> iter = this.content.getView(filter).iterator();
        while (iter.hasNext()) {
            Content child = (Content)iter.next();
            old.add(child);
            iter.remove();
        }
        return old;
    }

    public Element setContent(Collection<? extends Content> newContent) {
        this.content.clearAndSet(newContent);
        return this;
    }

    public Element setContent(int index, Content child) {
        this.content.set(index, child);
        return this;
    }

    public Parent setContent(int index, Collection<? extends Content> newContent) {
        this.content.remove(index);
        this.content.addAll(index, newContent);
        return this;
    }

    public Element addContent(String str) {
        return this.addContent(new Text(str));
    }

    @Override
    public Element addContent(Content child) {
        this.content.add(child);
        return this;
    }

    @Override
    public Element addContent(Collection<? extends Content> newContent) {
        this.content.addAll(newContent);
        return this;
    }

    @Override
    public Element addContent(int index, Content child) {
        this.content.add(index, child);
        return this;
    }

    @Override
    public Element addContent(int index, Collection<? extends Content> newContent) {
        this.content.addAll(index, newContent);
        return this;
    }

    @Override
    public List<Content> cloneContent() {
        int size = this.getContentSize();
        ArrayList<Content> list = new ArrayList<Content>(size);
        for (int i = 0; i < size; ++i) {
            Content child = this.getContent(i);
            list.add(child.clone());
        }
        return list;
    }

    @Override
    public Content getContent(int index) {
        return this.content.get(index);
    }

    @Override
    public boolean removeContent(Content child) {
        return this.content.remove(child);
    }

    @Override
    public Content removeContent(int index) {
        return this.content.remove(index);
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

    public boolean hasAttributes() {
        return this.attributes != null && !this.attributes.isEmpty();
    }

    public boolean hasAdditionalNamespaces() {
        return this.additionalNamespaces != null && !this.additionalNamespaces.isEmpty();
    }

    AttributeList getAttributeList() {
        if (this.attributes == null) {
            this.attributes = new AttributeList(this);
        }
        return this.attributes;
    }

    public List<Attribute> getAttributes() {
        return this.getAttributeList();
    }

    public int getAttributesSize() {
        return this.attributes == null ? 0 : this.attributes.size();
    }

    public Attribute getAttribute(String attname) {
        return this.getAttribute(attname, Namespace.NO_NAMESPACE);
    }

    public Attribute getAttribute(String attname, Namespace ns) {
        if (this.attributes == null) {
            return null;
        }
        return this.getAttributeList().get(attname, ns);
    }

    public String getAttributeValue(String attname) {
        if (this.attributes == null) {
            return null;
        }
        return this.getAttributeValue(attname, Namespace.NO_NAMESPACE);
    }

    public String getAttributeValue(String attname, String def) {
        if (this.attributes == null) {
            return def;
        }
        return this.getAttributeValue(attname, Namespace.NO_NAMESPACE, def);
    }

    public String getAttributeValue(String attname, Namespace ns) {
        if (this.attributes == null) {
            return null;
        }
        return this.getAttributeValue(attname, ns, null);
    }

    public String getAttributeValue(String attname, Namespace ns, String def) {
        if (this.attributes == null) {
            return def;
        }
        Attribute attribute = this.getAttributeList().get(attname, ns);
        if (attribute == null) {
            return def;
        }
        return attribute.getValue();
    }

    public Element setAttributes(Collection<? extends Attribute> newAttributes) {
        this.getAttributeList().clearAndSet(newAttributes);
        return this;
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
        this.getAttributeList().add(attribute);
        return this;
    }

    public boolean removeAttribute(String attname) {
        return this.removeAttribute(attname, Namespace.NO_NAMESPACE);
    }

    public boolean removeAttribute(String attname, Namespace ns) {
        if (this.attributes == null) {
            return false;
        }
        return this.getAttributeList().remove(attname, ns);
    }

    public boolean removeAttribute(Attribute attribute) {
        if (this.attributes == null) {
            return false;
        }
        return this.getAttributeList().remove(attribute);
    }

    public String toString() {
        StringBuilder stringForm = new StringBuilder(64).append("[Element: <").append(this.getQualifiedName());
        String nsuri = this.getNamespaceURI();
        if (!"".equals(nsuri)) {
            stringForm.append(" [Namespace: ").append(nsuri).append("]");
        }
        stringForm.append("/>]");
        return stringForm.toString();
    }

    @Override
    public Element clone() {
        int i;
        Element element = (Element)super.clone();
        element.content = new ContentList(element);
        AttributeList attributeList = element.attributes = this.attributes == null ? null : new AttributeList(element);
        if (this.attributes != null) {
            for (i = 0; i < this.attributes.size(); ++i) {
                Attribute attribute = this.attributes.get(i);
                element.attributes.add(attribute.clone());
            }
        }
        if (this.additionalNamespaces != null) {
            element.additionalNamespaces = new ArrayList<Namespace>(this.additionalNamespaces);
        }
        for (i = 0; i < this.content.size(); ++i) {
            Content c = this.content.get(i);
            element.content.add(c.clone());
        }
        return element;
    }

    @Override
    public IteratorIterable<Content> getDescendants() {
        return new DescendantIterator(this);
    }

    public <F extends Content> IteratorIterable<F> getDescendants(Filter<F> filter) {
        return new FilterIterator<F>(new DescendantIterator(this), filter);
    }

    public List<Element> getChildren() {
        return this.content.getView(new ElementFilter());
    }

    public List<Element> getChildren(String cname) {
        return this.getChildren(cname, Namespace.NO_NAMESPACE);
    }

    public List<Element> getChildren(String cname, Namespace ns) {
        return this.content.getView(new ElementFilter(cname, ns));
    }

    public Element getChild(String cname, Namespace ns) {
        List<Element> elements = this.content.getView(new ElementFilter(cname, ns));
        Iterator<Element> iter = elements.iterator();
        if (iter.hasNext()) {
            return iter.next();
        }
        return null;
    }

    public Element getChild(String cname) {
        return this.getChild(cname, Namespace.NO_NAMESPACE);
    }

    public boolean removeChild(String cname) {
        return this.removeChild(cname, Namespace.NO_NAMESPACE);
    }

    public boolean removeChild(String cname, Namespace ns) {
        ElementFilter filter = new ElementFilter(cname, ns);
        List<Element> old = this.content.getView(filter);
        Iterator<Element> iter = old.iterator();
        if (iter.hasNext()) {
            iter.next();
            iter.remove();
            return true;
        }
        return false;
    }

    public boolean removeChildren(String cname) {
        return this.removeChildren(cname, Namespace.NO_NAMESPACE);
    }

    public boolean removeChildren(String cname, Namespace ns) {
        boolean deletedSome = false;
        ElementFilter filter = new ElementFilter(cname, ns);
        List<Element> old = this.content.getView(filter);
        Iterator<Element> iter = old.iterator();
        while (iter.hasNext()) {
            iter.next();
            iter.remove();
            deletedSome = true;
        }
        return deletedSome;
    }

    @Override
    public List<Namespace> getNamespacesInScope() {
        Element pnt;
        TreeMap<String, Namespace> namespaces = new TreeMap<String, Namespace>();
        namespaces.put(Namespace.XML_NAMESPACE.getPrefix(), Namespace.XML_NAMESPACE);
        namespaces.put(this.getNamespacePrefix(), this.getNamespace());
        if (this.additionalNamespaces != null) {
            for (Namespace namespace : this.getAdditionalNamespaces()) {
                if (namespaces.containsKey(namespace.getPrefix())) continue;
                namespaces.put(namespace.getPrefix(), namespace);
            }
        }
        if (this.attributes != null) {
            for (Attribute attribute : this.getAttributes()) {
                Namespace ns = attribute.getNamespace();
                if (Namespace.NO_NAMESPACE.equals(ns) || namespaces.containsKey(ns.getPrefix())) continue;
                namespaces.put(ns.getPrefix(), ns);
            }
        }
        if ((pnt = this.getParentElement()) != null) {
            for (Namespace ns : pnt.getNamespacesInScope()) {
                if (namespaces.containsKey(ns.getPrefix())) continue;
                namespaces.put(ns.getPrefix(), ns);
            }
        }
        if (pnt == null && !namespaces.containsKey("")) {
            namespaces.put(Namespace.NO_NAMESPACE.getPrefix(), Namespace.NO_NAMESPACE);
        }
        ArrayList<Namespace> arrayList = new ArrayList<Namespace>(namespaces.size());
        arrayList.add(this.getNamespace());
        namespaces.remove(this.getNamespacePrefix());
        arrayList.addAll(namespaces.values());
        return Collections.unmodifiableList(arrayList);
    }

    @Override
    public List<Namespace> getNamespacesInherited() {
        if (this.getParentElement() == null) {
            ArrayList<Namespace> ret = new ArrayList<Namespace>(this.getNamespacesInScope());
            Iterator<Namespace> it = ret.iterator();
            while (it.hasNext()) {
                Namespace ns = it.next();
                if (ns == Namespace.NO_NAMESPACE || ns == Namespace.XML_NAMESPACE) continue;
                it.remove();
            }
            return Collections.unmodifiableList(ret);
        }
        HashMap<String, Namespace> parents = new HashMap<String, Namespace>();
        for (Namespace ns : this.getParentElement().getNamespacesInScope()) {
            parents.put(ns.getPrefix(), ns);
        }
        ArrayList<Namespace> al = new ArrayList<Namespace>();
        for (Namespace ns : this.getNamespacesInScope()) {
            if (ns != parents.get(ns.getPrefix())) continue;
            al.add(ns);
        }
        return Collections.unmodifiableList(al);
    }

    @Override
    public List<Namespace> getNamespacesIntroduced() {
        if (this.getParentElement() == null) {
            ArrayList<Namespace> ret = new ArrayList<Namespace>(this.getNamespacesInScope());
            Iterator it = ret.iterator();
            while (it.hasNext()) {
                Namespace ns = (Namespace)it.next();
                if (ns != Namespace.XML_NAMESPACE && ns != Namespace.NO_NAMESPACE) continue;
                it.remove();
            }
            return Collections.unmodifiableList(ret);
        }
        HashMap<String, Namespace> parents = new HashMap<String, Namespace>();
        for (Namespace ns : this.getParentElement().getNamespacesInScope()) {
            parents.put(ns.getPrefix(), ns);
        }
        ArrayList<Namespace> al = new ArrayList<Namespace>();
        for (Namespace ns : this.getNamespacesInScope()) {
            if (parents.containsKey(ns.getPrefix()) && ns == parents.get(ns.getPrefix())) continue;
            al.add(ns);
        }
        return Collections.unmodifiableList(al);
    }

    @Override
    public Element detach() {
        return (Element)super.detach();
    }

    @Override
    public void canContainContent(Content child, int index, boolean replace) throws IllegalAddException {
        if (child instanceof DocType) {
            throw new IllegalAddException("A DocType is not allowed except at the document level");
        }
    }

    public void sortContent(Comparator<? super Content> comparator) {
        this.content.sort(comparator);
    }

    public void sortChildren(Comparator<? super Element> comparator) {
        ((ContentList.FilterList)this.getChildren()).sort(comparator);
    }

    public void sortAttributes(Comparator<? super Attribute> comparator) {
        if (this.attributes != null) {
            this.attributes.sort(comparator);
        }
    }

    public <E extends Content> void sortContent(Filter<E> filter, Comparator<? super E> comparator) {
        ContentList.FilterList list = (ContentList.FilterList)this.getContent(filter);
        list.sort(comparator);
    }

    private final URI resolve(String uri, URI relative) throws URISyntaxException {
        if (uri == null) {
            return relative;
        }
        URI n = new URI(uri);
        if (relative == null) {
            return n;
        }
        return n.resolve(relative);
    }

    public URI getXMLBaseURI() throws URISyntaxException {
        URI ret = null;
        for (Parent p = this; p != null; p = p.getParent()) {
            ret = p instanceof Element ? this.resolve(p.getAttributeValue("base", Namespace.XML_NAMESPACE), ret) : this.resolve(((Document)p).getBaseURI(), ret);
            if (ret == null || !ret.isAbsolute()) continue;
            return ret;
        }
        return ret;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        int i;
        int ans;
        out.defaultWriteObject();
        if (this.hasAdditionalNamespaces()) {
            ans = this.additionalNamespaces.size();
            out.writeInt(ans);
            for (i = 0; i < ans; ++i) {
                out.writeObject(this.additionalNamespaces.get(i));
            }
        } else {
            out.writeInt(0);
        }
        if (this.hasAttributes()) {
            ans = this.attributes.size();
            out.writeInt(ans);
            for (i = 0; i < ans; ++i) {
                out.writeObject(this.attributes.get(i));
            }
        } else {
            out.writeInt(0);
        }
        int cs = this.content.size();
        out.writeInt(cs);
        for (i = 0; i < cs; ++i) {
            out.writeObject(this.content.get(i));
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.content = new ContentList(this);
        int nss = in.readInt();
        while (--nss >= 0) {
            this.addNamespaceDeclaration((Namespace)in.readObject());
        }
        int ats = in.readInt();
        while (--ats >= 0) {
            this.setAttribute((Attribute)in.readObject());
        }
        int cs = in.readInt();
        while (--cs >= 0) {
            this.addContent((Content)in.readObject());
        }
    }
}

