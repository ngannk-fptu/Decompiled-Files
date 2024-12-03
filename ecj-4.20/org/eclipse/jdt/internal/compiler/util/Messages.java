/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public final class Messages {
    private static String[] nlSuffixes;
    private static final String EXTENSION = ".properties";
    private static final String BUNDLE_NAME = "org.eclipse.jdt.internal.compiler.messages";
    public static String compilation_unresolvedProblem;
    public static String compilation_unresolvedProblems;
    public static String compilation_request;
    public static String compilation_loadBinary;
    public static String compilation_process;
    public static String compilation_write;
    public static String compilation_done;
    public static String compilation_units;
    public static String compilation_unit;
    public static String compilation_internalError;
    public static String compilation_beginningToCompile;
    public static String compilation_processing;
    public static String output_isFile;
    public static String output_notValidAll;
    public static String output_notValid;
    public static String problem_noSourceInformation;
    public static String problem_atLine;
    public static String abort_invalidAttribute;
    public static String abort_invalidExceptionAttribute;
    public static String abort_invalidOpcode;
    public static String abort_missingCode;
    public static String abort_againstSourceModel;
    public static String abort_externaAnnotationFile;
    public static String accept_cannot;
    public static String parser_incorrectPath;
    public static String parser_moveFiles;
    public static String parser_syntaxRecovery;
    public static String parser_regularParse;
    public static String parser_missingFile;
    public static String parser_corruptedFile;
    public static String parser_endOfFile;
    public static String parser_endOfConstructor;
    public static String parser_endOfMethod;
    public static String parser_endOfInitializer;
    public static String ast_missingCode;
    public static String constant_cannotCastedInto;
    public static String constant_cannotConvertedTo;
    public static String text_block;
    public static String pattern_matching_instanceof;
    public static String records;
    public static String sealed_types;

    static {
        Messages.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }

    public static String bind(String message) {
        return Messages.bind(message, null);
    }

    public static String bind(String message, Object binding) {
        return Messages.bind(message, new Object[]{binding});
    }

    public static String bind(String message, Object binding1, Object binding2) {
        return Messages.bind(message, new Object[]{binding1, binding2});
    }

    public static String bind(String message, Object[] bindings) {
        return MessageFormat.format(message, bindings);
    }

    private static String[] buildVariants(String root) {
        if (nlSuffixes == null) {
            String nl = Locale.getDefault().toString();
            ArrayList<String> result = new ArrayList<String>(4);
            while (true) {
                result.add(String.valueOf('_') + nl + EXTENSION);
                int lastSeparator = nl.lastIndexOf(95);
                if (lastSeparator == -1) break;
                nl = nl.substring(0, lastSeparator);
            }
            result.add(EXTENSION);
            nlSuffixes = result.toArray(new String[result.size()]);
        }
        root = root.replace('.', '/');
        String[] variants = new String[nlSuffixes.length];
        int i = 0;
        while (i < variants.length) {
            variants[i] = String.valueOf(root) + nlSuffixes[i];
            ++i;
        }
        return variants;
    }

    public static void initializeMessages(String bundleName, Class clazz) {
        Field[] fields = clazz.getDeclaredFields();
        Messages.load(bundleName, clazz.getClassLoader(), fields);
        int numFields = fields.length;
        int i = 0;
        while (i < numFields) {
            Field field = fields[i];
            if ((field.getModifiers() & 0x19) == 9) {
                try {
                    if (field.get(clazz) == null) {
                        String value = "Missing message: " + field.getName() + " in: " + bundleName;
                        field.set(null, value);
                    }
                }
                catch (IllegalAccessException | IllegalArgumentException exception) {}
            }
            ++i;
        }
    }

    public static void load(String bundleName, ClassLoader loader, Field[] fields) {
        String[] variants = Messages.buildVariants(bundleName);
        int i = variants.length;
        while (--i >= 0) {
            InputStream input;
            InputStream inputStream = input = loader == null ? ClassLoader.getSystemResourceAsStream(variants[i]) : loader.getResourceAsStream(variants[i]);
            if (input == null) continue;
            try {
                try {
                    MessagesProperties properties = new MessagesProperties(fields, bundleName);
                    properties.load(input);
                }
                catch (IOException iOException) {
                    try {
                        input.close();
                    }
                    catch (IOException iOException2) {}
                    continue;
                }
            }
            catch (Throwable throwable) {
                try {
                    input.close();
                }
                catch (IOException iOException) {}
                throw throwable;
            }
            try {
                input.close();
            }
            catch (IOException iOException) {}
        }
    }

    private static class MessagesProperties
    extends Properties {
        private static final int MOD_EXPECTED = 9;
        private static final int MOD_MASK = 25;
        private static final long serialVersionUID = 1L;
        private final Map fields;

        public MessagesProperties(Field[] fieldArray, String bundleName) {
            int len = fieldArray.length;
            this.fields = new HashMap(len * 2);
            int i = 0;
            while (i < len) {
                this.fields.put(fieldArray[i].getName(), fieldArray[i]);
                ++i;
            }
        }

        @Override
        public synchronized Object put(Object key, Object value) {
            Field field;
            block7: {
                block6: {
                    try {
                        field = (Field)this.fields.get(key);
                        if (field != null) break block6;
                        return null;
                    }
                    catch (SecurityException securityException) {}
                }
                if ((field.getModifiers() & 0x19) == 9) break block7;
                return null;
            }
            try {
                field.set(null, value);
            }
            catch (Exception exception) {}
            return null;
        }
    }
}

