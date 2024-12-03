/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.AbortException;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionOwner;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionWithoutChildState;
import com.ctc.wstx.shaded.msv_core.reader.trex.RootIncludedPatternState;

public class IncludePatternState
extends ExpressionWithoutChildState
implements ExpressionOwner {
    protected Expression included = Expression.nullSet;

    public void onEndChild(Expression included) {
        this.included = included;
    }

    protected Expression makeExpression() {
        String href = this.startTag.getAttribute("href");
        if (href == null) {
            this.reader.reportError("GrammarReader.MissingAttribute", (Object)this.startTag.localName, (Object)"href");
            return Expression.nullSet;
        }
        try {
            this.reader.switchSource(this, href, new RootIncludedPatternState(this));
        }
        catch (AbortException abortException) {
            // empty catch block
        }
        return this.included;
    }
}

