/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator;

@Deprecated
public class ISBNValidator {
    public boolean isValid(String isbn) {
        return org.apache.commons.validator.routines.ISBNValidator.getInstance().isValidISBN10(isbn);
    }
}

