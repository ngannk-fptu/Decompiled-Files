/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 */
package com.atlassian.data.activeobjects.repository.query;

import com.atlassian.data.activeobjects.repository.query.DeclaredQuery;
import com.atlassian.data.activeobjects.repository.query.QueryUtils;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.SpelQueryContext;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

class StringQuery
implements DeclaredQuery {
    private static final Logger log = LoggerFactory.getLogger(StringQuery.class);
    private final String query;
    private final List<ParameterBinding> bindings;
    @Nullable
    private final String alias;
    private final boolean hasConstructorExpression;
    private final boolean containsPageableInSpel;
    private final boolean usesJdbcStyleParameters;

    StringQuery(String query) {
        Assert.hasText((String)query, (String)"Query must not be null or empty!");
        this.bindings = new ArrayList<ParameterBinding>();
        this.containsPageableInSpel = query.contains("#pageable");
        Metadata queryMeta = new Metadata();
        this.query = ParameterBindingParser.INSTANCE.parseParameterBindingsOfQueryIntoBindingsAndReturnCleanedQuery(query, this.bindings, queryMeta);
        this.usesJdbcStyleParameters = queryMeta.usesJdbcStyleParameters;
        this.alias = QueryUtils.detectAlias(query);
        this.hasConstructorExpression = QueryUtils.hasConstructorExpression(query);
    }

    boolean hasParameterBindings() {
        return !this.bindings.isEmpty();
    }

    String getProjection() {
        return QueryUtils.getProjection(this.query);
    }

    @Override
    public List<ParameterBinding> getParameterBindings() {
        return this.bindings;
    }

    @Override
    public DeclaredQuery deriveCountQuery(@Nullable String countQuery, @Nullable String countQueryProjection) {
        return DeclaredQuery.of(countQuery != null ? countQuery : QueryUtils.createCountQueryFor(this.query, countQueryProjection));
    }

    @Override
    public boolean usesJdbcStyleParameters() {
        return this.usesJdbcStyleParameters;
    }

    @Override
    public String getQueryString() {
        return this.query;
    }

    @Override
    @Nullable
    public String getAlias() {
        return this.alias;
    }

    @Override
    public boolean hasConstructorExpression() {
        return this.hasConstructorExpression;
    }

    @Override
    public boolean isDefaultProjection() {
        return this.getProjection().equalsIgnoreCase(this.alias);
    }

    @Override
    public boolean hasNamedParameter() {
        return this.bindings.stream().anyMatch(b -> b.getName() != null);
    }

    @Override
    public boolean usesPaging() {
        return this.containsPageableInSpel;
    }

    static class Metadata {
        private boolean usesJdbcStyleParameters = false;

        Metadata() {
        }
    }

    static class LikeParameterBinding
    extends ParameterBinding {
        private static final List<Part.Type> SUPPORTED_TYPES = Arrays.asList(Part.Type.CONTAINING, Part.Type.STARTING_WITH, Part.Type.ENDING_WITH, Part.Type.LIKE);
        private final Part.Type type;

        LikeParameterBinding(String name, Part.Type type) {
            this(name, type, null);
        }

        LikeParameterBinding(String name, Part.Type type, @Nullable String expression) {
            super(name, null, expression);
            Assert.hasText((String)name, (String)"Name must not be null or empty!");
            Assert.notNull((Object)((Object)type), (String)"Type must not be null!");
            Assert.isTrue((boolean)SUPPORTED_TYPES.contains((Object)type), (String)String.format("Type must be one of %s!", StringUtils.collectionToCommaDelimitedString(SUPPORTED_TYPES)));
            this.type = type;
        }

        LikeParameterBinding(int position, Part.Type type) {
            this(position, type, null);
        }

        LikeParameterBinding(int position, Part.Type type, @Nullable String expression) {
            super(null, position, expression);
            Assert.isTrue((position > 0 ? 1 : 0) != 0, (String)"Position must be greater than zero!");
            Assert.notNull((Object)((Object)type), (String)"Type must not be null!");
            Assert.isTrue((boolean)SUPPORTED_TYPES.contains((Object)type), (String)String.format("Type must be one of %s!", StringUtils.collectionToCommaDelimitedString(SUPPORTED_TYPES)));
            this.type = type;
        }

        public Part.Type getType() {
            return this.type;
        }

        @Override
        @Nullable
        public Object prepare(@Nullable Object value) {
            if (value == null) {
                return null;
            }
            switch (this.type) {
                case STARTING_WITH: {
                    return String.format("%s%%", value.toString());
                }
                case ENDING_WITH: {
                    return String.format("%%%s", value.toString());
                }
                case CONTAINING: {
                    return String.format("%%%s%%", value.toString());
                }
            }
            return value;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof LikeParameterBinding)) {
                return false;
            }
            LikeParameterBinding that = (LikeParameterBinding)obj;
            return super.equals(obj) && this.type.equals((Object)that.type);
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            return result += ObjectUtils.nullSafeHashCode((Object)((Object)this.type));
        }

        @Override
        public String toString() {
            return String.format("LikeBinding [name: %s, position: %d, type: %s]", new Object[]{this.getName(), this.getPosition(), this.type});
        }

        private static Part.Type getLikeTypeFrom(String expression) {
            Assert.hasText((String)expression, (String)"Expression must not be null or empty!");
            if (expression.matches("%.*%")) {
                return Part.Type.CONTAINING;
            }
            if (expression.startsWith("%")) {
                return Part.Type.ENDING_WITH;
            }
            if (expression.endsWith("%")) {
                return Part.Type.STARTING_WITH;
            }
            return Part.Type.LIKE;
        }
    }

    static class InParameterBinding
    extends ParameterBinding {
        InParameterBinding(String name, @Nullable String expression) {
            super(name, null, expression);
        }

        InParameterBinding(int position, @Nullable String expression) {
            super(null, position, expression);
        }

        @Override
        public Object prepare(@Nullable Object value) {
            if (!ObjectUtils.isArray((Object)value)) {
                return value;
            }
            int length = Array.getLength(value);
            ArrayList<Object> result = new ArrayList<Object>(length);
            for (int i = 0; i < length; ++i) {
                result.add(Array.get(value, i));
            }
            return result;
        }
    }

    static class ParameterBinding {
        @Nullable
        private final String name;
        @Nullable
        private final String expression;
        @Nullable
        private final Integer position;

        ParameterBinding(Integer position) {
            this(null, position, null);
        }

        ParameterBinding(@Nullable String name, @Nullable Integer position, @Nullable String expression) {
            if (name == null) {
                Assert.notNull((Object)position, (String)"Position must not be null!");
            }
            if (position == null) {
                Assert.notNull((Object)name, (String)"Name must not be null!");
            }
            this.name = name;
            this.position = position;
            this.expression = expression;
        }

        boolean hasName(@Nullable String name) {
            return this.position == null && this.name != null && this.name.equals(name);
        }

        boolean hasPosition(@Nullable Integer position) {
            return position != null && this.name == null && position.equals(this.position);
        }

        @Nullable
        public String getName() {
            return this.name;
        }

        String getRequiredName() {
            String reqName = this.getName();
            if (reqName != null) {
                return reqName;
            }
            throw new IllegalStateException(String.format("Required name for %s not available!", this));
        }

        @Nullable
        Integer getPosition() {
            return this.position;
        }

        int getRequiredPosition() {
            Integer reqPos = this.getPosition();
            if (reqPos != null) {
                return reqPos;
            }
            throw new IllegalStateException(String.format("Required position for %s not available!", this));
        }

        public boolean isExpression() {
            return this.expression != null;
        }

        public int hashCode() {
            int result = 17;
            result += ObjectUtils.nullSafeHashCode((Object)this.name);
            result += ObjectUtils.nullSafeHashCode((Object)this.position);
            return result += ObjectUtils.nullSafeHashCode((Object)this.expression);
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof ParameterBinding)) {
                return false;
            }
            ParameterBinding that = (ParameterBinding)obj;
            return ObjectUtils.nullSafeEquals((Object)this.name, (Object)that.name) && ObjectUtils.nullSafeEquals((Object)this.position, (Object)that.position) && ObjectUtils.nullSafeEquals((Object)this.expression, (Object)that.expression);
        }

        public String toString() {
            return String.format("ParameterBinding [name: %s, position: %d, expression: %s]", this.getName(), this.getPosition(), this.getExpression());
        }

        @Nullable
        public Object prepare(@Nullable Object valueToBind) {
            return valueToBind;
        }

        @Nullable
        public String getExpression() {
            return this.expression;
        }
    }

    static enum ParameterBindingParser {
        INSTANCE;

        private static final String EXPRESSION_PARAMETER_PREFIX = "__$synthetic$__";
        public static final String POSITIONAL_OR_INDEXED_PARAMETER = "\\?(\\d*+(?![#\\w]))";
        private static final Pattern PARAMETER_BINDING_BY_INDEX;
        private static final Pattern PARAMETER_BINDING_PATTERN;
        private static final String MESSAGE = "Already found parameter binding with same index / parameter name but differing binding type! Already have: %s, found %s! If you bind a parameter multiple times make sure they use the same binding.";
        private static final int INDEXED_PARAMETER_GROUP = 4;
        private static final int NAMED_PARAMETER_GROUP = 6;
        private static final int COMPARISION_TYPE_GROUP = 1;

        private String parseParameterBindingsOfQueryIntoBindingsAndReturnCleanedQuery(String query, List<ParameterBinding> bindings, Metadata queryMeta) {
            boolean parametersShouldBeAccessedByIndex;
            int greatestParameterIndex = ParameterBindingParser.tryFindGreatestParameterIndexIn(query);
            boolean bl = parametersShouldBeAccessedByIndex = greatestParameterIndex != -1;
            if (!parametersShouldBeAccessedByIndex && query.contains("?#{")) {
                parametersShouldBeAccessedByIndex = true;
                greatestParameterIndex = 0;
            }
            SpelQueryContext.SpelExtractor spelExtractor = ParameterBindingParser.createSpelExtractor(query, parametersShouldBeAccessedByIndex, greatestParameterIndex);
            String resultingQuery = spelExtractor.getQueryString();
            Matcher matcher = PARAMETER_BINDING_PATTERN.matcher(resultingQuery);
            int expressionParameterIndex = parametersShouldBeAccessedByIndex ? greatestParameterIndex : 0;
            boolean usesJpaStyleParameters = false;
            while (matcher.find()) {
                if (spelExtractor.isQuoted(matcher.start())) continue;
                String parameterIndexString = matcher.group(4);
                String parameterName = parameterIndexString != null ? null : matcher.group(6);
                Integer parameterIndex = ParameterBindingParser.getParameterIndex(parameterIndexString);
                String typeSource = matcher.group(1);
                String expression = spelExtractor.getParameter(parameterName == null ? parameterIndexString : parameterName);
                String replacement = null;
                Assert.isTrue((parameterIndexString != null || parameterName != null ? 1 : 0) != 0, () -> String.format("We need either a name or an index! Offending query string: %s", query));
                ++expressionParameterIndex;
                if ("".equals(parameterIndexString)) {
                    queryMeta.usesJdbcStyleParameters = true;
                    parameterIndex = expressionParameterIndex;
                } else {
                    usesJpaStyleParameters = true;
                }
                if (usesJpaStyleParameters && queryMeta.usesJdbcStyleParameters) {
                    throw new IllegalArgumentException("Mixing of ? parameters and other forms like ?1 is not supported! Problem query: " + query);
                }
                switch (ParameterBindingType.of(typeSource)) {
                    case LIKE: {
                        Part.Type likeType = LikeParameterBinding.getLikeTypeFrom(matcher.group(2));
                        replacement = matcher.group(3);
                        if (parameterIndex != null) {
                            ParameterBindingParser.checkAndRegister(new LikeParameterBinding(parameterIndex, likeType, expression), bindings);
                            break;
                        }
                        ParameterBindingParser.checkAndRegister(new LikeParameterBinding(parameterName, likeType, expression), bindings);
                        replacement = expression != null ? ":" + parameterName : matcher.group(5);
                        break;
                    }
                    case IN: {
                        if (parameterIndex != null) {
                            ParameterBindingParser.checkAndRegister(new InParameterBinding(parameterIndex, expression), bindings);
                            break;
                        }
                        ParameterBindingParser.checkAndRegister(new InParameterBinding(parameterName, expression), bindings);
                        break;
                    }
                    default: {
                        bindings.add(parameterIndex != null ? new ParameterBinding(null, parameterIndex, expression) : new ParameterBinding(parameterName, null, expression));
                    }
                }
                if (replacement == null) continue;
                resultingQuery = ParameterBindingParser.replaceFirst(resultingQuery, matcher.group(2), replacement);
            }
            log.debug("Returning parsed query: [{}]", (Object)resultingQuery);
            return resultingQuery;
        }

        private static SpelQueryContext.SpelExtractor createSpelExtractor(String queryWithSpel, boolean parametersShouldBeAccessedByIndex, int greatestParameterIndex) {
            int expressionParameterIndex = parametersShouldBeAccessedByIndex ? greatestParameterIndex : 0;
            BiFunction<Integer, String, String> indexToParameterName = parametersShouldBeAccessedByIndex ? (index, expression) -> String.valueOf(index + expressionParameterIndex + 1) : (index, expression) -> EXPRESSION_PARAMETER_PREFIX + (index + 1);
            String fixedPrefix = parametersShouldBeAccessedByIndex ? "?" : ":";
            BiFunction<String, String, String> parameterNameToReplacement = (prefix, name) -> fixedPrefix + name;
            return SpelQueryContext.of(indexToParameterName, parameterNameToReplacement).parse(queryWithSpel);
        }

        private static String replaceFirst(String text, String substring, String replacement) {
            int index = text.indexOf(substring);
            if (index < 0) {
                return text;
            }
            return text.substring(0, index) + replacement + text.substring(index + substring.length());
        }

        @Nullable
        private static Integer getParameterIndex(@Nullable String parameterIndexString) {
            if (parameterIndexString == null || parameterIndexString.isEmpty()) {
                return null;
            }
            return Integer.valueOf(parameterIndexString);
        }

        private static int tryFindGreatestParameterIndexIn(String query) {
            Matcher parameterIndexMatcher = PARAMETER_BINDING_BY_INDEX.matcher(query);
            int greatestParameterIndex = -1;
            while (parameterIndexMatcher.find()) {
                String parameterIndexString = parameterIndexMatcher.group(1);
                Integer parameterIndex = ParameterBindingParser.getParameterIndex(parameterIndexString);
                if (parameterIndex == null) continue;
                greatestParameterIndex = Math.max(greatestParameterIndex, parameterIndex);
            }
            return greatestParameterIndex;
        }

        private static void checkAndRegister(ParameterBinding binding, List<ParameterBinding> bindings) {
            bindings.stream().filter(it -> it.hasName(binding.getName()) || it.hasPosition(binding.getPosition())).forEach(it -> Assert.isTrue((boolean)it.equals(binding), (String)String.format(MESSAGE, it, binding)));
            if (!bindings.contains(binding)) {
                bindings.add(binding);
            }
        }

        static {
            PARAMETER_BINDING_BY_INDEX = Pattern.compile(POSITIONAL_OR_INDEXED_PARAMETER);
            ArrayList<String> keywords = new ArrayList<String>();
            for (ParameterBindingType type : ParameterBindingType.values()) {
                if (type.getKeyword() == null) continue;
                keywords.add(type.getKeyword());
            }
            StringBuilder builder = new StringBuilder();
            builder.append("(");
            builder.append(StringUtils.collectionToDelimitedString(keywords, (String)"|"));
            builder.append(")?");
            builder.append("(?: )?");
            builder.append("\\(?");
            builder.append("(");
            builder.append("%?(\\?(\\d*+(?![#\\w])))%?");
            builder.append("|");
            builder.append("%?((?<![:\\\\]):" + QueryUtils.IDENTIFIER_GROUP + ")%?");
            builder.append(")");
            builder.append("\\)?");
            PARAMETER_BINDING_PATTERN = Pattern.compile(builder.toString(), 2);
        }

        private static enum ParameterBindingType {
            LIKE("like "),
            IN("in "),
            AS_IS(null);

            @Nullable
            private final String keyword;

            private ParameterBindingType(String keyword) {
                this.keyword = keyword;
            }

            @Nullable
            public String getKeyword() {
                return this.keyword;
            }

            static ParameterBindingType of(String typeSource) {
                if (!StringUtils.hasText((String)typeSource)) {
                    return AS_IS;
                }
                for (ParameterBindingType type : ParameterBindingType.values()) {
                    if (!type.name().equalsIgnoreCase(typeSource.trim())) continue;
                    return type;
                }
                throw new IllegalArgumentException(String.format("Unsupported parameter binding type %s!", typeSource));
            }
        }
    }
}

