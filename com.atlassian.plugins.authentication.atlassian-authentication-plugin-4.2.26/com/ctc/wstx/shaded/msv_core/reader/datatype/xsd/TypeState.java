/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.StringType;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionOwner;
import com.ctc.wstx.shaded.msv_core.reader.IgnoreState;
import com.ctc.wstx.shaded.msv_core.reader.SimpleState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.datatype.TypeOwner;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeExp;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSTypeOwner;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

abstract class TypeState
extends SimpleState {
    TypeState() {
    }

    private XSTypeOwner getParent() {
        if (this.parentState instanceof XSTypeOwner) {
            return (XSTypeOwner)((Object)this.parentState);
        }
        return null;
    }

    public final String getTargetNamespaceUri() {
        XSTypeOwner parent = this.getParent();
        if (parent != null) {
            return this.getParent().getTargetNamespaceUri();
        }
        return "";
    }

    public void endSelf() {
        super.endSelf();
        XSDatatypeExp type = this._makeType();
        if (this.parentState instanceof XSTypeOwner) {
            ((XSTypeOwner)((Object)this.parentState)).onEndChild(type);
            return;
        }
        if (this.parentState instanceof TypeOwner) {
            ((TypeOwner)((Object)this.parentState)).onEndChildType(type, type.name);
            return;
        }
        if (this.parentState instanceof ExpressionOwner) {
            ((ExpressionOwner)((Object)this.parentState)).onEndChild(type);
            return;
        }
        throw new Error(this.parentState.getClass().getName() + " doesn't implement any of TypeOwner");
    }

    XSDatatypeExp _makeType() {
        try {
            return this.makeType();
        }
        catch (DatatypeException be) {
            this.reader.reportError(be, "GrammarReader.BadType");
            return new XSDatatypeExp(StringType.theInstance, this.reader.pool);
        }
    }

    protected abstract XSDatatypeExp makeType() throws DatatypeException;

    public final void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
        StartTagInfo tag = new StartTagInfo(namespaceURI, localName, qName, new AttributesImpl(atts));
        State nextState = this.createChildState(tag);
        if (nextState != null) {
            this.reader.pushState(nextState, this, tag);
            return;
        }
        this.reader.reportError("GrammarReader.MalplacedElement", (Object)tag.qName);
        this.reader.pushState(new IgnoreState(), this, tag);
    }
}

