/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator.routines.checkdigit;

import org.apache.commons.validator.routines.checkdigit.CheckDigitException;

public interface CheckDigit {
    public String calculate(String var1) throws CheckDigitException;

    public boolean isValid(String var1);
}

