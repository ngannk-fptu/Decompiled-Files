/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.Field;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.IdentityConstraint;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XPath;

public class UniqueConstraint
extends IdentityConstraint {
    private static final long serialVersionUID = 1L;

    public UniqueConstraint(String namespaceURI, String localName, XPath[] selector, Field[] fields) {
        super(namespaceURI, localName, selector, fields);
    }
}

