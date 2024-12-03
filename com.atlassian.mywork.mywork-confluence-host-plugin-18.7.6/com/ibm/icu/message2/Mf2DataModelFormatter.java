/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.message2;

import com.ibm.icu.message2.DateTimeFormatterFactory;
import com.ibm.icu.message2.FormattedPlaceholder;
import com.ibm.icu.message2.Formatter;
import com.ibm.icu.message2.FormatterFactory;
import com.ibm.icu.message2.IdentityFormatterFactory;
import com.ibm.icu.message2.Mf2DataModel;
import com.ibm.icu.message2.Mf2FunctionRegistry;
import com.ibm.icu.message2.NumberFormatterFactory;
import com.ibm.icu.message2.PlainStringFormattedValue;
import com.ibm.icu.message2.PluralSelectorFactory;
import com.ibm.icu.message2.Selector;
import com.ibm.icu.message2.SelectorFactory;
import com.ibm.icu.message2.TextSelectorFactory;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.CurrencyAmount;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

class Mf2DataModelFormatter {
    private final Locale locale;
    private final Mf2DataModel dm;
    final Mf2FunctionRegistry standardFunctions;
    final Mf2FunctionRegistry customFunctions;
    private static final Mf2FunctionRegistry EMPTY_REGISTY = Mf2FunctionRegistry.builder().build();

    Mf2DataModelFormatter(Mf2DataModel dm, Locale locale, Mf2FunctionRegistry customFunctionRegistry) {
        this.locale = locale;
        this.dm = dm;
        this.customFunctions = customFunctionRegistry == null ? EMPTY_REGISTY : customFunctionRegistry;
        this.standardFunctions = Mf2FunctionRegistry.builder().setFormatter("datetime", new DateTimeFormatterFactory()).setDefaultFormatterNameForType(Date.class, "datetime").setDefaultFormatterNameForType(Calendar.class, "datetime").setFormatter("number", new NumberFormatterFactory()).setDefaultFormatterNameForType(Integer.class, "number").setDefaultFormatterNameForType(Double.class, "number").setDefaultFormatterNameForType(Number.class, "number").setDefaultFormatterNameForType(CurrencyAmount.class, "number").setFormatter("identity", new IdentityFormatterFactory()).setDefaultFormatterNameForType(String.class, "identity").setDefaultFormatterNameForType(CharSequence.class, "identity").setSelector("plural", new PluralSelectorFactory("cardinal")).setSelector("selectordinal", new PluralSelectorFactory("ordinal")).setSelector("select", new TextSelectorFactory()).setSelector("gender", new TextSelectorFactory()).build();
    }

