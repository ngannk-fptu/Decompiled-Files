/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.google.common.collect.Maps
 */
package com.google.template.soy.soytree;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.template.soy.base.SoySyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class CommandTextAttributesParser {
    private static final Pattern ATTRIBUTE_TEXT = Pattern.compile("([a-z][a-z-]*) = \" ([^\"]*) \" \\s*", 4);
    private final String commandName;
    private final Set<Attribute> supportedAttributes;
    private final Set<String> supportedAttributeNames;

    public CommandTextAttributesParser(String commandName, Attribute ... supportedAttributes) {
        this.commandName = commandName;
        this.supportedAttributes = ImmutableSet.copyOf((Object[])supportedAttributes);
        ImmutableSet.Builder supportedAttributeNamesBuilder = ImmutableSet.builder();
        for (Attribute attribute : supportedAttributes) {
            supportedAttributeNamesBuilder.add((Object)attribute.name);
            if (attribute.allowedValues == Attribute.ALLOW_ALL_VALUES || attribute.defaultValue == null || attribute.defaultValue == "__NDVBR__") continue;
            Preconditions.checkArgument((boolean)attribute.allowedValues.contains(attribute.defaultValue));
        }
        this.supportedAttributeNames = supportedAttributeNamesBuilder.build();
    }

    public Map<String, String> parse(String commandText) throws SoySyntaxException {
        HashMap attributes = Maps.newHashMap();
        int i = 0;
        Matcher matcher = ATTRIBUTE_TEXT.matcher(commandText);
        while (matcher.find(i)) {
            if (matcher.start() != i) {
                throw SoySyntaxException.createWithoutMetaInfo(String.format("Malformed attributes in '%s' command text (%s).", this.commandName, commandText));
            }
            i = matcher.end();
            String name = matcher.group(1);
            String value = matcher.group(2);
            if (!this.supportedAttributeNames.contains(name)) {
                throw SoySyntaxException.createWithoutMetaInfo(String.format("Unsupported attribute '%s' in '%s' command text (%s).", name, this.commandName, commandText));
            }
            if (attributes.containsKey(name)) {
                throw SoySyntaxException.createWithoutMetaInfo(String.format("Duplicate attribute '%s' in '%s' command text (%s).", name, this.commandName, commandText));
            }
            attributes.put(name, value);
        }
        if (i != commandText.length()) {
            throw SoySyntaxException.createWithoutMetaInfo(String.format("Malformed attributes in '%s' command text (%s).", this.commandName, commandText));
        }
        for (Attribute supportedAttribute : this.supportedAttributes) {
            if (attributes.containsKey(supportedAttribute.name)) {
                if (supportedAttribute.allowedValues == Attribute.ALLOW_ALL_VALUES || supportedAttribute.allowedValues.contains(attributes.get(supportedAttribute.name))) continue;
                throw SoySyntaxException.createWithoutMetaInfo(String.format("Invalid value for attribute '%s' in '%s' command text (%s).", supportedAttribute.name, this.commandName, commandText));
            }
            if ("__NDVBR__".equals(supportedAttribute.defaultValue)) {
                throw SoySyntaxException.createWithoutMetaInfo(String.format("Missing required attribute '%s' in '%s' command text (%s).", supportedAttribute.name, this.commandName, commandText));
            }
            attributes.put(supportedAttribute.name, supportedAttribute.defaultValue);
        }
        return attributes;
    }

    public static class Attribute {
        public static final Collection<String> ALLOW_ALL_VALUES = null;
        public static final Collection<String> BOOLEAN_VALUES = ImmutableSet.of((Object)"true", (Object)"false");
        public static final String NO_DEFAULT_VALUE_BECAUSE_REQUIRED = "__NDVBR__";
        final String name;
        final Collection<String> allowedValues;
        final String defaultValue;

        public Attribute(String name, Collection<String> allowedValues, String defaultValue) {
            this.name = name;
            this.allowedValues = allowedValues;
            this.defaultValue = defaultValue;
        }
    }
}

