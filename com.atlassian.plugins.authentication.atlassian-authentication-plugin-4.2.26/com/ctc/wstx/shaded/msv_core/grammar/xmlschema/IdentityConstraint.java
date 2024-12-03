/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.Field;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XPath;
import java.io.Serializable;

public class IdentityConstraint
implements Serializable {
    public final XPath[] selectors;
    public final String namespaceURI;
    public final String localName;
    public final Field[] fields;
    private static final long serialVersionUID = 1L;

    public IdentityConstraint(String namespaceURI, String localName, XPath[] selectors, Field[] fields) {
        this.namespaceURI = namespaceURI;
        this.localName = localName;
        this.selectors = selectors;
        this.fields = fields;
    }
}

