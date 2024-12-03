/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.data.repository.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.springframework.data.domain.Range;
import org.springframework.data.repository.query.Parameters;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.data.repository.query.SpelEvaluator;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class SpelQueryContext {
    private static final String SPEL_PATTERN_STRING = "([:?])#\\{([^}]+)}";
    private static final Pattern SPEL_PATTERN = Pattern.compile("([:?])#\\{([^}]+)}");
    private final BiFunction<Integer, String, String> parameterNameSource;
    private final BiFunction<String, String, String> replacementSource;

    private SpelQueryContext(BiFunction<Integer, String, String> parameterNameSource, BiFunction<String, String, String> replacementSource) {
        Assert.notNull(parameterNameSource, (String)"Parameter name source must not be null");
        Assert.notNull(replacementSource, (String)"Replacement source must not be null");
        this.parameterNameSource = parameterNameSource;
        this.replacementSource = replacementSource;
    }

    public static SpelQueryContext of(BiFunction<Integer, String, String> parameterNameSource, BiFunction<String, String, String> replacementSource) {
        return new SpelQueryContext(parameterNameSource, replacementSource);
    }

    public SpelExtractor parse(String query) {
        return new SpelExtractor(query);
    }

    public EvaluatingSpelQueryContext withEvaluationContextProvider(QueryMethodEvaluationContextProvider provider) {
        Assert.notNull((Object)provider, (String)"QueryMethodEvaluationContextProvider must not be null!");
        return new EvaluatingSpelQueryContext(provider, this.parameterNameSource, this.replacementSource);
    }

    static class QuotationMap {
        private static final Collection<Character> QUOTING_CHARACTERS = Arrays.asList(Character.valueOf('\"'), Character.valueOf('\''));
        private final List<Range<Integer>> quotedRanges = new ArrayList<Range<Integer>>();

        public QuotationMap(@Nullable String query) {
            if (query == null) {
                return;
            }
            Character inQuotation = null;
            int start = 0;
            for (int i = 0; i < query.length(); ++i) {
                char currentChar = query.charAt(i);
                if (!QUOTING_CHARACTERS.contains(Character.valueOf(currentChar))) continue;
                if (inQuotation == null) {
                    inQuotation = Character.valueOf(currentChar);
                    start = i;
                    continue;
                }
                if (currentChar != inQuotation.charValue()) continue;
                inQuotation = null;
                this.quotedRanges.add(Range.from(Range.Bound.inclusive(start)).to(Range.Bound.inclusive(i)));
            }
            if (inQuotation != null) {
                throw new IllegalArgumentException(String.format("The string <%s> starts a quoted range at %d, but never ends it.", query, start));
            }
        }

        public boolean isQuoted(int index) {
            return this.quotedRanges.stream().anyMatch(r -> r.contains(index));
        }
    }

    public class SpelExtractor {
        private static final int PREFIX_GROUP_INDEX = 1;
        private static final int EXPRESSION_GROUP_INDEX = 2;
        private final String query;
        private final Map<String, String> expressions;
        private final QuotationMap quotations;

        SpelExtractor(String query) {
            Assert.notNull((Object)query, (String)"Query must not be null");
            HashMap<String, String> expressions = new HashMap<String, String>();
            Matcher matcher = SPEL_PATTERN.matcher(query);
            StringBuilder resultQuery = new StringBuilder();
            QuotationMap quotedAreas = new QuotationMap(query);
            int expressionCounter = 0;
            int matchedUntil = 0;
            while (matcher.find()) {
                if (quotedAreas.isQuoted(matcher.start())) {
                    resultQuery.append(query, matchedUntil, matcher.end());
                } else {
                    String spelExpression = matcher.group(2);
                    String prefix = matcher.group(1);
                    String parameterName = (String)SpelQueryContext.this.parameterNameSource.apply(expressionCounter, spelExpression);
                    String replacement = (String)SpelQueryContext.this.replacementSource.apply(prefix, parameterName);
                    resultQuery.append(query, matchedUntil, matcher.start());
                    resultQuery.append(replacement);
                    expressions.put(parameterName, spelExpression);
                    ++expressionCounter;
                }
                matchedUntil = matcher.end();
            }
            resultQuery.append(query.substring(matchedUntil));
            this.expressions = Collections.unmodifiableMap(expressions);
            this.query = resultQuery.toString();
            this.quotations = new QuotationMap(this.query);
        }

        public String getQueryString() {
            return this.query;
        }

        public boolean isQuoted(int index) {
            return this.quotations.isQuoted(index);
        }

        public String getParameter(String name) {
            return this.expressions.get(name);
        }

        Map<String, String> getParameterMap() {
            return this.expressions;
        }

        Stream<Map.Entry<String, String>> getParameters() {
            return this.expressions.entrySet().stream();
        }
    }

    public static class EvaluatingSpelQueryContext
    extends SpelQueryContext {
        private final QueryMethodEvaluationContextProvider evaluationContextProvider;

        private EvaluatingSpelQueryContext(QueryMethodEvaluationContextProvider evaluationContextProvider, BiFunction<Integer, String, String> parameterNameSource, BiFunction<String, String, String> replacementSource) {
            super(parameterNameSource, replacementSource);
            this.evaluationContextProvider = evaluationContextProvider;
        }

        public SpelEvaluator parse(String query, Parameters<?, ?> parameters) {
            return new SpelEvaluator(this.evaluationContextProvider, parameters, this.parse(query));
        }
    }
}

