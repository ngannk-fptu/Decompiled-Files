/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv.org_isorelax.dispatcher;

import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.Dispatcher;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.ElementDecl;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public interface IslandVerifier
extends ContentHandler {
    public void setDispatcher(Dispatcher var1);

    public ElementDecl[] endIsland() throws SAXException;

    public void endChildIsland(String var1, ElementDecl[] var2) throws SAXException;
}

