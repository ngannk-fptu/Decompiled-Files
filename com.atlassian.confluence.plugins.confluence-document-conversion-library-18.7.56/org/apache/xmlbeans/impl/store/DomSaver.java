/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.store;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeLoader;
import org.apache.xmlbeans.XmlDocumentProperties;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.store.Cur;
import org.apache.xmlbeans.impl.store.Locale;
import org.apache.xmlbeans.impl.store.Saver;
import org.w3c.dom.Node;

final class DomSaver
extends Saver {
    private Cur _nodeCur;
    private SchemaType _type;
    private final SchemaTypeLoader _stl;
    private final XmlOptions _options;
    private final boolean _isFrag;

    DomSaver(Cur c, boolean isFrag, XmlOptions options) {
        super(c, options);
        if (c.isUserNode()) {
            this._type = c.getUser().get_schema_type();
        }
        this._stl = c._locale._schemaTypeLoader;
        this._options = options;
        this._isFrag = isFrag;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    Node saveDom() {
        Locale l = Locale.getLocale(this._stl, this._options);
        l.enter();
        try {
            this._nodeCur = l.getCur();
            while (this.process()) {
            }
            while (!this._nodeCur.isRoot()) {
                this._nodeCur.toParent();
            }
            if (this._type != null) {
                this._nodeCur.setType(this._type);
            }
            Node node = (Node)((Object)this._nodeCur.getDom());
            this._nodeCur.release();
            this._nodeCur = null;
            Node node2 = node;
            return node2;
        }
        finally {
            l.exit();
        }
    }

    @Override
    protected boolean emitElement(Saver.SaveCur c, List<QName> attrNames, List<String> attrValues) {
        if (Locale.isFragmentQName(c.getName())) {
            this._nodeCur.moveTo(null, -2);
        }
        this.ensureDoc();
        this._nodeCur.createElement(this.getQualifiedName(c, c.getName()));
        this._nodeCur.next();
        this.iterateMappings();
        while (this.hasMapping()) {
            this._nodeCur.createAttr(this._nodeCur._locale.createXmlns(this.mappingPrefix()));
            this._nodeCur.next();
            this._nodeCur.insertString(this.mappingUri());
            this._nodeCur.toParent();
            this._nodeCur.skipWithAttrs();
            this.nextMapping();
        }
        for (int i = 0; i < attrNames.size(); ++i) {
            this._nodeCur.createAttr(this.getQualifiedName(c, attrNames.get(i)));
            this._nodeCur.next();
            this._nodeCur.insertString(attrValues.get(i));
            this._nodeCur.toParent();
            this._nodeCur.skipWithAttrs();
        }
        return false;
    }

    @Override
    protected void emitFinish(Saver.SaveCur c) {
        if (!Locale.isFragmentQName(c.getName())) {
            assert (this._nodeCur.isEnd());
            this._nodeCur.next();
        }
    }

    @Override
    protected void emitText(Saver.SaveCur c) {
        this.ensureDoc();
        Object src = c.getChars();
        if (c._cchSrc > 0) {
            this._nodeCur.insertChars(src, c._offSrc, c._cchSrc);
            this._nodeCur.next();
        }
    }

    @Override
    protected void emitComment(Saver.SaveCur c) {
        this.ensureDoc();
        this._nodeCur.createComment();
        this.emitTextValue(c);
        this._nodeCur.skip();
    }

    @Override
    protected void emitProcinst(Saver.SaveCur c) {
        this.ensureDoc();
        this._nodeCur.createProcinst(c.getName().getLocalPart());
        this.emitTextValue(c);
        this._nodeCur.skip();
    }

    @Override
    protected void emitDocType(String docTypeName, String publicId, String systemId) {
        this.ensureDoc();
        XmlDocumentProperties props = Locale.getDocProps(this._nodeCur, true);
        props.setDoctypeName(docTypeName);
        props.setDoctypePublicId(publicId);
        props.setDoctypeSystemId(systemId);
    }

    @Override
    protected void emitStartDoc(Saver.SaveCur c) {
        this.ensureDoc();
    }

    @Override
    protected void emitEndDoc(Saver.SaveCur c) {
    }

    private QName getQualifiedName(Saver.SaveCur c, QName name) {
        String prefix;
        String uri = name.getNamespaceURI();
        String string = prefix = uri.length() > 0 ? this.getUriMapping(uri) : "";
        if (prefix.equals(name.getPrefix())) {
            return name;
        }
        return this._nodeCur._locale.makeQName(uri, name.getLocalPart(), prefix);
    }

    private void emitTextValue(Saver.SaveCur c) {
        c.push();
        c.next();
        if (c.isText()) {
            this._nodeCur.next();
            this._nodeCur.insertChars(c.getChars(), c._offSrc, c._cchSrc);
            this._nodeCur.toParent();
        }
        c.pop();
    }

    private void ensureDoc() {
        if (!this._nodeCur.isPositioned()) {
            if (this._isFrag) {
                this._nodeCur.createDomDocFragRoot();
            } else {
                this._nodeCur.createDomDocumentRoot();
            }
            this._nodeCur.next();
        }
    }
}

