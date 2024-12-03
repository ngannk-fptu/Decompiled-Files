/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceContainer;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaSchema;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionWithoutChildState;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader;

public class ElementRefState
extends ExpressionWithoutChildState {
    protected Expression makeExpression() {
        XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        if (!this.startTag.containsAttribute("ref")) {
            throw new Error();
        }
        Expression exp = reader.resolveQNameRef(this.startTag, "ref", new XMLSchemaReader.RefResolver(){

            public ReferenceContainer get(XMLSchemaSchema g) {
                return g.elementDecls;
            }
        });
        if (exp == null) {
            return Expression.epsilon;
        }
        return exp;
    }
}

