/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableSortedSet
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.labels;

import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.Namespace;
import com.atlassian.confluence.labels.ParsedLabelName;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableSortedSet;
import java.util.SortedSet;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class LabelParser {
    private static final SortedSet<Character> INVALID_LABEL_CHARACTERS = ImmutableSortedSet.of((Comparable)Character.valueOf(':'), (Comparable)Character.valueOf(';'), (Comparable)Character.valueOf(','), (Comparable)Character.valueOf('.'), (Comparable)Character.valueOf(' '), (Comparable)Character.valueOf('?'), (Comparable[])new Character[]{Character.valueOf('&'), Character.valueOf('['), Character.valueOf(']'), Character.valueOf('('), Character.valueOf(')'), Character.valueOf('#'), Character.valueOf('^'), Character.valueOf('*'), Character.valueOf('@'), Character.valueOf('!'), Character.valueOf('<'), Character.valueOf('>')});
    @Deprecated
    public static final char[] INVALID_CHARACTERS = ArrayUtils.toPrimitive((Character[])INVALID_LABEL_CHARACTERS.toArray(new Character[INVALID_LABEL_CHARACTERS.size()]));
    public static final int MAX_LABEL_NAME_LENGTH = 255;
    public static final int MAX_LABEL_PREFIX_LENGTH = 255;
    public static final String NAMESPACE_DELIMITER = ":";
    public static final String PERSONAL_LABEL_PREFIX = Namespace.PERSONAL.getPrefix() + ":";
    public static final String TEAM_LABEL_PREFIX = Namespace.TEAM.getPrefix() + ":";
    public static final String GLOBAL_LABEL_PREFIX = Namespace.GLOBAL.getPrefix() + ":";
    public static final String USER_LABEL_PREFIX = "~";

    private static boolean isValidLabelName(String name) {
        char[] chars;
        for (char c : chars = name.toCharArray()) {
            if (!Character.isWhitespace(c) && !LabelParser.isInvalidCharacter(c)) continue;
            return false;
        }
        return true;
    }

    private static boolean isInvalidCharacter(char c) {
        return INVALID_LABEL_CHARACTERS.contains(Character.valueOf(c));
    }

    public static boolean isValidLabelLength(ParsedLabelName labelName) {
        return LabelParser.isValidNameLength(labelName) && LabelParser.isValidPrefixLength(labelName);
    }

    public static boolean isValidNameLength(ParsedLabelName labelName) {
        return labelName.getName().length() <= 255;
    }

    public static boolean isValidPrefixLength(ParsedLabelName labelName) {
        return labelName.getPrefix() == null || labelName.getPrefix().length() <= 255;
    }

    public static boolean isPersonalLabel(String labelString) {
        ParsedLabelName label = LabelParser.parse(labelString, AuthenticatedUserThreadLocal.get());
        return label != null && PERSONAL_LABEL_PREFIX.equals(label.getPrefix());
    }

    public static boolean isLabelOwnedByUser(String labelString, User user) {
        if (user == null) {
            return false;
        }
        ParsedLabelName label = LabelParser.parse(labelString, user);
        if (label == null) {
            return false;
        }
        String labelOwner = label.getOwner();
        return PERSONAL_LABEL_PREFIX.equals(label.getPrefix()) && StringUtils.isNotBlank((CharSequence)labelOwner) && labelOwner.equalsIgnoreCase(user.getName());
    }

    public static @Nullable ParsedLabelName parse(@NonNull String labelString, @Nullable User user) {
        String prefix;
        String labelName;
        String userName;
        String lowerCaseLabel = labelString.toLowerCase();
        if (lowerCaseLabel.startsWith(PERSONAL_LABEL_PREFIX)) {
            userName = user != null ? user.getName() : null;
            labelName = labelString.substring(PERSONAL_LABEL_PREFIX.length());
            prefix = PERSONAL_LABEL_PREFIX;
        } else if (labelString.startsWith(USER_LABEL_PREFIX)) {
            int index = labelString.lastIndexOf(NAMESPACE_DELIMITER);
            if (index == -1) {
                return null;
            }
            userName = labelString.substring(1, index);
            if (StringUtils.isBlank((CharSequence)userName)) {
                return null;
            }
            labelName = labelString.substring(index + 1);
            prefix = PERSONAL_LABEL_PREFIX;
        } else if (lowerCaseLabel.startsWith(TEAM_LABEL_PREFIX)) {
            userName = null;
            labelName = labelString.substring(TEAM_LABEL_PREFIX.length());
            prefix = TEAM_LABEL_PREFIX;
        } else if (lowerCaseLabel.startsWith(GLOBAL_LABEL_PREFIX)) {
            userName = null;
            labelName = StringUtils.substringAfter((String)labelString, (String)GLOBAL_LABEL_PREFIX);
            prefix = GLOBAL_LABEL_PREFIX;
        } else {
            userName = null;
            labelName = labelString;
            prefix = null;
        }
        if (!LabelParser.isValidLabelName(labelName)) {
            return null;
        }
        return new ParsedLabelName(labelName, userName, prefix);
    }

    @Deprecated
    public static ParsedLabelName parse(String labelString) {
        return LabelParser.parse(labelString, AuthenticatedUserThreadLocal.get());
    }

    public static String getInvalidCharactersAsString() {
        return StringUtils.join(INVALID_LABEL_CHARACTERS, (String)", ");
    }

    public static ParsedLabelName create(String labelName, String userName) {
        if (!LabelParser.isValidLabelName(labelName)) {
            return null;
        }
        return new ParsedLabelName(labelName, userName);
    }

    public static ParsedLabelName create(Label label) {
        return new ParsedLabelName(label.getName(), label.getOwner());
    }

    public static String render(Label label) {
        return LabelParser.render(LabelParser.create(label), false);
    }

    public static String render(ParsedLabelName parsedLabelName) {
        return LabelParser.render(parsedLabelName, false);
    }

    public static String render(ParsedLabelName parsedLabelName, boolean ignoreCurrentUser) {
        return LabelParser.render(parsedLabelName, ignoreCurrentUser ? null : AuthenticatedUserThreadLocal.get());
    }

    public static String render(ParsedLabelName parsedLabelName, User currentUser) {
        StringBuilder output = new StringBuilder();
        if (currentUser != null && parsedLabelName.getOwner() != null && currentUser.getName() != null && currentUser.getName().equalsIgnoreCase(parsedLabelName.getOwner())) {
            output.append(PERSONAL_LABEL_PREFIX);
        } else if (parsedLabelName.getOwner() != null) {
            output.append(USER_LABEL_PREFIX);
            output.append(parsedLabelName.getOwner());
            output.append(NAMESPACE_DELIMITER);
        } else if (StringUtils.isNotEmpty((CharSequence)parsedLabelName.getPrefix())) {
            output.append(parsedLabelName.getPrefix());
            output.append(NAMESPACE_DELIMITER);
        }
        output.append(parsedLabelName.getName());
        return output.toString();
    }
}

