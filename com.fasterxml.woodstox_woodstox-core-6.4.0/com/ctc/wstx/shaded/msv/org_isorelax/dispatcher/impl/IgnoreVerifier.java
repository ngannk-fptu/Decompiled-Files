/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.impl;

import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.Dispatcher;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.ElementDecl;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchema;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandVerifier;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public final class IgnoreVerifier
extends DefaultHandler
implements IslandVerifier {
    private final ElementDecl[] rules;
    private final String namespaceToIgnore;
    private Dispatcher dispatcher;

    public IgnoreVerifier(String s, ElementDecl[] aelementdecl) {
        this.namespaceToIgnore = s;
        this.rules = aelementdecl;
    }

    public ElementDecl[] endIsland() {
        return this.rules;
    }

    public void endChildIsland(String s, ElementDecl[] aelementdecl) {
    }

    public void setDispatcher(Dispatcher dispatcher1) {
        this.dispatcher = dispatcher1;
    }

    public void startElement(String s, String s1, String s2, Attributes attributes) throws SAXException {
        if (this.namespaceToIgnore.equals(s)) {
            return;
        }
        IslandSchema islandschema = this.dispatcher.getSchemaProvider().getSchemaByNamespace(s);
        if (islandschema == null) {
            return;
        }
        IslandVerifier islandverifier = islandschema.createNewVerifier(s, islandschema.getElementDecls());
        this.dispatcher.switchVerifier(islandverifier);
        islandverifier.startElement(s, s1, s2, attributes);
    }
}

