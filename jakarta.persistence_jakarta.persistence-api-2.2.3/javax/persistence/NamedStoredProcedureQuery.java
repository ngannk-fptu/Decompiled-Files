/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.persistence.NamedStoredProcedureQueries;
import javax.persistence.QueryHint;
import javax.persistence.StoredProcedureParameter;

@Repeatable(value=NamedStoredProcedureQueries.class)
@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface NamedStoredProcedureQuery {
    public String name();

    public String procedureName();

    public StoredProcedureParameter[] parameters() default {};

    public Class[] resultClasses() default {};

    public String[] resultSetMappings() default {};

    public QueryHint[] hints() default {};
}

