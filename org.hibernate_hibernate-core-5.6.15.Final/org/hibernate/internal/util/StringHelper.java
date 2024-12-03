/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import org.hibernate.boot.model.source.internal.hbm.CommaSeparatedStringHelper;
import org.hibernate.dialect.Dialect;
import org.hibernate.loader.internal.AliasConstantsHelper;

public final class StringHelper {
    private static final int ALIAS_TRUNCATE_LENGTH = 10;
    public static final String WHITESPACE = " \n\r\f\t";
    public static final String[] EMPTY_STRINGS = new String[0];
    public static final String BATCH_ID_PLACEHOLDER = "$$BATCH_ID_PLACEHOLDER$$";

    private StringHelper() {
    }

    public static int lastIndexOfLetter(String string) {
        for (int i = 0; i < string.length(); ++i) {
            char character = string.charAt(i);
            if (Character.isLetter(character) || '_' == character) continue;
            return i - 1;
        }
        return string.length() - 1;
    }

    public static String joinWithQualifierAndSuffix(String[] values, String qualifier, String suffix, String deliminator) {
        int length = values.length;
        if (length == 0) {
            return "";
        }
        StringBuilder buf = new StringBuilder(length * (values[0].length() + suffix.length())).append(StringHelper.qualify(qualifier, values[0])).append(suffix);
        for (int i = 1; i < length; ++i) {
            buf.append(deliminator).append(StringHelper.qualify(qualifier, values[i])).append(suffix);
        }
        return buf.toString();
    }

    public static String join(String separator, Iterator<?> objects) {
        StringBuilder buf = new StringBuilder();
        if (objects.hasNext()) {
            buf.append(objects.next());
        }
        while (objects.hasNext()) {
            buf.append(separator).append(objects.next());
        }
        return buf.toString();
    }

    public static String[] add(String[] x, String sep, String[] y) {
        String[] result = new String[x.length];
        for (int i = 0; i < x.length; ++i) {
            result[i] = x[i] + sep + y[i];
        }
        return result;
    }

    public static String repeat(String string, int times) {
        StringBuilder buf = new StringBuilder(string.length() * times);
        for (int i = 0; i < times; ++i) {
            buf.append(string);
        }
        return buf.toString();
    }

    public static String repeat(String string, int times, String deliminator) {
        StringBuilder buf = new StringBuilder(string.length() * times + deliminator.length() * (times - 1)).append(string);
        for (int i = 1; i < times; ++i) {
            buf.append(deliminator).append(string);
        }
        return buf.toString();
    }

    public static String repeat(char character, int times) {
        char[] buffer = new char[times];
        Arrays.fill(buffer, character);
        return new String(buffer);
    }

    public static String replace(String template, String placeholder, String replacement) {
        return StringHelper.replace(template, placeholder, replacement, false);
    }

    public static String[] replace(String[] templates, String placeholder, String replacement) {
        String[] result = new String[templates.length];
        for (int i = 0; i < templates.length; ++i) {
            result[i] = StringHelper.replace(templates[i], placeholder, replacement);
        }
        return result;
    }

    public static String replace(String template, String placeholder, String replacement, boolean wholeWords) {
        return StringHelper.replace(template, placeholder, replacement, wholeWords, false);
    }

    public static String replace(String template, String placeholder, String replacement, boolean wholeWords, boolean encloseInParensIfNecessary) {
        if (template == null) {
            return null;
        }
        int loc = StringHelper.indexOfPlaceHolder(template, placeholder, wholeWords);
        if (loc < 0) {
            return template;
        }
        String beforePlaceholder = template.substring(0, loc);
        String afterPlaceholder = template.substring(loc + placeholder.length());
        return StringHelper.replace(beforePlaceholder, afterPlaceholder, placeholder, replacement, wholeWords, encloseInParensIfNecessary);
    }

