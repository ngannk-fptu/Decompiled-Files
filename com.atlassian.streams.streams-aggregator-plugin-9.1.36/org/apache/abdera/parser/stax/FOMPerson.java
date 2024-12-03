/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser.stax;

import javax.xml.namespace.QName;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.IRIElement;
import org.apache.abdera.model.Person;
import org.apache.abdera.parser.stax.FOMExtensibleElement;
import org.apache.abdera.parser.stax.FOMFactory;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;

public class FOMPerson
extends FOMExtensibleElement
implements Person {
    private static final long serialVersionUID = 2147684807662492625L;

    protected FOMPerson(String name, OMNamespace namespace, OMContainer parent, OMFactory factory) throws OMException {
        super(name, namespace, parent, factory);
    }

    protected FOMPerson(QName qname, OMContainer parent, OMFactory factory) throws OMException {
        super(qname, parent, factory);
    }

    protected FOMPerson(String localName, OMContainer parent, OMFactory factory, OMXMLParserWrapper builder) throws OMException {
        super(localName, parent, factory, builder);
    }

    public Element getNameElement() {
        return (Element)((Object)this.getFirstChildWithName(NAME));
    }

    public Person setNameElement(Element element) {
        this.complete();
        if (element != null) {
            this._setChild(NAME, (OMElement)((Object)element));
        } else {
            this._removeChildren(NAME, false);
        }
        return this;
    }

    public Element setName(String name) {
        this.complete();
        if (name != null) {
            FOMFactory fomfactory = (FOMFactory)this.factory;
            Element el = fomfactory.newName(null);
            el.setText(name);
            this._setChild(NAME, (OMElement)((Object)el));
            return el;
        }
        this._removeChildren(NAME, false);
        return null;
    }

    public String getName() {
        Element name = this.getNameElement();
        return name != null ? name.getText() : null;
    }

    public Element getEmailElement() {
        return (Element)((Object)this.getFirstChildWithName(EMAIL));
    }

    public Person setEmailElement(Element element) {
        this.complete();
        if (element != null) {
            this._setChild(EMAIL, (OMElement)((Object)element));
        } else {
            this._removeChildren(EMAIL, false);
        }
        return this;
    }

    public Element setEmail(String email) {
        this.complete();
        if (email != null) {
            FOMFactory fomfactory = (FOMFactory)this.factory;
            Element el = fomfactory.newEmail(null);
            el.setText(email);
            this._setChild(EMAIL, (OMElement)((Object)el));
            return el;
        }
        this._removeChildren(EMAIL, false);
        return null;
    }

    public String getEmail() {
        Element email = this.getEmailElement();
        return email != null ? email.getText() : null;
    }

    public IRIElement getUriElement() {
        return (IRIElement)((Object)this.getFirstChildWithName(URI));
    }

    public Person setUriElement(IRIElement uri) {
        this.complete();
        if (uri != null) {
            this._setChild(URI, (OMElement)((Object)uri));
        } else {
            this._removeChildren(URI, false);
        }
        return this;
    }

    public IRIElement setUri(String uri) {
        this.complete();
        if (uri != null) {
            FOMFactory fomfactory = (FOMFactory)this.factory;
            IRIElement el = fomfactory.newUri(null);
            el.setValue(uri);
            this._setChild(URI, (OMElement)((Object)el));
            return el;
        }
        this._removeChildren(URI, false);
        return null;
    }

    public IRI getUri() {
        IRIElement iri = this.getUriElement();
        return iri != null ? iri.getResolvedValue() : null;
    }
}

