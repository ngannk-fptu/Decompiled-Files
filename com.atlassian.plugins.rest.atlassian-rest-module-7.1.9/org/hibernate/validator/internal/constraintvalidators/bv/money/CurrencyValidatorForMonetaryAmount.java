/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.money.CurrencyUnit
 *  javax.money.Monetary
 *  javax.money.MonetaryAmount
 *  javax.validation.ConstraintValidator
 *  javax.validation.ConstraintValidatorContext
 */
package org.hibernate.validator.internal.constraintvalidators.bv.money;

import java.util.ArrayList;
import java.util.List;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraints.Currency;
import org.hibernate.validator.internal.util.CollectionHelper;

public class CurrencyValidatorForMonetaryAmount
implements ConstraintValidator<Currency, MonetaryAmount> {
    private List<CurrencyUnit> acceptedCurrencies;

    public void initialize(Currency currency) {
        ArrayList<CurrencyUnit> acceptedCurrencies = new ArrayList<CurrencyUnit>();
        for (String currencyCode : currency.value()) {
            acceptedCurrencies.add(Monetary.getCurrency((String)currencyCode, (String[])new String[0]));
        }
        this.acceptedCurrencies = CollectionHelper.toImmutableList(acceptedCurrencies);
    }

    public boolean isValid(MonetaryAmount value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return this.acceptedCurrencies.contains(value.getCurrency());
    }
}

