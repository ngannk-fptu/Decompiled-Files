/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.verifier.identity;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv_core.driver.textui.Debug;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.IdentityConstraint;
import com.ctc.wstx.shaded.msv_core.verifier.identity.FieldsMatcher;
import com.ctc.wstx.shaded.msv_core.verifier.identity.IDConstraintChecker;
import com.ctc.wstx.shaded.msv_core.verifier.identity.PathMatcher;
import org.xml.sax.SAXException;

public class SelectorMatcher
extends PathMatcher {
    protected IdentityConstraint idConst;

    SelectorMatcher(IDConstraintChecker owner, IdentityConstraint idConst, String namespaceURI, String localName) throws SAXException {
        super(owner, idConst.selectors);
        this.idConst = idConst;
        owner.pushActiveScope(idConst, this);
        if (Debug.debug) {
            System.out.println("new id scope is available for {" + idConst.localName + "}");
        }
        super.start(namespaceURI, localName);
    }

    protected void onRemoved() throws SAXException {
        super.onRemoved();
        this.owner.popActiveScope(this.idConst, this);
    }

    protected void onElementMatched(String namespaceURI, String localName) throws SAXException {
        if (Debug.debug) {
            System.out.println("find a match for a selector: " + this.idConst.localName);
        }
        this.owner.add(new FieldsMatcher(this, namespaceURI, localName));
    }

    protected void onAttributeMatched(String namespaceURI, String localName, String value, Datatype type) {
        throw new Error();
    }
}

