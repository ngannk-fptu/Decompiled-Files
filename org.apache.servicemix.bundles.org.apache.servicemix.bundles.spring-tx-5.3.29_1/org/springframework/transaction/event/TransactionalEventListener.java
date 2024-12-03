/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.event.EventListener
 *  org.springframework.core.annotation.AliasFor
 */
package org.springframework.transaction.event;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AliasFor;
import org.springframework.transaction.event.TransactionPhase;

@Target(value={ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
@EventListener
public @interface TransactionalEventListener {
    public TransactionPhase phase() default TransactionPhase.AFTER_COMMIT;

    public boolean fallbackExecution() default false;

    @AliasFor(annotation=EventListener.class, attribute="classes")
    public Class<?>[] value() default {};

    @AliasFor(annotation=EventListener.class, attribute="classes")
    public Class<?>[] classes() default {};

    @AliasFor(annotation=EventListener.class, attribute="condition")
    public String condition() default "";

    @AliasFor(annotation=EventListener.class, attribute="id")
    public String id() default "";
}

