/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.ComplexTypeExp;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

public class ComplexContentState
extends ExpressionWithChildState {
    protected ComplexTypeExp parentDecl;

    protected ComplexContentState(ComplexTypeExp decl) {
        this.parentDecl = decl;
    }

    protected State createChildState(StartTagInfo tag) {
        XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        if (this.exp != null) {
            return null;
        }
        if (tag.localName.equals("restriction")) {
            return reader.sfactory.complexRst(this, tag, this.parentDecl);
        }
        if (tag.localName.equals("extension")) {
            return reader.sfactory.complexExt(this, tag, this.parentDecl);
        }
        return super.createChildState(tag);
    }

    protected Expression castExpression(Expression halfCastedExpression, Expression newChildExpression) {
        if (halfCastedExpression != null) {
            throw new Error();
        }
        return newChildExpression;
    }
}

