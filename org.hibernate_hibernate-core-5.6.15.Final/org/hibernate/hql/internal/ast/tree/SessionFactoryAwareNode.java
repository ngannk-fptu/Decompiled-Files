/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.ast.tree;

import org.hibernate.engine.spi.SessionFactoryImplementor;

public interface SessionFactoryAwareNode {
    public void setSessionFactory(SessionFactoryImplementor var1);
}

