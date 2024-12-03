/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.money.CurrencyUnit
 *  javax.money.Monetary
 */
package org.springframework.format.number.money;

import java.util.Locale;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import org.springframework.format.Formatter;

public class CurrencyUnitFormatter
implements Formatter<CurrencyUnit> {
    @Override
    public String print(CurrencyUnit object, Locale locale) {
        return object.getCurrencyCode();
    }

    @Override
    public CurrencyUnit parse(String text, Locale locale) {
        return Monetary.getCurrency((String)text, (String[])new String[0]);
    }
}

