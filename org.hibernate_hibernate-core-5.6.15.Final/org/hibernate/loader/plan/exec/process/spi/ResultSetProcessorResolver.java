/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.exec.process.spi;

import org.hibernate.loader.plan.exec.process.internal.ResultSetProcessorImpl;
import org.hibernate.loader.plan.exec.process.spi.ReaderCollector;
import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessor;
import org.hibernate.loader.plan.exec.spi.AliasResolutionContext;
import org.hibernate.loader.plan.spi.LoadPlan;

public interface ResultSetProcessorResolver {
    public static final ResultSetProcessorResolver DEFAULT = (loadPlan, aliasResolutionContext, readerCollector, shouldUseOptionalEntityInstance, hadSubselectFetches) -> new ResultSetProcessorImpl(loadPlan, aliasResolutionContext, readerCollector.buildRowReader(), shouldUseOptionalEntityInstance, hadSubselectFetches);

    public ResultSetProcessor resolveResultSetProcessor(LoadPlan var1, AliasResolutionContext var2, ReaderCollector var3, boolean var4, boolean var5);
}

