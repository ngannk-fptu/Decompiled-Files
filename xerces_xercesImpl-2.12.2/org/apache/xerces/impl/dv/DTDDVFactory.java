/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dv;

import java.util.Hashtable;
import org.apache.xerces.impl.dv.DVFactoryException;
import org.apache.xerces.impl.dv.DatatypeValidator;
import org.apache.xerces.impl.dv.ObjectFactory;

public abstract class DTDDVFactory {
    private static final String DEFAULT_FACTORY_CLASS = "org.apache.xerces.impl.dv.dtd.DTDDVFactoryImpl";

    public static final DTDDVFactory getInstance() throws DVFactoryException {
        return DTDDVFactory.getInstance(DEFAULT_FACTORY_CLASS);
    }

    public static final DTDDVFactory getInstance(String string) throws DVFactoryException {
        try {
            return (DTDDVFactory)ObjectFactory.newInstance(string, ObjectFactory.findClassLoader(), true);
        }
        catch (ClassCastException classCastException) {
            throw new DVFactoryException("DTD factory class " + string + " does not extend from DTDDVFactory.");
        }
    }

    protected DTDDVFactory() {
    }

    public abstract DatatypeValidator getBuiltInDV(String var1);

    public abstract Hashtable getBuiltInTypes();
}

