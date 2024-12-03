/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dv.xs;

import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.impl.dv.xs.TypeValidator;
import org.apache.xerces.util.XMLChar;

public class IDREFDV
extends TypeValidator {
    @Override
    public short getAllowedFacets() {
        return 2079;
    }

    @Override
    public Object getActualValue(String string, ValidationContext validationContext) throws InvalidDatatypeValueException {
        if (!XMLChar.isValidNCName(string)) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{string, "NCName"});
        }
        return string;
    }

    @Override
    public void checkExtraRules(Object object, ValidationContext validationContext) throws InvalidDatatypeValueException {
        validationContext.addIdRef((String)object);
    }
}

