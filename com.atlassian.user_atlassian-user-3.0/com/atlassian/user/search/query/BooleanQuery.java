/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.search.query;

import com.atlassian.user.Entity;
import com.atlassian.user.search.query.Query;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface BooleanQuery<T extends Entity>
extends Query<T> {
    public static final String AND = "&";
    public static final String OR = "|";

    public boolean isAND();

    public boolean isOR();

    public List<Query<T>> getQueries();
}

