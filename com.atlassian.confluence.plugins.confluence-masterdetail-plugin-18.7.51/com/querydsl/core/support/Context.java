/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.support;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Path;
import java.util.ArrayList;
import java.util.List;

public class Context {
    public boolean replace;
    public final List<Path<?>> paths = new ArrayList();
    public final List<EntityPath<?>> replacements = new ArrayList();

    public void add(Path<?> anyPath, EntityPath<?> replacement) {
        this.replace = true;
        this.paths.add(anyPath);
        this.replacements.add(replacement);
    }

    public void add(Context c) {
        this.replace |= c.replace;
        this.paths.addAll(c.paths);
        this.replacements.addAll(c.replacements);
    }

    public void clear() {
        this.paths.clear();
        this.replacements.clear();
    }
}

