/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.graph.internal.parse;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.graph.spi.AttributeNodeImplementor;
import org.hibernate.graph.spi.SubGraphImplementor;

@FunctionalInterface
public interface SubGraphGenerator {
    public SubGraphImplementor<?> createSubGraph(AttributeNodeImplementor<?> var1, String var2, SessionFactoryImplementor var3);
}

