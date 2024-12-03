/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.jdom2.CDATA;
import org.jdom2.CloneBase;
import org.jdom2.Comment;
import org.jdom2.Content;
import org.jdom2.ContentList;
import org.jdom2.DescendantIterator;
import org.jdom2.DocType;
import org.jdom2.Element;
import org.jdom2.EntityRef;
import org.jdom2.FilterIterator;
import org.jdom2.IllegalAddException;
import org.jdom2.Namespace;
import org.jdom2.Parent;
import org.jdom2.ProcessingInstruction;
import org.jdom2.Text;
import org.jdom2.Verifier;
import org.jdom2.filter.Filter;
import org.jdom2.util.IteratorIterable;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Document
extends CloneBase
implements Parent {
    transient ContentList content = new ContentList(this);
    protected String baseURI = null;
    private transient HashMap<String, Object> propertyMap = null;
    private static final long serialVersionUID = 200L;

    public Document() {
    }

    public Document(Element rootElement, DocType docType, String baseURI) {
        if (rootElement != null) {
            this.setRootElement(rootElement);
        }
        if (docType != null) {
            this.setDocType(docType);
        }
        if (baseURI != null) {
            this.setBaseURI(baseURI);
        }
    }

    public Document(Element rootElement, DocType docType) {
        this(rootElement, docType, null);
    }

    public Document(Element rootElement) {
        this(rootElement, null, null);
    }

    public Document(List<? extends Content> content) {
        this.setContent(content);
    }

    @Override
    public int getContentSize() {
        return this.content.size();
    }

    @Override
    public int indexOf(Content child) {
        return this.content.indexOf(child);
    }

    public boolean hasRootElement() {
        return this.content.indexOfFirstElement() >= 0;
    }

    public Element getRootElement() {
        int index = this.content.indexOfFirstElement();
        if (index < 0) {
            throw new IllegalStateException("Root element not set");
        }
        return (Element)this.content.get(index);
    }

    public Document setRootElement(Element rootElement) {
        int index = this.content.indexOfFirstElement();
        if (index < 0) {
            this.content.add(rootElement);
        } else {
            this.content.set(index, rootElement);
        }
        return this;
    }

    public Element detachRootElement() {
        int index = this.content.indexOfFirstElement();
        if (index < 0) {
            return null;
        }
        return (Element)this.removeContent(index);
    }

    public DocType getDocType() {
        int index = this.content.indexOfDocType();
        if (index < 0) {
            return null;
        }
        return (DocType)this.content.get(index);
    }

    public Document setDocType(DocType docType) {
        if (docType == null) {
            int docTypeIndex = this.content.indexOfDocType();
            if (docTypeIndex >= 0) {
                this.content.remove(docTypeIndex);
            }
            return this;
        }
        if (docType.getParent() != null) {
            throw new IllegalAddException(docType, "The DocType already is attached to a document");
        }
        int docTypeIndex = this.content.indexOfDocType();
        if (docTypeIndex < 0) {
            this.content.add(0, docType);
        } else {
            this.content.set(docTypeIndex, docType);
        }
        return this;
    }

    @Override
    public Document addContent(Content child) {
        this.content.add(child);
        return this;
    }

    @Override
    public Document addContent(Collection<? extends Content> c) {
        this.content.addAll(c);
        return this;
    }

    @Override
    public Document addContent(int index, Content child) {
        this.content.add(index, child);
        return this;
    }

    @Override
    public Document addContent(int index, Collection<? extends Content> c) {
        this.content.addAll(index, c);
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
    public List<Content> getContent() {
        if (!this.hasRootElement()) {
            throw new IllegalStateException("Root element not set");
        }
        return this.content;
    }

    public <F extends Content> List<F> getContent(Filter<F> filter) {
        if (!this.hasRootElement()) {
            throw new IllegalStateException("Root element not set");
        }
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
        Iterator<F> itr = this.content.getView(filter).iterator();
        while (itr.hasNext()) {
            Content child = (Content)itr.next();
            old.add(child);
            itr.remove();
        }
        return old;
    }

    public Document setContent(Collection<? extends Content> newContent) {
        this.content.clearAndSet(newContent);
        return this;
    }

    public final void setBaseURI(String uri) {
        this.baseURI = uri;
    }

    public final String getBaseURI() {
        return this.baseURI;
    }

    public Document setContent(int index, Content child) {
        this.content.set(index, child);
        return this;
    }

    public Document setContent(int index, Collection<? extends Content> collection) {
        this.content.remove(index);
        this.content.addAll(index, collection);
        return this;
    }

    @Override
    public boolean removeContent(Content child) {
        return this.content.remove(child);
    }

    @Override
    public Content removeContent(int index) {
        return this.content.remove(index);
    }

    public Document setContent(Content child) {
        this.content.clear();
        this.content.add(child);
        return this;
    }

    public String toString() {
        Element rootElement;
        StringBuilder stringForm = new StringBuilder().append("[Document: ");
        DocType docType = this.getDocType();
        if (docType != null) {
            stringForm.append(docType.toString()).append(", ");
        } else {
            stringForm.append(" No DOCTYPE declaration, ");
        }
        Element element = rootElement = this.hasRootElement() ? this.getRootElement() : null;
        if (rootElement != null) {
            stringForm.append("Root is ").append(rootElement.toString());
        } else {
            stringForm.append(" No root element");
        }
        stringForm.append("]");
        return stringForm.toString();
    }

    public final boolean equals(Object ob) {
        return ob == this;
    }

    public final int hashCode() {
        return super.hashCode();
    }

    @Override
    public Document clone() {
        Document doc = (Document)super.clone();
        doc.content = new ContentList(doc);
        for (int i = 0; i < this.content.size(); ++i) {
            Content obj = this.content.get(i);
            if (obj instanceof Element) {
                Element element = ((Element)obj).clone();
                doc.content.add(element);
                continue;
            }
            if (obj instanceof Comment) {
                Comment comment = ((Comment)obj).clone();
                doc.content.add(comment);
                continue;
            }
            if (obj instanceof ProcessingInstruction) {
                ProcessingInstruction pi = ((ProcessingInstruction)obj).clone();
                doc.content.add(pi);
                continue;
            }
            if (!(obj instanceof DocType)) continue;
            DocType dt = ((DocType)obj).clone();
            doc.content.add(dt);
        }
        return doc;
    }

    @Override
    public IteratorIterable<Content> getDescendants() {
        return new DescendantIterator(this);
    }

    public <F extends Content> IteratorIterable<F> getDescendants(Filter<F> filter) {
        return new FilterIterator<F>(new DescendantIterator(this), filter);
    }

    @Override
    public Parent getParent() {
        return null;
    }

    @Override
    public Document getDocument() {
        return this;
    }

    public void setProperty(String id, Object value) {
        if (this.propertyMap == null) {
            this.propertyMap = new HashMap();
        }
        this.propertyMap.put(id, value);
    }

    public Object getProperty(String id) {
        if (this.propertyMap == null) {
            return null;
        }
        return this.propertyMap.get(id);
    }

    @Override
    public void canContainContent(Content child, int index, boolean replace) {
        if (child instanceof Element) {
            int cre = this.content.indexOfFirstElement();
            if (replace && cre == index) {
                return;
            }
            if (cre >= 0) {
                throw new IllegalAddException("Cannot add a second root element, only one is allowed");
            }
            if (this.content.indexOfDocType() >= index) {
                throw new IllegalAddException("A root element cannot be added before the DocType");
            }
        }
        if (child instanceof DocType) {
            int cdt = this.content.indexOfDocType();
            if (replace && cdt == index) {
                return;
            }
            if (cdt >= 0) {
                throw new IllegalAddException("Cannot add a second doctype, only one is allowed");
            }
            int firstElt = this.content.indexOfFirstElement();
            if (firstElt != -1 && firstElt < index) {
                throw new IllegalAddException("A DocType cannot be added after the root element");
            }
        }
        if (child instanceof CDATA) {
            throw new IllegalAddException("A CDATA is not allowed at the document root");
        }
        if (child instanceof Text) {
            if (Verifier.isAllXMLWhitespace(((Text)child).getText())) {
                return;
            }
            throw new IllegalAddException("A Text is not allowed at the document root");
        }
        if (child instanceof EntityRef) {
            throw new IllegalAddException("An EntityRef is not allowed at the document root");
        }
    }

    @Override
    public List<Namespace> getNamespacesInScope() {
        return Collections.unmodifiableList(Arrays.asList(Namespace.NO_NAMESPACE, Namespace.XML_NAMESPACE));
    }

    @Override
    public List<Namespace> getNamespacesIntroduced() {
        return Collections.unmodifiableList(Arrays.asList(Namespace.NO_NAMESPACE, Namespace.XML_NAMESPACE));
    }

    @Override
    public List<Namespace> getNamespacesInherited() {
        return Collections.emptyList();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        int cs = this.content.size();
        out.writeInt(cs);
        for (int i = 0; i < cs; ++i) {
            out.writeObject(this.getContent(i));
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.content = new ContentList(this);
        int cs = in.readInt();
        while (--cs >= 0) {
            this.addContent((Content)in.readObject());
        }
    }
}

