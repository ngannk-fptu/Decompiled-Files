/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.txw2;

import com.sun.xml.txw2.Attribute;
import com.sun.xml.txw2.Content;
import com.sun.xml.txw2.ContentVisitor;
import com.sun.xml.txw2.DatatypeWriter;
import com.sun.xml.txw2.NamespaceDecl;
import com.sun.xml.txw2.NamespaceResolver;
import com.sun.xml.txw2.NamespaceSupport;
import com.sun.xml.txw2.StartDocument;
import com.sun.xml.txw2.output.XmlSerializer;
import java.util.HashMap;
import java.util.Map;

public final class Document {
    private final XmlSerializer out;
    private boolean started = false;
    private Content current = null;
    private final Map<Class, DatatypeWriter> datatypeWriters = new HashMap<Class, DatatypeWriter>();
    private int iota = 1;
    private final NamespaceSupport inscopeNamespace = new NamespaceSupport();
    private NamespaceDecl activeNamespaces;
    private final ContentVisitor visitor = new ContentVisitor(){

        @Override
        public void onStartDocument() {
            throw new IllegalStateException();
        }

        @Override
        public void onEndDocument() {
            Document.this.out.endDocument();
        }

        @Override
        public void onEndTag() {
            Document.this.out.endTag();
            Document.this.inscopeNamespace.popContext();
            Document.this.activeNamespaces = null;
        }

        @Override
        public void onPcdata(StringBuilder buffer) {
            if (Document.this.activeNamespaces != null) {
                buffer = Document.this.fixPrefix(buffer);
            }
            Document.this.out.text(buffer);
        }

        @Override
        public void onCdata(StringBuilder buffer) {
            if (Document.this.activeNamespaces != null) {
                buffer = Document.this.fixPrefix(buffer);
            }
            Document.this.out.cdata(buffer);
        }

        @Override
        public void onComment(StringBuilder buffer) {
            if (Document.this.activeNamespaces != null) {
                buffer = Document.this.fixPrefix(buffer);
            }
            Document.this.out.comment(buffer);
        }

        @Override
        public void onStartTag(String nsUri, String localName, Attribute attributes, NamespaceDecl namespaces) {
            assert (nsUri != null);
            assert (localName != null);
            Document.this.activeNamespaces = namespaces;
            if (!Document.this.started) {
                Document.this.started = true;
                Document.this.out.startDocument();
            }
            Document.this.inscopeNamespace.pushContext();
            NamespaceDecl ns = namespaces;
            while (ns != null) {
                String uri;
                ns.declared = false;
                if (!(ns.prefix == null || (uri = Document.this.inscopeNamespace.getURI(ns.prefix)) != null && uri.equals(ns.uri))) {
                    Document.this.inscopeNamespace.declarePrefix(ns.prefix, ns.uri);
                    ns.declared = true;
                }
                ns = ns.next;
            }
            ns = namespaces;
            while (ns != null) {
                if (ns.prefix == null) {
                    if (Document.this.inscopeNamespace.getURI("").equals(ns.uri)) {
                        ns.prefix = "";
                    } else {
                        String p = Document.this.inscopeNamespace.getPrefix(ns.uri);
                        if (p == null) {
                            do {
                                p = Document.this.newPrefix();
                            } while (Document.this.inscopeNamespace.getURI(p) != null);
                            ns.declared = true;
                            Document.this.inscopeNamespace.declarePrefix(p, ns.uri);
                        }
                        ns.prefix = p;
                    }
                }
                ns = ns.next;
            }
            assert (namespaces.uri.equals(nsUri));
            assert (namespaces.prefix != null) : "a prefix must have been all allocated";
            Document.this.out.beginStartTag(nsUri, localName, namespaces.prefix);
            ns = namespaces;
            while (ns != null) {
                if (ns.declared) {
                    Document.this.out.writeXmlns(ns.prefix, ns.uri);
                }
                ns = ns.next;
            }
            Attribute a = attributes;
            while (a != null) {
                String prefix = a.nsUri.length() == 0 ? "" : Document.this.inscopeNamespace.getPrefix(a.nsUri);
                Document.this.out.writeAttribute(a.nsUri, a.localName, prefix, Document.this.fixPrefix(a.value));
                a = a.next;
            }
            Document.this.out.endStartTag(nsUri, localName, namespaces.prefix);
        }
    };
    private final StringBuilder prefixSeed = new StringBuilder("ns");
    private int prefixIota = 0;
    static final char MAGIC = '\u0000';

    Document(XmlSerializer out) {
        this.out = out;
        for (DatatypeWriter<?> dw : DatatypeWriter.BUILTIN) {
            this.datatypeWriters.put(dw.getType(), dw);
        }
    }

    void flush() {
        this.out.flush();
    }

    void setFirstContent(Content c) {
        assert (this.current == null);
        this.current = new StartDocument();
        this.current.setNext(this, c);
    }

    public void addDatatypeWriter(DatatypeWriter<?> dw) {
        this.datatypeWriters.put(dw.getType(), dw);
    }

    void run() {
        Content next;
        while ((next = this.current.getNext()) != null && next.isReadyToCommit()) {
            next.accept(this.visitor);
            next.written();
            this.current = next;
        }
        return;
    }

    void writeValue(Object obj, NamespaceResolver nsResolver, StringBuilder buf) {
        if (obj == null) {
            throw new IllegalArgumentException("argument contains null");
        }
        if (obj instanceof Object[]) {
            for (Object o : (Object[])obj) {
                this.writeValue(o, nsResolver, buf);
            }
            return;
        }
        if (obj instanceof Iterable) {
            for (Object o : (Iterable)obj) {
                this.writeValue(o, nsResolver, buf);
            }
            return;
        }
        if (buf.length() > 0) {
            buf.append(' ');
        }
        for (Class<?> c = obj.getClass(); c != null; c = c.getSuperclass()) {
            DatatypeWriter dw = this.datatypeWriters.get(c);
            if (dw == null) continue;
            dw.print(obj, nsResolver, buf);
            return;
        }
        buf.append(obj);
    }

    private String newPrefix() {
        this.prefixSeed.setLength(2);
        this.prefixSeed.append(++this.prefixIota);
        return this.prefixSeed.toString();
    }

    private StringBuilder fixPrefix(StringBuilder buf) {
        int i;
        assert (this.activeNamespaces != null);
        int len = buf.length();
        for (i = 0; i < len && buf.charAt(i) != '\u0000'; ++i) {
        }
        if (i == len) {
            return buf;
        }
        while (i < len) {
            char uriIdx = buf.charAt(i + 1);
            NamespaceDecl ns = this.activeNamespaces;
            while (ns != null && ns.uniqueId != uriIdx) {
                ns = ns.next;
            }
            if (ns == null) {
                throw new IllegalStateException("Unexpected use of prefixes " + buf);
            }
            int length = 2;
            String prefix = ns.prefix;
            if (prefix.length() == 0) {
                if (buf.length() <= i + 2 || buf.charAt(i + 2) != ':') {
                    throw new IllegalStateException("Unexpected use of prefixes " + buf);
                }
                length = 3;
            }
            buf.replace(i, i + length, prefix);
            len += prefix.length() - length;
            while (i < len && buf.charAt(i) != '\u0000') {
                ++i;
            }
        }
        return buf;
    }

    char assignNewId() {
        return (char)this.iota++;
    }
}

