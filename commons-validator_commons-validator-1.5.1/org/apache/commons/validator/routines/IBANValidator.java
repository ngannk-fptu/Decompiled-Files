/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator.routines;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.validator.routines.RegexValidator;
import org.apache.commons.validator.routines.checkdigit.IBANCheckDigit;

public class IBANValidator {
    private final Map<String, Validator> formatValidators;
    private static final Validator[] DEFAULT_FORMATS = new Validator[]{new Validator("AL", 28, "AL\\d{10}[A-Z0-9]{16}"), new Validator("AD", 24, "AD\\d{10}[A-Z0-9]{12}"), new Validator("AT", 20, "AT\\d{18}"), new Validator("AZ", 28, "AZ\\d{2}[A-Z]{4}[A-Z0-9]{20}"), new Validator("BH", 22, "BH\\d{2}[A-Z]{4}[A-Z0-9]{14}"), new Validator("BE", 16, "BE\\d{14}"), new Validator("BA", 20, "BA\\d{18}"), new Validator("BR", 29, "BR\\d{25}[A-Z]{1}[A-Z0-9]{1}"), new Validator("BG", 22, "BG\\d{2}[A-Z]{4}\\d{6}[A-Z0-9]{8}"), new Validator("CR", 21, "CR\\d{19}"), new Validator("HR", 21, "HR\\d{19}"), new Validator("CY", 28, "CY\\d{10}[A-Z0-9]{16}"), new Validator("CZ", 24, "CZ\\d{22}"), new Validator("DK", 18, "DK\\d{16}"), new Validator("FO", 18, "FO\\d{16}"), new Validator("GL", 18, "GL\\d{16}"), new Validator("DO", 28, "DO\\d{2}[A-Z0-9]{4}\\d{20}"), new Validator("EE", 20, "EE\\d{18}"), new Validator("FI", 18, "FI\\d{16}"), new Validator("FR", 27, "FR\\d{12}[A-Z0-9]{11}\\d{2}"), new Validator("GE", 22, "GE\\d{2}[A-Z]{2}\\d{16}"), new Validator("DE", 22, "DE\\d{20}"), new Validator("GI", 23, "GI\\d{2}[A-Z]{4}[A-Z0-9]{15}"), new Validator("GR", 27, "GR\\d{9}[A-Z0-9]{16}"), new Validator("GT", 28, "GT\\d{2}[A-Z0-9]{24}"), new Validator("HU", 28, "HU\\d{26}"), new Validator("IS", 26, "IS\\d{24}"), new Validator("IE", 22, "IE\\d{2}[A-Z]{4}\\d{14}"), new Validator("IL", 23, "IL\\d{21}"), new Validator("IT", 27, "IT\\d{2}[A-Z]{1}\\d{10}[A-Z0-9]{12}"), new Validator("JO", 30, "JO\\d{2}[A-Z]{4}\\d{4}[A-Z0-9]{18}"), new Validator("KZ", 20, "KZ\\d{5}[A-Z0-9]{13}"), new Validator("XK", 20, "XK\\d{18}"), new Validator("KW", 30, "KW\\d{2}[A-Z]{4}[A-Z0-9]{22}"), new Validator("LV", 21, "LV\\d{2}[A-Z]{4}[A-Z0-9]{13}"), new Validator("LB", 28, "LB\\d{6}[A-Z0-9]{20}"), new Validator("LI", 21, "LI\\d{7}[A-Z0-9]{12}"), new Validator("LT", 20, "LT\\d{18}"), new Validator("LU", 20, "LU\\d{5}[A-Z0-9]{13}"), new Validator("MK", 19, "MK\\d{5}[A-Z0-9]{10}\\d{2}"), new Validator("MT", 31, "MT\\d{2}[A-Z]{4}\\d{5}[A-Z0-9]{18}"), new Validator("MR", 27, "MR13\\d{23}"), new Validator("MU", 30, "MU\\d{2}[A-Z]{4}\\d{19}[A-Z]{3}"), new Validator("MD", 24, "MD\\d{2}[A-Z0-9]{20}"), new Validator("MC", 27, "MC\\d{12}[A-Z0-9]{11}\\d{2}"), new Validator("ME", 22, "ME\\d{20}"), new Validator("NL", 18, "NL\\d{2}[A-Z]{4}\\d{10}"), new Validator("NO", 15, "NO\\d{13}"), new Validator("PK", 24, "PK\\d{2}[A-Z]{4}[A-Z0-9]{16}"), new Validator("PS", 29, "PS\\d{2}[A-Z]{4}[A-Z0-9]{21}"), new Validator("PL", 28, "PL\\d{26}"), new Validator("PT", 25, "PT\\d{23}"), new Validator("QA", 29, "QA\\d{2}[A-Z]{4}[A-Z0-9]{21}"), new Validator("RO", 24, "RO\\d{2}[A-Z]{4}[A-Z0-9]{16}"), new Validator("LC", 32, "LC\\d{2}[A-Z]{4}\\d{24}"), new Validator("SM", 27, "SM\\d{2}[A-Z]{1}\\d{10}[A-Z0-9]{12}"), new Validator("ST", 25, "ST\\d{23}"), new Validator("SA", 24, "SA\\d{4}[A-Z0-9]{18}"), new Validator("RS", 22, "RS\\d{20}"), new Validator("SK", 24, "SK\\d{22}"), new Validator("SI", 19, "SI\\d{17}"), new Validator("ES", 24, "ES\\d{22}"), new Validator("SE", 24, "SE\\d{22}"), new Validator("CH", 21, "CH\\d{7}[A-Z0-9]{12}"), new Validator("TL", 23, "TL\\d{21}"), new Validator("TN", 24, "TN59\\d{20}"), new Validator("TR", 26, "TR\\d{8}[A-Z0-9]{16}"), new Validator("AE", 23, "AE\\d{21}"), new Validator("GB", 22, "GB\\d{2}[A-Z]{4}\\d{14}"), new Validator("VG", 24, "VG\\d{2}[A-Z]{4}\\d{16}")};
    public static final IBANValidator DEFAULT_IBAN_VALIDATOR = new IBANValidator();

