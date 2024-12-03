/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dv.dtd;

import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.impl.dv.dtd.NMTOKENDatatypeValidator;
import org.apache.xerces.util.XML11Char;

public class XML11NMTOKENDatatypeValidator
extends NMTOKENDatatypeValidator {
    @Override
    public void validate(String string, ValidationContext validationContext) throws InvalidDatatypeValueException {
        if (!XML11Char.isXML11ValidNmtoken(string)) {
            throw new InvalidDatatypeValueException("NMTOKENInvalid", new Object[]{string});
        }
    }
}

