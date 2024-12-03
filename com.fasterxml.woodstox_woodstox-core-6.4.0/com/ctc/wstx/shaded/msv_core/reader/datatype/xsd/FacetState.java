/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv_core.reader.ChildlessState;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.FacetStateParent;
import java.util.HashSet;
import java.util.Set;

public class FacetState
extends ChildlessState {
    public static final Set facetNames = FacetState.initFacetNames();

    private static Set initFacetNames() {
        HashSet<String> s = new HashSet<String>();
        s.add("length");
        s.add("minLength");
        s.add("maxLength");
        s.add("pattern");
        s.add("enumeration");
        s.add("maxInclusive");
        s.add("minInclusive");
        s.add("maxExclusive");
        s.add("minExclusive");
        s.add("whiteSpace");
        s.add("fractionDigits");
        s.add("totalDigits");
        return s;
    }

    protected void startSelf() {
        super.startSelf();
        String value = this.startTag.getAttribute("value");
        if (value == null) {
            this.reader.reportError("GrammarReader.MissingAttribute", (Object)this.startTag.localName, (Object)"value");
        } else {
            try {
                ((FacetStateParent)((Object)this.parentState)).getIncubator().addFacet(this.startTag.localName, value, "true".equals(this.startTag.getAttribute("fixed")), this.reader);
            }
            catch (DatatypeException e) {
                this.reader.reportError(e, "GrammarReader.BadType", (Object)e.getMessage());
            }
        }
    }
}

