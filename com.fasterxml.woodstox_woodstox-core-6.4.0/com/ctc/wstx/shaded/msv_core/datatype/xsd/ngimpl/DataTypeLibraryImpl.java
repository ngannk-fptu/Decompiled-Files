/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd.ngimpl;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeBuilder;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeLibrary;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeLibraryFactory;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.DatatypeFactory;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.ngimpl.DatatypeBuilderImpl;

public class DataTypeLibraryImpl
implements DatatypeLibrary,
DatatypeLibraryFactory {
    public Datatype createDatatype(String typeName) throws DatatypeException {
        return this.getType(typeName);
    }

    private XSDatatype getType(String typeName) throws DatatypeException {
        return DatatypeFactory.getTypeByName(typeName);
    }

    public DatatypeBuilder createDatatypeBuilder(String typeName) throws DatatypeException {
        return new DatatypeBuilderImpl(this.getType(typeName));
    }

    public DatatypeLibrary createDatatypeLibrary(String uri) {
        if (uri.equals("http://www.w3.org/2001/XMLSchema") || uri.equals("http://www.w3.org/2001/XMLSchema-datatypes")) {
            return this;
        }
        return null;
    }
}

