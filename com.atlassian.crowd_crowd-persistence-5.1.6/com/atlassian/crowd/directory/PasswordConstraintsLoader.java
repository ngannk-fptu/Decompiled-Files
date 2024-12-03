/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Attributes
 *  com.atlassian.crowd.embedded.api.PasswordConstraint
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.embedded.api.Attributes;
import com.atlassian.crowd.embedded.api.PasswordConstraint;
import java.util.Set;

public interface PasswordConstraintsLoader {
    public Set<PasswordConstraint> getFromDirectoryAttributes(long var1, Attributes var3);
}

