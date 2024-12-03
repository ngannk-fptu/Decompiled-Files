/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedSubgraph;

@Repeatable(value=NamedEntityGraphs.class)
@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface NamedEntityGraph {
    public String name() default "";

    public NamedAttributeNode[] attributeNodes() default {};

    public boolean includeAllAttributes() default false;

    public NamedSubgraph[] subgraphs() default {};

    public NamedSubgraph[] subclassSubgraphs() default {};
}

