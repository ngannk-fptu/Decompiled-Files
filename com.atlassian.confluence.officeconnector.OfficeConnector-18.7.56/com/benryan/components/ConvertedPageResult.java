/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.util.concurrent.ExecutionList
 *  com.google.common.util.concurrent.ForwardingFuture
 *  com.google.common.util.concurrent.ListenableFuture
 *  com.google.common.util.concurrent.MoreExecutors
 */
package com.benryan.components;

import com.benryan.conversion.SlideConversionDataHolder;
import com.benryan.conversion.SlidePageConversionData;
import com.google.common.util.concurrent.ExecutionList;
import com.google.common.util.concurrent.ForwardingFuture;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class ConvertedPageResult<T>
extends ForwardingFuture<SlidePageConversionData>
implements ListenableFuture<SlidePageConversionData> {
    private final ListenableFuture<T> wrappedBatchFuture;
    private final int slideNum;
    private final ExecutionList listeners = new ExecutionList();

    public static final ConvertedPageResult<?> createPdfConversionResult(ListenableFuture<Collection<SlidePageConversionData>> wrappedFuture, int slideNum) {
        return new ConvertedPageResult<Collection<SlidePageConversionData>>(wrappedFuture, slideNum){

            @Override
            protected SlidePageConversionData findPage(Collection<SlidePageConversionData> pages) {
                if (pages == null || pages.isEmpty()) {
                    return null;
                }
                for (SlidePageConversionData page : pages) {
                    if (page.getSlideNum() != this.getSlideNum()) continue;
                    return page;
                }
                return null;
            }
        };
    }

    public static final ConvertedPageResult<SlideConversionDataHolder> createSlideConversionResult(ListenableFuture<SlideConversionDataHolder> wrappedFuture, int slideNum) {
        return new ConvertedPageResult<SlideConversionDataHolder>(wrappedFuture, slideNum){

            @Override
            SlidePageConversionData findPage(SlideConversionDataHolder delegateResult) {
                if (delegateResult == null) {
                    return null;
                }
                return delegateResult.getPage(this.getSlideNum());
            }
        };
    }

    public static ConvertedPageResult<SlideConversionDataHolder> copySlideConversionResult(ConvertedPageResult<SlideConversionDataHolder> toCopy, int newSlideNum) {
        return ConvertedPageResult.createSlideConversionResult((ListenableFuture<SlideConversionDataHolder>)toCopy.delegate(), newSlideNum);
    }

    private ConvertedPageResult(ListenableFuture<T> wrappedFuture, int slideNum) {
        this.wrappedBatchFuture = wrappedFuture;
        this.slideNum = slideNum;
        this.wrappedBatchFuture.addListener(this::done, MoreExecutors.directExecutor());
    }

    protected final ListenableFuture delegate() {
        return this.wrappedBatchFuture;
    }

    abstract SlidePageConversionData findPage(T var1) throws ExecutionException;

    public final SlidePageConversionData get() throws InterruptedException, ExecutionException {
        return this.findPage(this.wrappedBatchFuture.get());
    }

    public final SlidePageConversionData get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.findPage(this.wrappedBatchFuture.get(timeout, unit));
    }

    protected final void done() {
        this.listeners.execute();
    }

    public final void addListener(Runnable listener, Executor exec) {
        this.listeners.add(listener, exec);
    }

    public final int getSlideNum() {
        return this.slideNum;
    }
}

