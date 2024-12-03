/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.trex.ng;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv_core.reader.ChildlessState;
import com.ctc.wstx.shaded.msv_core.reader.trex.ng.DataState;

public class DataParamState
extends ChildlessState {
    protected final StringBuffer text = new StringBuffer();

    public void characters(char[] buf, int from, int len) {
        this.text.append(buf, from, len);
    }

    public void ignorableWhitespace(char[] buf, int from, int len) {
        this.text.append(buf, from, len);
    }

    protected void endSelf() {
        String facet = this.startTag.getAttribute("name");
        if (facet == null) {
            this.reader.reportError("GrammarReader.MissingAttribute", (Object)this.startTag.qName, (Object)"name");
        } else {
            try {
                ((DataState)this.parentState).typeBuilder.addParameter(facet, this.text.toString(), this.reader);
            }
            catch (DatatypeException dte) {
                this.reader.reportError("RELAXNGReader.BadFacet", (Object)facet, (Object)dte.getMessage());
            }
        }
        super.endSelf();
    }
}

