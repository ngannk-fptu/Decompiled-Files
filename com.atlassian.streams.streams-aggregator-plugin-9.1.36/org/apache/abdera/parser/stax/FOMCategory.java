/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser.stax;

import javax.xml.namespace.QName;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Category;
import org.apache.abdera.parser.stax.FOMExtensibleElement;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;

public class FOMCategory
extends FOMExtensibleElement
implements Category {
    private static final long serialVersionUID = -4313042828936786803L;

    protected FOMCategory(String name, OMNamespace namespace, OMContainer parent, OMFactory factory) throws OMException {
        super(name, namespace, parent, factory);
    }

    protected FOMCategory(QName qname, OMContainer parent, OMFactory factory) {
        super(qname, parent, factory);
    }

    protected FOMCategory(String localName, OMContainer parent, OMFactory factory, OMXMLParserWrapper builder) {
        super(localName, parent, factory, builder);
    }

    protected FOMCategory(OMContainer parent, OMFactory factory) {
        super(CATEGORY, parent, factory);
    }

    public String getTerm() {
        return this.getAttributeValue(TERM);
    }

    public Category setTerm(String term) {
        this.complete();
        if (term != null) {
            this.setAttributeValue(TERM, term);
        } else {
            this.removeAttribute(TERM);
        }
        return this;
    }

    public IRI getScheme() {
        String value = this.getAttributeValue(SCHEME);
        return value != null ? new IRI(value) : null;
    }

    public Category setScheme(String scheme) {
        this.complete();
        if (scheme != null) {
            this.setAttributeValue(SCHEME, new IRI(scheme).toString());
        } else {
            this.removeAttribute(SCHEME);
        }
        return this;
    }

    public String getLabel() {
        return this.getAttributeValue(LABEL);
    }

    public Category setLabel(String label) {
        this.complete();
        if (label != null) {
            this.setAttributeValue(LABEL, label);
        } else {
            this.removeAttribute(LABEL);
        }
        return this;
    }

    public String getValue() {
        return this.getText();
    }

    public void setValue(String value) {
        this.complete();
        if (value != null) {
            this.setText(value);
        } else {
            this._removeAllChildren();
        }
    }
}

