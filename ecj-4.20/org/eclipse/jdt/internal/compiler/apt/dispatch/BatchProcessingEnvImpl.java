/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.apt.dispatch;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import javax.tools.JavaFileManager;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseAnnotationProcessorManager;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BatchMessagerImpl;
import org.eclipse.jdt.internal.compiler.apt.util.EclipseFileManager;
import org.eclipse.jdt.internal.compiler.batch.Main;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;

public class BatchProcessingEnvImpl
extends BaseProcessingEnvImpl {
    protected final BaseAnnotationProcessorManager _dispatchManager;
    protected final JavaFileManager _fileManager;
    protected final Main _compilerOwner;

    public BatchProcessingEnvImpl(BaseAnnotationProcessorManager dispatchManager, Main batchCompiler, String[] commandLineArguments) {
        this._compilerOwner = batchCompiler;
        this._compiler = batchCompiler.batchCompiler;
        this._dispatchManager = dispatchManager;
        Class<?> c = null;
        try {
            c = Class.forName("org.eclipse.jdt.internal.compiler.tool.EclipseCompilerImpl");
        }
        catch (ClassNotFoundException classNotFoundException) {}
        Field field = null;
        JavaFileManager javaFileManager = null;
        if (c != null) {
            try {
                field = c.getField("fileManager");
            }
            catch (SecurityException securityException) {
            }
            catch (IllegalArgumentException illegalArgumentException) {
            }
            catch (NoSuchFieldException noSuchFieldException) {}
        }
        if (field != null) {
            try {
                javaFileManager = (JavaFileManager)field.get(batchCompiler);
            }
            catch (IllegalArgumentException illegalArgumentException) {
            }
            catch (IllegalAccessException illegalAccessException) {}
        }
        if (javaFileManager != null) {
            this._fileManager = javaFileManager;
        } else {
            String encoding = batchCompiler.options.get("org.eclipse.jdt.core.encoding");
            Charset charset = encoding != null ? Charset.forName(encoding) : null;
            EclipseFileManager manager = new EclipseFileManager(batchCompiler.compilerLocale, charset);
            ArrayList<String> options = new ArrayList<String>();
            options.addAll(Arrays.asList(commandLineArguments));
            Iterator<String> iterator = options.iterator();
            while (iterator.hasNext()) {
                manager.handleOption((String)iterator.next(), iterator);
            }
            this._fileManager = manager;
        }
        this._processorOptions = Collections.unmodifiableMap(this.parseProcessorOptions(commandLineArguments));
        this._filer = new BatchFilerImpl(this._dispatchManager, this);
        this._messager = new BatchMessagerImpl(this, this._compilerOwner);
    }

    private Map<String, String> parseProcessorOptions(String[] args) {
        LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
        String[] stringArray = args;
        int n = args.length;
        int n2 = 0;
        while (n2 < n) {
            String arg = stringArray[n2];
            if (arg.startsWith("-A")) {
                int equals = arg.indexOf(61);
                if (equals == 2) {
                    IllegalArgumentException e = new IllegalArgumentException("-A option must have a key before the equals sign");
                    throw new AbortCompilation(null, (Throwable)e);
                }
                if (equals == arg.length() - 1) {
                    options.put(arg.substring(2, equals), null);
                } else if (equals == -1) {
                    options.put(arg.substring(2), null);
                } else {
                    options.put(arg.substring(2, equals), arg.substring(equals + 1));
                }
            }
            ++n2;
        }
        return options;
    }

    public JavaFileManager getFileManager() {
        return this._fileManager;
    }

    @Override
    public Locale getLocale() {
        return this._compilerOwner.compilerLocale;
    }

    public boolean shouldIgnoreOptionalProblems(char[] fileName) {
        return Main.shouldIgnoreOptionalProblems(this._compilerOwner.ignoreOptionalProblemsFromFolders, fileName);
    }
}

