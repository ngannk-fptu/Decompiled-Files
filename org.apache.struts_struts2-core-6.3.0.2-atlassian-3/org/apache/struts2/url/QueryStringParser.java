/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.url;

import java.io.Serializable;
import java.util.Map;

public interface QueryStringParser
extends Serializable {
    @Deprecated
    public Map<String, Object> parse(String var1, boolean var2);

    public Result parse(String var1);

    public Result empty();

    public static interface Result {
        public Result addParam(String var1, String var2);

        public Result withQueryFragment(String var1);

        public Map<String, Object> getQueryParams();

        public String getQueryFragment();

        public boolean contains(String var1);

        public boolean isEmpty();
    }
}