    public static IBANValidator getInstance() {
        return DEFAULT_IBAN_VALIDATOR;
    }

    public IBANValidator() {
        this(DEFAULT_FORMATS);
    }

    public IBANValidator(Validator[] formatMap) {
        this.formatValidators = this.createValidators(formatMap);
    }

    private Map<String, Validator> createValidators(Validator[] formatMap) {
        ConcurrentHashMap<String, Validator> m = new ConcurrentHashMap<String, Validator>();
        for (Validator v : formatMap) {
            m.put(v.countryCode, v);
        }
        return m;
    }

    public boolean isValid(String code) {
        Validator formatValidator = this.getValidator(code);
        if (formatValidator == null || code.length() != formatValidator.lengthOfIBAN || !formatValidator.validator.isValid(code)) {
            return false;
        }
        return IBANCheckDigit.IBAN_CHECK_DIGIT.isValid(code);
    }

    public boolean hasValidator(String code) {
        return this.getValidator(code) != null;
    }

    public Validator[] getDefaultValidators() {
        return Arrays.copyOf(DEFAULT_FORMATS, DEFAULT_FORMATS.length);
    }

    public Validator getValidator(String code) {
        if (code == null || code.length() < 2) {
            return null;
        }
        String key = code.substring(0, 2);
        return this.formatValidators.get(key);
    }

    public Validator setValidator(Validator validator) {
        if (this == DEFAULT_IBAN_VALIDATOR) {
            throw new IllegalStateException("The singleton validator cannot be modified");
        }
        return this.formatValidators.put(validator.countryCode, validator);
    }

    public Validator setValidator(String countryCode, int length, String format) {
        if (this == DEFAULT_IBAN_VALIDATOR) {
            throw new IllegalStateException("The singleton validator cannot be modified");
        }
        if (length < 0) {
            return this.formatValidators.remove(countryCode);
        }
        return this.setValidator(new Validator(countryCode, length, format));
    }

    public static class Validator {
        private static final int MIN_LEN = 8;
        private static final int MAX_LEN = 34;
        final String countryCode;
        final RegexValidator validator;
        final int lengthOfIBAN;

        public Validator(String cc, int len, String format) {
            if (cc.length() != 2 || !Character.isUpperCase(cc.charAt(0)) || !Character.isUpperCase(cc.charAt(1))) {
                throw new IllegalArgumentException("Invalid country Code; must be exactly 2 upper-case characters");
            }
            if (len > 34 || len < 8) {
                throw new IllegalArgumentException("Invalid length parameter, must be in range 8 to 34 inclusive: " + len);
            }
            if (!format.startsWith(cc)) {
                throw new IllegalArgumentException("countryCode '" + cc + "' does not agree with format: " + format);
            }
            this.countryCode = cc;
            this.lengthOfIBAN = len;
            this.validator = new RegexValidator(format);
        }
    }
}