    public static String replace(String beforePlaceholder, String afterPlaceholder, String placeholder, String replacement, boolean wholeWords, boolean encloseInParensIfNecessary) {
        boolean actuallyReplace = !wholeWords || afterPlaceholder.length() == 0 || !Character.isJavaIdentifierPart(afterPlaceholder.charAt(0));
        boolean encloseInParens = actuallyReplace && encloseInParensIfNecessary && StringHelper.getLastNonWhitespaceCharacter(beforePlaceholder) != '(' && (StringHelper.getLastNonWhitespaceCharacter(beforePlaceholder) != ',' || StringHelper.getFirstNonWhitespaceCharacter(afterPlaceholder) != ')');
        StringBuilder buf = new StringBuilder(beforePlaceholder);
        if (encloseInParens) {
            buf.append('(');
        }
        buf.append(actuallyReplace ? replacement : placeholder);
        if (encloseInParens) {
            buf.append(')');
        }
        buf.append(StringHelper.replace(afterPlaceholder, placeholder, replacement, wholeWords, encloseInParensIfNecessary));
        return buf.toString();
    }

    private static int indexOfPlaceHolder(String template, String placeholder, boolean wholeWords) {
        if (wholeWords) {
            boolean isPartialPlaceholderMatch;
            int placeholderIndex = -1;
            do {
                boolean bl = isPartialPlaceholderMatch = (placeholderIndex = template.indexOf(placeholder, placeholderIndex + 1)) != -1 && template.length() > placeholderIndex + placeholder.length() && Character.isJavaIdentifierPart(template.charAt(placeholderIndex + placeholder.length()));
            } while (placeholderIndex != -1 && isPartialPlaceholderMatch);
            return placeholderIndex;
        }
        return template.indexOf(placeholder);
    }

    public static int indexOfIdentifierWord(String str, String word) {
        if (str == null || str.length() == 0 || word == null || word.length() == 0) {
            return -1;
        }
        int position = str.indexOf(word);
        while (position >= 0 && position < str.length()) {
            if (!(position != 0 && Character.isJavaIdentifierPart(str.charAt(position - 1)) || position + word.length() != str.length() && Character.isJavaIdentifierPart(str.charAt(position + word.length())))) {
                return position;
            }
            position = str.indexOf(word, position + 1);
        }
        return -1;
    }

    public static char getLastNonWhitespaceCharacter(String str) {
        if (str != null && str.length() > 0) {
            for (int i = str.length() - 1; i >= 0; --i) {
                char ch = str.charAt(i);
                if (Character.isWhitespace(ch)) continue;
                return ch;
            }
        }
        return '\u0000';
    }

    public static char getFirstNonWhitespaceCharacter(String str) {
        if (str != null && str.length() > 0) {
            for (int i = 0; i < str.length(); ++i) {
                char ch = str.charAt(i);
                if (Character.isWhitespace(ch)) continue;
                return ch;
            }
        }
        return '\u0000';
    }

    public static String replaceOnce(String template, String placeholder, String replacement) {
        if (template == null) {
            return null;
        }
        int loc = template.indexOf(placeholder);
        if (loc < 0) {
            return template;
        }
        return template.substring(0, loc) + replacement + template.substring(loc + placeholder.length());
    }

    public static String[] split(String separators, String list) {
        return StringHelper.split(separators, list, false);
    }

    public static String[] split(String separators, String list, boolean include) {
        StringTokenizer tokens = new StringTokenizer(list, separators, include);
        String[] result = new String[tokens.countTokens()];
        int i = 0;
        while (tokens.hasMoreTokens()) {
            result[i++] = tokens.nextToken();
        }
        return result;
    }

    public static String[] splitTrimmingTokens(String separators, String list, boolean include) {
        StringTokenizer tokens = new StringTokenizer(list, separators, include);
        String[] result = new String[tokens.countTokens()];
        int i = 0;
        while (tokens.hasMoreTokens()) {
            result[i++] = tokens.nextToken().trim();
        }
        return result;
    }

    public static String unqualify(String qualifiedName) {
        int loc = qualifiedName.lastIndexOf(46);
        return loc < 0 ? qualifiedName : qualifiedName.substring(loc + 1);
    }

