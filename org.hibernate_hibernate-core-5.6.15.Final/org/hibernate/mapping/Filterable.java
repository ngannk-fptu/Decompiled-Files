/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import java.util.List;
import java.util.Map;

public interface Filterable {
    public void addFilter(String var1, String var2, boolean var3, Map<String, String> var4, Map<String, String> var5);

    public List getFilters();
}

