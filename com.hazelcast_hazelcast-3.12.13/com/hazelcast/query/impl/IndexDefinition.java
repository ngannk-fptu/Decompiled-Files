/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.query.impl;

import com.hazelcast.query.QueryConstants;
import com.hazelcast.query.impl.predicates.PredicateUtils;
import com.hazelcast.util.StringUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.HashSet;
import java.util.regex.Pattern;

public final class IndexDefinition {
    private static final int MAX_INDEX_COMPONENTS = 255;
    private static final Pattern COMMA_PATTERN = Pattern.compile("\\s*,\\s*");
    private static final String BITMAP_PREFIX = "BITMAP(";
    private static final String BITMAP_POSTFIX = ")";
    private final String name;
    private final boolean ordered;
    private final String uniqueKey;
    private final UniqueKeyTransform uniqueKeyTransform;
    private final String[] components;

    private IndexDefinition(String name, boolean ordered, String ... components) {
        this.name = name;
        this.ordered = ordered;
        this.uniqueKey = null;
        this.uniqueKeyTransform = UniqueKeyTransform.OBJECT;
        this.components = components;
    }

    private IndexDefinition(String name, boolean ordered, String uniqueKey, UniqueKeyTransform uniqueKeyTransform, String ... components) {
        this.name = name;
        this.ordered = ordered;
        this.uniqueKey = uniqueKey;
        this.uniqueKeyTransform = uniqueKeyTransform;
        this.components = components;
    }

    public static IndexDefinition parse(String definition, boolean ordered) {
        IndexDefinition parsedDefinition = IndexDefinition.tryParseBitmap(definition, ordered);
        if (parsedDefinition != null) {
            return parsedDefinition;
        }
        parsedDefinition = IndexDefinition.tryParseComposite(definition, ordered);
        if (parsedDefinition != null) {
            return parsedDefinition;
        }
        String attribute = PredicateUtils.canonicalizeAttribute(definition);
        return new IndexDefinition(attribute, ordered, attribute);
    }

    private static IndexDefinition tryParseBitmap(String definition, boolean ordered) {
        UniqueKeyTransform uniqueKeyTransform;
        if (definition == null || !definition.toUpperCase().startsWith(BITMAP_PREFIX)) {
            return null;
        }
        if (!definition.endsWith(BITMAP_POSTFIX)) {
            throw IndexDefinition.makeInvalidBitmapIndexDefinitionException(definition);
        }
        String innerText = definition.substring(BITMAP_PREFIX.length(), definition.length() - 1);
        String[] parts = COMMA_PATTERN.split(innerText, -1);
        if (parts.length == 0 || parts.length > 3) {
            throw IndexDefinition.makeInvalidBitmapIndexDefinitionException(definition);
        }
        String attribute = PredicateUtils.canonicalizeAttribute(parts[0]);
        String uniqueKey = parts.length >= 2 ? PredicateUtils.canonicalizeAttribute(parts[1]) : QueryConstants.KEY_ATTRIBUTE_NAME.value();
        UniqueKeyTransform uniqueKeyTransform2 = uniqueKeyTransform = parts.length == 3 ? UniqueKeyTransform.parse(parts[2]) : UniqueKeyTransform.OBJECT;
        if (attribute.isEmpty() || uniqueKey.isEmpty() || attribute.equals(uniqueKey)) {
            throw IndexDefinition.makeInvalidBitmapIndexDefinitionException(definition);
        }
        String canonicalName = BITMAP_PREFIX + attribute + ", " + uniqueKey + ", " + (Object)((Object)uniqueKeyTransform) + BITMAP_POSTFIX;
        return new IndexDefinition(canonicalName, ordered, uniqueKey, uniqueKeyTransform, attribute);
    }

    private static IllegalArgumentException makeInvalidBitmapIndexDefinitionException(String definition) {
        return new IllegalArgumentException("Invalid bitmap index definition: " + definition);
    }

    private static IndexDefinition tryParseComposite(String definition, boolean ordered) {
        String[] attributes = COMMA_PATTERN.split(definition, -1);
        if (attributes.length == 1) {
            return null;
        }
        if (attributes.length > 255) {
            throw new IllegalArgumentException("Too many composite index attributes: " + definition);
        }
        HashSet<String> seenAttributes = new HashSet<String>(attributes.length);
        for (int i = 0; i < attributes.length; ++i) {
            String component;
            attributes[i] = component = PredicateUtils.canonicalizeAttribute(attributes[i]);
            if (component.isEmpty()) {
                throw new IllegalArgumentException("Empty composite index attribute: " + definition);
            }
            if (seenAttributes.add(component)) continue;
            throw new IllegalArgumentException("Duplicate composite index attribute: " + definition);
        }
        return new IndexDefinition(PredicateUtils.constructCanonicalCompositeIndexName(attributes), ordered, attributes);
    }

    public String getName() {
        return this.name;
    }

    public boolean isOrdered() {
        return this.ordered;
    }

    public String getUniqueKey() {
        return this.uniqueKey;
    }

    public UniqueKeyTransform getUniqueKeyTransform() {
        return this.uniqueKeyTransform;
    }

    @SuppressFBWarnings(value={"EI_EXPOSE_REP"})
    public String[] getComponents() {
        return this.components;
    }

    public static enum UniqueKeyTransform {
        OBJECT("OBJECT"),
        LONG("LONG"),
        RAW("RAW");

        private final String text;

        private UniqueKeyTransform(String text) {
            this.text = text;
        }

        private static UniqueKeyTransform parse(String text) {
            if (StringUtil.isNullOrEmpty(text)) {
                throw new IllegalArgumentException("empty unique key transform");
            }
            String upperCasedText = text.toUpperCase();
            if (upperCasedText.equals(UniqueKeyTransform.OBJECT.text)) {
                return OBJECT;
            }
            if (upperCasedText.equals(UniqueKeyTransform.LONG.text)) {
                return LONG;
            }
            if (upperCasedText.equals(UniqueKeyTransform.RAW.text)) {
                return RAW;
            }
            throw new IllegalArgumentException("unexpected unique key transform: " + text);
        }

        public String toString() {
            return this.text;
        }
    }
}

