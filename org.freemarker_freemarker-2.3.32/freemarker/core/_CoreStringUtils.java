/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.BugException;
import freemarker.template.utility.StringUtil;
import java.util.Collection;

public final class _CoreStringUtils {
    private _CoreStringUtils() {
    }

    public static String toFTLIdentifierReferenceAfterDot(String name) {
        return _CoreStringUtils.backslashEscapeIdentifier(name);
    }

    public static String toFTLTopLevelIdentifierReference(String name) {
        return _CoreStringUtils.backslashEscapeIdentifier(name);
    }

    public static String toFTLTopLevelTragetIdentifier(String name) {
        int quotationType = 0;
        for (int i = 0; i < name.length(); ++i) {
            char c = name.charAt(i);
            if ((i != 0 ? StringUtil.isFTLIdentifierPart(c) : StringUtil.isFTLIdentifierStart(c)) || c == '@') continue;
            if ((quotationType == 0 || quotationType == 92) && StringUtil.isBackslashEscapedFTLIdentifierCharacter(c)) {
                quotationType = 92;
                continue;
            }
            quotationType = 34;
            break;
        }
        switch (quotationType) {
            case 0: {
                return name;
            }
            case 34: {
                return StringUtil.ftlQuote(name);
            }
            case 92: {
                return _CoreStringUtils.backslashEscapeIdentifier(name);
            }
        }
        throw new BugException();
    }

    public static String backslashEscapeIdentifier(String name) {
        StringBuilder sb = null;
        for (int i = 0; i < name.length(); ++i) {
            char c = name.charAt(i);
            if (StringUtil.isBackslashEscapedFTLIdentifierCharacter(c)) {
                if (sb == null) {
                    sb = new StringBuilder(name.length() + 8);
                    sb.append(name, 0, i);
                }
                sb.append('\\');
            }
            if (sb == null) continue;
            sb.append(c);
        }
        return sb == null ? name : sb.toString();
    }

    public static int getIdentifierNamingConvention(String name) {
        int ln = name.length();
        for (int i = 0; i < ln; ++i) {
            char c = name.charAt(i);
            if (c == '_') {
                return 11;
            }
            if (!_CoreStringUtils.isUpperUSASCII(c)) continue;
            return 12;
        }
        return 10;
    }

    public static String camelCaseToUnderscored(String camelCaseName) {
        int i;
        for (i = 0; i < camelCaseName.length() && Character.isLowerCase(camelCaseName.charAt(i)); ++i) {
        }
        if (i == camelCaseName.length()) {
            return camelCaseName;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(camelCaseName.substring(0, i));
        while (i < camelCaseName.length()) {
            char c = camelCaseName.charAt(i);
            if (_CoreStringUtils.isUpperUSASCII(c)) {
                sb.append('_');
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
            ++i;
        }
        return sb.toString();
    }

    public static boolean isUpperUSASCII(char c) {
        return c >= 'A' && c <= 'Z';
    }

    public static String commaSeparatedJQuotedItems(Collection<String> items) {
        StringBuilder sb = new StringBuilder();
        for (String item : items) {
            if (sb.length() != 0) {
                sb.append(", ");
            }
            sb.append(StringUtil.jQuote(item));
        }
        return sb.toString();
    }
}

