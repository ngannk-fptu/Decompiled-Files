/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.trex.ng;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeLibrary;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeLibraryFactory;
import com.ctc.wstx.shaded.msv.relaxng_datatype.helpers.DatatypeLibraryLoader;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.ngimpl.DataTypeLibraryImpl;
import com.ctc.wstx.shaded.msv_core.grammar.relaxng.datatype.BuiltinDatatypeLibrary;
import com.ctc.wstx.shaded.msv_core.grammar.relaxng.datatype.CompatibilityDatatypeLibrary;

class DefaultDatatypeLibraryFactory
implements DatatypeLibraryFactory {
    private final DatatypeLibraryFactory loader = new DatatypeLibraryLoader();
    private DatatypeLibrary xsdlib;
    private DatatypeLibrary compatibilityLib;

    DefaultDatatypeLibraryFactory() {
    }

    public DatatypeLibrary createDatatypeLibrary(String namespaceURI) {
        DatatypeLibrary lib = this.loader.createDatatypeLibrary(namespaceURI);
        if (lib != null) {
            return lib;
        }
        if (namespaceURI.equals("")) {
            return BuiltinDatatypeLibrary.theInstance;
        }
        if (namespaceURI.equals("http://www.w3.org/2001/XMLSchema-datatypes") || namespaceURI.equals("http://www.w3.org/2001/XMLSchema")) {
            if (this.xsdlib == null) {
                this.xsdlib = new DataTypeLibraryImpl();
            }
            return this.xsdlib;
        }
        if (namespaceURI.equals("http://relaxng.org/ns/compatibility/datatypes/1.0")) {
            if (this.compatibilityLib == null) {
                this.compatibilityLib = new CompatibilityDatatypeLibrary();
            }
            return this.compatibilityLib;
        }
        return null;
    }
}

