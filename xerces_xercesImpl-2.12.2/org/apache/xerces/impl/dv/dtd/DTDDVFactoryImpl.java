/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dv.dtd;

import java.util.Hashtable;
import org.apache.xerces.impl.dv.DTDDVFactory;
import org.apache.xerces.impl.dv.DatatypeValidator;
import org.apache.xerces.impl.dv.dtd.ENTITYDatatypeValidator;
import org.apache.xerces.impl.dv.dtd.IDDatatypeValidator;
import org.apache.xerces.impl.dv.dtd.IDREFDatatypeValidator;
import org.apache.xerces.impl.dv.dtd.ListDatatypeValidator;
import org.apache.xerces.impl.dv.dtd.NMTOKENDatatypeValidator;
import org.apache.xerces.impl.dv.dtd.NOTATIONDatatypeValidator;
import org.apache.xerces.impl.dv.dtd.StringDatatypeValidator;

public class DTDDVFactoryImpl
extends DTDDVFactory {
    static final Hashtable fBuiltInTypes = new Hashtable();

    @Override
    public DatatypeValidator getBuiltInDV(String string) {
        return (DatatypeValidator)fBuiltInTypes.get(string);
    }

    @Override
    public Hashtable getBuiltInTypes() {
        return (Hashtable)fBuiltInTypes.clone();
    }

    static void createBuiltInTypes() {
        fBuiltInTypes.put("string", new StringDatatypeValidator());
        fBuiltInTypes.put("ID", new IDDatatypeValidator());
        DatatypeValidator datatypeValidator = new IDREFDatatypeValidator();
        fBuiltInTypes.put("IDREF", datatypeValidator);
        fBuiltInTypes.put("IDREFS", new ListDatatypeValidator(datatypeValidator));
        datatypeValidator = new ENTITYDatatypeValidator();
        fBuiltInTypes.put("ENTITY", new ENTITYDatatypeValidator());
        fBuiltInTypes.put("ENTITIES", new ListDatatypeValidator(datatypeValidator));
        fBuiltInTypes.put("NOTATION", new NOTATIONDatatypeValidator());
        datatypeValidator = new NMTOKENDatatypeValidator();
        fBuiltInTypes.put("NMTOKEN", datatypeValidator);
        fBuiltInTypes.put("NMTOKENS", new ListDatatypeValidator(datatypeValidator));
    }

    static {
        DTDDVFactoryImpl.createBuiltInTypes();
    }
}

