/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.ldap.transaction.compensating;

import javax.naming.Name;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public final class LdapTransactionUtils {
    public static final String REBIND_METHOD_NAME = "rebind";
    public static final String BIND_METHOD_NAME = "bind";
    public static final String RENAME_METHOD_NAME = "rename";
    public static final String UNBIND_METHOD_NAME = "unbind";
    public static final String MODIFY_ATTRIBUTES_METHOD_NAME = "modifyAttributes";

    private LdapTransactionUtils() {
    }

    public static Name getFirstArgumentAsName(Object[] args) {
        Assert.notEmpty((Object[])args);
        Object firstArg = args[0];
        return LdapTransactionUtils.getArgumentAsName(firstArg);
    }

    public static Name getArgumentAsName(Object arg) {
        if (arg instanceof String) {
            return LdapUtils.newLdapName((String)arg);
        }
        if (arg instanceof Name) {
            return (Name)arg;
        }
        throw new IllegalArgumentException("First argument needs to be a Name or a String representation thereof");
    }

    public static boolean isSupportedWriteTransactionOperation(String methodName) {
        return ObjectUtils.nullSafeEquals((Object)methodName, (Object)BIND_METHOD_NAME) || ObjectUtils.nullSafeEquals((Object)methodName, (Object)REBIND_METHOD_NAME) || ObjectUtils.nullSafeEquals((Object)methodName, (Object)RENAME_METHOD_NAME) || ObjectUtils.nullSafeEquals((Object)methodName, (Object)MODIFY_ATTRIBUTES_METHOD_NAME) || ObjectUtils.nullSafeEquals((Object)methodName, (Object)UNBIND_METHOD_NAME);
    }
}

