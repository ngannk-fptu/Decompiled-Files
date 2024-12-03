/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.instrumentation.operations.OpTimerFactory
 */
package com.atlassian.confluence.util.profiling;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.instrumentation.operations.OpTimerFactory;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@Internal
public interface AtlassianInstrumentationControl {
    public void setOpTimerFactorySupplier(Supplier<OpTimerFactory> var1);

    public void resetOpTimerFactorySupplier();
}

