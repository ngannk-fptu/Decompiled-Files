/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dv.dtd;

import org.apache.xerces.impl.dv.DatatypeValidator;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.util.XMLChar;

public class IDREFDatatypeValidator
implements DatatypeValidator {
    @Override
    public void validate(String string, ValidationContext validationContext) throws InvalidDatatypeValueException {
        if (validationContext.useNamespaces()) {
            if (!XMLChar.isValidNCName(string)) {
                throw new InvalidDatatypeValueException("IDREFInvalidWithNamespaces", new Object[]{string});
            }
        } else if (!XMLChar.isValidName(string)) {
            throw new InvalidDatatypeValueException("IDREFInvalid", new Object[]{string});
        }
        validationContext.addIdRef(string);
    }
}

