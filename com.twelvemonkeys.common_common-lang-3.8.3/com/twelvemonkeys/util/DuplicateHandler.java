/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.util;

public interface DuplicateHandler<T> {
    public static final DuplicateHandler<?> USE_FIRST_VALUE = new DuplicateHandler(){

        public Object resolve(Object object, Object object2) {
            return object;
        }
    };
    public static final DuplicateHandler<?> USE_LAST_VALUE = new DuplicateHandler(){

        public Object resolve(Object object, Object object2) {
            return object2;
        }
    };
    public static final DuplicateHandler<?> DUPLICATES_AS_ARRAY = new DuplicateHandler(){

        public Object resolve(Object object, Object object2) {
            Object[] objectArray;
            if (object instanceof Object[]) {
                Object[] objectArray2 = (Object[])object;
                objectArray = new Object[objectArray2.length + 1];
                System.arraycopy(objectArray2, 0, objectArray, 0, objectArray2.length);
                objectArray[objectArray2.length] = object2;
            } else {
                objectArray = new Object[]{object, object2};
            }
            return objectArray;
        }
    };
    public static final DuplicateHandler<String> DUPLICATES_AS_CSV = new DuplicateHandler<String>(){

        @Override
        public String resolve(String string, String string2) {
            StringBuilder stringBuilder = new StringBuilder(String.valueOf(string));
            stringBuilder.append(',');
            stringBuilder.append(string2);
            return stringBuilder.toString();
        }
    };

    public T resolve(T var1, T var2);
}

