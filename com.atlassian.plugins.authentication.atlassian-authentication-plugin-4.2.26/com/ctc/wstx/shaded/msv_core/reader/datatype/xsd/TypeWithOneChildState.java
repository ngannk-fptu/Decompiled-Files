/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.StringType;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.TypeState;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeExp;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSTypeOwner;

abstract class TypeWithOneChildState
extends TypeState
implements XSTypeOwner {
    protected XSDatatypeExp type;

    TypeWithOneChildState() {
    }

    public void onEndChild(XSDatatypeExp child) {
        if (this.type != null) {
            this.reader.reportError("GrammarReader.Abstract.MoreThanOneChildType");
        } else {
            this.type = child;
        }
    }

    protected final XSDatatypeExp makeType() throws DatatypeException {
        if (this.type == null) {
            this.reader.reportError("GrammarReader.Abstract.MissingChildType");
            return new XSDatatypeExp(StringType.theInstance, this.reader.pool);
        }
        return this.annealType(this.type);
    }

    protected XSDatatypeExp annealType(XSDatatypeExp dt) throws DatatypeException {
        return dt;
    }
}

