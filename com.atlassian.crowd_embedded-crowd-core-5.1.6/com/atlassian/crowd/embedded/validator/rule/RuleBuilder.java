/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.util.I18nHelper
 */
package com.atlassian.crowd.embedded.validator.rule;

import com.atlassian.crowd.embedded.validator.FieldValidationError;
import com.atlassian.crowd.embedded.validator.ValidationRule;
import com.atlassian.crowd.util.I18nHelper;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RuleBuilder<V> {
    private String fieldName;
    private Function<V, String> message;
    private final List<Condition> conditions = new ArrayList<Condition>();

    public static Predicate<String> regex(String pattern) {
        return value -> value != null && value.matches(pattern);
    }

    public static Predicate<String> notNull() {
        return Objects::nonNull;
    }

    public static Predicate<String> isNull() {
        return Objects::isNull;
    }

    public static Predicate<String> eq(String testValue) {
        return value -> Objects.equals(value, testValue);
    }

    public static <K> Predicate<K> not(Predicate<K> predicate) {
        return predicate.negate();
    }

    public static <K> Predicate<K> matchesAny(Predicate<K> ... predicates) {
        return Arrays.stream(predicates).reduce(Predicate::or).get();
    }

    public static <K> Predicate<K> matchesAll(Predicate<K> ... predicates) {
        return Arrays.stream(predicates).reduce(Predicate::and).get();
    }

    public static Supplier<String> message(I18nHelper i18nHelper, String messageKey) {
        return () -> i18nHelper.getText(messageKey);
    }

    public static Predicate<String> isValidURI() {
        return value -> {
            try {
                if (value == null) {
                    return false;
                }
                URI uri = new URI((String)value);
                return uri.getHost() != null;
            }
            catch (URISyntaxException e) {
                return false;
            }
        };
    }

    public static Predicate<String> inLongRange(Long min, Long max) {
        return value -> {
            try {
                Long actualValue = Long.parseLong(value);
                return actualValue >= min && actualValue <= max;
            }
            catch (NumberFormatException e) {
                return false;
            }
        };
    }

    public static Predicate<String> greaterThanOrEquals(Long number) {
        return value -> {
            try {
                Long actualValue = Long.parseLong(value);
                return actualValue >= number;
            }
            catch (NumberFormatException e) {
                return false;
            }
        };
    }

    public static Predicate<String> isValidRegex() {
        return value -> {
            try {
                if (value == null) {
                    return false;
                }
                Pattern.compile(value);
                return true;
            }
            catch (PatternSyntaxException e) {
                return false;
            }
        };
    }

    public RuleBuilder(String fieldName) {
        this.fieldName = fieldName;
    }

    public <M> RuleBuilder<V> check(Function<V, M> valueRetriever, Predicate<M> predicate) {
        this.conditions.add(new Condition(predicate, valueRetriever));
        return this;
    }

    public RuleBuilder<V> check(Predicate<V> valueRetriever, boolean expectedValue) {
        this.conditions.add(new Condition(arg -> arg == expectedValue, valueRetriever::test));
        return this;
    }

    public RuleBuilder<V> ifMatchesThenSet(Supplier<String> messageSupplier) {
        this.message = unused -> (String)messageSupplier.get();
        return this;
    }

    public RuleBuilder<V> ifMatchesThenSet(Function<V, String> messageFunction) {
        this.message = messageFunction;
        return this;
    }

    public ValidationRule<V> build() {
        return arg -> this.conditions.stream().allMatch(condition -> condition.evaluate(arg)) ? FieldValidationError.of(this.fieldName, this.message.apply(arg)) : null;
    }

    private final class Condition {
        final Predicate<V> validationFunction = valueFunction.andThen(predicate::test)::apply;

        <M> Condition(Predicate<M> predicate, Function<V, M> valueFunction) {
        }

        boolean evaluate(V argument) {
            return this.validationFunction.test(argument);
        }
    }
}

