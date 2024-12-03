/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.verifier.identity;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv_core.verifier.identity.IDConstraintChecker;
import org.xml.sax.SAXException;

public abstract class Matcher {
    protected final IDConstraintChecker owner;

    Matcher(IDConstraintChecker owner) {
        this.owner = owner;
    }

    protected abstract void startElement(String var1, String var2) throws SAXException;

    protected abstract void onAttribute(String var1, String var2, String var3, Datatype var4) throws SAXException;

    protected abstract void endElement(Datatype var1) throws SAXException;

    protected void characters(char[] buf, int start, int len) throws SAXException {
    }
}

