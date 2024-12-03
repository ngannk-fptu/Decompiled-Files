/*
 * Decompiled with CFR 0.152.
 */
package org.xmlpull.v1.builder.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.xmlpull.v1.builder.Iterable;
import org.xmlpull.v1.builder.XmlBuilderException;
import org.xmlpull.v1.builder.XmlComment;
import org.xmlpull.v1.builder.XmlDoctype;
import org.xmlpull.v1.builder.XmlDocument;
import org.xmlpull.v1.builder.XmlElement;
import org.xmlpull.v1.builder.XmlNamespace;
import org.xmlpull.v1.builder.XmlNotation;
import org.xmlpull.v1.builder.XmlProcessingInstruction;
import org.xmlpull.v1.builder.impl.XmlCommentImpl;
import org.xmlpull.v1.builder.impl.XmlElementImpl;

public class XmlDocumentImpl
implements XmlDocument {
    private List children = new ArrayList();
    private XmlElement root;
    private String version;
    private Boolean standalone;
    private String characterEncoding;

    public Object clone() throws CloneNotSupportedException {
        XmlDocumentImpl cloned = (XmlDocumentImpl)super.clone();
        cloned.root = null;
        cloned.children = this.cloneList(cloned, this.children);
        int pos = cloned.findDocumentElement();
        if (pos >= 0) {
            cloned.root = (XmlElement)cloned.children.get(pos);
            cloned.root.setParent(cloned);
        }
        return cloned;
    }

    private List cloneList(XmlDocumentImpl cloned, List list) throws CloneNotSupportedException {
        if (list == null) {
            return null;
        }
        ArrayList<Object> newList = new ArrayList<Object>(list.size());
        for (int i = 0; i < list.size(); ++i) {
            Object newMember;
            Object member = list.get(i);
            if (member instanceof XmlElement) {
                XmlElement el = (XmlElement)member;
                newMember = el.clone();
            } else if (member instanceof Cloneable) {
                try {
                    newMember = member.getClass().getMethod("clone", null).invoke(member, null);
                }
                catch (Exception e) {
                    throw new CloneNotSupportedException("failed to call clone() on  " + member + e);
                }
            } else {
                throw new CloneNotSupportedException("could not clone " + member + " of " + (member != null ? member.getClass().toString() : ""));
            }
            newList.add(newMember);
        }
        return newList;
    }

    public XmlDocumentImpl(String version, Boolean standalone, String characterEncoding) {
        this.version = version;
        this.standalone = standalone;
        this.characterEncoding = characterEncoding;
    }

    public String getVersion() {
        return this.version;
    }

    public Boolean isStandalone() {
        return this.standalone;
    }

    public String getCharacterEncodingScheme() {
        return this.characterEncoding;
    }

    public void setCharacterEncodingScheme(String characterEncoding) {
        this.characterEncoding = characterEncoding;
    }

    public XmlProcessingInstruction newProcessingInstruction(String target, String content) {
        throw new XmlBuilderException("not implemented");
    }

    public XmlProcessingInstruction addProcessingInstruction(String target, String content) {
        throw new XmlBuilderException("not implemented");
    }

    public Iterable children() {
        return new Iterable(){

            public Iterator iterator() {
                return XmlDocumentImpl.this.children.iterator();
            }
        };
    }

    public void removeAllUnparsedEntities() {
        throw new XmlBuilderException("not implemented");
    }

    public void setDocumentElement(XmlElement rootElement) {
        int pos = this.findDocumentElement();
        if (pos >= 0) {
            this.children.set(pos, rootElement);
        } else {
            this.children.add(rootElement);
        }
        this.root = rootElement;
        rootElement.setParent(this);
    }

    private int findDocumentElement() {
        for (int i = 0; i < this.children.size(); ++i) {
            Object element = this.children.get(i);
            if (!(element instanceof XmlElement)) continue;
            return i;
        }
        return -1;
    }

    public XmlElement requiredElement(XmlNamespace n, String name) {
        XmlElement el = this.element(n, name);
        if (el == null) {
            throw new XmlBuilderException("document does not contain element with name " + name + " in namespace " + n.getNamespaceName());
        }
        return el;
    }

    public XmlElement element(XmlNamespace n, String name) {
        return this.element(n, name, false);
    }

    public XmlElement element(XmlNamespace namespace, String name, boolean create) {
        String eNamespaceName;
        XmlElement e = this.getDocumentElement();
        if (e == null) {
            return null;
        }
        String string = eNamespaceName = e.getNamespace() != null ? e.getNamespace().getNamespaceName() : null;
        if (namespace != null ? name.equals(e.getName()) && eNamespaceName != null && eNamespaceName.equals(namespace.getNamespaceName()) : name.equals(e.getName()) && eNamespaceName == null) {
            return e;
        }
        if (create) {
            return this.addDocumentElement(namespace, name);
        }
        return null;
    }

    public void insertChild(int pos, Object child) {
        throw new XmlBuilderException("not implemented");
    }

    public XmlComment addComment(String content) {
        XmlCommentImpl comment = new XmlCommentImpl(this, content);
        this.children.add(comment);
        return comment;
    }

    public XmlDoctype newDoctype(String systemIdentifier, String publicIdentifier) {
        throw new XmlBuilderException("not implemented");
    }

    public Iterable unparsedEntities() {
        throw new XmlBuilderException("not implemented");
    }

    public void removeAllChildren() {
        throw new XmlBuilderException("not implemented");
    }

    public XmlComment newComment(String content) {
        return new XmlCommentImpl(null, content);
    }

    public void removeAllNotations() {
        throw new XmlBuilderException("not implemented");
    }

    public XmlDoctype addDoctype(String systemIdentifier, String publicIdentifier) {
        throw new XmlBuilderException("not implemented");
    }

    public void addChild(Object child) {
        throw new XmlBuilderException("not implemented");
    }

    public XmlNotation addNotation(String name, String systemIdentifier, String publicIdentifier, String declarationBaseUri) {
        throw new XmlBuilderException("not implemented");
    }

    public String getBaseUri() {
        throw new XmlBuilderException("not implemented");
    }

    public Iterable notations() {
        throw new XmlBuilderException("not implemented");
    }

    public XmlElement addDocumentElement(String name) {
        return this.addDocumentElement(null, name);
    }

    public XmlElement addDocumentElement(XmlNamespace namespace, String name) {
        XmlElementImpl el = new XmlElementImpl(namespace, name);
        if (this.getDocumentElement() != null) {
            throw new XmlBuilderException("document already has root element");
        }
        this.setDocumentElement(el);
        return el;
    }

    public boolean isAllDeclarationsProcessed() {
        throw new XmlBuilderException("not implemented");
    }

    public XmlElement getDocumentElement() {
        return this.root;
    }
}

