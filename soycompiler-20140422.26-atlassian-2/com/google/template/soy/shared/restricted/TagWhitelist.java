/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package com.google.template.soy.shared.restricted;

import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

public final class TagWhitelist {
    private final ImmutableSet<String> safeTagNames;
    private static final Pattern VALID_TAG_NAME = Pattern.compile("^[A-Za-z][A-Za-z0-9]*(?:-[A-Za-z][A-Za-z0-9]*)*\\z");
    public static final TagWhitelist FORMATTING = new TagWhitelist("b", "br", "em", "i", "s", "sub", "sup", "u");

    TagWhitelist(Collection<? extends String> tagNames) {
        this.safeTagNames = ImmutableSet.copyOf(tagNames);
        assert (TagWhitelist.requireLowerCaseTagNames(this.safeTagNames));
    }

    TagWhitelist(String ... tagNames) {
        this(Arrays.asList(tagNames));
    }

    public boolean isSafeTag(String tagName) {
        return this.safeTagNames.contains((Object)tagName);
    }

    public Set<String> asSet() {
        return this.safeTagNames;
    }

    private static boolean requireLowerCaseTagNames(Iterable<? extends String> strs) {
        for (String string : strs) {
            assert (string.equals(string.toLowerCase(Locale.ENGLISH)) && VALID_TAG_NAME.matcher(string).matches()) : string;
        }
        return true;
    }
}

