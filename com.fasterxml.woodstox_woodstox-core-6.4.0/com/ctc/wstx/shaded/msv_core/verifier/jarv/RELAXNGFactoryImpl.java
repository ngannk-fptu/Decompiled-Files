/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.verifier.jarv;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeLibraryFactory;
import com.ctc.wstx.shaded.msv_core.grammar.Grammar;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController;
import com.ctc.wstx.shaded.msv_core.reader.trex.ng.RELAXNGReader;
import com.ctc.wstx.shaded.msv_core.verifier.jarv.FactoryImpl;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public class RELAXNGFactoryImpl
extends FactoryImpl {
    private DatatypeLibraryFactory datatypeLibraryFactory = null;
    private static final String PROP_NAME = "datatypeLibraryFactory";

    protected Grammar parse(InputSource is, GrammarReaderController controller) {
        RELAXNGReader reader = new RELAXNGReader(controller, this.factory);
        if (this.datatypeLibraryFactory != null) {
            reader.setDatatypeLibraryFactory(this.datatypeLibraryFactory);
        }
        reader.parse(is);
        return reader.getResult();
    }

    public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name.equals(PROP_NAME)) {
            return this.datatypeLibraryFactory;
        }
        return super.getProperty(name);
    }

    public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name.equals(PROP_NAME)) {
            this.datatypeLibraryFactory = (DatatypeLibraryFactory)value;
            return;
        }
        super.setProperty(name, value);
    }
}

