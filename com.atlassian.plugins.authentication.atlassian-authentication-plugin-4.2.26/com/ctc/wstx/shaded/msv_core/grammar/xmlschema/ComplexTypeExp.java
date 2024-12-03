/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar.xmlschema;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.AttributeWildcard;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.RedefinableExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.SimpleTypeExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaSchema;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaTypeExp;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeExp;

public class ComplexTypeExp
extends XMLSchemaTypeExp {
    public final ReferenceExp body = new ReferenceExp(null);
    public final ReferenceExp attWildcard = new ReferenceExp(null, Expression.epsilon);
    public final XMLSchemaSchema parent;
    public AttributeWildcard wildcard;
    public ComplexTypeExp complexBaseType;
    public XSDatatypeExp simpleBaseType;
    public int derivationMethod = -1;
    public int finalValue = 0;
    public int block = 0;
    private static final long serialVersionUID = 1L;

    public ComplexTypeExp(XMLSchemaSchema schema, String localName) {
        super(localName);
        this.parent = schema;
        this.setAbstract(false);
    }

    public AttributeWildcard getAttributeWildcard() {
        return this.wildcard;
    }

    public void setAttributeWildcard(AttributeWildcard local) {
        this.wildcard = local;
    }

    public final String getTargetNamespace() {
        return this.parent.targetNamespace;
    }

    public boolean isAbstract() {
        return this.exp == Expression.nullSet;
    }

    public void setAbstract(boolean isAbstract) {
        this.exp = isAbstract ? Expression.nullSet : this.parent.pool.createSequence(this.body, this.attWildcard);
    }

    public boolean isDerivedTypeOf(ComplexTypeExp baseType, int constraint) {
        ComplexTypeExp derived = this;
        while (derived != null) {
            if (derived == baseType) {
                return true;
            }
            if ((derived.derivationMethod & constraint) != 0) {
                return false;
            }
            derived = derived.complexBaseType;
        }
        return false;
    }

    public boolean isDerivedTypeOf(XSDatatype baseType, int constraint) {
        ComplexTypeExp derived = this;
        while (true) {
            if (derived.complexBaseType == null) {
                if (derived.simpleBaseType != null) {
                    return derived.simpleBaseType.getCreatedType().isDerivedTypeOf(baseType, (constraint & 1) == 0);
                }
                return false;
            }
            if ((derived.derivationMethod & constraint) != 0) {
                return false;
            }
            derived = derived.complexBaseType;
        }
    }

    public boolean isDerivedTypeOf(XMLSchemaTypeExp exp, int constraint) {
        if (exp instanceof ComplexTypeExp) {
            return this.isDerivedTypeOf((ComplexTypeExp)exp, constraint);
        }
        return this.isDerivedTypeOf(((SimpleTypeExp)exp).getDatatype(), constraint);
    }

    public int getBlock() {
        return this.block;
    }

    public RedefinableExp getClone() {
        ComplexTypeExp exp = new ComplexTypeExp(this.parent, this.name);
        exp.redefine(this);
        return exp;
    }

    public void redefine(RedefinableExp _rhs) {
        super.redefine(_rhs);
        ComplexTypeExp rhs = (ComplexTypeExp)_rhs;
        this.body.exp = rhs.body.exp;
        this.attWildcard.exp = rhs.attWildcard.exp;
        this.complexBaseType = rhs.complexBaseType;
        this.simpleBaseType = rhs.simpleBaseType;
        this.derivationMethod = rhs.derivationMethod;
        this.finalValue = rhs.finalValue;
        this.block = rhs.block;
        this.wildcard = rhs.wildcard == null ? null : rhs.wildcard.copy();
        if (this.parent != rhs.parent) {
            throw new IllegalArgumentException();
        }
    }

    public boolean isDefined() {
        return this.body.isDefined();
    }
}

