/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser.stax;

import javax.xml.namespace.QName;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.IRIElement;
import org.apache.abdera.parser.stax.FOMElement;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;

public class FOMIRI
extends FOMElement
implements IRIElement {
    private static final long serialVersionUID = -8434722753544181200L;

    protected FOMIRI(String name, OMNamespace namespace, OMContainer parent, OMFactory factory) throws OMException {
        super(name, namespace, parent, factory);
    }

    protected FOMIRI(QName qname, OMContainer parent, OMFactory factory) throws OMException {
        super(qname, parent, factory);
    }

    protected FOMIRI(String localName, OMContainer parent, OMFactory factory, OMXMLParserWrapper builder) throws OMException {
        super(localName, parent, factory, builder);
    }

    public IRI getValue() {
        return this._getUriValue(this.getText());
    }

    public IRIElement setValue(String iri) {
        this.complete();
        if (iri != null) {
            this.setText(new IRI(iri).toString());
        } else {
            this._removeAllChildren();
        }
        return this;
    }

    public IRI getResolvedValue() {
        return this._resolve(this.getResolvedBaseUri(), this.getValue());
    }

    public IRIElement setNormalizedValue(String uri) {
        if (uri != null) {
            this.setValue(IRI.normalizeString(uri));
        } else {
            this.setValue(null);
        }
        return this;
    }
}

