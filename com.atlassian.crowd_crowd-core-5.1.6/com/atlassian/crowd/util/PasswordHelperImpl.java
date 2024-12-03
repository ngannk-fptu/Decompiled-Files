/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.util.PasswordHelper
 */
package com.atlassian.crowd.util;

import com.atlassian.crowd.util.PasswordHelper;
import com.atlassian.crowd.util.SecureRandomStringUtils;

public class PasswordHelperImpl
implements PasswordHelper {
    public String generateRandomPassword() {
        return SecureRandomStringUtils.getInstance().randomAlphanumericString(22);
    }
}

