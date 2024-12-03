/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser.stax;

import javax.xml.namespace.QName;
import org.apache.abdera.model.Control;
import org.apache.abdera.parser.stax.FOMExtensibleElement;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;

public class FOMControl
extends FOMExtensibleElement
implements Control {
    private static final long serialVersionUID = -3816493378953555206L;

    protected FOMControl(String name, OMNamespace namespace, OMContainer parent, OMFactory factory) throws OMException {
        super(name, namespace, parent, factory);
    }

    protected FOMControl(QName qname, OMContainer parent, OMFactory factory) {
        super(qname, parent, factory);
    }

    protected FOMControl(String localName, OMContainer parent, OMFactory factory, OMXMLParserWrapper builder) {
        super(localName, parent, factory, builder);
    }

    protected FOMControl(OMContainer parent, OMFactory factory) throws OMException {
        super(CONTROL, parent, factory);
    }

    public boolean isDraft() {
        String value = this._getElementValue(DRAFT);
        if (value == null) {
            value = this._getElementValue(PRE_RFC_DRAFT);
        }
        return value != null && "yes".equalsIgnoreCase(value);
    }

    public Control setDraft(boolean draft) {
        this.complete();
        this._removeChildren(PRE_RFC_DRAFT, true);
        this._setElementValue(DRAFT, draft ? "yes" : "no");
        return this;
    }

    public Control unsetDraft() {
        this.complete();
        this._removeChildren(PRE_RFC_DRAFT, true);
        this._removeChildren(DRAFT, true);
        return this;
    }
}

