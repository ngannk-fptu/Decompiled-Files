/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.util.profiling.Timers
 *  com.google.common.base.Throwables
 *  io.atlassian.util.concurrent.ThreadFactories
 *  io.atlassian.util.concurrent.Timeout
 *  org.apache.commons.lang3.StringUtils
 *  org.eclipse.core.runtime.IProgressMonitor
 *  org.eclipse.core.runtime.NullProgressMonitor
 *  org.jdom.Document
 *  org.jdom.output.Format
 *  org.jdom.output.XMLOutputter
 *  org.jdom.transform.JDOMResult
 *  org.outerj.daisy.diff.HtmlCleaner
 *  org.outerj.daisy.diff.html.HTMLDiffer
 *  org.outerj.daisy.diff.html.TextNodeComparator
 *  org.outerj.daisy.diff.html.dom.DomTree
 *  org.outerj.daisy.diff.html.dom.DomTreeBuilder
 *  org.outerj.daisy.diff.output.DiffOutput
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.diff;

import com.atlassian.confluence.diff.ConfluenceHtmlSaxDiffOutput;
import com.atlassian.confluence.diff.DaisyHtmlDiffer;
import com.atlassian.confluence.diff.DiffException;
import com.atlassian.confluence.diff.DiffPostProcessor;
import com.atlassian.confluence.diff.HtmlDiffer;
import com.atlassian.confluence.diff.InterruptedDiffException;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timers;
import com.google.common.base.Throwables;
import io.atlassian.util.concurrent.ThreadFactories;
import io.atlassian.util.concurrent.Timeout;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import javax.xml.transform.Result;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.transform.JDOMResult;
import org.outerj.daisy.diff.HtmlCleaner;
import org.outerj.daisy.diff.html.HTMLDiffer;
import org.outerj.daisy.diff.html.TextNodeComparator;
import org.outerj.daisy.diff.html.dom.DomTree;
import org.outerj.daisy.diff.html.dom.DomTreeBuilder;
import org.outerj.daisy.diff.output.DiffOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

