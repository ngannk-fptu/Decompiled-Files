/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.persistence.NamedAttributeNode;

@Target(value={})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface NamedSubgraph {
    public String name();

    public Class type() default void.class;

    public NamedAttributeNode[] attributeNodes();
}

