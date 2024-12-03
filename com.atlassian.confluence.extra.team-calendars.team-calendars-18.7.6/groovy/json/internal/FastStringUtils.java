/*
 * Decompiled with CFR 0.152.
 */
package groovy.json.internal;

import java.lang.reflect.Field;
import sun.misc.Unsafe;

public class FastStringUtils {
    public static final Unsafe UNSAFE;
    public static final long STRING_VALUE_FIELD_OFFSET;
    public static final long STRING_OFFSET_FIELD_OFFSET;
    public static final long STRING_COUNT_FIELD_OFFSET;
    public static final boolean ENABLED;
    private static final boolean WRITE_TO_FINAL_FIELDS;
    private static final boolean DISABLE;
    public static StringImplementation STRING_IMPLEMENTATION;

    private static Unsafe loadUnsafe() {
        try {
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            return (Unsafe)unsafeField.get(null);
        }
        catch (Exception e) {
            return null;
        }
    }

    private static long getFieldOffset(String fieldName) {
        if (ENABLED) {
            try {
                return UNSAFE.objectFieldOffset(String.class.getDeclaredField(fieldName));
            }
            catch (NoSuchFieldException noSuchFieldException) {
                // empty catch block
            }
        }
        return -1L;
    }

    private static StringImplementation computeStringImplementation() {
        if (STRING_VALUE_FIELD_OFFSET != -1L) {
            if (STRING_OFFSET_FIELD_OFFSET != -1L && STRING_COUNT_FIELD_OFFSET != -1L) {
                return StringImplementation.OFFSET;
            }
            if (STRING_OFFSET_FIELD_OFFSET == -1L && STRING_COUNT_FIELD_OFFSET == -1L && FastStringUtils.valueFieldIsCharArray()) {
                return StringImplementation.DIRECT_CHARS;
            }
            return StringImplementation.UNKNOWN;
        }
        return StringImplementation.UNKNOWN;
    }

    private static boolean valueFieldIsCharArray() {
        Object o = UNSAFE.getObject("", STRING_VALUE_FIELD_OFFSET);
        return o instanceof char[];
    }

    public static char[] toCharArray(String string) {
        return STRING_IMPLEMENTATION.toCharArray(string);
    }

    public static char[] toCharArray(CharSequence charSequence) {
        return FastStringUtils.toCharArray(charSequence.toString());
    }

    public static String noCopyStringFromChars(char[] chars) {
        return STRING_IMPLEMENTATION.noCopyStringFromChars(chars);
    }

    static {
        WRITE_TO_FINAL_FIELDS = Boolean.parseBoolean(System.getProperty("groovy.json.faststringutils.write.to.final.fields", "false"));
        DISABLE = Boolean.parseBoolean(System.getProperty("groovy.json.faststringutils.disable", "false"));
        UNSAFE = DISABLE ? null : FastStringUtils.loadUnsafe();
        ENABLED = UNSAFE != null;
        STRING_VALUE_FIELD_OFFSET = FastStringUtils.getFieldOffset("value");
        STRING_OFFSET_FIELD_OFFSET = FastStringUtils.getFieldOffset("offset");
        STRING_COUNT_FIELD_OFFSET = FastStringUtils.getFieldOffset("count");
        STRING_IMPLEMENTATION = FastStringUtils.computeStringImplementation();
    }

    protected static enum StringImplementation {
        DIRECT_CHARS{

            @Override
            public char[] toCharArray(String string) {
                return (char[])UNSAFE.getObject(string, STRING_VALUE_FIELD_OFFSET);
            }

            @Override
            public String noCopyStringFromChars(char[] chars) {
                if (WRITE_TO_FINAL_FIELDS) {
                    String string = new String();
                    UNSAFE.putObject(string, STRING_VALUE_FIELD_OFFSET, chars);
                    return string;
                }
                return new String(chars);
            }
        }
        ,
        OFFSET{

            @Override
            public char[] toCharArray(String string) {
                char[] value = (char[])UNSAFE.getObject(string, STRING_VALUE_FIELD_OFFSET);
                int offset = UNSAFE.getInt(string, STRING_OFFSET_FIELD_OFFSET);
                int count = UNSAFE.getInt(string, STRING_COUNT_FIELD_OFFSET);
                if (offset == 0 && count == value.length) {
                    return value;
                }
                return string.toCharArray();
            }

            @Override
            public String noCopyStringFromChars(char[] chars) {
                if (WRITE_TO_FINAL_FIELDS) {
                    String string = new String();
                    UNSAFE.putObject(string, STRING_VALUE_FIELD_OFFSET, chars);
                    UNSAFE.putInt(string, STRING_COUNT_FIELD_OFFSET, chars.length);
                    return string;
                }
                return new String(chars);
            }
        }
        ,
        UNKNOWN{

            @Override
            public char[] toCharArray(String string) {
                return string.toCharArray();
            }

            @Override
            public String noCopyStringFromChars(char[] chars) {
                return new String(chars);
            }
        };


        public abstract char[] toCharArray(String var1);

        public abstract String noCopyStringFromChars(char[] var1);
    }
}

