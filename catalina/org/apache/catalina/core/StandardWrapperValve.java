/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.core;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import javax.servlet.ServletException;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.core.StandardWrapper;
import org.apache.catalina.valves.ValveBase;
import org.apache.tomcat.util.res.StringManager;

final class StandardWrapperValve
extends ValveBase {
    private static final StringManager sm = StringManager.getManager(StandardWrapperValve.class);
    private final LongAdder processingTime = new LongAdder();
    private volatile long maxTime;
    private volatile long minTime = Long.MAX_VALUE;
    private final AtomicInteger requestCount = new AtomicInteger(0);
    private final AtomicInteger errorCount = new AtomicInteger(0);

    StandardWrapperValve() {
        super(true);
    }

    /*
     * Exception decompiling
     */
    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 5 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    private void checkWrapperAvailable(Response response, StandardWrapper wrapper) throws IOException {
        long available = wrapper.getAvailable();
        if (available > 0L && available < Long.MAX_VALUE) {
            response.setDateHeader("Retry-After", available);
            response.sendError(503, sm.getString("standardWrapper.isUnavailable", new Object[]{wrapper.getName()}));
        } else if (available == Long.MAX_VALUE) {
            response.sendError(404, sm.getString("standardWrapper.notFound", new Object[]{wrapper.getName()}));
        }
    }

    private void exception(Request request, Response response, Throwable exception) {
        this.exception(request, response, exception, 500);
    }

    private void exception(Request request, Response response, Throwable exception, int errorCode) {
        request.setAttribute("javax.servlet.error.exception", exception);
        response.setStatus(errorCode);
        response.setError();
    }

    public long getProcessingTime() {
        return this.processingTime.sum();
    }

    public long getMaxTime() {
        return this.maxTime;
    }

    public long getMinTime() {
        return this.minTime;
    }

    public int getRequestCount() {
        return this.requestCount.get();
    }

    public int getErrorCount() {
        return this.errorCount.get();
    }

    public void incrementErrorCount() {
        this.errorCount.incrementAndGet();
    }

    @Override
    protected void initInternal() throws LifecycleException {
    }
}

