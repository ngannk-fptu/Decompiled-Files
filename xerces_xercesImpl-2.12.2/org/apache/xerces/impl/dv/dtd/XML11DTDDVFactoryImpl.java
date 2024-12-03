/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dv.dtd;

import java.util.Hashtable;
import java.util.Map;
import org.apache.xerces.impl.dv.DatatypeValidator;
import org.apache.xerces.impl.dv.dtd.DTDDVFactoryImpl;
import org.apache.xerces.impl.dv.dtd.ListDatatypeValidator;
import org.apache.xerces.impl.dv.dtd.XML11IDDatatypeValidator;
import org.apache.xerces.impl.dv.dtd.XML11IDREFDatatypeValidator;
import org.apache.xerces.impl.dv.dtd.XML11NMTOKENDatatypeValidator;

public class XML11DTDDVFactoryImpl
extends DTDDVFactoryImpl {
    static final Hashtable fXML11BuiltInTypes = new Hashtable();

    @Override
    public DatatypeValidator getBuiltInDV(String string) {
        if (fXML11BuiltInTypes.get(string) != null) {
            return (DatatypeValidator)fXML11BuiltInTypes.get(string);
        }
        return (DatatypeValidator)fBuiltInTypes.get(string);
    }

    @Override
    public Hashtable getBuiltInTypes() {
        Hashtable hashtable = (Hashtable)fBuiltInTypes.clone();
        for (Map.Entry entry : fXML11BuiltInTypes.entrySet()) {
            Object k = entry.getKey();
            Object v = entry.getValue();
            hashtable.put(k, v);
        }
        return hashtable;
    }

    static {
        fXML11BuiltInTypes.put("XML11ID", new XML11IDDatatypeValidator());
        DatatypeValidator datatypeValidator = new XML11IDREFDatatypeValidator();
        fXML11BuiltInTypes.put("XML11IDREF", datatypeValidator);
        fXML11BuiltInTypes.put("XML11IDREFS", new ListDatatypeValidator(datatypeValidator));
        datatypeValidator = new XML11NMTOKENDatatypeValidator();
        fXML11BuiltInTypes.put("XML11NMTOKEN", datatypeValidator);
        fXML11BuiltInTypes.put("XML11NMTOKENS", new ListDatatypeValidator(datatypeValidator));
    }
}

