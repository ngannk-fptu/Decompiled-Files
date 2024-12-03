/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser.stax;

import javax.xml.namespace.QName;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Generator;
import org.apache.abdera.parser.stax.FOMElement;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;

public class FOMGenerator
extends FOMElement
implements Generator {
    private static final long serialVersionUID = -8441971633807437976L;

    protected FOMGenerator(String name, OMNamespace namespace, OMContainer parent, OMFactory factory) throws OMException {
        super(name, namespace, parent, factory);
    }

    protected FOMGenerator(QName qname, OMContainer parent, OMFactory factory) {
        super(qname, parent, factory);
    }

    protected FOMGenerator(String localName, OMContainer parent, OMFactory factory, OMXMLParserWrapper builder) {
        super(localName, parent, factory, builder);
    }

    protected FOMGenerator(OMContainer parent, OMFactory factory) throws OMException {
        super(GENERATOR, parent, factory);
    }

    public IRI getUri() {
        String value = this.getAttributeValue(AURI);
        return value != null ? new IRI(value) : null;
    }

    public IRI getResolvedUri() {
        return this._resolve(this.getResolvedBaseUri(), this.getUri());
    }

    public Generator setUri(String uri) {
        this.complete();
        if (uri != null) {
            this.setAttributeValue(AURI, new IRI(uri).toString());
        } else {
            this.removeAttribute(AURI);
        }
        return this;
    }

    public String getVersion() {
        return this.getAttributeValue(VERSION);
    }

    public Generator setVersion(String version) {
        this.complete();
        if (version != null) {
            this.setAttributeValue(VERSION, version);
        } else {
            this.removeAttribute(VERSION);
        }
        return this;
    }
}

