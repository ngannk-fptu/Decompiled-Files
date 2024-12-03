/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv.org_isorelax.dispatcher;

import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.AttributesDecl;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.AttributesVerifier;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.ElementDecl;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandVerifier;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.SchemaProvider;
import java.util.Iterator;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public interface IslandSchema {
    public IslandVerifier createNewVerifier(String var1, ElementDecl[] var2);

    public ElementDecl getElementDeclByName(String var1);

    public Iterator iterateElementDecls();

    public ElementDecl[] getElementDecls();

    public AttributesDecl getAttributesDeclByName(String var1);

    public Iterator iterateAttributesDecls();

    public AttributesDecl[] getAttributesDecls();

    public AttributesVerifier createNewAttributesVerifier(String var1, AttributesDecl[] var2);

    public void bind(SchemaProvider var1, ErrorHandler var2) throws SAXException;
}

