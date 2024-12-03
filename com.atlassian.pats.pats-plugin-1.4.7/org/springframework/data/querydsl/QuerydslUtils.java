/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.data.querydsl;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathType;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

public abstract class QuerydslUtils {
    public static final boolean QUERY_DSL_PRESENT = ClassUtils.isPresent((String)"com.querydsl.core.types.Predicate", (ClassLoader)QuerydslUtils.class.getClassLoader());

    private QuerydslUtils() {
    }

    public static String toDotPath(Path<?> path) {
        return QuerydslUtils.toDotPath(path, "");
    }

    private static String toDotPath(@Nullable Path<?> path, String tail) {
        if (path == null) {
            return tail;
        }
        PathMetadata metadata = path.getMetadata();
        Path<?> parent = metadata.getParent();
        if (parent == null) {
            return tail;
        }
        if (metadata.getPathType().equals(PathType.DELEGATE)) {
            return QuerydslUtils.toDotPath(parent, tail);
        }
        Object element = metadata.getElement();
        if (element == null || !StringUtils.hasText((String)element.toString())) {
            return QuerydslUtils.toDotPath(parent, tail);
        }
        return QuerydslUtils.toDotPath(parent, StringUtils.hasText((String)tail) ? String.format("%s.%s", element, tail) : element.toString());
    }
}

