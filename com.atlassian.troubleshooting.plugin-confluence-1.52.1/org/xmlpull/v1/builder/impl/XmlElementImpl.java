/*
 * Decompiled with CFR 0.152.
 */
package org.xmlpull.v1.builder.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.xmlpull.v1.builder.Iterable;
import org.xmlpull.v1.builder.XmlAttribute;
import org.xmlpull.v1.builder.XmlBuilderException;
import org.xmlpull.v1.builder.XmlCharacters;
import org.xmlpull.v1.builder.XmlContained;
import org.xmlpull.v1.builder.XmlContainer;
import org.xmlpull.v1.builder.XmlDocument;
import org.xmlpull.v1.builder.XmlElement;
import org.xmlpull.v1.builder.XmlNamespace;
import org.xmlpull.v1.builder.impl.XmlAttributeImpl;
import org.xmlpull.v1.builder.impl.XmlNamespaceImpl;

public class XmlElementImpl
implements XmlElement {
    private XmlContainer parent;
    private XmlNamespace namespace;
    private String name;
    private List attrs;
    private List nsList;
    private List children;
    private static final Iterator EMPTY_ITERATOR = new EmptyIterator();
    private static final Iterable EMPTY_ITERABLE = new Iterable(){

        public Iterator iterator() {
            return EMPTY_ITERATOR;
        }
    };

    public Object clone() throws CloneNotSupportedException {
        XmlElementImpl cloned = (XmlElementImpl)super.clone();
        cloned.parent = null;
        cloned.attrs = this.cloneList(cloned, this.attrs);
        cloned.nsList = this.cloneList(cloned, this.nsList);
        cloned.children = this.cloneList(cloned, this.children);
        if (cloned.children != null) {
            for (int i = 0; i < cloned.children.size(); ++i) {
                XmlContained contained;
                Object member = cloned.children.get(i);
                if (!(member instanceof XmlContained) || (contained = (XmlContained)member).getParent() != this) continue;
                contained.setParent(null);
                contained.setParent(cloned);
            }
        }
        return cloned;
    }

    private List cloneList(XmlElementImpl cloned, List list) throws CloneNotSupportedException {
        if (list == null) {
            return null;
        }
        ArrayList newList = new ArrayList(list.size());
        for (int i = 0; i < list.size(); ++i) {
            Object newMember;
            Object member = list.get(i);
            if (member instanceof XmlNamespace || member instanceof String) {
                newMember = member;
            } else if (member instanceof XmlElement) {
                XmlElement el = (XmlElement)member;
                newMember = el.clone();
            } else if (member instanceof XmlAttribute) {
                XmlAttribute attr = (XmlAttribute)member;
                newMember = new XmlAttributeImpl(cloned, attr.getType(), attr.getNamespace(), attr.getName(), attr.getValue(), attr.isSpecified());
            } else if (member instanceof Cloneable) {
                try {
                    newMember = member.getClass().getMethod("clone", null).invoke(member, null);
                }
                catch (Exception e) {
                    throw new CloneNotSupportedException("failed to call clone() on  " + member + e);
                }
            } else {
                throw new CloneNotSupportedException();
            }
            newList.add(newMember);
        }
        return newList;
    }

    XmlElementImpl(String name) {
        this.name = name;
    }

    XmlElementImpl(XmlNamespace namespace, String name) {
        this.namespace = namespace;
        this.name = name;
    }

    XmlElementImpl(String namespaceName, String name) {
        if (namespaceName != null) {
            this.namespace = new XmlNamespaceImpl(null, namespaceName);
        }
        this.name = name;
    }

    public XmlContainer getRoot() {
        XmlElement el;
        XmlContainer root = this;
        while (root instanceof XmlElement && (el = (XmlElement)root).getParent() != null) {
            root = el.getParent();
        }
        return root;
    }

    public XmlContainer getParent() {
        return this.parent;
    }

    public void setParent(XmlContainer parent) {
        XmlDocument doc;
        if (parent != null && parent instanceof XmlDocument && (doc = (XmlDocument)parent).getDocumentElement() != this) {
            throw new XmlBuilderException("this element must be root document element to have document set as parent but already different element is set as root document element");
        }
        this.parent = parent;
    }

    public XmlNamespace getNamespace() {
        return this.namespace;
    }

    public String getNamespaceName() {
        return this.namespace != null ? this.namespace.getNamespaceName() : null;
    }

    public void setNamespace(XmlNamespace namespace) {
        this.namespace = namespace;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return "name[" + this.name + "]" + (this.namespace != null ? " namespace[" + this.namespace.getNamespaceName() + "]" : "");
    }

    public String getBaseUri() {
        throw new XmlBuilderException("not implemented");
    }

    public void setBaseUri(String baseUri) {
        throw new XmlBuilderException("not implemented");
    }

    public Iterator attributes() {
        if (this.attrs == null) {
            return EMPTY_ITERATOR;
        }
        return this.attrs.iterator();
    }

    public XmlAttribute addAttribute(XmlAttribute attributeValueToAdd) {
        if (this.attrs == null) {
            this.ensureAttributeCapacity(5);
        }
        this.attrs.add(attributeValueToAdd);
        return attributeValueToAdd;
    }

    public XmlAttribute addAttribute(XmlNamespace namespace, String name, String value) {
        return this.addAttribute("CDATA", namespace, name, value, false);
    }

    public XmlAttribute addAttribute(String name, String value) {
        return this.addAttribute("CDATA", null, name, value, false);
    }

    public XmlAttribute addAttribute(String attributeType, XmlNamespace namespace, String name, String value) {
        return this.addAttribute(attributeType, namespace, name, value, false);
    }

    public XmlAttribute addAttribute(String attributeType, XmlNamespace namespace, String name, String value, boolean specified) {
        XmlAttributeImpl a = new XmlAttributeImpl(this, attributeType, namespace, name, value, specified);
        return this.addAttribute(a);
    }

    public XmlAttribute addAttribute(String attributeType, String attributePrefix, String attributeNamespace, String attributeName, String attributeValue, boolean specified) {
        XmlNamespace n = this.newNamespace(attributePrefix, attributeNamespace);
        return this.addAttribute(attributeType, n, attributeName, attributeValue, specified);
    }

    public void ensureAttributeCapacity(int minCapacity) {
        if (this.attrs == null) {
            this.attrs = new ArrayList(minCapacity);
        } else {
            ((ArrayList)this.attrs).ensureCapacity(minCapacity);
        }
    }

    public String getAttributeValue(String attributeNamespaceName, String attributeName) {
        XmlAttribute xat = this.findAttribute(attributeNamespaceName, attributeName);
        if (xat != null) {
            return xat.getValue();
        }
        return null;
    }

    public boolean hasAttributes() {
        return this.attrs != null && this.attrs.size() > 0;
    }

    public XmlAttribute attribute(String attributeName) {
        return this.attribute(null, attributeName);
    }

    public XmlAttribute attribute(XmlNamespace attributeNamespace, String attributeName) {
        return this.findAttribute(attributeNamespace != null ? attributeNamespace.getNamespaceName() : null, attributeName);
    }

    public XmlAttribute findAttribute(String attributeNamespace, String attributeName) {
        if (attributeName == null) {
            throw new IllegalArgumentException("attribute name ca not ber null");
        }
        if (this.attrs == null) {
            return null;
        }
        int length = this.attrs.size();
        for (int i = 0; i < length; ++i) {
            XmlAttribute a = (XmlAttribute)this.attrs.get(i);
            String aName = a.getName();
            if (aName != attributeName && !attributeName.equals(aName)) continue;
            if (attributeNamespace != null) {
                String aNamespace = a.getNamespaceName();
                if (attributeNamespace.equals(aNamespace)) {
                    return a;
                }
                if (attributeNamespace != "" || aNamespace != null) continue;
                return a;
            }
            if (a.getNamespace() == null) {
                return a;
            }
            if (a.getNamespace().getNamespaceName() != "") continue;
            return a;
        }
        return null;
    }

    public void removeAllAttributes() {
        this.attrs = null;
    }

    public void removeAttribute(XmlAttribute attr) {
        if (this.attrs == null) {
            throw new XmlBuilderException("this element has no attributes to remove");
        }
        for (int i = 0; i < this.attrs.size(); ++i) {
            if (!this.attrs.get(i).equals(attr)) continue;
            this.attrs.remove(i);
            break;
        }
    }

    public XmlNamespace declareNamespace(String prefix, String namespaceName) {
        if (prefix == null) {
            throw new XmlBuilderException("namespace added to element must have not null prefix");
        }
        XmlNamespace n = this.newNamespace(prefix, namespaceName);
        return this.declareNamespace(n);
    }

    public XmlNamespace declareNamespace(XmlNamespace n) {
        if (n.getPrefix() == null) {
            throw new XmlBuilderException("namespace added to element must have not null prefix");
        }
        if (this.nsList == null) {
            this.ensureNamespaceDeclarationsCapacity(5);
        }
        this.nsList.add(n);
        return n;
    }

    public boolean hasNamespaceDeclarations() {
        return this.nsList != null && this.nsList.size() > 0;
    }

    public XmlNamespace lookupNamespaceByPrefix(String namespacePrefix) {
        if (namespacePrefix == null) {
            throw new IllegalArgumentException("namespace prefix can not be null");
        }
        if (this.hasNamespaceDeclarations()) {
            int length = this.nsList.size();
            for (int i = 0; i < length; ++i) {
                XmlNamespace n = (XmlNamespace)this.nsList.get(i);
                if (!namespacePrefix.equals(n.getPrefix())) continue;
                return n;
            }
        }
        if (this.parent != null && this.parent instanceof XmlElement) {
            return ((XmlElement)this.parent).lookupNamespaceByPrefix(namespacePrefix);
        }
        return null;
    }

    public XmlNamespace lookupNamespaceByName(String namespaceName) {
        if (namespaceName == null) {
            throw new IllegalArgumentException("namespace name can not ber null");
        }
        if (this.hasNamespaceDeclarations()) {
            int length = this.nsList.size();
            for (int i = 0; i < length; ++i) {
                XmlNamespace n = (XmlNamespace)this.nsList.get(i);
                if (!namespaceName.equals(n.getNamespaceName())) continue;
                return n;
            }
        }
        if (this.parent != null && this.parent instanceof XmlElement) {
            return ((XmlElement)this.parent).lookupNamespaceByName(namespaceName);
        }
        return null;
    }

    public Iterator namespaces() {
        if (this.nsList == null) {
            return EMPTY_ITERATOR;
        }
        return this.nsList.iterator();
    }

    public XmlNamespace newNamespace(String namespaceName) {
        return this.newNamespace(null, namespaceName);
    }

    public XmlNamespace newNamespace(String prefix, String namespaceName) {
        return new XmlNamespaceImpl(prefix, namespaceName);
    }

    public void ensureNamespaceDeclarationsCapacity(int minCapacity) {
        if (this.nsList == null) {
            this.nsList = new ArrayList(minCapacity);
        } else {
            ((ArrayList)this.nsList).ensureCapacity(minCapacity);
        }
    }

    public void removeAllNamespaceDeclarations() {
        this.nsList = null;
    }

    public void addChild(Object child) {
        if (child == null) {
            throw new NullPointerException();
        }
        if (this.children == null) {
            this.ensureChildrenCapacity(1);
        }
        this.children.add(child);
    }

    public void addChild(int index, Object child) {
        if (this.children == null) {
            this.ensureChildrenCapacity(1);
        }
        this.children.add(index, child);
    }

    private void checkChildParent(Object child) {
        if (child instanceof XmlContainer) {
            if (child instanceof XmlElement) {
                XmlElement elChild = (XmlElement)child;
                XmlContainer childParent = elChild.getParent();
                if (childParent != null && childParent != this.parent) {
                    throw new XmlBuilderException("child must have no parent to be added to this node");
                }
            } else if (child instanceof XmlDocument) {
                throw new XmlBuilderException("docuemet can not be stored as element child");
            }
        }
    }

    private void setChildParent(Object child) {
        if (child instanceof XmlElement) {
            XmlElement elChild = (XmlElement)child;
            elChild.setParent(this);
        }
    }

    public XmlElement addElement(XmlElement element) {
        this.checkChildParent(element);
        this.addChild(element);
        this.setChildParent(element);
        return element;
    }

    public XmlElement addElement(int pos, XmlElement element) {
        this.checkChildParent(element);
        this.addChild(pos, element);
        this.setChildParent(element);
        return element;
    }

    public XmlElement addElement(XmlNamespace namespace, String name) {
        XmlElement el = this.newElement(namespace, name);
        this.addChild(el);
        this.setChildParent(el);
        return el;
    }

    public XmlElement addElement(String name) {
        return this.addElement(null, name);
    }

    public Iterator children() {
        if (this.children == null) {
            return EMPTY_ITERATOR;
        }
        return this.children.iterator();
    }

    public Iterable requiredElementContent() {
        if (this.children == null) {
            return EMPTY_ITERABLE;
        }
        return new Iterable(){

            public Iterator iterator() {
                return new RequiredElementContentIterator(XmlElementImpl.this.children.iterator());
            }
        };
    }

    public String requiredTextContent() {
        if (this.children == null) {
            return "";
        }
        if (this.children.size() == 0) {
            return "";
        }
        if (this.children.size() == 1) {
            Object child = this.children.get(0);
            if (child instanceof String) {
                return child.toString();
            }
            if (child instanceof XmlCharacters) {
                return ((XmlCharacters)child).getText();
            }
            throw new XmlBuilderException("expected text content and not " + (child != null ? child.getClass() : null) + " with '" + child + "'");
        }
        Iterator i = this.children();
        StringBuffer buf = new StringBuffer();
        while (i.hasNext()) {
            Object child = i.next();
            if (child instanceof String) {
                buf.append(child.toString());
                continue;
            }
            if (child instanceof XmlCharacters) {
                buf.append(((XmlCharacters)child).getText());
                continue;
            }
            throw new XmlBuilderException("expected text content and not " + child.getClass() + " with '" + child + "'");
        }
        return buf.toString();
    }

    public void ensureChildrenCapacity(int minCapacity) {
        if (this.children == null) {
            this.children = new ArrayList(minCapacity);
        } else {
            ((ArrayList)this.children).ensureCapacity(minCapacity);
        }
    }

    public XmlElement element(int position) {
        if (this.children == null) {
            return null;
        }
        int length = this.children.size();
        int count = 0;
        if (position >= 0 && position < length + 1) {
            for (int pos = 0; pos < length; ++pos) {
                Object child = this.children.get(pos);
                if (!(child instanceof XmlElement) || ++count != position) continue;
                return (XmlElement)child;
            }
        } else {
            throw new IndexOutOfBoundsException("position " + position + " bigger or equal to " + length + " children");
        }
        throw new IndexOutOfBoundsException("position " + position + " too big as only " + count + " element(s) available");
    }

    public XmlElement requiredElement(XmlNamespace n, String name) throws XmlBuilderException {
        XmlElement el = this.element(n, name);
        if (el == null) {
            throw new XmlBuilderException("could not find element with name " + name + " in namespace " + (n != null ? n.getNamespaceName() : null));
        }
        return el;
    }

    public XmlElement element(XmlNamespace n, String name) {
        return this.element(n, name, false);
    }

    public XmlElement element(XmlNamespace n, String name, boolean create) {
        XmlElement e;
        XmlElement xmlElement = e = n != null ? this.findElementByName(n.getNamespaceName(), name) : this.findElementByName(name);
        if (e != null) {
            return e;
        }
        if (create) {
            return this.addElement(n, name);
        }
        return null;
    }

    public Iterable elements(final XmlNamespace n, final String name) {
        return new Iterable(){

            public Iterator iterator() {
                return new ElementsSimpleIterator(n, name, XmlElementImpl.this.children());
            }
        };
    }

    public XmlElement findElementByName(String name) {
        if (this.children == null) {
            return null;
        }
        int length = this.children.size();
        for (int i = 0; i < length; ++i) {
            XmlElement childEl;
            Object child = this.children.get(i);
            if (!(child instanceof XmlElement) || !name.equals((childEl = (XmlElement)child).getName())) continue;
            return childEl;
        }
        return null;
    }

    public XmlElement findElementByName(String namespaceName, String name, XmlElement elementToStartLooking) {
        throw new UnsupportedOperationException();
    }

    public XmlElement findElementByName(String name, XmlElement elementToStartLooking) {
        throw new UnsupportedOperationException();
    }

    public XmlElement findElementByName(String namespaceName, String name) {
        if (this.children == null) {
            return null;
        }
        int length = this.children.size();
        for (int i = 0; i < length; ++i) {
            XmlElement childEl;
            XmlNamespace namespace;
            Object child = this.children.get(i);
            if (!(child instanceof XmlElement) || !((namespace = (childEl = (XmlElement)child).getNamespace()) != null ? name.equals(childEl.getName()) && namespaceName.equals(namespace.getNamespaceName()) : name.equals(childEl.getName()) && namespaceName == null)) continue;
            return childEl;
        }
        return null;
    }

    public boolean hasChild(Object child) {
        if (this.children == null) {
            return false;
        }
        for (int i = 0; i < this.children.size(); ++i) {
            if (this.children.get(i) != child) continue;
            return true;
        }
        return false;
    }

    public boolean hasChildren() {
        return this.children != null && this.children.size() > 0;
    }

    public void insertChild(int pos, Object childToInsert) {
        if (this.children == null) {
            this.ensureChildrenCapacity(1);
        }
        this.children.add(pos, childToInsert);
    }

    public XmlElement newElement(String name) {
        return this.newElement((XmlNamespace)null, name);
    }

    public XmlElement newElement(String namespace, String name) {
        return new XmlElementImpl(namespace, name);
    }

    public XmlElement newElement(XmlNamespace namespace, String name) {
        return new XmlElementImpl(namespace, name);
    }

    public void replaceChild(Object newChild, Object oldChild) {
        if (newChild == null) {
            throw new IllegalArgumentException("new child to replace can not be null");
        }
        if (oldChild == null) {
            throw new IllegalArgumentException("old child to replace can not be null");
        }
        if (!this.hasChildren()) {
            throw new XmlBuilderException("no children available for replacement");
        }
        int pos = this.children.indexOf(oldChild);
        if (pos == -1) {
            throw new XmlBuilderException("could not find child to replace");
        }
        this.children.set(pos, newChild);
    }

    public void removeAllChildren() {
        this.children = null;
    }

    public void removeChild(Object child) {
        if (child == null) {
            throw new IllegalArgumentException("child to remove can not be null");
        }
        if (!this.hasChildren()) {
            throw new XmlBuilderException("no children to remove");
        }
        int pos = this.children.indexOf(child);
        if (pos != -1) {
            this.children.remove(pos);
        }
    }

    public void replaceChildrenWithText(String textContent) {
        this.removeAllChildren();
        this.addChild(textContent);
    }

    private static final boolean isWhiteSpace(String txt) {
        for (int i = 0; i < txt.length(); ++i) {
            if (txt.charAt(i) == ' ' || txt.charAt(i) == '\n' || txt.charAt(i) == '\t' || txt.charAt(i) == '\r') continue;
            return false;
        }
        return true;
    }

    private static class EmptyIterator
    implements Iterator {
        private EmptyIterator() {
        }

        public boolean hasNext() {
            return false;
        }

        public Object next() {
            throw new XmlBuilderException("this iterator has no content and next() is not allowed");
        }

        public void remove() {
            throw new XmlBuilderException("this iterator has no content and remove() is not allowed");
        }
    }

    private static class RequiredElementContentIterator
    implements Iterator {
        private Iterator children;
        private XmlElement currentEl;

        RequiredElementContentIterator(Iterator children) {
            this.children = children;
            this.findNextEl();
        }

        private void findNextEl() {
            this.currentEl = null;
            while (this.children.hasNext()) {
                Object child = this.children.next();
                if (child instanceof XmlElement) {
                    this.currentEl = (XmlElement)child;
                    break;
                }
                if (child instanceof String) {
                    String s = child.toString();
                    if (XmlElementImpl.isWhiteSpace(s)) continue;
                    throw new XmlBuilderException("only whitespace string children allowed for non mixed element content");
                }
                if (child instanceof XmlCharacters) {
                    XmlCharacters xc = (XmlCharacters)child;
                    if (Boolean.TRUE.equals(xc.isWhitespaceContent()) && XmlElementImpl.isWhiteSpace(xc.getText())) continue;
                    throw new XmlBuilderException("only whitespace characters children allowed for non mixed element content");
                }
                throw new XmlBuilderException("only whitespace characters and element children allowed for non mixed element content and not " + child.getClass());
            }
        }

        public boolean hasNext() {
            return this.currentEl != null;
        }

        public Object next() {
            if (this.currentEl == null) {
                throw new XmlBuilderException("this iterator has no content and next() is not allowed");
            }
            XmlElement el = this.currentEl;
            this.findNextEl();
            return el;
        }

        public void remove() {
            throw new XmlBuilderException("this iterator does nto support remove()");
        }
    }

    private class ElementsSimpleIterator
    implements Iterator {
        private Iterator children;
        private XmlElement currentEl;
        private XmlNamespace n;
        private String name;

        ElementsSimpleIterator(XmlNamespace n, String name, Iterator children) {
            this.children = children;
            this.n = n;
            this.name = name;
            this.findNextEl();
        }

        private void findNextEl() {
            this.currentEl = null;
            while (this.children.hasNext()) {
                Object child = this.children.next();
                if (!(child instanceof XmlElement)) continue;
                XmlElement el = (XmlElement)child;
                if (this.name != null && el.getName() != this.name && !this.name.equals(el.getName()) || this.n != null && el.getNamespace() != this.n && !this.n.equals(el.getNamespace())) continue;
                this.currentEl = el;
                break;
            }
        }

        public boolean hasNext() {
            return this.currentEl != null;
        }

        public Object next() {
            if (this.currentEl == null) {
                throw new XmlBuilderException("this iterator has no content and next() is not allowed");
            }
            XmlElement el = this.currentEl;
            this.findNextEl();
            return el;
        }

        public void remove() {
            throw new XmlBuilderException("this element iterator does nto support remove()");
        }
    }
}

