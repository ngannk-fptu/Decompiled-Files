/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarcommonscli;

class OptionValidator {
    OptionValidator() {
    }

    static void validateOption(String opt) throws IllegalArgumentException {
        if (opt == null) {
            return;
        }
        if (opt.length() == 1) {
            char ch = opt.charAt(0);
            if (!OptionValidator.isValidOpt(ch)) {
                throw new IllegalArgumentException("illegal option value '" + ch + "'");
            }
        } else {
            char[] chars = opt.toCharArray();
            for (int i = 0; i < chars.length; ++i) {
                if (OptionValidator.isValidChar(chars[i])) continue;
                throw new IllegalArgumentException("opt contains illegal character value '" + chars[i] + "'");
            }
        }
    }

    private static boolean isValidOpt(char c) {
        return OptionValidator.isValidChar(c) || c == ' ' || c == '?' || c == '@';
    }

    private static boolean isValidChar(char c) {
        return Character.isJavaIdentifierPart(c);
    }
}

