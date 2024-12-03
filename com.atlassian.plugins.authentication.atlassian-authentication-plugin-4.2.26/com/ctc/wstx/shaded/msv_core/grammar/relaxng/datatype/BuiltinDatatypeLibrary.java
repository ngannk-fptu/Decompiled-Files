/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar.relaxng.datatype;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeBuilder;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeLibrary;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.StringType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.TokenType;
import com.ctc.wstx.shaded.msv_core.grammar.relaxng.datatype.DatatypeBuilderImpl;

public class BuiltinDatatypeLibrary
implements DatatypeLibrary {
    public static final BuiltinDatatypeLibrary theInstance = new BuiltinDatatypeLibrary();

    protected BuiltinDatatypeLibrary() {
    }

    public Datatype createDatatype(String name) throws DatatypeException {
        if (name.equals("string")) {
            return StringType.theInstance;
        }
        if (name.equals("token")) {
            return TokenType.theInstance;
        }
        throw new DatatypeException("undefined built-in type:" + name);
    }

    public DatatypeBuilder createDatatypeBuilder(String name) throws DatatypeException {
        return new DatatypeBuilderImpl(this.createDatatype(name));
    }
}

