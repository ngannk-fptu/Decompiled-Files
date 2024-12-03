/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.apt.dispatch;

import java.util.List;
import javax.annotation.processing.Processor;
import org.eclipse.jdt.internal.compiler.apt.dispatch.ProcessorInfo;

public interface IProcessorProvider {
    public ProcessorInfo discoverNextProcessor();

    public List<ProcessorInfo> getDiscoveredProcessors();

    public void reportProcessorException(Processor var1, Exception var2);
}

