/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.util.TextUtils
 */
package com.atlassian.user.configuration.util;

import com.atlassian.user.configuration.ConfigurationException;
import com.opensymphony.util.TextUtils;
import java.util.HashMap;
import java.util.Set;

public class InitializationCheck {
    public static void validateArgs(HashMap dependencies, String[] requiredDependencyKeys, Object caller) throws ConfigurationException {
        Set keySet = dependencies.keySet();
        String errMessage = "";
        for (int i = 0; i < requiredDependencyKeys.length; ++i) {
            String key = requiredDependencyKeys[i];
            if (keySet.contains(key)) continue;
            errMessage = errMessage + "Unsatisfied dependency in [" + caller.getClass().getName() + "] - missing [" + key + "]. ";
        }
        if (TextUtils.stringSet((String)errMessage)) {
            throw new ConfigurationException(errMessage);
        }
    }
}

