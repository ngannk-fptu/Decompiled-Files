/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.impl.number;

import com.ibm.icu.text.DecimalFormatSymbols;
import com.ibm.icu.util.Currency;
import com.ibm.icu.util.ULocale;

public class CustomSymbolCurrency
extends Currency {
    private static final long serialVersionUID = 2497493016770137670L;
    private String symbol1;
    private String symbol2;

    public static Currency resolve(Currency currency, ULocale locale, DecimalFormatSymbols symbols) {
        if (currency == null) {
            currency = symbols.getCurrency();
        }
        if (currency == null) {
            return Currency.getInstance("XXX");
        }
        if (!currency.equals(symbols.getCurrency())) {
            return currency;
        }
        String currency1Sym = symbols.getCurrencySymbol();
        String currency2Sym = symbols.getInternationalCurrencySymbol();
        String currency1 = currency.getName(symbols.getULocale(), 0, null);
        String currency2 = currency.getCurrencyCode();
        if (!currency1.equals(currency1Sym) || !currency2.equals(currency2Sym)) {
            return new CustomSymbolCurrency(currency2, currency1Sym, currency2Sym);
        }
        return currency;
    }

    public CustomSymbolCurrency(String isoCode, String currency1Sym, String currency2Sym) {
        super(isoCode);
        this.symbol1 = currency1Sym;
        this.symbol2 = currency2Sym;
    }

    @Override
    public String getName(ULocale locale, int nameStyle, boolean[] isChoiceFormat) {
        if (nameStyle == 0) {
            return this.symbol1;
        }
        return super.getName(locale, nameStyle, isChoiceFormat);
    }

    @Override
    public String getName(ULocale locale, int nameStyle, String pluralCount, boolean[] isChoiceFormat) {
        return super.getName(locale, nameStyle, pluralCount, isChoiceFormat);
    }

    @Override
    public String getCurrencyCode() {
        return this.symbol2;
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ this.symbol1.hashCode() ^ this.symbol2.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return super.equals(other) && ((CustomSymbolCurrency)other).symbol1.equals(this.symbol1) && ((CustomSymbolCurrency)other).symbol2.equals(this.symbol2);
    }
}

