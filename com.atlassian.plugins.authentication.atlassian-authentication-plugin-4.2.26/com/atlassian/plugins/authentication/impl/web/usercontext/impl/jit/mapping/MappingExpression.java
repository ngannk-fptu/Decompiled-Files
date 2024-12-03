/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit.mapping;

import com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit.mapping.MappingExpressionException;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MappingExpression {
    private static final Pattern VARIABLE_SELECT_REGEX = Pattern.compile("\\$\\{([^}]*)}", 128);
    private final String rawExpression;
    private final List<String> substitutionVariables;
    private final Matcher matcher;

    public MappingExpression(String rawExpression) {
        MappingExpression.validate(rawExpression);
        this.rawExpression = rawExpression;
        this.matcher = VARIABLE_SELECT_REGEX.matcher(rawExpression);
        this.substitutionVariables = this.parseSubstitutionVariables();
    }

    public static String toMappingExpressionVariable(String variableBase) {
        return new StringBuilder(variableBase).insert(0, "${").append("}").toString();
    }

    public static void validate(String expression) {
        boolean isVariableOpen = false;
        int previousVariableOpenerIndex = -1;
        for (int index = 0; index < expression.length(); ++index) {
            if (MappingExpression.isVariableOpener(expression, index)) {
                if (isVariableOpen) {
                    throw MappingExpressionException.bracketAlreadyOpened(index);
                }
                isVariableOpen = true;
                previousVariableOpenerIndex = index;
                continue;
            }
            if (!MappingExpression.isVariableCloser(expression.charAt(index))) continue;
            if (isVariableOpen) {
                if (MappingExpression.isVariableOpener(expression, index - 1)) {
                    throw MappingExpressionException.emptyVariable(index);
                }
                isVariableOpen = false;
                continue;
            }
            throw MappingExpressionException.bracketAlreadyClosed(index);
        }
        if (isVariableOpen) {
            throw MappingExpressionException.missingClosingBracket(previousVariableOpenerIndex);
        }
    }

    public static boolean containsVariableOpenerOrCloser(String value) {
        return value.matches(".*(\\$\\{|}).*");
    }

    public synchronized String evaluateWithValues(Map<String, String> substitutionValues) {
        if (substitutionValues.size() != this.substitutionVariables.size()) {
            throw new MappingExpressionException(String.format("Incorrect amount of substitution values: %d, required: %d", substitutionValues.size(), this.substitutionVariables.size()), -1);
        }
        StringBuffer buffer = new StringBuffer();
        this.matcher.reset();
        while (this.matcher.find()) {
            String substitutionValue = substitutionValues.get(this.matcher.group(1));
            if (substitutionValue == null) {
                throw new MappingExpressionException(String.format("Invalid substitution variable: [%s] was not found", this.matcher.group(1)), -1);
            }
            this.matcher.appendReplacement(buffer, Matcher.quoteReplacement(substitutionValue));
        }
        this.matcher.appendTail(buffer);
        return buffer.toString();
    }

    public synchronized String evaluateWithValues(UnaryOperator<String> variableNameToValueMappingFunction) {
        Map<String, String> variableSubstitutions = this.substitutionVariables.stream().collect(Collectors.toMap(varName -> varName, variableNameToValueMappingFunction));
        return this.evaluateWithValues(variableSubstitutions);
    }

    public String getRawExpression() {
        return this.rawExpression;
    }

    public List<String> getSubstitutionVariables() {
        return ImmutableList.copyOf(this.substitutionVariables);
    }

    public String toString() {
        return this.rawExpression;
    }

    private static boolean isVariableOpener(String expression, int index) {
        if (expression.charAt(index) == '{') {
            if (index >= 1 && expression.charAt(index - 1) == '$') {
                return true;
            }
            throw MappingExpressionException.invalidOpeningBracket(index);
        }
        return false;
    }

    private static boolean isVariableCloser(char character) {
        return character == '}';
    }

    private synchronized List<String> parseSubstitutionVariables() {
        ArrayList<String> variableNames = new ArrayList<String>();
        this.matcher.reset();
        while (this.matcher.find()) {
            variableNames.add(this.matcher.group(1));
        }
        return variableNames;
    }
}

