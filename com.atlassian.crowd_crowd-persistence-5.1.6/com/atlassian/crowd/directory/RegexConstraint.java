/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.PasswordConstraint
 *  com.atlassian.crowd.embedded.api.ValidatePasswordRequest
 *  com.google.common.base.Preconditions
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.embedded.api.PasswordConstraint;
import com.atlassian.crowd.embedded.api.ValidatePasswordRequest;
import com.google.common.base.Preconditions;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class RegexConstraint
implements PasswordConstraint {
    private final String regex;

    public RegexConstraint(String regex) {
        this.regex = (String)Preconditions.checkNotNull((Object)regex);
    }

    public String getRegex() {
        return this.regex;
    }

    public boolean validate(ValidatePasswordRequest request) {
        return StringUtils.isBlank((CharSequence)this.regex) || Pattern.compile(this.regex).matcher(request.getPassword().getCredential()).find();
    }

    public String toString() {
        return "PasswordRegexConstraint[regex@" + this.regex.hashCode() + "]";
    }
}

