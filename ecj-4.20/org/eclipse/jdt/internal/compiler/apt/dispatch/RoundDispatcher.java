/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.apt.dispatch;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import org.eclipse.jdt.internal.compiler.apt.dispatch.IProcessorProvider;
import org.eclipse.jdt.internal.compiler.apt.dispatch.ProcessorInfo;

public class RoundDispatcher {
    private final Set<TypeElement> _unclaimedAnnotations;
    private final RoundEnvironment _roundEnv;
    private final IProcessorProvider _provider;
    private boolean _searchForStar = false;
    private final PrintWriter _traceProcessorInfo;
    private final PrintWriter _traceRounds;
    private final List<ProcessorInfo> _processors;

    public RoundDispatcher(IProcessorProvider provider, RoundEnvironment env, Set<TypeElement> rootAnnotations, PrintWriter traceProcessorInfo, PrintWriter traceRounds) {
        this._provider = provider;
        this._processors = provider.getDiscoveredProcessors();
        this._roundEnv = env;
        this._unclaimedAnnotations = new HashSet<TypeElement>(rootAnnotations);
        this._traceProcessorInfo = traceProcessorInfo;
        this._traceRounds = traceRounds;
    }

    public void round() {
        if (this._traceRounds != null) {
            StringBuilder sbElements = new StringBuilder();
            sbElements.append("\tinput files: {");
            Iterator<? extends Element> iElements = this._roundEnv.getRootElements().iterator();
            boolean hasNext = iElements.hasNext();
            while (hasNext) {
                sbElements.append(iElements.next());
                hasNext = iElements.hasNext();
                if (!hasNext) continue;
                sbElements.append(',');
            }
            sbElements.append('}');
            this._traceRounds.println(sbElements.toString());
            StringBuilder sbAnnots = new StringBuilder();
            sbAnnots.append("\tannotations: [");
            Iterator<TypeElement> iAnnots = this._unclaimedAnnotations.iterator();
            hasNext = iAnnots.hasNext();
            while (hasNext) {
                sbAnnots.append(iAnnots.next());
                hasNext = iAnnots.hasNext();
                if (!hasNext) continue;
                sbAnnots.append(',');
            }
            sbAnnots.append(']');
            this._traceRounds.println(sbAnnots.toString());
            this._traceRounds.println("\tlast round: " + this._roundEnv.processingOver());
        }
        this._searchForStar = this._unclaimedAnnotations.isEmpty();
        for (ProcessorInfo pi : this._processors) {
            this.handleProcessor(pi);
        }
        while (this._searchForStar || !this._unclaimedAnnotations.isEmpty()) {
            ProcessorInfo pi;
            pi = this._provider.discoverNextProcessor();
            if (pi == null) break;
            this.handleProcessor(pi);
        }
    }

    private void handleProcessor(ProcessorInfo pi) {
        try {
            HashSet<TypeElement> annotationsToProcess = new HashSet<TypeElement>();
            boolean shouldCall = pi.computeSupportedAnnotations(this._unclaimedAnnotations, annotationsToProcess);
            if (shouldCall) {
                boolean claimed = pi._processor.process(annotationsToProcess, this._roundEnv);
                if (this._traceProcessorInfo != null && !this._roundEnv.processingOver()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Processor ");
                    sb.append(pi._processor.getClass().getName());
                    sb.append(" matches [");
                    Iterator i = annotationsToProcess.iterator();
                    boolean hasNext = i.hasNext();
                    while (hasNext) {
                        sb.append(i.next());
                        hasNext = i.hasNext();
                        if (!hasNext) continue;
                        sb.append(' ');
                    }
                    sb.append("] and returns ");
                    sb.append(claimed);
                    this._traceProcessorInfo.println(sb.toString());
                }
                if (claimed) {
                    this._unclaimedAnnotations.removeAll(annotationsToProcess);
                    if (pi.supportsStar()) {
                        this._searchForStar = false;
                    }
                }
            }
        }
        catch (Throwable e) {
            this._provider.reportProcessorException(pi._processor, new Exception(e));
        }
    }
}

