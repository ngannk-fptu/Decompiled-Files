/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  com.atlassian.util.concurrent.ResettableLazyReference
 *  com.google.common.annotations.VisibleForTesting
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.impl.security.recovery;

import com.atlassian.user.User;
import com.atlassian.util.concurrent.ResettableLazyReference;
import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.lang3.StringUtils;

public class RecoveryUtil {
    public static final String RECOVERY_ADMIN_NAME = "recovery_admin";
    public static final String RECOVERY_ADMIN_PASSWORD_KEY = "atlassian.recovery.password";
    private static ResettableLazyReference<Boolean> supplier = new ResettableLazyReference<Boolean>(){

        protected Boolean create() throws Exception {
            return StringUtils.isNotBlank((CharSequence)System.getProperty(RecoveryUtil.RECOVERY_ADMIN_PASSWORD_KEY));
        }
    };

    public static boolean isRecoveryMode() {
        return (Boolean)supplier.get();
    }

    public static boolean isRecoveryAdmin(User user) {
        return RecoveryUtil.isRecoveryMode() && user != null && RECOVERY_ADMIN_NAME.equals(user.getName());
    }

    public static boolean isRecoveryAdmin(String username) {
        return RecoveryUtil.isRecoveryMode() && RECOVERY_ADMIN_NAME.equals(username);
    }

    @VisibleForTesting
    public static void resetRecoveryMode() {
        supplier.reset();
    }
}

