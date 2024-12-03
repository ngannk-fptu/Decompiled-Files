/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator;

import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.validator.util.Flags;

@Deprecated
public class CreditCardValidator {
    public static final int NONE = 0;
    public static final int AMEX = 1;
    public static final int VISA = 2;
    public static final int MASTERCARD = 4;
    public static final int DISCOVER = 8;
    private final Collection<CreditCardType> cardTypes = new ArrayList<CreditCardType>();

    public CreditCardValidator() {
        this(15);
    }

    public CreditCardValidator(int options) {
        Flags f = new Flags(options);
        if (f.isOn(2L)) {
            this.cardTypes.add(new Visa());
        }
        if (f.isOn(1L)) {
            this.cardTypes.add(new Amex());
        }
        if (f.isOn(4L)) {
            this.cardTypes.add(new Mastercard());
        }
        if (f.isOn(8L)) {
            this.cardTypes.add(new Discover());
        }
    }

    public boolean isValid(String card) {
        if (card == null || card.length() < 13 || card.length() > 19) {
            return false;
        }
        if (!this.luhnCheck(card)) {
            return false;
        }
        for (CreditCardType cardType : this.cardTypes) {
            CreditCardType type = cardType;
            if (!type.matches(card)) continue;
            return true;
        }
        return false;
    }

    public void addAllowedCardType(CreditCardType type) {
        this.cardTypes.add(type);
    }

    protected boolean luhnCheck(String cardNumber) {
        int digits = cardNumber.length();
        int oddOrEven = digits & 1;
        long sum = 0L;
        for (int count = 0; count < digits; ++count) {
            int digit = 0;
            try {
                digit = Integer.parseInt(cardNumber.charAt(count) + "");
            }
            catch (NumberFormatException e) {
                return false;
            }
            if ((count & 1 ^ oddOrEven) == 0 && (digit *= 2) > 9) {
                digit -= 9;
            }
            sum += (long)digit;
        }
        return sum == 0L ? false : sum % 10L == 0L;
    }

    private static class Mastercard
    implements CreditCardType {
        private static final String PREFIX = "51,52,53,54,55,";

        private Mastercard() {
        }

        @Override
        public boolean matches(String card) {
            String prefix2 = card.substring(0, 2) + ",";
            return PREFIX.contains(prefix2) && card.length() == 16;
        }
    }

    private static class Discover
    implements CreditCardType {
        private static final String PREFIX = "6011";

        private Discover() {
        }

        @Override
        public boolean matches(String card) {
            return card.substring(0, 4).equals(PREFIX) && card.length() == 16;
        }
    }

    private static class Amex
    implements CreditCardType {
        private static final String PREFIX = "34,37,";

        private Amex() {
        }

        @Override
        public boolean matches(String card) {
            String prefix2 = card.substring(0, 2) + ",";
            return PREFIX.contains(prefix2) && card.length() == 15;
        }
    }

    private static class Visa
    implements CreditCardType {
        private static final String PREFIX = "4";

        private Visa() {
        }

        @Override
        public boolean matches(String card) {
            return card.substring(0, 1).equals(PREFIX) && (card.length() == 13 || card.length() == 16);
        }
    }

    public static interface CreditCardType {
        public boolean matches(String var1);
    }
}

