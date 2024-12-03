/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.relaxns.verifier;

import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.Dispatcher;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.ElementDecl;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchema;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandVerifier;
import com.ctc.wstx.shaded.msv_core.relaxns.grammar.relax.AnyOtherElementExp;
import com.ctc.wstx.shaded.msv_core.relaxns.verifier.Localizer;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class AnyOtherElementVerifier
extends DefaultHandler
implements IslandVerifier {
    private final AnyOtherElementExp[] exps;
    protected Dispatcher dispatcher;
    protected Locator locator;
    public static final String ERR_UNEXPECTED_NAMESPACE = "AnyOtherElementVerifier.UnexpectedNamespace";

    public AnyOtherElementVerifier(AnyOtherElementExp[] exps) {
        this.exps = exps;
    }

    public void setDispatcher(Dispatcher disp) {
        this.dispatcher = disp;
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        IslandSchema is = this.dispatcher.getSchemaProvider().getSchemaByNamespace(namespaceURI);
        if (is != null) {
            IslandVerifier iv = is.createNewVerifier(namespaceURI, is.getElementDecls());
            this.dispatcher.switchVerifier(iv);
            iv.startElement(namespaceURI, localName, qName, atts);
            return;
        }
        boolean atLeastOneIsValid = false;
        for (int i = 0; i < this.exps.length; ++i) {
            if (this.exps[i] == null) continue;
            if (this.exps[i].getNameClass().accepts(namespaceURI, localName)) {
                atLeastOneIsValid = true;
                continue;
            }
            this.exps[i] = null;
        }
        if (!atLeastOneIsValid) {
            this.dispatcher.getErrorHandler().error(new SAXParseException(Localizer.localize(ERR_UNEXPECTED_NAMESPACE, new Object[]{namespaceURI}), this.locator));
        }
    }

    public void endChildIsland(String namespaceURI, ElementDecl[] rules) {
    }

    public ElementDecl[] endIsland() {
        int i;
        int len = 0;
        for (i = 0; i < this.exps.length; ++i) {
            if (this.exps[i] == null) continue;
            ++len;
        }
        ElementDecl[] r = new ElementDecl[len];
        int j = 0;
        for (i = 0; i < this.exps.length; ++i) {
            if (this.exps[i] == null) continue;
            r[j++] = this.exps[i];
        }
        return r;
    }

    public void setDocumentLocator(Locator loc) {
        this.locator = loc;
    }
}

