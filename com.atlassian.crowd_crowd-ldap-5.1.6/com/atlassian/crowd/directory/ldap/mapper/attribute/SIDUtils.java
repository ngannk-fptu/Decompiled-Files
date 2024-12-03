/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.Validate
 */
package com.atlassian.crowd.directory.ldap.mapper.attribute;

import org.apache.commons.lang3.Validate;

public class SIDUtils {
    public static String substituteLastRidInSid(String baseSid, String rid) {
        Validate.notNull((Object)baseSid, (String)"baseSID argument cannot be null", (Object[])new Object[0]);
        Validate.notNull((Object)rid, (String)"RID argument cannot be null", (Object[])new Object[0]);
        return baseSid.substring(0, baseSid.lastIndexOf(45)) + "-" + rid;
    }

    public static String getLastRidFromSid(String sid) {
        Validate.notEmpty((CharSequence)sid, (String)"sid argument cannot be empty", (Object[])new Object[0]);
        return sid.substring(sid.lastIndexOf(45) + 1);
    }
}

