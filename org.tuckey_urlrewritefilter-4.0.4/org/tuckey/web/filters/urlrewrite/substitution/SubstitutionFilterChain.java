/*
 * Decompiled with CFR 0.152.
 */
package org.tuckey.web.filters.urlrewrite.substitution;

import org.tuckey.web.filters.urlrewrite.substitution.SubstitutionContext;

public interface SubstitutionFilterChain {
    public String substitute(String var1, SubstitutionContext var2);
}

