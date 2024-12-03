/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory
 *  com.atlassian.confluence.content.render.xhtml.XmlOutputFactory
 *  com.atlassian.confluence.content.render.xhtml.XmlOutputFactoryProvider
 *  com.atlassian.confluence.util.LoggingUncaughtExceptionHandler
 *  com.atlassian.confluence.util.diffs.MergeResult
 *  com.atlassian.confluence.util.diffs.Merger
 *  com.atlassian.confluence.util.diffs.SimpleMergeResult
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory
 *  com.atlassian.util.concurrent.ThreadFactories
 *  com.atlassian.util.concurrent.ThreadFactories$Type
 *  com.google.common.annotations.VisibleForTesting
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  tdm.lib.BaseNode
 *  tdm.lib.BranchNode
 *  tdm.lib.HeuristicMatching
 *  tdm.lib.InterruptedRuntimeException
 *  tdm.lib.Merge
 *  tdm.lib.NodeFactory
 *  tdm.lib.ParseException
 *  tdm.lib.StaxWrapperParser
 *  tdm.lib.TriMatching
 */
package com.atlassian.confluence.plugins.merge;

import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactory;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactoryProvider;
import com.atlassian.confluence.util.LoggingUncaughtExceptionHandler;
import com.atlassian.confluence.util.diffs.MergeResult;
import com.atlassian.confluence.util.diffs.Merger;
import com.atlassian.confluence.util.diffs.SimpleMergeResult;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory;
import com.atlassian.util.concurrent.ThreadFactories;
import com.google.common.annotations.VisibleForTesting;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import tdm.lib.BaseNode;
import tdm.lib.BranchNode;
import tdm.lib.HeuristicMatching;
import tdm.lib.InterruptedRuntimeException;
import tdm.lib.Merge;
import tdm.lib.NodeFactory;
import tdm.lib.ParseException;
import tdm.lib.StaxWrapperParser;
import tdm.lib.TriMatching;

public class TDMMerger
implements Merger,
DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(TDMMerger.class);
    private static final int TIMEOUT_SECONDS = Integer.getInteger("tdm.merger.timeout.seconds", 25);
    private static final int MAX_THREADS = Integer.getInteger("tdm.merger.threads", Runtime.getRuntime().availableProcessors());
    private static final String THREAD_PREFIX = TDMMerger.class.getName();
    private final XmlEventReaderFactory xmlEventReaderFactory;
    private final XmlOutputFactory xmlOutputFactory;
    @VisibleForTesting
    final ExecutorService executorService;

    public TDMMerger(@ComponentImport XmlEventReaderFactory xmlEventReaderFactory, @ComponentImport XmlOutputFactoryProvider xmlOutputFactoryProvider, @ComponentImport ThreadLocalDelegateExecutorFactory threadLocalDelegateExecutorFactory) {
        this.xmlEventReaderFactory = xmlEventReaderFactory;
        this.xmlOutputFactory = xmlOutputFactoryProvider.getXmlFragmentOutputFactory();
        this.executorService = threadLocalDelegateExecutorFactory.createExecutorService((ExecutorService)new ThreadPoolExecutor(0, MAX_THREADS, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(), ThreadFactories.named((String)THREAD_PREFIX).type(ThreadFactories.Type.DAEMON).uncaughtExceptionHandler((Thread.UncaughtExceptionHandler)LoggingUncaughtExceptionHandler.INSTANCE).build()));
    }

    private MergeResult mergeContentInternal(String base, String left, String right) {
        try {
            if (base != null && base.equals(left)) {
                return new SimpleMergeResult(false, right);
            }
            if (base != null && base.equals(right)) {
                return new SimpleMergeResult(false, left);
            }
            HeuristicMatching matcher = new HeuristicMatching();
            BaseNode baseNode = base != null ? (BaseNode)StaxWrapperParser.parse((NodeFactory)matcher.getBaseNodeFactory(), (XMLEventReader)this.xmlEventReaderFactory.createStorageXmlEventReader((Reader)new StringReader(base))) : null;
            BranchNode leftNode = (BranchNode)StaxWrapperParser.parse((NodeFactory)matcher.getBranchNodeFactory(), (XMLEventReader)this.xmlEventReaderFactory.createStorageXmlEventReader((Reader)new StringReader(left)));
            BranchNode rightNode = (BranchNode)StaxWrapperParser.parse((NodeFactory)matcher.getBranchNodeFactory(), (XMLEventReader)this.xmlEventReaderFactory.createStorageXmlEventReader((Reader)new StringReader(right)));
            Merge merger = new Merge(new TriMatching(leftNode, baseNode, rightNode));
            StringWriter writer = new StringWriter(base != null ? base.length() : 0);
            merger.merge(this.xmlOutputFactory.createXMLEventWriter((Writer)writer));
            return new SimpleMergeResult(merger.getConflictLog().hasConflicts(), writer.toString());
        }
        catch (InterruptedRuntimeException e) {
            log.trace("Merge was interrupted.", (Throwable)e);
        }
        catch (XMLStreamException | ParseException e) {
            log.warn("Merge failed.", e);
        }
        catch (RuntimeException e) {
            log.warn("Merge failed : {}", (Object)e.getMessage());
        }
        return SimpleMergeResult.FAIL_MERGE_RESULT;
    }

    public MergeResult mergeContent(String base, String left, String right, long timeout, @NonNull TimeUnit unit) {
        Future<MergeResult> result = this.executorService.submit(() -> this.mergeContentInternal(base, left, right));
        try {
            return result.get(timeout, unit);
        }
        catch (InterruptedException e) {
            result.cancel(true);
            Thread.interrupted();
        }
        catch (ExecutionException e) {
            log.warn("Merge failed.", (Throwable)e);
        }
        catch (TimeoutException e) {
            result.cancel(true);
            log.debug("Cancelled a merge that was taking longer than {} seconds", (Object)unit.toSeconds(timeout));
        }
        return SimpleMergeResult.FAIL_MERGE_RESULT;
    }

    public MergeResult mergeContent(String base, String left, String right) {
        return this.mergeContent(base, left, right, TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    public void destroy() {
        this.executorService.shutdownNow();
    }
}

