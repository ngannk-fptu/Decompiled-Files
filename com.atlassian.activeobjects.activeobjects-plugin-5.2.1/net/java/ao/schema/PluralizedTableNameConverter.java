/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.collect.Lists
 */
package net.java.ao.schema;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.java.ao.schema.CanonicalClassNameTableNameConverter;
import net.java.ao.schema.OrderedProperties;
import net.java.ao.schema.TableNameConverter;
import net.java.ao.schema.TransformsTableNameConverter;

public final class PluralizedTableNameConverter
extends TransformsTableNameConverter {
    private TableNameConverter delegate;

    public PluralizedTableNameConverter() {
        this(new TransformsTableNameConverter.ClassNameTableNameConverter());
    }

    public PluralizedTableNameConverter(CanonicalClassNameTableNameConverter delegateTableNameConverter) {
        super(PluralizedTableNameConverter.transforms(), delegateTableNameConverter);
        this.delegate = delegateTableNameConverter;
    }

    private static List<TransformsTableNameConverter.Transform> transforms() {
        final OrderedProperties patterns = OrderedProperties.load("/net/java/ao/schema/englishPluralRules.properties");
        return Lists.transform((List)Lists.newArrayList((Iterable)patterns), (Function)new Function<String, TransformsTableNameConverter.Transform>(){

            public TransformsTableNameConverter.Transform apply(String from) {
                return new PatternTransform(from, (String)patterns.get(from));
            }
        });
    }

    public TableNameConverter getDelegate() {
        return this.delegate;
    }

    static final class PatternTransform
    implements TransformsTableNameConverter.Transform {
        private static final Pattern PLACE_HOLDER_PATTERN = Pattern.compile("(\\{\\})");
        private final Pattern patternToMatch;
        private final String transformationPattern;

        PatternTransform(String patternToMatch, String transformationPattern) {
            this.patternToMatch = Pattern.compile(Objects.requireNonNull(patternToMatch, "patternToMatch can't be null"), 2);
            this.transformationPattern = Objects.requireNonNull(transformationPattern, "transformationPattern can't be null");
        }

        @Override
        public boolean accept(String entityClassCanonicalName) {
            return this.patternToMatch.matcher(entityClassCanonicalName).matches();
        }

        @Override
        public String apply(String entityClassCanonicalName) {
            return PatternTransform.transform(this.patternToMatch, entityClassCanonicalName, this.transformationPattern);
        }

        static String transform(Pattern patternToMatch, String currentString, String transformationPattern) {
            Matcher m = patternToMatch.matcher(currentString);
            if (m.matches()) {
                return PatternTransform.replacePlaceHolders(transformationPattern, m.group(1));
            }
            return currentString;
        }

        static String replacePlaceHolders(String stringWithPlaceHolders, String value) {
            return PLACE_HOLDER_PATTERN.matcher(stringWithPlaceHolders).replaceAll(value);
        }
    }
}

