/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.trex.classic;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionWithoutChildState;
import com.ctc.wstx.shaded.msv_core.reader.trex.classic.TREXGrammarReader;
import com.ctc.wstx.shaded.msv_core.util.StringPair;

public class DataState
extends ExpressionWithoutChildState {
    protected Expression makeExpression() {
        String typeName = this.startTag.getAttribute("type");
        if (typeName == null) {
            this.reader.reportError("GrammarReader.MissingAttribute", (Object)this.startTag.qName, (Object)"type");
            return Expression.anyString;
        }
        return this.reader.pool.createData(((TREXGrammarReader)this.reader).resolveDatatype(typeName), new StringPair("", typeName));
    }
}

