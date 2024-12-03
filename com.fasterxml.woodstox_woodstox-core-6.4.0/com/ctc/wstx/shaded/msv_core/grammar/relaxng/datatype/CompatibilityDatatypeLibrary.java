/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar.relaxng.datatype;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeBuilder;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeLibrary;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.DatatypeFactory;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.IDREFType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.IDType;
import com.ctc.wstx.shaded.msv_core.grammar.relaxng.datatype.DatatypeBuilderImpl;

public class CompatibilityDatatypeLibrary
implements DatatypeLibrary {
    public static final String namespaceURI = "http://relaxng.org/ns/compatibility/datatypes/1.0";

    public Datatype createDatatype(String name) throws DatatypeException {
        if (name.equals("ID")) {
            return IDType.theInstance;
        }
        if (name.equals("IDREF")) {
            return IDREFType.theInstance;
        }
        if (name.equals("IDREFS")) {
            return DatatypeFactory.getTypeByName("IDREFS");
        }
        throw new DatatypeException("undefined built-in type:" + name);
    }

    public DatatypeBuilder createDatatypeBuilder(String name) throws DatatypeException {
        return new DatatypeBuilderImpl(this.createDatatype(name));
    }
}

