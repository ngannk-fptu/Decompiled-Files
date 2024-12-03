/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator.routines;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.validator.routines.CodeValidator;
import org.apache.commons.validator.routines.RegexValidator;
import org.apache.commons.validator.routines.checkdigit.CheckDigit;
import org.apache.commons.validator.routines.checkdigit.LuhnCheckDigit;

public class CreditCardValidator
implements Serializable {
    private static final long serialVersionUID = 5955978921148959496L;
    public static final long NONE = 0L;
    public static final long AMEX = 1L;
    public static final long VISA = 2L;
    public static final long MASTERCARD = 4L;
    public static final long DISCOVER = 8L;
    public static final long DINERS = 16L;
    public static final long VPAY = 32L;
    @Deprecated
    public static final long MASTERCARD_PRE_OCT2016 = 64L;
    private final List<CodeValidator> cardTypes = new ArrayList<CodeValidator>();
    private static final CheckDigit LUHN_VALIDATOR = LuhnCheckDigit.LUHN_CHECK_DIGIT;
    public static final CodeValidator AMEX_VALIDATOR = new CodeValidator("^(3[47]\\d{13})$", LUHN_VALIDATOR);
    public static final CodeValidator DINERS_VALIDATOR = new CodeValidator("^(30[0-5]\\d{11}|3095\\d{10}|36\\d{12}|3[8-9]\\d{12})$", LUHN_VALIDATOR);
    private static final RegexValidator DISCOVER_REGEX = new RegexValidator(new String[]{"^(6011\\d{12})$", "^(64[4-9]\\d{13})$", "^(65\\d{14})$"});
    public static final CodeValidator DISCOVER_VALIDATOR = new CodeValidator(DISCOVER_REGEX, LUHN_VALIDATOR);
    private static final RegexValidator MASTERCARD_REGEX = new RegexValidator(new String[]{"^(5[1-5]\\d{14})$", "^(2221\\d{12})$", "^(222[2-9]\\d{12})$", "^(22[3-9]\\d{13})$", "^(2[3-6]\\d{14})$", "^(27[01]\\d{13})$", "^(2720\\d{12})$"});
    public static final CodeValidator MASTERCARD_VALIDATOR = new CodeValidator(MASTERCARD_REGEX, LUHN_VALIDATOR);
    @Deprecated
    public static final CodeValidator MASTERCARD_VALIDATOR_PRE_OCT2016 = new CodeValidator("^(5[1-5]\\d{14})$", LUHN_VALIDATOR);
    public static final CodeValidator VISA_VALIDATOR = new CodeValidator("^(4)(\\d{12}|\\d{15})$", LUHN_VALIDATOR);
    public static final CodeValidator VPAY_VALIDATOR = new CodeValidator("^(4)(\\d{12,18})$", LUHN_VALIDATOR);

    public CreditCardValidator() {
        this(15L);
    }

    public CreditCardValidator(long options) {
        if (this.isOn(options, 2L)) {
            this.cardTypes.add(VISA_VALIDATOR);
        }
        if (this.isOn(options, 32L)) {
            this.cardTypes.add(VPAY_VALIDATOR);
        }
        if (this.isOn(options, 1L)) {
            this.cardTypes.add(AMEX_VALIDATOR);
        }
        if (this.isOn(options, 4L)) {
            this.cardTypes.add(MASTERCARD_VALIDATOR);
        }
        if (this.isOn(options, 64L)) {
            this.cardTypes.add(MASTERCARD_VALIDATOR_PRE_OCT2016);
        }
        if (this.isOn(options, 8L)) {
            this.cardTypes.add(DISCOVER_VALIDATOR);
        }
        if (this.isOn(options, 16L)) {
            this.cardTypes.add(DINERS_VALIDATOR);
        }
    }

    public CreditCardValidator(CodeValidator[] creditCardValidators) {
        if (creditCardValidators == null) {
            throw new IllegalArgumentException("Card validators are missing");
        }
        Collections.addAll(this.cardTypes, creditCardValidators);
    }

    public boolean isValid(String card) {
        if (card == null || card.length() == 0) {
            return false;
        }
        for (CodeValidator cardType : this.cardTypes) {
            if (!cardType.isValid(card)) continue;
            return true;
        }
        return false;
    }

    public Object validate(String card) {
        if (card == null || card.length() == 0) {
            return null;
        }
        Object result = null;
        for (CodeValidator cardType : this.cardTypes) {
            result = cardType.validate(card);
            if (result == null) continue;
            return result;
        }
        return null;
    }

    private boolean isOn(long options, long flag) {
        return (options & flag) > 0L;
    }
}

