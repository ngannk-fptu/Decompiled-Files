/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.JoinColumn
 */
package org.hibernate.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.persistence.JoinColumn;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;

@Target(value={ElementType.METHOD, ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
@Repeatable(value=JoinColumnsOrFormulas.class)
public @interface JoinColumnOrFormula {
    public JoinFormula formula() default @JoinFormula(value="", referencedColumnName="");

    public JoinColumn column() default @JoinColumn;
}

