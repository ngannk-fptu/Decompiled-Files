/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.trex.ng;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionWithoutChildState;
import com.ctc.wstx.shaded.msv_core.reader.trex.ng.RELAXNGReader;
import com.ctc.wstx.shaded.msv_core.util.StringPair;

public class ValueState
extends ExpressionWithoutChildState {
    protected final StringBuffer text = new StringBuffer();

    public void characters(char[] buf, int from, int len) {
        this.text.append(buf, from, len);
    }

    public void ignorableWhitespace(char[] buf, int from, int len) {
        this.text.append(buf, from, len);
    }

    protected Expression makeExpression() {
        StringPair typeFullName;
        Datatype type;
        RELAXNGReader reader = (RELAXNGReader)this.reader;
        String typeName = this.startTag.getCollapsedAttribute("type");
        if (typeName == null) {
            try {
                type = reader.resolveDataTypeLibrary("").createDatatype("token");
                typeFullName = new StringPair("", "token");
            }
            catch (DatatypeException e) {
                e.printStackTrace();
                throw new InternalError();
            }
        } else {
            type = reader.resolveDataType(typeName);
            typeFullName = new StringPair(reader.datatypeLibURI, typeName);
        }
        Object value = type.createValue(this.text.toString(), reader);
        if (value == null) {
            reader.reportError("RELAXNGReader.BadDataValue", (Object)typeName, (Object)this.text.toString().trim());
            return Expression.nullSet;
        }
        return reader.pool.createValue(type, typeFullName, value);
    }
}

