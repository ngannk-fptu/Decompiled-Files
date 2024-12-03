/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql;

import com.querydsl.sql.RelationalPath;
import java.util.Map;

public class Beans {
    private final Map<? extends RelationalPath<?>, ?> beans;

    public Beans(Map<? extends RelationalPath<?>, ?> beans) {
        this.beans = beans;
    }

    public <T> T get(RelationalPath<T> path) {
        return (T)this.beans.get(path);
    }
}

