/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.relaxns.grammar;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.OtherExp;
import org.xml.sax.Locator;

public class ExternalAttributeExp
extends OtherExp {
    public final String namespaceURI;
    public final String role;
    public transient Locator source;

    public ExternalAttributeExp(ExpressionPool pool, String namespaceURI, String role, Locator loc) {
        this.source = loc;
        this.namespaceURI = namespaceURI;
        this.role = role;
        this.exp = Expression.epsilon;
    }
}

