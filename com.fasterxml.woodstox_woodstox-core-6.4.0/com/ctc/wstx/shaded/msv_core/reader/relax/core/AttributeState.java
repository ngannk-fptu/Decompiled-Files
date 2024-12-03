/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.relax.core;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.FacetStateParent;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSTypeIncubator;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.RELAXCoreReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

public class AttributeState
extends ExpressionState
implements FacetStateParent {
    protected XSTypeIncubator incubator;

    public XSTypeIncubator getIncubator() {
        return this.incubator;
    }

    protected void startSelf() {
        super.startSelf();
        RELAXCoreReader reader = (RELAXCoreReader)this.reader;
        String type = this.startTag.getAttribute("type");
        if (type == null) {
            type = "string";
        }
        this.incubator = reader.resolveXSDatatype(type).createIncubator();
    }

    protected Expression makeExpression() {
        try {
            String name = this.startTag.getAttribute("name");
            String required = this.startTag.getAttribute("required");
            if (name == null) {
                this.reader.reportError("GrammarReader.MissingAttribute", (Object)"attribute", (Object)"name");
                return Expression.epsilon;
            }
            Expression exp = this.reader.pool.createAttribute(new SimpleNameClass("", name), this.incubator.derive(null, null));
            if (!"true".equals(required)) {
                exp = this.reader.pool.createOptional(exp);
            }
            return exp;
        }
        catch (DatatypeException e) {
            this.reader.reportError(e, "GrammarReader.BadType", (Object)e.getMessage());
            return Expression.anyString;
        }
    }

    protected State createChildState(StartTagInfo tag) {
        return ((RELAXCoreReader)this.reader).createFacetState(this, tag);
    }
}

