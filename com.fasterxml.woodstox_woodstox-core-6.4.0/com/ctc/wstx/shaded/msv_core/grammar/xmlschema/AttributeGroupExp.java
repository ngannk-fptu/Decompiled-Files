/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.AttWildcardExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.AttributeWildcard;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.RedefinableExp;

public class AttributeGroupExp
extends RedefinableExp
implements AttWildcardExp {
    public AttributeWildcard wildcard;
    private static final long serialVersionUID = 1L;

    public AttributeWildcard getAttributeWildcard() {
        return this.wildcard;
    }

    public void setAttributeWildcard(AttributeWildcard local) {
        this.wildcard = local;
    }

    public AttributeGroupExp(String typeLocalName) {
        super(typeLocalName);
    }

    public RedefinableExp getClone() {
        AttributeGroupExp exp = new AttributeGroupExp(this.name);
        ((RedefinableExp)exp).redefine(this);
        return exp;
    }

    public void redefine(RedefinableExp _rhs) {
        super.redefine(_rhs);
        AttributeGroupExp rhs = (AttributeGroupExp)_rhs;
        this.wildcard = rhs.wildcard == null ? null : rhs.wildcard.copy();
    }
}