    public static String qualifier(String qualifiedName) {
        int loc = qualifiedName.lastIndexOf(46);
        return loc < 0 ? "" : qualifiedName.substring(0, loc);
    }

    public static String collapse(String name) {
        if (name == null) {
            return null;
        }
        int breakPoint = name.lastIndexOf(46);
        if (breakPoint < 0) {
            return name;
        }
        return StringHelper.collapseQualifier(name.substring(0, breakPoint), true) + name.substring(breakPoint);
    }

    public static String collapseQualifier(String qualifier, boolean includeDots) {
        StringTokenizer tokenizer = new StringTokenizer(qualifier, ".");
        StringBuilder sb = new StringBuilder();
        sb.append(Character.toString(tokenizer.nextToken().charAt(0)));
        while (tokenizer.hasMoreTokens()) {
            if (includeDots) {
                sb.append('.');
            }
            sb.append(tokenizer.nextToken().charAt(0));
        }
        return sb.toString();
    }

    public static String partiallyUnqualify(String name, String qualifierBase) {
        if (name == null || !name.startsWith(qualifierBase)) {
            return name;
        }
        return name.substring(qualifierBase.length() + 1);
    }

    public static String collapseQualifierBase(String name, String qualifierBase) {
        if (name == null || !name.startsWith(qualifierBase)) {
            return StringHelper.collapse(name);
        }
        return StringHelper.collapseQualifier(qualifierBase, true) + name.substring(qualifierBase.length());
    }

    public static String[] suffix(String[] columns, String suffix) {
        if (suffix == null) {
            return columns;
        }
        String[] qualified = new String[columns.length];
        for (int i = 0; i < columns.length; ++i) {
            qualified[i] = StringHelper.suffix(columns[i], suffix);
        }
        return qualified;
    }

    private static String suffix(String name, String suffix) {
        return suffix == null ? name : name + suffix;
    }

    public static String root(String qualifiedName) {
        int loc = qualifiedName.indexOf(46);
        return loc < 0 ? qualifiedName : qualifiedName.substring(0, loc);
    }

    public static String unroot(String qualifiedName) {
        int loc = qualifiedName.indexOf(46);
        return loc < 0 ? qualifiedName : qualifiedName.substring(loc + 1, qualifiedName.length());
    }

    public static String toString(Object[] array) {
        int len = array.length;
        if (len == 0) {
            return "";
        }
        StringBuilder buf = new StringBuilder(len * 12);
        for (int i = 0; i < len - 1; ++i) {
            buf.append(array[i]).append(", ");
        }
        return buf.append(array[len - 1]).toString();
    }

    public static String[] multiply(String string, Iterator<String> placeholders, Iterator<String[]> replacements) {
        String[] result = new String[]{string};
        while (placeholders.hasNext()) {
            result = StringHelper.multiply(result, placeholders.next(), replacements.next());
        }
        return result;
    }

    private static String[] multiply(String[] strings, String placeholder, String[] replacements) {
        String[] results = new String[replacements.length * strings.length];
        int n = 0;
        for (String replacement : replacements) {
            for (String string : strings) {
                results[n++] = StringHelper.replaceOnce(string, placeholder, replacement);
            }
        }
        return results;
    }

    public static int countUnquoted(String string, char character) {
        if ('\'' == character) {
            throw new IllegalArgumentException("Unquoted count of quotes is invalid");
        }
        if (string == null) {
            return 0;
        }
        int count = 0;
        int stringLength = string.length();
        boolean inQuote = false;
        for (int indx = 0; indx < stringLength; ++indx) {
            char c = string.charAt(indx);
            if (inQuote) {
                if ('\'' != c) continue;
                inQuote = false;
                continue;
            }
            if ('\'' == c) {
                inQuote = true;
                continue;
            }
            if (c != character) continue;
            ++count;
        }
        return count;
    }

    public static boolean isNotEmpty(String string) {
        return string != null && string.length() > 0;
    }

