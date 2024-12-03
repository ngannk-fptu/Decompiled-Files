/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.relax.core;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.FacetState;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.FacetStateParent;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSTypeIncubator;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.ElementRuleBaseState;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.RELAXCoreReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

public class ElementRuleWithTypeState
extends ElementRuleBaseState
implements FacetStateParent {
    protected XSTypeIncubator incubator;

    public XSTypeIncubator getIncubator() {
        return this.incubator;
    }

    protected void startSelf() {
        super.startSelf();
        RELAXCoreReader reader = (RELAXCoreReader)this.reader;
        this.incubator = reader.resolveXSDatatype(this.startTag.getAttribute("type")).createIncubator();
    }

    protected Expression getContentModel() {
        try {
            return this.incubator.derive(null, null);
        }
        catch (DatatypeException e) {
            this.reader.reportError(e, "GrammarReader.BadType", (Object)e.getMessage());
            return Expression.anyString;
        }
    }

    protected State createChildState(StartTagInfo tag) {
        FacetState next = this.getReader().createFacetState(this, tag);
        if (next != null) {
            return next;
        }
        return super.createChildState(tag);
    }
}

