/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.data.repository.query.parser;

import java.beans.Introspector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public class Part {
    private static final Pattern IGNORE_CASE = Pattern.compile("Ignor(ing|e)Case");
    private final PropertyPath propertyPath;
    private final Type type;
    private IgnoreCaseType ignoreCase = IgnoreCaseType.NEVER;

    public Part(String source, Class<?> clazz) {
        this(source, clazz, false);
    }

    public Part(String source, Class<?> clazz, boolean alwaysIgnoreCase) {
        Assert.hasText((String)source, (String)"Part source must not be null or empty!");
        Assert.notNull(clazz, (String)"Type must not be null!");
        String partToUse = this.detectAndSetIgnoreCase(source);
        if (alwaysIgnoreCase && this.ignoreCase != IgnoreCaseType.ALWAYS) {
            this.ignoreCase = IgnoreCaseType.WHEN_POSSIBLE;
        }
        this.type = Type.fromProperty(partToUse);
        this.propertyPath = PropertyPath.from(this.type.extractProperty(partToUse), clazz);
    }

    private String detectAndSetIgnoreCase(String part) {
        Matcher matcher = IGNORE_CASE.matcher(part);
        String result = part;
        if (matcher.find()) {
            this.ignoreCase = IgnoreCaseType.ALWAYS;
            result = part.substring(0, matcher.start()) + part.substring(matcher.end(), part.length());
        }
        return result;
    }

    boolean isParameterRequired() {
        return this.getNumberOfArguments() > 0;
    }

    public int getNumberOfArguments() {
        return this.type.getNumberOfArguments();
    }

    public PropertyPath getProperty() {
        return this.propertyPath;
    }

    public Type getType() {
        return this.type;
    }

    public IgnoreCaseType shouldIgnoreCase() {
        return this.ignoreCase;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Part)) {
            return false;
        }
        Part part = (Part)o;
        if (!ObjectUtils.nullSafeEquals((Object)this.propertyPath, (Object)part.propertyPath)) {
            return false;
        }
        if (this.type != part.type) {
            return false;
        }
        return this.ignoreCase == part.ignoreCase;
    }

    public int hashCode() {
        int result = ObjectUtils.nullSafeHashCode((Object)this.propertyPath);
        result = 31 * result + ObjectUtils.nullSafeHashCode((Object)((Object)this.type));
        result = 31 * result + ObjectUtils.nullSafeHashCode((Object)((Object)this.ignoreCase));
        return result;
    }

    public String toString() {
        return String.format("%s %s %s", new Object[]{this.propertyPath.getSegment(), this.type, this.ignoreCase});
    }

    public static enum IgnoreCaseType {
        NEVER,
        ALWAYS,
        WHEN_POSSIBLE;

    }

    public static enum Type {
        BETWEEN(2, "IsBetween", "Between"),
        IS_NOT_NULL(0, "IsNotNull", "NotNull"),
        IS_NULL(0, "IsNull", "Null"),
        LESS_THAN("IsLessThan", "LessThan"),
        LESS_THAN_EQUAL("IsLessThanEqual", "LessThanEqual"),
        GREATER_THAN("IsGreaterThan", "GreaterThan"),
        GREATER_THAN_EQUAL("IsGreaterThanEqual", "GreaterThanEqual"),
        BEFORE("IsBefore", "Before"),
        AFTER("IsAfter", "After"),
        NOT_LIKE("IsNotLike", "NotLike"),
        LIKE("IsLike", "Like"),
        STARTING_WITH("IsStartingWith", "StartingWith", "StartsWith"),
        ENDING_WITH("IsEndingWith", "EndingWith", "EndsWith"),
        IS_NOT_EMPTY(0, "IsNotEmpty", "NotEmpty"),
        IS_EMPTY(0, "IsEmpty", "Empty"),
        NOT_CONTAINING("IsNotContaining", "NotContaining", "NotContains"),
        CONTAINING("IsContaining", "Containing", "Contains"),
        NOT_IN("IsNotIn", "NotIn"),
        IN("IsIn", "In"),
        NEAR("IsNear", "Near"),
        WITHIN("IsWithin", "Within"),
        REGEX("MatchesRegex", "Matches", "Regex"),
        EXISTS(0, "Exists"),
        TRUE(0, "IsTrue", "True"),
        FALSE(0, "IsFalse", "False"),
        NEGATING_SIMPLE_PROPERTY("IsNot", "Not"),
        SIMPLE_PROPERTY("Is", "Equals");

        private static final List<Type> ALL;
        public static final Collection<String> ALL_KEYWORDS;
        private final List<String> keywords;
        private final int numberOfArguments;

        private Type(int numberOfArguments, String ... keywords) {
            this.numberOfArguments = numberOfArguments;
            this.keywords = Arrays.asList(keywords);
        }

        private Type(String ... keywords) {
            this(1, keywords);
        }

        public static Type fromProperty(String rawProperty) {
            for (Type type : ALL) {
                if (!type.supports(rawProperty)) continue;
                return type;
            }
            return SIMPLE_PROPERTY;
        }

        public Collection<String> getKeywords() {
            return Collections.unmodifiableList(this.keywords);
        }

        protected boolean supports(String property) {
            for (String keyword : this.keywords) {
                if (!property.endsWith(keyword)) continue;
                return true;
            }
            return false;
        }

        public int getNumberOfArguments() {
            return this.numberOfArguments;
        }

        public String extractProperty(String part) {
            String candidate = Introspector.decapitalize(part);
            for (String keyword : this.keywords) {
                if (!candidate.endsWith(keyword)) continue;
                return candidate.substring(0, candidate.length() - keyword.length());
            }
            return candidate;
        }

        public String toString() {
            return String.format("%s (%s): %s", this.name(), this.getNumberOfArguments(), this.getKeywords());
        }

        static {
            ALL = Arrays.asList(IS_NOT_NULL, IS_NULL, BETWEEN, LESS_THAN, LESS_THAN_EQUAL, GREATER_THAN, GREATER_THAN_EQUAL, BEFORE, AFTER, NOT_LIKE, LIKE, STARTING_WITH, ENDING_WITH, IS_NOT_EMPTY, IS_EMPTY, NOT_CONTAINING, CONTAINING, NOT_IN, IN, NEAR, WITHIN, REGEX, EXISTS, TRUE, FALSE, NEGATING_SIMPLE_PROPERTY, SIMPLE_PROPERTY);
            ArrayList<String> allKeywords = new ArrayList<String>();
            for (Type type : ALL) {
                allKeywords.addAll(type.keywords);
            }
            ALL_KEYWORDS = Collections.unmodifiableList(allKeywords);
        }
    }
}

