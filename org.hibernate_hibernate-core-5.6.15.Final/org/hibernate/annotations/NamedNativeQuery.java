/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.hibernate.annotations.CacheModeType;
import org.hibernate.annotations.FlushModeType;
import org.hibernate.annotations.NamedNativeQueries;

@Target(value={ElementType.TYPE, ElementType.PACKAGE})
@Retention(value=RetentionPolicy.RUNTIME)
@Repeatable(value=NamedNativeQueries.class)
public @interface NamedNativeQuery {
    public String name();

    public String query();

    public Class resultClass() default void.class;

    public String resultSetMapping() default "";

    public FlushModeType flushMode() default FlushModeType.PERSISTENCE_CONTEXT;

    public boolean cacheable() default false;

    public String cacheRegion() default "";

    public int fetchSize() default -1;

    public int timeout() default -1;

    public boolean callable() default false;

    public String comment() default "";

    public CacheModeType cacheMode() default CacheModeType.NORMAL;

    public boolean readOnly() default false;

    public String[] querySpaces() default {};
}