    public static boolean isEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static boolean isBlank(String string) {
        if (string == null || string.isEmpty()) {
            return true;
        }
        for (int i = 0; i < string.length(); ++i) {
            if (Character.isWhitespace(string.charAt(i))) continue;
            return false;
        }
        return true;
    }

    @Deprecated
    public static boolean isEmptyOrWhitespace(String string) {
        return StringHelper.isBlank(string);
    }

    public static String qualify(String prefix, String name) {
        if (name == null || prefix == null) {
            throw new NullPointerException("prefix or name were null attempting to build qualified name");
        }
        return prefix + '.' + name;
    }

    public static String qualifyConditionally(String prefix, String name) {
        if (name == null) {
            throw new NullPointerException("name was null attempting to build qualified name");
        }
        return StringHelper.isEmpty(prefix) ? name : prefix + '.' + name;
    }

    public static String[] qualify(String prefix, String[] names) {
        if (prefix == null) {
            return names;
        }
        int len = names.length;
        String[] qualified = new String[len];
        for (int i = 0; i < len; ++i) {
            qualified[i] = StringHelper.qualify(prefix, names[i]);
        }
        return qualified;
    }

    public static int firstIndexOfChar(String sqlString, BitSet keys, int startindex) {
        int size = sqlString.length();
        for (int i = startindex; i < size; ++i) {
            if (!keys.get(sqlString.charAt(i))) continue;
            return i;
        }
        return -1;
    }

    public static int firstIndexOfChar(String sqlString, String string, int startindex) {
        BitSet keys = new BitSet();
        int size = string.length();
        for (int i = 0; i < size; ++i) {
            keys.set(string.charAt(i));
        }
        return StringHelper.firstIndexOfChar(sqlString, keys, startindex);
    }

    public static String truncate(String string, int length) {
        if (string.length() <= length) {
            return string;
        }
        return string.substring(0, length);
    }

    public static String generateAlias(String description) {
        return StringHelper.generateAliasRoot(description) + '_';
    }

    public static String generateAlias(String description, int unique) {
        return StringHelper.generateAliasRoot(description) + AliasConstantsHelper.get(unique);
    }

    private static String generateAliasRoot(String description) {
        String result = StringHelper.truncate(StringHelper.unqualifyEntityName(description), 10).toLowerCase(Locale.ROOT).replace('/', '_').replace('$', '_');
        if (Character.isDigit((result = StringHelper.cleanAlias(result)).charAt(result.length() - 1))) {
            return result + "x";
        }
        return result;
    }

    private static String cleanAlias(String alias) {
        char[] chars = alias.toCharArray();
        if (!Character.isLetter(chars[0])) {
            for (int i = 1; i < chars.length; ++i) {
                if (!Character.isLetter(chars[i])) continue;
                return alias.substring(i);
            }
        }
        return alias;
    }

    public static String unqualifyEntityName(String entityName) {
        String result = StringHelper.unqualify(entityName);
        int slashPos = result.indexOf(47);
        if (slashPos > 0) {
            result = result.substring(0, slashPos - 1);
        }
        return result;
    }

    public static String moveAndToBeginning(String filter) {
        if (!StringHelper.isBlank(filter) && (filter = filter + " and ").startsWith(" and ")) {
            filter = filter.substring(4);
        }
        return filter;
    }

    public static boolean isQuoted(String name) {
        char last;
        if (name == null || name.isEmpty()) {
            return false;
        }
        char first = name.charAt(0);
        return first == (last = name.charAt(name.length() - 1)) && (first == '`' || first == '\"');
    }

    public static String unquote(String name) {
        return StringHelper.isQuoted(name) ? name.substring(1, name.length() - 1) : name;
    }

    public static boolean isQuoted(String name, Dialect dialect) {
        char last;
        if (name == null || name.isEmpty()) {
            return false;
        }
        char first = name.charAt(0);
        return first == (last = name.charAt(name.length() - 1)) && (first == '`' || first == '\"') || first == dialect.openQuote() && last == dialect.closeQuote();
    }

    public static String unquote(String name, Dialect dialect) {
        return StringHelper.isQuoted(name, dialect) ? name.substring(1, name.length() - 1) : name;
    }