    private static Map<String, Object> mf2OptToFixedOptions(Map<String, Mf2DataModel.Value> options) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        for (Map.Entry<String, Mf2DataModel.Value> option : options.entrySet()) {
            Mf2DataModel.Value value = option.getValue();
            if (!value.isLiteral()) continue;
            result.put(option.getKey(), value.getLiteral());
        }
        return result;
    }

    private Map<String, Object> mf2OptToVariableOptions(Map<String, Mf2DataModel.Value> options, Map<String, Object> arguments) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        for (Map.Entry<String, Mf2DataModel.Value> option : options.entrySet()) {
            Mf2DataModel.Value value = option.getValue();
            if (!value.isVariable()) continue;
            result.put(option.getKey(), this.variableToObjectEx(value, arguments));
        }
        return result;
    }

    FormatterFactory getFormattingFunctionFactoryByName(Object toFormat, String functionName) {
        FormatterFactory func;
        if (functionName == null || functionName.isEmpty()) {
            if (toFormat == null) {
                return null;
            }
            Class<?> clazz = toFormat.getClass();
            functionName = this.standardFunctions.getDefaultFormatterNameForType(clazz);
            if (functionName == null) {
                functionName = this.customFunctions.getDefaultFormatterNameForType(clazz);
            }
            if (functionName == null) {
                throw new IllegalArgumentException("Object to format without a function, and unknown type: " + toFormat.getClass().getName());
            }
        }
        if ((func = this.standardFunctions.getFormatter(functionName)) == null && (func = this.customFunctions.getFormatter(functionName)) == null) {
            throw new IllegalArgumentException("Can't find an implementation for function: '" + functionName + "'");
        }
        return func;
    }

    String format(Map<String, Object> arguments) {
        List<Mf2DataModel.Expression> selectors = this.dm.getSelectors();
        Mf2DataModel.Pattern patternToRender = selectors.isEmpty() ? this.dm.getPattern() : this.findBestMatchingPattern(selectors, arguments);
        StringBuilder result = new StringBuilder();
        for (Mf2DataModel.Part part : patternToRender.getParts()) {
            if (part instanceof Mf2DataModel.Text) {
                result.append(part);
                continue;
            }
            if (part instanceof Mf2DataModel.Expression) {
                FormattedPlaceholder fp = this.formatPlaceholder((Mf2DataModel.Expression)part, arguments, false);
                result.append(fp.toString());
                continue;
            }
            throw new IllegalArgumentException("Unknown part type: " + part);
        }
        return result.toString();
    }

    private Mf2DataModel.Pattern findBestMatchingPattern(List<Mf2DataModel.Expression> selectors, Map<String, Object> arguments) {
        Mf2DataModel.Pattern patternToRender = null;
        ArrayList<Selector> selectorFunctions = new ArrayList<Selector>(selectors.size());
        for (Mf2DataModel.Expression expression : selectors) {
            String functionName = expression.getFunctionName();
            SelectorFactory funcFactory = this.standardFunctions.getSelector(functionName);
            if (funcFactory == null) {
                funcFactory = this.customFunctions.getSelector(functionName);
            }
            if (funcFactory != null) {
                Map<String, Object> opt = Mf2DataModelFormatter.mf2OptToFixedOptions(expression.getOptions());
                selectorFunctions.add(funcFactory.createSelector(this.locale, opt));
                continue;
            }
            throw new IllegalArgumentException("Unknown selector type: " + functionName);
        }
        if (selectorFunctions.size() != selectors.size()) {
            throw new IllegalArgumentException("Something went wrong, not enough selector functions, " + selectorFunctions.size() + " vs. " + selectors.size());
        }
        for (Map.Entry entry : this.dm.getVariants().entrySet()) {
            int maxCount = selectors.size();
            List<String> keysToCheck = ((Mf2DataModel.SelectorKeys)entry.getKey()).getKeys();
            if (selectors.size() != keysToCheck.size()) {
                throw new IllegalArgumentException("Mismatch between the number of selectors and the number of keys: " + selectors.size() + " vs. " + keysToCheck.size());
            }
            boolean matches = true;
            for (int i = 0; i < maxCount; ++i) {
                Mf2DataModel.Expression selector = selectors.get(i);
                String valToCheck = keysToCheck.get(i);
                Selector func = (Selector)selectorFunctions.get(i);
                Map<String, Object> options = this.mf2OptToVariableOptions(selector.getOptions(), arguments);
                if (func.matches(this.variableToObjectEx(selector.getOperand(), arguments), valToCheck, options)) continue;
                matches = false;
                break;
            }
            if (!matches) continue;
            patternToRender = (Mf2DataModel.Pattern)entry.getValue();
            break;
        }
        if (patternToRender == null) {
            throw new IllegalArgumentException("The selection went wrong, cannot select any option.");
        }
        return patternToRender;
    }

    private Object variableToObjectEx(Mf2DataModel.Value value, Map<String, Object> arguments) {
        if (value == null) {
            return null;
        }
        if (value.isLiteral()) {
            return value.getLiteral();
        }
        if (value.isVariable()) {
            String varName = value.getVariableName();
            Mf2DataModel.Expression localPh = (Mf2DataModel.Expression)this.dm.getLocalVariables().get(varName);
            if (localPh != null) {
                return this.formatPlaceholder(localPh, arguments, false);
            }
            return arguments.get(varName);
        }
        throw new IllegalArgumentException("Invalid operand type " + value);
    }

    private FormattedPlaceholder formatPlaceholder(Mf2DataModel.Expression ph, Map<String, Object> arguments, boolean localExpression) {
        FormatterFactory funcFactory;
        Object toFormat;
        Mf2DataModel.Value operand = ph.getOperand();
        if (operand == null) {
            toFormat = null;
        } else if (operand.isLiteral()) {
            toFormat = operand.getLiteral();
        } else if (operand.isVariable()) {
            Mf2DataModel.Expression localPh;
            String varName = operand.getVariableName();
            if (!localExpression && (localPh = (Mf2DataModel.Expression)this.dm.getLocalVariables().get(varName)) != null) {
                return this.formatPlaceholder(localPh, arguments, true);
            }
            toFormat = arguments.get(varName);
        } else {
            throw new IllegalArgumentException("Invalid operand type " + ph.getOperand());
        }
        if (ph.formatter == null && (funcFactory = this.getFormattingFunctionFactoryByName(toFormat, ph.getFunctionName())) != null) {
            Formatter ff;
            Map<String, Object> fixedOptions = Mf2DataModelFormatter.mf2OptToFixedOptions(ph.getOptions());
            ph.formatter = ff = funcFactory.createFormatter(this.locale, fixedOptions);
        }
        if (ph.formatter != null) {
            Map<String, Object> variableOptions = this.mf2OptToVariableOptions(ph.getOptions(), arguments);
            try {
                return ph.formatter.format(toFormat, variableOptions);
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }
        return new FormattedPlaceholder(toFormat, new PlainStringFormattedValue("{" + ph.getOperand() + "}"));
    }
}

