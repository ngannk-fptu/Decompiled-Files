/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.AttributeDeclExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.AttributeWildcard;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaSchema;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.AnyAttributeOwner;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.AnyState;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader;

public class AnyAttributeState
extends AnyState {
    protected Expression createExpression(String namespace, String process) {
        int mode;
        XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        XMLSchemaSchema currentSchema = reader.currentSchema;
        if (process.equals("skip")) {
            mode = 0;
        } else if (process.equals("lax")) {
            mode = 1;
        } else if (process.equals("strict")) {
            mode = 2;
        } else {
            reader.reportError("GrammarReader.BadAttributeValue", (Object)"processContents", (Object)process);
            mode = 0;
        }
        ((AnyAttributeOwner)((Object)this.parentState)).setAttributeWildcard(new AttributeWildcard(this.getNameClass(namespace, currentSchema), mode));
        return Expression.epsilon;
    }

    protected NameClass getNameClassFrom(ReferenceExp exp) {
        return ((AttributeDeclExp)exp).self.nameClass;
    }
}

