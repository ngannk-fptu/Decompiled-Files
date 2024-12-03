/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dv.dtd;

import org.apache.xerces.impl.dv.DatatypeValidator;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.util.XMLChar;

public class NMTOKENDatatypeValidator
implements DatatypeValidator {
    @Override
    public void validate(String string, ValidationContext validationContext) throws InvalidDatatypeValueException {
        if (!XMLChar.isValidNmtoken(string)) {
            throw new InvalidDatatypeValueException("NMTOKENInvalid", new Object[]{string});
        }
    }
}

