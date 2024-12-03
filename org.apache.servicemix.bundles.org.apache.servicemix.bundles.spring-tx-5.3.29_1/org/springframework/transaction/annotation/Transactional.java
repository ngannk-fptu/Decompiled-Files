/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.annotation.AliasFor
 */
package org.springframework.transaction.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;

@Target(value={ElementType.TYPE, ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Transactional {
    @AliasFor(value="transactionManager")
    public String value() default "";

    @AliasFor(value="value")
    public String transactionManager() default "";

    public String[] label() default {};

    public Propagation propagation() default Propagation.REQUIRED;

    public Isolation isolation() default Isolation.DEFAULT;

    public int timeout() default -1;

    public String timeoutString() default "";

    public boolean readOnly() default false;

    public Class<? extends Throwable>[] rollbackFor() default {};

    public String[] rollbackForClassName() default {};

    public Class<? extends Throwable>[] noRollbackFor() default {};

    public String[] noRollbackForClassName() default {};
}

