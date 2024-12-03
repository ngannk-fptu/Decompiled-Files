/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.MaybeNot
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.sal.api.ApplicationProperties
 *  org.osgi.framework.Version
 */
package com.atlassian.confluence.plugins.email;

import com.atlassian.confluence.core.MaybeNot;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.sal.api.ApplicationProperties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.osgi.framework.Version;

public enum VersionUtil {
    INSTANCE;

    private static final Pattern APPLICATION_VERSION_PATTERN;

    public Maybe<Version> parseApplicationVersion(ApplicationProperties applicationProperties) {
        String applicationVersionString = applicationProperties.getVersion();
        Matcher applicationVersionMatcher = APPLICATION_VERSION_PATTERN.matcher(applicationVersionString);
        if (applicationVersionMatcher.find()) {
            return this.parseVersion(applicationVersionMatcher.group(1));
        }
        return MaybeNot.becauseOf((String)"Application version [%s] did not match pattern [%s].", (Object[])new Object[]{applicationVersionString, APPLICATION_VERSION_PATTERN.pattern()});
    }

    public Maybe<Version> parseVersion(String versionString) {
        try {
            return Option.some((Object)Version.parseVersion((String)versionString));
        }
        catch (RuntimeException e) {
            return MaybeNot.becauseOfException((Exception)e);
        }
    }

    static {
        APPLICATION_VERSION_PATTERN = Pattern.compile("([0-9]*(?:\\.[0-9]*){0,2})");
    }
}

