/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.apt.dispatch;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import javax.annotation.processing.Processor;
import javax.lang.model.SourceVersion;
import javax.tools.JavaFileManager;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseAnnotationProcessorManager;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BatchProcessingEnvImpl;
import org.eclipse.jdt.internal.compiler.apt.dispatch.ProcessorInfo;
import org.eclipse.jdt.internal.compiler.batch.Main;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;

public class BatchAnnotationProcessorManager
extends BaseAnnotationProcessorManager {
    private List<Processor> _setProcessors = null;
    private Iterator<Processor> _setProcessorIter = null;
    private List<String> _commandLineProcessors;
    private Iterator<String> _commandLineProcessorIter = null;
    private ServiceLoader<Processor> _serviceLoader = null;
    private Iterator<Processor> _serviceLoaderIter;
    private ClassLoader _procLoader;
    private static final boolean VERBOSE_PROCESSOR_DISCOVERY = true;
    private boolean _printProcessorDiscovery = false;

    @Override
    public void configure(Object batchCompiler, String[] commandLineArguments) {
        if (this._processingEnv != null) {
            throw new IllegalStateException("Calling configure() more than once on an AnnotationProcessorManager is not supported");
        }
        BatchProcessingEnvImpl processingEnv = new BatchProcessingEnvImpl(this, (Main)batchCompiler, commandLineArguments);
        this._processingEnv = processingEnv;
        JavaFileManager fileManager = processingEnv.getFileManager();
        if (fileManager instanceof StandardJavaFileManager) {
            Iterable<? extends File> location = null;
            if (SourceVersion.latest().compareTo(SourceVersion.RELEASE_8) > 0) {
                location = ((StandardJavaFileManager)fileManager).getLocation(StandardLocation.ANNOTATION_PROCESSOR_MODULE_PATH);
            }
            this._procLoader = location != null ? fileManager.getClassLoader(StandardLocation.ANNOTATION_PROCESSOR_MODULE_PATH) : fileManager.getClassLoader(StandardLocation.ANNOTATION_PROCESSOR_PATH);
        } else {
            this._procLoader = fileManager.getClassLoader(StandardLocation.ANNOTATION_PROCESSOR_PATH);
        }
        this.parseCommandLine(commandLineArguments);
        this._round = 0;
    }

    private void parseCommandLine(String[] commandLineArguments) {
        ArrayList<String> commandLineProcessors = null;
        int i = 0;
        while (i < commandLineArguments.length) {
            String option = commandLineArguments[i];
            if ("-XprintProcessorInfo".equals(option)) {
                this._printProcessorInfo = true;
                this._printProcessorDiscovery = true;
            } else if ("-XprintRounds".equals(option)) {
                this._printRounds = true;
            } else if ("-processor".equals(option)) {
                commandLineProcessors = new ArrayList<String>();
                String procs = commandLineArguments[++i];
                commandLineProcessors.addAll(Arrays.asList(procs.split(",")));
                break;
            }
            ++i;
        }
        this._commandLineProcessors = commandLineProcessors;
        if (this._commandLineProcessors != null) {
            this._commandLineProcessorIter = this._commandLineProcessors.iterator();
        }
    }

    @Override
    public ProcessorInfo discoverNextProcessor() {
        if (this._setProcessors != null) {
            if (this._setProcessorIter.hasNext()) {
                Processor p = this._setProcessorIter.next();
                p.init(this._processingEnv);
                ProcessorInfo pi = new ProcessorInfo(p);
                this._processors.add(pi);
                if (this._printProcessorDiscovery && this._out != null) {
                    this._out.println("API specified processor: " + pi);
                }
                return pi;
            }
            return null;
        }
        if (this._commandLineProcessors != null) {
            if (this._commandLineProcessorIter.hasNext()) {
                String proc = this._commandLineProcessorIter.next();
                try {
                    Class<?> clazz = this._procLoader.loadClass(proc);
                    Object o = clazz.newInstance();
                    Processor p = (Processor)o;
                    p.init(this._processingEnv);
                    ProcessorInfo pi = new ProcessorInfo(p);
                    this._processors.add(pi);
                    if (this._printProcessorDiscovery && this._out != null) {
                        this._out.println("Command line specified processor: " + pi);
                    }
                    return pi;
                }
                catch (Exception e) {
                    throw new AbortCompilation(null, (Throwable)e);
                }
            }
            return null;
        }
        if (this._serviceLoader == null) {
            this._serviceLoader = ServiceLoader.load(Processor.class, this._procLoader);
            this._serviceLoaderIter = this._serviceLoader.iterator();
        }
        try {
            if (this._serviceLoaderIter.hasNext()) {
                Processor p = this._serviceLoaderIter.next();
                p.init(this._processingEnv);
                ProcessorInfo pi = new ProcessorInfo(p);
                this._processors.add(pi);
                if (this._printProcessorDiscovery && this._out != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Discovered processor service ");
                    sb.append(pi);
                    sb.append("\n  supporting ");
                    sb.append(pi.getSupportedAnnotationTypesAsString());
                    sb.append("\n  in ");
                    sb.append(this.getProcessorLocation(p));
                    this._out.println(sb.toString());
                }
                return pi;
            }
        }
        catch (ServiceConfigurationError e) {
            throw new AbortCompilation(null, (Throwable)e);
        }
        return null;
    }

    private String getProcessorLocation(Processor p) {
        String location;
        boolean isMember = false;
        Class<?> outerClass = p.getClass();
        StringBuilder innerName = new StringBuilder();
        while (outerClass.isMemberClass()) {
            innerName.insert(0, outerClass.getSimpleName());
            innerName.insert(0, '$');
            isMember = true;
            outerClass = outerClass.getEnclosingClass();
        }
        String path = outerClass.getName();
        path = path.replace('.', '/');
        if (isMember) {
            path = String.valueOf(path) + innerName;
        }
        if ((location = this._procLoader.getResource(path = String.valueOf(path) + ".class").toString()).endsWith(path)) {
            location = location.substring(0, location.length() - path.length());
        }
        return location;
    }

    @Override
    public void reportProcessorException(Processor p, Exception e) {
        throw new AbortCompilation(null, (Throwable)e);
    }

    @Override
    public void setProcessors(Object[] processors) {
        if (!this._isFirstRound) {
            throw new IllegalStateException("setProcessors() cannot be called after processing has begun");
        }
        this._setProcessors = new ArrayList<Processor>(processors.length);
        Object[] objectArray = processors;
        int n = processors.length;
        int n2 = 0;
        while (n2 < n) {
            Object o = objectArray[n2];
            Processor p = (Processor)o;
            this._setProcessors.add(p);
            ++n2;
        }
        this._setProcessorIter = this._setProcessors.iterator();
        this._commandLineProcessors = null;
        this._commandLineProcessorIter = null;
    }

    @Override
    protected void cleanUp() {
        if (this._procLoader instanceof URLClassLoader) {
            try {
                ((URLClassLoader)this._procLoader).close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