final class DaisyDelegate
implements HtmlDiffer {
    private static final Logger log = LoggerFactory.getLogger(DaisyDelegate.class);
    private static final String PREFIX = "diff";
    private static final HtmlCleaner CLEANER = new HtmlCleaner();
    private static final Locale LOCALE = Locale.getDefault();
    private final boolean useOldAlgorithm = Boolean.getBoolean("DaisyHtmlDiffer.useOldAlgorithm");
    private final Iterable<DiffPostProcessor> postProcessors;
    private final Format outputFormat;
    private final SAXTransformerFactory saxTransformerFactory = DaisyDelegate.createTransformerFactory();

    DaisyDelegate(Iterable<DiffPostProcessor> postProcessors) {
        this.postProcessors = postProcessors;
        this.outputFormat = Format.getRawFormat();
        this.outputFormat.setOmitDeclaration(true);
        this.outputFormat.setOmitEncoding(true);
    }

    @Override
    public String diff(String leftHtml, String rightHtml, Timeout timeout) {
        return this.diff(leftHtml, rightHtml, timeout, this.useOldAlgorithm);
    }

    String diff(String leftHtml, String rightHtml, Timeout timeout, boolean useOldAlgorithm) {
        String documentString;
        if (timeout.isExpired()) {
            throw new InterruptedDiffException("Diff timed out before start of diffing.", DaisyHtmlDiffer.TIMEOUT);
        }
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        ExecutorService executor = Executors.newSingleThreadExecutor(ThreadFactories.namedThreadFactory((String)this.getClass().getSimpleName()));
        Future<String> handler = executor.submit(() -> {
            ConfluenceUser threadUser = AuthenticatedUserThreadLocal.get();
            AuthenticatedUserThreadLocal.set(currentUser);
            try {
                String string;
                block9: {
                    Ticker ignored = Timers.start((String)"DaisyHtmlDiffer.diff()");
                    try {
                        JDOMResult jdomResult = this.htmlDiff(leftHtml, rightHtml, timeout, useOldAlgorithm);
                        Document outputDocument = this.postProcess(jdomResult.getDocument());
                        string = this.toString(outputDocument);
                        if (ignored == null) break block9;
                    }
                    catch (Throwable throwable) {
                        if (ignored != null) {
                            try {
                                ignored.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    ignored.close();
                }
                return string;
            }
            finally {
                AuthenticatedUserThreadLocal.set(threadUser);
            }
        });
        try {
            documentString = handler.get(timeout.getTime(), timeout.getUnit());
        }
        catch (TimeoutException e) {
            handler.cancel(true);
            throw new InterruptedDiffException("Diff timed out during diffing.", DaisyHtmlDiffer.TIMEOUT);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InterruptedDiffException("Diff was interrupted or already failed during diffing.", DaisyHtmlDiffer.TIMEOUT);
        }
        catch (ExecutionException e) {
            Throwables.propagateIfInstanceOf((Throwable)e.getCause(), DiffException.class);
            throw Throwables.propagate((Throwable)e.getCause());
        }
        finally {
            executor.shutdown();
        }
        return documentString;
    }

    private JDOMResult htmlDiff(String leftHtml, String rightHtml, Timeout timeout, boolean useOldAlgorithm) {
        TransformerHandler transformerHandler;
        JDOMResult jdomResult = new JDOMResult();
        try {
            transformerHandler = this.saxTransformerFactory.newTransformerHandler();
        }
        catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        }
        transformerHandler.setResult((Result)jdomResult);
        TextNodeComparator leftComparator = new TextNodeComparator(this.getCleanedDomTree(leftHtml), LOCALE);
        TextNodeComparator rightComparator = new TextNodeComparator(this.getCleanedDomTree(rightHtml), LOCALE);
        try {
            transformerHandler.startDocument();
            ConfluenceHtmlSaxDiffOutput output = new ConfluenceHtmlSaxDiffOutput(transformerHandler, PREFIX);
            HTMLDiffer differ = new HTMLDiffer((DiffOutput)output);
            differ.diff(leftComparator, rightComparator, (IProgressMonitor)new TimingOutProgressMonitor(timeout), !useOldAlgorithm);
            if (timeout.isExpired()) {
                throw new InterruptedDiffException("Diff timed out during daisydiff.", DaisyHtmlDiffer.TIMEOUT);
            }
            transformerHandler.endDocument();
        }
        catch (SAXException e) {
            throw new DiffException("Error generating diff", e);
        }
        return jdomResult;
    }

    private Document postProcess(Document document) {
        Document outputDocument = document;
        for (DiffPostProcessor processor : this.postProcessors) {
            outputDocument = processor.process(outputDocument);
        }
        return outputDocument;
    }

    private String toString(Document outputDocument) {
        StringWriter xmlWriter = new StringWriter();
        XMLOutputter outputter = new XMLOutputter(this.outputFormat);
        try {
            outputter.output(outputDocument, (Writer)xmlWriter);
            String output = xmlWriter.toString();
            output = output.replaceFirst("\\A<body>", "");
            output = output.replaceFirst("</body>\\Z", "");
            return StringUtils.trim((String)output);
        }
        catch (IOException e) {
            throw new DiffException("The Diff output could not be written.", e);
        }
    }

    private DomTree getCleanedDomTree(String input) {
        DomTreeBuilder result = new DomTreeBuilder();
        try (Ticker ignored = Timers.start((String)"DaisyHtmlDiffer.getCleanedDomTree()");){
            CLEANER.cleanAndParse(new InputSource(new StringReader(input)), (ContentHandler)result);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        catch (SAXException e) {
            throw new RuntimeException("Error parsing html during html cleaning", e);
        }
        return result;
    }

    private static SAXTransformerFactory createTransformerFactory() {
        SAXTransformerFactory factory = (SAXTransformerFactory)TransformerFactory.newInstance();
        try {
            factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
        }
        catch (TransformerConfigurationException e) {
            log.error("Failed to enable secure processing", (Throwable)e);
        }
        return factory;
    }

    static class TimingOutProgressMonitor
    extends NullProgressMonitor {
        final Timeout timeout;

        TimingOutProgressMonitor(Timeout timeout) {
            this.timeout = timeout;
        }

        public boolean isCanceled() {
            return this.timeout.isExpired();
        }
    }
}

