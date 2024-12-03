/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.WhiteSpaceProcessor;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl;

public abstract class ConcreteType
extends XSDatatypeImpl {
    private static final long serialVersionUID = 1L;

    protected ConcreteType(String nsUri, String typeName, WhiteSpaceProcessor whiteSpace) {
        super(nsUri, typeName, whiteSpace);
    }

    protected ConcreteType(String nsUri, String typeName) {
        this(nsUri, typeName, WhiteSpaceProcessor.theCollapse);
    }

    public final ConcreteType getConcreteType() {
        return this;
    }

    public boolean isFinal(int derivationType) {
        return false;
    }

    protected void _checkValid(String content, ValidationContext context) throws DatatypeException {
        if (this.checkFormat(content, context)) {
            return;
        }
        throw new DatatypeException(-1, ConcreteType.localize("DataTypeErrorDiagnosis.InappropriateForType", content, this.getName()));
    }

    public Object _createJavaObject(String literal, ValidationContext context) {
        return this._createValue(literal, context);
    }

    public String serializeJavaObject(Object value, SerializationContext context) {
        String literal = this.convertToLexicalValue(value, context);
        if (!this.isValid(literal, serializedValueChecker)) {
            return null;
        }
        return literal;
    }
}

