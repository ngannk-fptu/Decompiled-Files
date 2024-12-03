/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.verifier.identity;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv_core.driver.textui.Debug;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.Field;
import com.ctc.wstx.shaded.msv_core.verifier.identity.FieldsMatcher;
import com.ctc.wstx.shaded.msv_core.verifier.identity.PathMatcher;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class FieldMatcher
extends PathMatcher {
    protected Field field;
    protected Object value;
    protected final FieldsMatcher parent;
    protected StringBuffer elementText = null;

    FieldMatcher(FieldsMatcher parent, Field field, String namespaceURI, String localName) throws SAXException {
        super(parent.owner, field.paths);
        this.parent = parent;
        super.start(namespaceURI, localName);
    }

    protected void onElementMatched(String namespaceURI, String localName) throws SAXException {
        if (Debug.debug) {
            System.out.println("field match for " + this.parent.selector.idConst.localName);
        }
        this.elementText = new StringBuffer();
    }

    protected void onAttributeMatched(String namespaceURI, String localName, String value, Datatype type) throws SAXException {
        if (Debug.debug) {
            System.out.println("field match for " + this.parent.selector.idConst.localName);
        }
        this.setValue(value, type);
    }

    protected void startElement(String namespaceURI, String localName, Attributes attributes) throws SAXException {
        if (this.elementText != null) {
            this.elementText = null;
        }
        super.startElement(namespaceURI, localName);
    }

    protected void endElement(Datatype type) throws SAXException {
        super.endElement(type);
        if (this.elementText != null) {
            this.setValue(this.elementText.toString(), type);
            this.elementText = null;
        }
    }

    protected void characters(char[] buf, int start, int len) throws SAXException {
        super.characters(buf, start, len);
        if (this.elementText != null) {
            this.elementText.append(buf, start, len);
        }
    }

    private void setValue(String lexical, Datatype type) throws SAXException {
        if (this.value != null) {
            this.doubleMatchError();
            return;
        }
        if (type == null) {
            this.value = lexical;
            if (Debug.debug) {
                System.out.println("no type info available");
            }
        } else {
            this.value = type.createValue(lexical, this.owner);
        }
    }

    private void doubleMatchError() throws SAXException {
        int i;
        for (i = 0; i < this.parent.children.length && this.parent.children[i] != this; ++i) {
        }
        this.owner.reportError(null, "IdentityConstraint.DoubleMatch", new Object[]{this.parent.selector.idConst.namespaceURI, this.parent.selector.idConst.localName, new Integer(i + 1)});
    }
}

