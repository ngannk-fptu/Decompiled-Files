/*
 * Decompiled with CFR 0.152.
 */
package net.sf.cglib.core;

import net.sf.cglib.core.KeyFactory;

public class ClassesKey {
    private static final Key FACTORY = (Key)((Object)KeyFactory.create(class$net$sf$cglib$core$ClassesKey$Key == null ? (class$net$sf$cglib$core$ClassesKey$Key = ClassesKey.class$("net.sf.cglib.core.ClassesKey$Key")) : class$net$sf$cglib$core$ClassesKey$Key, KeyFactory.OBJECT_BY_CLASS));
    static /* synthetic */ Class class$net$sf$cglib$core$ClassesKey$Key;

    private ClassesKey() {
    }

    public static Object create(Object[] array) {
        return FACTORY.newInstance(array);
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static interface Key {
        public Object newInstance(Object[] var1);
    }
}

