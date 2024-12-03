/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dv.dtd;

import java.util.StringTokenizer;
import org.apache.xerces.impl.dv.DatatypeValidator;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;

public class ListDatatypeValidator
implements DatatypeValidator {
    final DatatypeValidator fItemValidator;

    public ListDatatypeValidator(DatatypeValidator datatypeValidator) {
        this.fItemValidator = datatypeValidator;
    }

    @Override
    public void validate(String string, ValidationContext validationContext) throws InvalidDatatypeValueException {
        StringTokenizer stringTokenizer = new StringTokenizer(string, " ");
        int n = stringTokenizer.countTokens();
        if (n == 0) {
            throw new InvalidDatatypeValueException("EmptyList", null);
        }
        while (stringTokenizer.hasMoreTokens()) {
            this.fItemValidator.validate(stringTokenizer.nextToken(), validationContext);
        }
    }
}