    public static String[] unquote(String[] names, Dialect dialect) {
        if (names == null) {
            return null;
        }
        int failedIndex = -1;
        int length = names.length;
        for (int i = 0; i < length; ++i) {
            if (!StringHelper.isQuoted(names[i], dialect)) continue;
            failedIndex = i;
            break;
        }
        if (failedIndex == -1) {
            return names;
        }
        String[] unquoted = new String[length];
        System.arraycopy(names, 0, unquoted, 0, failedIndex);
        for (int i = failedIndex; i < length; ++i) {
            unquoted[i] = StringHelper.unquote(names[i], dialect);
        }
        return unquoted;
    }

    public static StringBuilder buildBatchFetchRestrictionFragment(String alias, String[] columnNames, Dialect dialect) {
        if (columnNames.length == 1) {
            return new StringBuilder(StringHelper.qualify(alias, columnNames[0])).append(" in (").append(BATCH_ID_PLACEHOLDER).append(')');
        }
        if (dialect.supportsRowValueConstructorSyntaxInInList()) {
            StringBuilder builder = new StringBuilder();
            builder.append('(');
            boolean firstPass = true;
            String deliminator = "";
            for (String columnName : columnNames) {
                builder.append(deliminator).append(StringHelper.qualify(alias, columnName));
                if (!firstPass) continue;
                firstPass = false;
                deliminator = ",";
            }
            builder.append(") in (");
            builder.append(BATCH_ID_PLACEHOLDER);
            builder.append(')');
            return builder;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('(').append(BATCH_ID_PLACEHOLDER).append(')');
        return stringBuilder;
    }

    public static String expandBatchIdPlaceholder(String sql, Serializable[] ids, String alias, String[] keyColumnNames, Dialect dialect) {
        if (keyColumnNames.length == 1) {
            return StringHelper.replace(sql, BATCH_ID_PLACEHOLDER, StringHelper.repeat("?", ids.length, ","));
        }
        if (dialect.supportsRowValueConstructorSyntaxInInList()) {
            String tuple = '(' + StringHelper.repeat("?", keyColumnNames.length, ",") + ')';
            return StringHelper.replace(sql, BATCH_ID_PLACEHOLDER, StringHelper.repeat(tuple, ids.length, ","));
        }
        String keyCheck = '(' + StringHelper.joinWithQualifierAndSuffix(keyColumnNames, alias, " = ?", " and ") + ')';
        return StringHelper.replace(sql, BATCH_ID_PLACEHOLDER, StringHelper.repeat(keyCheck, ids.length, " or "));
    }

    public static String nullIfEmpty(String value) {
        return StringHelper.isEmpty(value) ? null : value;
    }

    public static List<String> parseCommaSeparatedString(String incomingString) {
        return CommaSeparatedStringHelper.parseCommaSeparatedString(incomingString);
    }

    public static <T> String join(Collection<T> values, Renderer<T> renderer) {
        StringBuilder buffer = new StringBuilder();
        for (T value : values) {
            buffer.append(String.join((CharSequence)", ", renderer.render(value)));
        }
        return buffer.toString();
    }

    public static String getNonEmptyOrConjunctionIfBothNonEmpty(String firstExpression, String secondExpression) {
        boolean isFirstExpressionNonEmpty = StringHelper.isNotEmpty(firstExpression);
        boolean isSecondExpressionNonEmpty = StringHelper.isNotEmpty(secondExpression);
        if (isFirstExpressionNonEmpty && isSecondExpressionNonEmpty) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("( ").append(firstExpression).append(" ) and ( ").append(secondExpression).append(" )");
            return buffer.toString();
        }
        if (isFirstExpressionNonEmpty) {
            return firstExpression;
        }
        if (isSecondExpressionNonEmpty) {
            return secondExpression;
        }
        return null;
    }

    public static String safeInterning(String string) {
        if (string == null) {
            return null;
        }
        return string.intern();
    }

    public static interface Renderer<T> {
        public String render(T var1);
    }
}

