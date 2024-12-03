/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.relax.core;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.relax.AttPoolClause;
import com.ctc.wstx.shaded.msv_core.grammar.relax.ElementRule;
import com.ctc.wstx.shaded.msv_core.grammar.relax.TagClause;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.FacetStateParent;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSTypeIncubator;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.RELAXCoreReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

public class InlineElementState
extends ExpressionState
implements FacetStateParent {
    protected XSTypeIncubator incubator;

    public XSTypeIncubator getIncubator() {
        return this.incubator;
    }

    protected State createChildState(StartTagInfo tag) {
        if (this.incubator != null) {
            return ((RELAXCoreReader)this.reader).createFacetState(this, tag);
        }
        return null;
    }

    protected void startSelf() {
        super.startSelf();
        RELAXCoreReader reader = (RELAXCoreReader)this.reader;
        String type = this.startTag.getAttribute("type");
        String label = this.startTag.getAttribute("label");
        if (type != null && label != null) {
            reader.reportError("GrammarReader.ConflictingAttribute", (Object)"type", (Object)"label");
        }
        if (type == null && label == null) {
            reader.reportError("GrammarReader.MissingAttribute.2", "element", "type", "label");
            type = "string";
        }
        this.incubator = label != null ? null : reader.resolveXSDatatype(type).createIncubator();
    }

    protected Expression makeExpression() {
        try {
            ReferenceExp contentModel;
            RELAXCoreReader reader = (RELAXCoreReader)this.reader;
            String name = this.startTag.getAttribute("name");
            if (name == null) {
                reader.reportError("GrammarReader.MissingAttribute", (Object)"element", (Object)"name");
                return Expression.nullSet;
            }
            if (this.incubator != null) {
                contentModel = this.incubator.derive(null, null);
            } else {
                String label = this.startTag.getAttribute("label");
                if (label == null) {
                    throw new Error();
                }
                contentModel = reader.module.hedgeRules.getOrCreate(label);
                reader.backwardReference.memorizeLink(contentModel);
            }
            TagClause c = new TagClause();
            c.nameClass = new SimpleNameClass(reader.module.targetNamespace, name);
            String role = this.startTag.getAttribute("role");
            if (role == null) {
                c.exp = Expression.epsilon;
            } else {
                AttPoolClause att = reader.module.attPools.getOrCreate(role);
                c.exp = att;
                reader.backwardReference.memorizeLink(att);
            }
            return new ElementRule(reader.pool, c, contentModel);
        }
        catch (DatatypeException e) {
            this.reader.reportError(e, "GrammarReader.BadType", (Object)e.getMessage());
            return Expression.nullSet;
        }
    }
}

