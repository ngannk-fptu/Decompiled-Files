/*
 * Decompiled with CFR 0.152.
 */
package org.tuckey.web.filters.urlrewrite.substitution;

import org.tuckey.web.filters.urlrewrite.substitution.SubstitutionContext;
import org.tuckey.web.filters.urlrewrite.substitution.SubstitutionFilterChain;

public interface SubstitutionFilter {
    public String substitute(String var1, SubstitutionContext var2, SubstitutionFilterChain var3);
}

