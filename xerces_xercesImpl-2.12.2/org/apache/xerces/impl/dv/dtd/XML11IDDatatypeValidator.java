/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dv.dtd;

import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.impl.dv.dtd.IDDatatypeValidator;
import org.apache.xerces.util.XML11Char;

public class XML11IDDatatypeValidator
extends IDDatatypeValidator {
    @Override
    public void validate(String string, ValidationContext validationContext) throws InvalidDatatypeValueException {
        if (validationContext.useNamespaces()) {
            if (!XML11Char.isXML11ValidNCName(string)) {
                throw new InvalidDatatypeValueException("IDInvalidWithNamespaces", new Object[]{string});
            }
        } else if (!XML11Char.isXML11ValidName(string)) {
            throw new InvalidDatatypeValueException("IDInvalid", new Object[]{string});
        }
        if (validationContext.isIdDeclared(string)) {
            throw new InvalidDatatypeValueException("IDNotUnique", new Object[]{string});
        }
        validationContext.addId(string);
    }
}

