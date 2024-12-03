/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.id.enhanced;

import org.hibernate.id.enhanced.HiLoOptimizer;
import org.hibernate.id.enhanced.LegacyHiLoAlgorithmOptimizer;
import org.hibernate.id.enhanced.NoopOptimizer;
import org.hibernate.id.enhanced.Optimizer;
import org.hibernate.id.enhanced.PooledLoOptimizer;
import org.hibernate.id.enhanced.PooledLoThreadLocalOptimizer;
import org.hibernate.id.enhanced.PooledOptimizer;
import org.hibernate.internal.util.StringHelper;
import org.jboss.logging.Logger;

public enum StandardOptimizerDescriptor {
    NONE("none", NoopOptimizer.class),
    HILO("hilo", HiLoOptimizer.class),
    LEGACY_HILO("legacy-hilo", LegacyHiLoAlgorithmOptimizer.class),
    POOLED("pooled", PooledOptimizer.class, true),
    POOLED_LO("pooled-lo", PooledLoOptimizer.class, true),
    POOLED_LOTL("pooled-lotl", PooledLoThreadLocalOptimizer.class, true);

    private static final Logger log;
    private final String externalName;
    private final Class<? extends Optimizer> optimizerClass;
    private final boolean isPooled;

    private StandardOptimizerDescriptor(String externalName, Class<? extends Optimizer> optimizerClass) {
        this(externalName, optimizerClass, false);
    }

    private StandardOptimizerDescriptor(String externalName, Class<? extends Optimizer> optimizerClass, boolean pooled) {
        this.externalName = externalName;
        this.optimizerClass = optimizerClass;
        this.isPooled = pooled;
    }

    public String getExternalName() {
        return this.externalName;
    }

    public Class<? extends Optimizer> getOptimizerClass() {
        return this.optimizerClass;
    }

    public boolean isPooled() {
        return this.isPooled;
    }

    public static StandardOptimizerDescriptor fromExternalName(String externalName) {
        if (StringHelper.isEmpty(externalName)) {
            log.debug((Object)"No optimizer specified, using NONE as default");
            return NONE;
        }
        if (StandardOptimizerDescriptor.NONE.externalName.equals(externalName)) {
            return NONE;
        }
        if (StandardOptimizerDescriptor.HILO.externalName.equals(externalName)) {
            return HILO;
        }
        if (StandardOptimizerDescriptor.LEGACY_HILO.externalName.equals(externalName)) {
            return LEGACY_HILO;
        }
        if (StandardOptimizerDescriptor.POOLED.externalName.equals(externalName)) {
            return POOLED;
        }
        if (StandardOptimizerDescriptor.POOLED_LO.externalName.equals(externalName)) {
            return POOLED_LO;
        }
        if (StandardOptimizerDescriptor.POOLED_LOTL.externalName.equals(externalName)) {
            return POOLED_LOTL;
        }
        log.debugf("Unknown optimizer key [%s]; returning null assuming Optimizer impl class name", (Object)externalName);
        return null;
    }

    static {
        log = Logger.getLogger(StandardOptimizerDescriptor.class);
    }
}

