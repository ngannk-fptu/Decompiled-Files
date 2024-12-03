/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.txw2;

import com.sun.xml.txw2.Cdata;
import com.sun.xml.txw2.Comment;
import com.sun.xml.txw2.Content;
import com.sun.xml.txw2.Document;
import com.sun.xml.txw2.EndDocument;
import com.sun.xml.txw2.EndTag;
import com.sun.xml.txw2.IllegalAnnotationException;
import com.sun.xml.txw2.IllegalSignatureException;
import com.sun.xml.txw2.Pcdata;
import com.sun.xml.txw2.StartTag;
import com.sun.xml.txw2.TXW;
import com.sun.xml.txw2.Text;
import com.sun.xml.txw2.TypedXmlWriter;
import com.sun.xml.txw2.annotation.XmlAttribute;
import com.sun.xml.txw2.annotation.XmlCDATA;
import com.sun.xml.txw2.annotation.XmlElement;
import com.sun.xml.txw2.annotation.XmlNamespace;
import com.sun.xml.txw2.annotation.XmlValue;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import javax.xml.namespace.QName;

final class ContainerElement
implements InvocationHandler,
TypedXmlWriter {
    final Document document;
    StartTag startTag;
    final EndTag endTag = new EndTag();
    private final String nsUri;
    private Content tail;
    private ContainerElement prevOpen;
    private ContainerElement nextOpen;
    private final ContainerElement parent;
    private ContainerElement lastOpenChild;
    private boolean blocked;

    public ContainerElement(Document document, ContainerElement parent, String nsUri, String localName) {
        this.parent = parent;
        this.document = document;
        this.nsUri = nsUri;
        this.startTag = new StartTag(this, nsUri, localName);
        this.tail = this.startTag;
        if (this.isRoot()) {
            document.setFirstContent(this.startTag);
        }
    }

    private boolean isRoot() {
        return this.parent == null;
    }

    private boolean isCommitted() {
        return this.tail == null;
    }

    @Override
    public Document getDocument() {
        return this.document;
    }

    boolean isBlocked() {
        return this.blocked && !this.isCommitted();
    }

    @Override
    public void block() {
        this.blocked = true;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == TypedXmlWriter.class || method.getDeclaringClass() == Object.class) {
            try {
                return method.invoke((Object)this, args);
            }
            catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }
        XmlAttribute xa = method.getAnnotation(XmlAttribute.class);
        XmlValue xv = method.getAnnotation(XmlValue.class);
        XmlElement xe = method.getAnnotation(XmlElement.class);
        if (xa != null) {
            if (xv != null || xe != null) {
                throw new IllegalAnnotationException(method.toString());
            }
            this.addAttribute(xa, method, args);
            return proxy;
        }
        if (xv != null) {
            if (xe != null) {
                throw new IllegalAnnotationException(method.toString());
            }
            this._pcdata(args);
            return proxy;
        }
        return this.addElement(xe, method, args);
    }

    private void addAttribute(XmlAttribute xa, Method method, Object[] args) {
        assert (xa != null);
        this.checkStartTag();
        String localName = xa.value();
        if (xa.value().length() == 0) {
            localName = method.getName();
        }
        this._attribute(xa.ns(), localName, args);
    }

    private void checkStartTag() {
        if (this.startTag == null) {
            throw new IllegalStateException("start tag has already been written");
        }
    }

    private Object addElement(XmlElement e, Method method, Object[] args) {
        Class<?> rt = method.getReturnType();
        String nsUri = "##default";
        String localName = method.getName();
        if (e != null) {
            if (e.value().length() != 0) {
                localName = e.value();
            }
            nsUri = e.ns();
        }
        if (nsUri.equals("##default")) {
            Class<?> c = method.getDeclaringClass();
            XmlElement ce = c.getAnnotation(XmlElement.class);
            if (ce != null) {
                nsUri = ce.ns();
            }
            if (nsUri.equals("##default")) {
                nsUri = this.getNamespace(c.getPackage());
            }
        }
        if (rt == Void.TYPE) {
            boolean isCDATA = method.getAnnotation(XmlCDATA.class) != null;
            StartTag st = new StartTag(this.document, nsUri, localName);
            this.addChild(st);
            for (Object arg : args) {
                Text text = isCDATA ? new Cdata(this.document, st, arg) : new Pcdata(this.document, st, arg);
                this.addChild(text);
            }
            this.addChild(new EndTag());
            return null;
        }
        if (TypedXmlWriter.class.isAssignableFrom(rt)) {
            return this._element(nsUri, localName, rt);
        }
        throw new IllegalSignatureException("Illegal return type: " + rt);
    }

    private String getNamespace(Package pkg) {
        if (pkg == null) {
            return "";
        }
        XmlNamespace ns = pkg.getAnnotation(XmlNamespace.class);
        String nsUri = ns != null ? ns.value() : "";
        return nsUri;
    }

    private void addChild(Content child) {
        this.tail.setNext(this.document, child);
        this.tail = child;
    }

    @Override
    public void commit() {
        this.commit(true);
    }

    @Override
    public void commit(boolean includingAllPredecessors) {
        this._commit(includingAllPredecessors);
        this.document.flush();
    }

    private void _commit(boolean includingAllPredecessors) {
        if (this.isCommitted()) {
            return;
        }
        this.addChild(this.endTag);
        if (this.isRoot()) {
            this.addChild(new EndDocument());
        }
        this.tail = null;
        if (includingAllPredecessors) {
            ContainerElement e = this;
            while (e != null) {
                while (e.prevOpen != null) {
                    e.prevOpen._commit(false);
                }
                e = e.parent;
            }
        }
        while (this.lastOpenChild != null) {
            this.lastOpenChild._commit(false);
        }
        if (this.parent != null) {
            if (this.parent.lastOpenChild == this) {
                assert (this.nextOpen == null) : "this must be the last one";
                this.parent.lastOpenChild = this.prevOpen;
            } else {
                assert (this.nextOpen.prevOpen == this);
                this.nextOpen.prevOpen = this.prevOpen;
            }
            if (this.prevOpen != null) {
                assert (this.prevOpen.nextOpen == this);
                this.prevOpen.nextOpen = this.nextOpen;
            }
        }
        this.nextOpen = null;
        this.prevOpen = null;
    }

    @Override
    public void _attribute(String localName, Object value) {
        this._attribute("", localName, value);
    }

    @Override
    public void _attribute(String nsUri, String localName, Object value) {
        this.checkStartTag();
        this.startTag.addAttribute(nsUri, localName, value);
    }

    @Override
    public void _attribute(QName attributeName, Object value) {
        this._attribute(attributeName.getNamespaceURI(), attributeName.getLocalPart(), value);
    }

    @Override
    public void _namespace(String uri) {
        this._namespace(uri, false);
    }

    @Override
    public void _namespace(String uri, String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException();
        }
        this.checkStartTag();
        this.startTag.addNamespaceDecl(uri, prefix, false);
    }

    @Override
    public void _namespace(String uri, boolean requirePrefix) {
        this.checkStartTag();
        this.startTag.addNamespaceDecl(uri, null, requirePrefix);
    }

    @Override
    public void _pcdata(Object value) {
        this.addChild(new Pcdata(this.document, this.startTag, value));
    }

    @Override
    public void _cdata(Object value) {
        this.addChild(new Cdata(this.document, this.startTag, value));
    }

    @Override
    public void _comment(Object value) throws UnsupportedOperationException {
        this.addChild(new Comment(this.document, this.startTag, value));
    }

    @Override
    public <T extends TypedXmlWriter> T _element(String localName, Class<T> contentModel) {
        return this._element(this.nsUri, localName, contentModel);
    }

    @Override
    public <T extends TypedXmlWriter> T _element(QName tagName, Class<T> contentModel) {
        return this._element(tagName.getNamespaceURI(), tagName.getLocalPart(), contentModel);
    }

    @Override
    public <T extends TypedXmlWriter> T _element(Class<T> contentModel) {
        return this._element(TXW.getTagName(contentModel), contentModel);
    }

    @Override
    public <T extends TypedXmlWriter> T _cast(Class<T> facadeType) {
        return (T)((TypedXmlWriter)facadeType.cast(Proxy.newProxyInstance(facadeType.getClassLoader(), new Class[]{facadeType}, (InvocationHandler)this)));
    }

    @Override
    public <T extends TypedXmlWriter> T _element(String nsUri, String localName, Class<T> contentModel) {
        ContainerElement child = new ContainerElement(this.document, this, nsUri, localName);
        this.addChild(child.startTag);
        this.tail = child.endTag;
        if (this.lastOpenChild != null) {
            assert (this.lastOpenChild.parent == this);
            assert (child.prevOpen == null);
            assert (child.nextOpen == null);
            child.prevOpen = this.lastOpenChild;
            assert (this.lastOpenChild.nextOpen == null);
            this.lastOpenChild.nextOpen = child;
        }
        this.lastOpenChild = child;
        return child._cast(contentModel);
    }
}

