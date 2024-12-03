/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.relaxns.reader.relax;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionWithoutChildState;
import com.ctc.wstx.shaded.msv_core.relaxns.grammar.relax.AnyOtherElementExp;
import com.ctc.wstx.shaded.msv_core.relaxns.reader.relax.RELAXCoreIslandSchemaReader;
import org.xml.sax.Locator;

public class AnyOtherElementState
extends ExpressionWithoutChildState {
    protected Expression makeExpression() {
        String in = this.startTag.getAttribute("includeNamespace");
        String ex = this.startTag.getAttribute("excludeNamespace");
        if (in != null && ex != null) {
            this.reader.reportError(new Locator[]{this.location}, "GrammarReader.ConflictingAttribute", new Object[]{"includeNamespace", "excludeNamespace"});
            ex = null;
        }
        if (in == null && ex == null) {
            ex = "";
        }
        AnyOtherElementExp exp = new AnyOtherElementExp(this.location, in, ex);
        ((RELAXCoreIslandSchemaReader)this.reader).pendingAnyOtherElements.add(exp);
        return exp;
    }
}

