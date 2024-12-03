/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.expression.spel.ast;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.spel.ast.Operator;
import org.springframework.expression.spel.ast.SpelNodeImpl;
import org.springframework.expression.spel.support.BooleanTypedValue;

public class OperatorMatches
extends Operator {
    private static final int PATTERN_ACCESS_THRESHOLD = 1000000;
    private static final int MAX_REGEX_LENGTH = 1000;
    private final ConcurrentMap<String, Pattern> patternCache;

    @Deprecated
    public OperatorMatches(int startPos, int endPos, SpelNodeImpl ... operands) {
        this(new ConcurrentHashMap<String, Pattern>(), startPos, endPos, operands);
    }

    public OperatorMatches(ConcurrentMap<String, Pattern> patternCache, int startPos, int endPos, SpelNodeImpl ... operands) {
        super("matches", startPos, endPos, operands);
        this.patternCache = patternCache;
    }

    @Override
    public BooleanTypedValue getValueInternal(ExpressionState state) throws EvaluationException {
        SpelNodeImpl leftOp = this.getLeftOperand();
        SpelNodeImpl rightOp = this.getRightOperand();
        String input = leftOp.getValue(state, String.class);
        if (input == null) {
            throw new SpelEvaluationException(leftOp.getStartPosition(), SpelMessage.INVALID_FIRST_OPERAND_FOR_MATCHES_OPERATOR, new Object[]{null});
        }
        Object right = rightOp.getValue(state);
        if (!(right instanceof String)) {
            throw new SpelEvaluationException(rightOp.getStartPosition(), SpelMessage.INVALID_SECOND_OPERAND_FOR_MATCHES_OPERATOR, right);
        }
        String regex = (String)right;
        try {
            Pattern pattern = (Pattern)this.patternCache.get(regex);
            if (pattern == null) {
                this.checkRegexLength(regex);
                pattern = Pattern.compile(regex);
                this.patternCache.putIfAbsent(regex, pattern);
            }
            Matcher matcher = pattern.matcher(new MatcherInput(input, new AccessCount()));
            return BooleanTypedValue.forValue(matcher.matches());
        }
        catch (PatternSyntaxException ex) {
            throw new SpelEvaluationException(rightOp.getStartPosition(), (Throwable)ex, SpelMessage.INVALID_PATTERN, right);
        }
        catch (IllegalStateException ex) {
            throw new SpelEvaluationException(rightOp.getStartPosition(), (Throwable)ex, SpelMessage.FLAWED_PATTERN, right);
        }
    }

    private void checkRegexLength(String regex) {
        if (regex.length() > 1000) {
            throw new SpelEvaluationException(this.getStartPosition(), SpelMessage.MAX_REGEX_LENGTH_EXCEEDED, 1000);
        }
    }

    private static class MatcherInput
    implements CharSequence {
        private final CharSequence value;
        private final AccessCount access;

        public MatcherInput(CharSequence value, AccessCount access) {
            this.value = value;
            this.access = access;
        }

        @Override
        public char charAt(int index) {
            this.access.check();
            return this.value.charAt(index);
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            return new MatcherInput(this.value.subSequence(start, end), this.access);
        }

        @Override
        public int length() {
            return this.value.length();
        }

        @Override
        public String toString() {
            return this.value.toString();
        }
    }

    private static class AccessCount {
        private int count;

        private AccessCount() {
        }

        public void check() throws IllegalStateException {
            if (this.count++ > 1000000) {
                throw new IllegalStateException("Pattern access threshold exceeded");
            }
        }
    }
}

