/*
 * Decompiled with CFR 0.152.
 */
package javax.ejb;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.ejb.TransactionManagementType;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface TransactionManagement {
    public TransactionManagementType value() default TransactionManagementType.CONTAINER;
}

