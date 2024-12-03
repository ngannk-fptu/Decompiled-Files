/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.txw2;

import com.sun.xml.txw2.Attribute;
import com.sun.xml.txw2.ContainerElement;
import com.sun.xml.txw2.Content;
import com.sun.xml.txw2.ContentVisitor;
import com.sun.xml.txw2.Document;
import com.sun.xml.txw2.NamespaceDecl;
import com.sun.xml.txw2.NamespaceResolver;

class StartTag
extends Content
implements NamespaceResolver {
    private String uri;
    private final String localName;
    private Attribute firstAtt;
    private Attribute lastAtt;
    private ContainerElement owner;
    private NamespaceDecl firstNs;
    private NamespaceDecl lastNs;
    final Document document;

    public StartTag(ContainerElement owner, String uri, String localName) {
        this(owner.document, uri, localName);
        this.owner = owner;
    }

    public StartTag(Document document, String uri, String localName) {
        assert (uri != null);
        assert (localName != null);
        this.uri = uri;
        this.localName = localName;
        this.document = document;
        this.addNamespaceDecl(uri, null, false);
    }

    public void addAttribute(String nsUri, String localName, Object arg) {
        this.checkWritable();
        Attribute a = this.firstAtt;
        while (a != null && !a.hasName(nsUri, localName)) {
            a = a.next;
        }
        if (a == null) {
            a = new Attribute(nsUri, localName);
            if (this.lastAtt == null) {
                assert (this.firstAtt == null);
                this.firstAtt = this.lastAtt = a;
            } else {
                assert (this.firstAtt != null);
                this.lastAtt.next = a;
                this.lastAtt = a;
            }
            if (nsUri.length() > 0) {
                this.addNamespaceDecl(nsUri, null, true);
            }
        }
        this.document.writeValue(arg, this, a.value);
    }

    public NamespaceDecl addNamespaceDecl(String uri, String prefix, boolean requirePrefix) {
        this.checkWritable();
        if (uri == null) {
            throw new IllegalArgumentException();
        }
        if (uri.length() == 0) {
            if (requirePrefix) {
                throw new IllegalArgumentException("The empty namespace cannot have a non-empty prefix");
            }
            if (prefix != null && prefix.length() > 0) {
                throw new IllegalArgumentException("The empty namespace can be only bound to the empty prefix");
            }
            prefix = "";
        }
        NamespaceDecl n = this.firstNs;
        while (n != null) {
            if (uri.equals(n.uri)) {
                if (prefix == null) {
                    n.requirePrefix |= requirePrefix;
                    return n;
                }
                if (n.prefix == null) {
                    n.prefix = prefix;
                    n.requirePrefix |= requirePrefix;
                    return n;
                }
                if (prefix.equals(n.prefix)) {
                    n.requirePrefix |= requirePrefix;
                    return n;
                }
            }
            if (prefix != null && n.prefix != null && n.prefix.equals(prefix)) {
                throw new IllegalArgumentException("Prefix '" + prefix + "' is already bound to '" + n.uri + '\'');
            }
            n = n.next;
        }
        NamespaceDecl ns = new NamespaceDecl(this.document.assignNewId(), uri, prefix, requirePrefix);
        if (this.lastNs == null) {
            assert (this.firstNs == null);
            this.firstNs = this.lastNs = ns;
        } else {
            assert (this.firstNs != null);
            this.lastNs.next = ns;
            this.lastNs = ns;
        }
        return ns;
    }

    private void checkWritable() {
        if (this.isWritten()) {
            throw new IllegalStateException("The start tag of " + this.localName + " has already been written. If you need out of order writing, see the TypedXmlWriter.block method");
        }
    }

    boolean isWritten() {
        return this.uri == null;
    }

    @Override
    boolean isReadyToCommit() {
        if (this.owner != null && this.owner.isBlocked()) {
            return false;
        }
        for (Content c = this.getNext(); c != null; c = c.getNext()) {
            if (!c.concludesPendingStartTag()) continue;
            return true;
        }
        return false;
    }

    @Override
    public void written() {
        this.lastAtt = null;
        this.firstAtt = null;
        this.uri = null;
        if (this.owner != null) {
            assert (this.owner.startTag == this);
            this.owner.startTag = null;
        }
    }

    @Override
    boolean concludesPendingStartTag() {
        return true;
    }

    @Override
    void accept(ContentVisitor visitor) {
        visitor.onStartTag(this.uri, this.localName, this.firstAtt, this.firstNs);
    }

    @Override
    public String getPrefix(String nsUri) {
        NamespaceDecl ns = this.addNamespaceDecl(nsUri, null, false);
        if (ns.prefix != null) {
            return ns.prefix;
        }
        return ns.dummyPrefix;
    }
}

