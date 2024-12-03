/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar.xmlschema;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.RedefinableExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaTypeExp;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeExp;

public class SimpleTypeExp
extends XMLSchemaTypeExp {
    protected XSDatatypeExp type;
    private static final long serialVersionUID = 1L;

    SimpleTypeExp(String typeName) {
        super(typeName);
    }

    public void set(XSDatatypeExp exp) {
        this.type = exp;
        this.exp = this.type;
    }

    public XSDatatypeExp getType() {
        return this.type;
    }

    public XSDatatype getDatatype() {
        return this.type.getCreatedType();
    }

    public int getBlock() {
        return 0;
    }

    public RedefinableExp getClone() {
        SimpleTypeExp exp = new SimpleTypeExp(this.name);
        exp.redefine(this);
        return exp;
    }

    public void redefine(RedefinableExp _rhs) {
        super.redefine(_rhs);
        SimpleTypeExp rhs = (SimpleTypeExp)_rhs;
        if (this.type == null) {
            this.type = rhs.getType().getClone();
        } else {
            if (rhs.getType() == null) {
                throw new InternalError();
            }
            this.type.redefine(rhs.getType());
        }
    }
}

