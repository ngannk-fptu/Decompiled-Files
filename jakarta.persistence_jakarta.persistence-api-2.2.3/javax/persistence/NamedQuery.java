/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.persistence.LockModeType;
import javax.persistence.NamedQueries;
import javax.persistence.QueryHint;

@Repeatable(value=NamedQueries.class)
@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface NamedQuery {
    public String name();

    public String query();

    public LockModeType lockMode() default LockModeType.NONE;

    public QueryHint[] hints() default {};
}

