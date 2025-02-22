/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import java.io.Serializable;

public abstract class PropertyNamingStrategies
implements Serializable {
    private static final long serialVersionUID = 2L;
    public static final PropertyNamingStrategy LOWER_CAMEL_CASE = LowerCamelCaseStrategy.INSTANCE;
    public static final PropertyNamingStrategy UPPER_CAMEL_CASE = UpperCamelCaseStrategy.INSTANCE;
    public static final PropertyNamingStrategy SNAKE_CASE = SnakeCaseStrategy.INSTANCE;
    public static final PropertyNamingStrategy UPPER_SNAKE_CASE = UpperSnakeCaseStrategy.INSTANCE;
    public static final PropertyNamingStrategy LOWER_CASE = LowerCaseStrategy.INSTANCE;
    public static final PropertyNamingStrategy KEBAB_CASE = KebabCaseStrategy.INSTANCE;
    public static final PropertyNamingStrategy LOWER_DOT_CASE = LowerDotCaseStrategy.INSTANCE;

    public static class LowerDotCaseStrategy
    extends NamingBase {
        private static final long serialVersionUID = 2L;
        public static final LowerDotCaseStrategy INSTANCE = new LowerDotCaseStrategy();

        @Override
        public String translate(String input) {
            return this.translateLowerCaseWithSeparator(input, '.');
        }
    }

    public static class KebabCaseStrategy
    extends NamingBase {
        private static final long serialVersionUID = 2L;
        public static final KebabCaseStrategy INSTANCE = new KebabCaseStrategy();

        @Override
        public String translate(String input) {
            return this.translateLowerCaseWithSeparator(input, '-');
        }
    }

    public static class LowerCaseStrategy
    extends NamingBase {
        private static final long serialVersionUID = 2L;
        public static final LowerCaseStrategy INSTANCE = new LowerCaseStrategy();

        @Override
        public String translate(String input) {
            return input.toLowerCase();
        }
    }

    public static class UpperCamelCaseStrategy
    extends NamingBase {
        private static final long serialVersionUID = 2L;
        public static final UpperCamelCaseStrategy INSTANCE = new UpperCamelCaseStrategy();

        @Override
        public String translate(String input) {
            char uc;
            if (input == null || input.isEmpty()) {
                return input;
            }
            char c = input.charAt(0);
            if (c == (uc = Character.toUpperCase(c))) {
                return input;
            }
            StringBuilder sb = new StringBuilder(input);
            sb.setCharAt(0, uc);
            return sb.toString();
        }
    }

    public static class LowerCamelCaseStrategy
    extends NamingBase {
        private static final long serialVersionUID = 2L;
        public static final LowerCamelCaseStrategy INSTANCE = new LowerCamelCaseStrategy();

        @Override
        public String translate(String input) {
            return input;
        }
    }

    public static class UpperSnakeCaseStrategy
    extends SnakeCaseStrategy {
        private static final long serialVersionUID = 2L;
        public static final UpperSnakeCaseStrategy INSTANCE = new UpperSnakeCaseStrategy();

        @Override
        public String translate(String input) {
            String output = super.translate(input);
            if (output == null) {
                return null;
            }
            return super.translate(input).toUpperCase();
        }
    }

    public static class SnakeCaseStrategy
    extends NamingBase {
        private static final long serialVersionUID = 2L;
        public static final SnakeCaseStrategy INSTANCE = new SnakeCaseStrategy();

        @Override
        public String translate(String input) {
            if (input == null) {
                return input;
            }
            int length = input.length();
            StringBuilder result = new StringBuilder(length * 2);
            int resultLength = 0;
            boolean wasPrevTranslated = false;
            for (int i = 0; i < length; ++i) {
                char c = input.charAt(i);
                if (i <= 0 && c == '_') continue;
                if (Character.isUpperCase(c)) {
                    if (!wasPrevTranslated && resultLength > 0 && result.charAt(resultLength - 1) != '_') {
                        result.append('_');
                        ++resultLength;
                    }
                    c = Character.toLowerCase(c);
                    wasPrevTranslated = true;
                } else {
                    wasPrevTranslated = false;
                }
                result.append(c);
                ++resultLength;
            }
            return resultLength > 0 ? result.toString() : input;
        }
    }

    public static abstract class NamingBase
    extends PropertyNamingStrategy {
        private static final long serialVersionUID = 2L;

        @Override
        public String nameForField(MapperConfig<?> config, AnnotatedField field, String defaultName) {
            return this.translate(defaultName);
        }

        @Override
        public String nameForGetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
            return this.translate(defaultName);
        }

        @Override
        public String nameForSetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
            return this.translate(defaultName);
        }

        @Override
        public String nameForConstructorParameter(MapperConfig<?> config, AnnotatedParameter ctorParam, String defaultName) {
            return this.translate(defaultName);
        }

        public abstract String translate(String var1);

        protected String translateLowerCaseWithSeparator(String input, char separator) {
            if (input == null) {
                return input;
            }
            int length = input.length();
            if (length == 0) {
                return input;
            }
            StringBuilder result = new StringBuilder(length + (length >> 1));
            int upperCount = 0;
            for (int i = 0; i < length; ++i) {
                char ch = input.charAt(i);
                char lc = Character.toLowerCase(ch);
                if (lc == ch) {
                    if (upperCount > 1) {
                        result.insert(result.length() - 1, separator);
                    }
                    upperCount = 0;
                } else {
                    if (upperCount == 0 && i > 0) {
                        result.append(separator);
                    }
                    ++upperCount;
                }
                result.append(lc);
            }
            return result.toString();
        }
    }
}

