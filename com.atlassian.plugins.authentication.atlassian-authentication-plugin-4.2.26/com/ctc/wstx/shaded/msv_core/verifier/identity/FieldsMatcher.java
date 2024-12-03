/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.verifier.identity;

import com.ctc.wstx.shaded.msv_core.driver.textui.Debug;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.KeyConstraint;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.KeyRefConstraint;
import com.ctc.wstx.shaded.msv_core.verifier.identity.FieldMatcher;
import com.ctc.wstx.shaded.msv_core.verifier.identity.KeyValue;
import com.ctc.wstx.shaded.msv_core.verifier.identity.Matcher;
import com.ctc.wstx.shaded.msv_core.verifier.identity.MatcherBundle;
import com.ctc.wstx.shaded.msv_core.verifier.identity.SelectorMatcher;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;

public class FieldsMatcher
extends MatcherBundle {
    protected final Locator startTag;
    protected final SelectorMatcher selector;

    protected FieldsMatcher(SelectorMatcher selector, String namespaceURI, String localName) throws SAXException {
        super(selector.owner);
        this.selector = selector;
        this.startTag = this.owner.getLocator() == null ? null : new LocatorImpl(this.owner.getLocator());
        this.children = new Matcher[selector.idConst.fields.length];
        for (int i = 0; i < selector.idConst.fields.length; ++i) {
            this.children[i] = new FieldMatcher(this, selector.idConst.fields[i], namespaceURI, localName);
        }
    }

    protected void onRemoved() throws SAXException {
        int i;
        KeyValue kv;
        Object[] values = new Object[this.children.length];
        for (int i2 = 0; i2 < this.children.length; ++i2) {
            values[i2] = ((FieldMatcher)this.children[i2]).value;
            if (values[i2] != null) continue;
            if (!(this.selector.idConst instanceof KeyConstraint)) {
                return;
            }
            this.owner.reportError(this.startTag, null, "IdentityConstraint.UnmatchedKeyField", new Object[]{this.selector.idConst.namespaceURI, this.selector.idConst.localName, new Integer(i2 + 1)});
            return;
        }
        if (Debug.debug) {
            System.out.println("fields collected for " + this.selector.idConst.localName);
        }
        if (this.owner.addKeyValue(this.selector, kv = new KeyValue(values, this.startTag))) {
            return;
        }
        if (this.selector.idConst instanceof KeyRefConstraint) {
            return;
        }
        KeyValue[] items = this.owner.getKeyValues(this.selector);
        for (i = 0; i < values.length && !((Object)items[i]).equals(kv); ++i) {
        }
        this.owner.reportError(this.startTag, null, "IdentityConstraint.NotUnique", new Object[]{this.selector.idConst.namespaceURI, this.selector.idConst.localName});
        this.owner.reportError(items[i].locator, null, "IdentityConstraint.NotUnique.Diag", new Object[]{this.selector.idConst.namespaceURI, this.selector.idConst.localName});
    }
}

