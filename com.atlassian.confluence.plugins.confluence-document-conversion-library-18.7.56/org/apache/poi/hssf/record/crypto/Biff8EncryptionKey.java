/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record.crypto;

public final class Biff8EncryptionKey {
    private static final ThreadLocal<String> _userPasswordTLS = new ThreadLocal();

    public static void setCurrentUserPassword(String password) {
        if (password == null) {
            _userPasswordTLS.remove();
        } else {
            _userPasswordTLS.set(password);
        }
    }

    public static String getCurrentUserPassword() {
        return _userPasswordTLS.get();
    }
}

