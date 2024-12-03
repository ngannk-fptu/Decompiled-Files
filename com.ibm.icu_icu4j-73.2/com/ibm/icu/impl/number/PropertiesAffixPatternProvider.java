/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.impl.number;

import com.ibm.icu.impl.number.AffixPatternProvider;
import com.ibm.icu.impl.number.AffixUtils;
import com.ibm.icu.impl.number.CurrencyPluralInfoAffixProvider;
import com.ibm.icu.impl.number.DecimalFormatProperties;

public class PropertiesAffixPatternProvider
implements AffixPatternProvider {
    private final String posPrefix;
    private final String posSuffix;
    private final String negPrefix;
    private final String negSuffix;
    private final boolean isCurrencyPattern;
    private final boolean currencyAsDecimal;

    public static AffixPatternProvider forProperties(DecimalFormatProperties properties) {
        if (properties.getCurrencyPluralInfo() == null) {
            return new PropertiesAffixPatternProvider(properties);
        }
        return new CurrencyPluralInfoAffixProvider(properties.getCurrencyPluralInfo(), properties);
    }

    PropertiesAffixPatternProvider(DecimalFormatProperties properties) {
        String ppo = AffixUtils.escape(properties.getPositivePrefix());
        String pso = AffixUtils.escape(properties.getPositiveSuffix());
        String npo = AffixUtils.escape(properties.getNegativePrefix());
        String nso = AffixUtils.escape(properties.getNegativeSuffix());
        String ppp = properties.getPositivePrefixPattern();
        String psp = properties.getPositiveSuffixPattern();
        String npp = properties.getNegativePrefixPattern();
        String nsp = properties.getNegativeSuffixPattern();
        this.posPrefix = ppo != null ? ppo : (ppp != null ? ppp : "");
        this.posSuffix = pso != null ? pso : (psp != null ? psp : "");
        if (npo != null) {
            this.negPrefix = npo;
        } else if (npp != null) {
            this.negPrefix = npp;
        } else {
            String string = this.negPrefix = ppp == null ? "-" : "-" + ppp;
        }
        this.negSuffix = nso != null ? nso : (nsp != null ? nsp : (psp == null ? "" : psp));
        this.isCurrencyPattern = AffixUtils.hasCurrencySymbols(ppp) || AffixUtils.hasCurrencySymbols(psp) || AffixUtils.hasCurrencySymbols(npp) || AffixUtils.hasCurrencySymbols(nsp) || properties.getCurrencyAsDecimal();
        this.currencyAsDecimal = properties.getCurrencyAsDecimal();
    }

    @Override
    public char charAt(int flags, int i) {
        return this.getString(flags).charAt(i);
    }

    @Override
    public int length(int flags) {
        return this.getString(flags).length();
    }

    @Override
    public String getString(int flags) {
        boolean negative;
        boolean prefix = (flags & 0x100) != 0;
        boolean bl = negative = (flags & 0x200) != 0;
        if (prefix && negative) {
            return this.negPrefix;
        }
        if (prefix) {
            return this.posPrefix;
        }
        if (negative) {
            return this.negSuffix;
        }
        return this.posSuffix;
    }

    @Override
    public boolean positiveHasPlusSign() {
        return AffixUtils.containsType(this.posPrefix, -2) || AffixUtils.containsType(this.posSuffix, -2);
    }

    @Override
    public boolean hasNegativeSubpattern() {
        return this.negSuffix != this.posSuffix || this.negPrefix.length() != this.posPrefix.length() + 1 || !this.negPrefix.regionMatches(1, this.posPrefix, 0, this.posPrefix.length()) || this.negPrefix.charAt(0) != '-';
    }

    @Override
    public boolean negativeHasMinusSign() {
        return AffixUtils.containsType(this.negPrefix, -1) || AffixUtils.containsType(this.negSuffix, -1);
    }

    @Override
    public boolean hasCurrencySign() {
        return this.isCurrencyPattern;
    }

    @Override
    public boolean containsSymbolType(int type) {
        return AffixUtils.containsType(this.posPrefix, type) || AffixUtils.containsType(this.posSuffix, type) || AffixUtils.containsType(this.negPrefix, type) || AffixUtils.containsType(this.negSuffix, type);
    }

    @Override
    public boolean hasBody() {
        return true;
    }

    @Override
    public boolean currencyAsDecimal() {
        return this.currencyAsDecimal;
    }

    public String toString() {
        return super.toString() + " {" + this.posPrefix + "#" + this.posSuffix + ";" + this.negPrefix + "#" + this.negSuffix + "}";
    }
}

