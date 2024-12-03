/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.util;

import java.util.Set;
import java.util.regex.Pattern;

public interface MemberAccessValueStack {
    public void useExcludeProperties(Set<Pattern> var1);

    public void useAcceptProperties(Set<Pattern> var1);
}

