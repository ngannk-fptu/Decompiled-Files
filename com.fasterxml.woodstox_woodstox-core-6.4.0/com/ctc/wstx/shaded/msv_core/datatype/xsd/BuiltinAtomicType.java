/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.ConcreteType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.DatatypeFactory;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.WhiteSpaceProcessor;
import java.io.ObjectStreamException;

public abstract class BuiltinAtomicType
extends ConcreteType {
    private static final long serialVersionUID = 1L;

    protected BuiltinAtomicType(String typeName, WhiteSpaceProcessor whiteSpace) {
        super("http://www.w3.org/2001/XMLSchema", typeName, whiteSpace);
    }

    protected BuiltinAtomicType(String typeName) {
        this(typeName, WhiteSpaceProcessor.theCollapse);
    }

    public final int getVariety() {
        return 1;
    }

    public final String displayName() {
        return this.getName();
    }

    protected Object readResolve() throws ObjectStreamException {
        String name = this.getName();
        if (name != null) {
            try {
                return DatatypeFactory.getTypeByName(name);
            }
            catch (DatatypeException datatypeException) {
                // empty catch block
            }
        }
        return this;
    }
}

