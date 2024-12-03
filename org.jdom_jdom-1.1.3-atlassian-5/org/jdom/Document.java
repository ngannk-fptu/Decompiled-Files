/*
 * Decompiled with CFR 0.152.
 */
package org.jdom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.jdom.Comment;
import org.jdom.Content;
import org.jdom.ContentList;
import org.jdom.DescendantIterator;
import org.jdom.DocType;
import org.jdom.Element;
import org.jdom.FilterIterator;
import org.jdom.IllegalAddException;
import org.jdom.Parent;
import org.jdom.ProcessingInstruction;
import org.jdom.filter.Filter;

public class Document
implements Parent {
    private static final String CVS_ID = "@(#) $RCSfile: Document.java,v $ $Revision: 1.85 $ $Date: 2007/11/10 05:28:58 $ $Name:  $";
    ContentList content = new ContentList(this);
    protected String baseURI = null;
    private HashMap propertyMap = null;

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

    public Document(List content) {
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

    public Document addContent(Content child) {
        this.content.add(child);
        return this;
    }

    public Document addContent(Collection c) {
        this.content.addAll(c);
        return this;
    }

    public Document addContent(int index, Content child) {
        this.content.add(index, child);
        return this;
    }

    public Document addContent(int index, Collection c) {
        this.content.addAll(index, c);
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
    public List getContent() {
        if (!this.hasRootElement()) {
            throw new IllegalStateException("Root element not set");
        }
        return this.content;
    }

    @Override
    public List getContent(Filter filter) {
        if (!this.hasRootElement()) {
            throw new IllegalStateException("Root element not set");
        }
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
        Iterator itr = this.content.getView(filter).iterator();
        while (itr.hasNext()) {
            Content child = (Content)itr.next();
            old.add(child);
            itr.remove();
        }
        return old;
    }

    public Document setContent(Collection newContent) {
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

    public Document setContent(int index, Collection collection) {
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
        return (Content)this.content.remove(index);
    }

    public Document setContent(Content child) {
        this.content.clear();
        this.content.add(child);
        return this;
    }

    public String toString() {
        StringBuffer stringForm = new StringBuffer().append("[Document: ");
        DocType docType = this.getDocType();
        if (docType != null) {
            stringForm.append(docType.toString()).append(", ");
        } else {
            stringForm.append(" No DOCTYPE declaration, ");
        }
        if (this.hasRootElement()) {
            stringForm.append("Root is ").append(this.getRootElement().toString());
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
    public Object clone() {
        Document doc = null;
        try {
            doc = (Document)super.clone();
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            // empty catch block
        }
        doc.content = new ContentList(doc);
        for (int i = 0; i < this.content.size(); ++i) {
            Object obj = this.content.get(i);
            if (obj instanceof Element) {
                Element element = (Element)((Element)obj).clone();
                doc.content.add(element);
                continue;
            }
            if (obj instanceof Comment) {
                Comment comment = (Comment)((Comment)obj).clone();
                doc.content.add(comment);
                continue;
            }
            if (obj instanceof ProcessingInstruction) {
                ProcessingInstruction pi = (ProcessingInstruction)((ProcessingInstruction)obj).clone();
                doc.content.add(pi);
                continue;
            }
            if (!(obj instanceof DocType)) continue;
            DocType dt = (DocType)((DocType)obj).clone();
            doc.content.add(dt);
        }
        return doc;
    }

    @Override
    public Iterator getDescendants() {
        return new DescendantIterator(this);
    }

    @Override
    public Iterator getDescendants(Filter filter) {
        return new FilterIterator(new DescendantIterator(this), filter);
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
}

