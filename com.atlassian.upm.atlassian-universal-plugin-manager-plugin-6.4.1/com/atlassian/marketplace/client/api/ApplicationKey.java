/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.marketplace.client.api;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

public final class ApplicationKey {
    public static final ApplicationKey BAMBOO = new ApplicationKey("bamboo");
    public static final ApplicationKey BITBUCKET = new ApplicationKey("bitbucket");
    public static final ApplicationKey CONFLUENCE = new ApplicationKey("confluence");
    public static final ApplicationKey FECRU = new ApplicationKey("fecru");
    public static final ApplicationKey HIPCHAT = new ApplicationKey("hipchat");
    public static final ApplicationKey JIRA = new ApplicationKey("jira");
    public static final ApplicationKey CROWD = new ApplicationKey("crowd");
    private static final ApplicationKey[] PREDEFINED = new ApplicationKey[]{BAMBOO, BITBUCKET, CONFLUENCE, FECRU, HIPCHAT, JIRA, CROWD};
    private final String key;

    private ApplicationKey(String key) {
        this.key = key;
    }

    public static ApplicationKey valueOf(String key) {
        String s = ((String)Preconditions.checkNotNull((Object)StringUtils.trimToNull((String)key))).toLowerCase();
        for (ApplicationKey a : PREDEFINED) {
            if (!a.key.equals(s)) continue;
            return a;
        }
        return new ApplicationKey(s);
    }

    public String getKey() {
        return this.key;
    }

    public boolean equals(Object other) {
        if (other instanceof ApplicationKey) {
            return this.key.equals(((ApplicationKey)other).key);
        }
        return false;
    }

    public int hashCode() {
        return this.key.hashCode();
    }

    public String toString() {
        return "ApplicationKey(" + this.key + ")";
    }
}

