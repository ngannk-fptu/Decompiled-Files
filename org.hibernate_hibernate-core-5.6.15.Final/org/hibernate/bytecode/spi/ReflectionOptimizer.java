/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.bytecode.spi;

public interface ReflectionOptimizer {
    public InstantiationOptimizer getInstantiationOptimizer();

    public AccessOptimizer getAccessOptimizer();

    public static interface AccessOptimizer {
        public String[] getPropertyNames();

        public Object[] getPropertyValues(Object var1);

        public void setPropertyValues(Object var1, Object[] var2);
    }

    public static interface InstantiationOptimizer {
        public Object newInstance();
    }
}

