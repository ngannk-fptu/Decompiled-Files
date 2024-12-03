/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.eclipse.jdt.core.compiler.CategorizedProblem
 *  org.eclipse.jdt.internal.compiler.ClassFile
 *  org.eclipse.jdt.internal.compiler.CompilationResult
 *  org.eclipse.jdt.internal.compiler.Compiler
 *  org.eclipse.jdt.internal.compiler.DefaultErrorHandlingPolicies
 *  org.eclipse.jdt.internal.compiler.ICompilerRequestor
 *  org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy
 *  org.eclipse.jdt.internal.compiler.IProblemFactory
 *  org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader
 *  org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException
 *  org.eclipse.jdt.internal.compiler.env.IBinaryType
 *  org.eclipse.jdt.internal.compiler.env.ICompilationUnit
 *  org.eclipse.jdt.internal.compiler.env.INameEnvironment
 *  org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer
 *  org.eclipse.jdt.internal.compiler.impl.CompilerOptions
 *  org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory
 */
package org.apache.jasper.compiler;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.jasper.JasperException;
import org.apache.jasper.compiler.Compiler;
import org.apache.jasper.compiler.ErrorDispatcher;
import org.apache.jasper.compiler.JavacErrorDetail;
import org.apache.jasper.compiler.Localizer;
import org.apache.jasper.compiler.SmapStratum;
import org.apache.jasper.compiler.SmapUtil;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.DefaultErrorHandlingPolicies;
import org.eclipse.jdt.internal.compiler.ICompilerRequestor;
import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import org.eclipse.jdt.internal.compiler.IProblemFactory;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;

public class JDTCompiler
extends Compiler {
    private final Log log = LogFactory.getLog(JDTCompiler.class);

    @Override
    protected void generateClass(Map<String, SmapStratum> smaps) throws FileNotFoundException, JasperException, Exception {
        File javaFile;
        String actualTarget;
        String requestedTarget;
        String actualSource;
        String opt;
        long t1 = 0L;
        if (this.log.isDebugEnabled()) {
            t1 = System.currentTimeMillis();
        }
        final String sourceFile = this.ctxt.getServletJavaFileName();
        final String outputDir = this.ctxt.getOptions().getScratchDir().getAbsolutePath();
        String packageName = this.ctxt.getServletPackageName();
        final String targetClassName = (packageName.length() != 0 ? packageName + "." : "") + this.ctxt.getServletClassName();
        final ClassLoader classLoader = this.ctxt.getJspLoader();
        String[] fileNames = new String[]{sourceFile};
        String[] classNames = new String[]{targetClassName};
        final ArrayList problemList = new ArrayList();
        INameEnvironment env = new INameEnvironment(){

            public NameEnvironmentAnswer findType(char[][] compoundTypeName) {
                StringBuilder result = new StringBuilder();
                for (int i = 0; i < compoundTypeName.length; ++i) {
                    if (i > 0) {
                        result.append('.');
                    }
                    result.append(compoundTypeName[i]);
                }
                return this.findType(result.toString());
            }

            public NameEnvironmentAnswer findType(char[] typeName, char[][] packageName) {
                int i;
                StringBuilder result = new StringBuilder();
                for (i = 0; i < packageName.length; ++i) {
                    if (i > 0) {
                        result.append('.');
                    }
                    result.append(packageName[i]);
                }
                if (i > 0) {
                    result.append('.');
                }
                result.append(typeName);
                return this.findType(result.toString());
            }

            /*
             * Enabled aggressive block sorting
             * Enabled unnecessary exception pruning
             * Enabled aggressive exception aggregation
             */
            private NameEnvironmentAnswer findType(String className) {
                if (className.equals(targetClassName)) {
                    class CompilationUnit
                    implements ICompilationUnit {
                        private final String className;
                        private final String sourceFile;

                        CompilationUnit(String sourceFile, String className) {
                            this.className = className;
                            this.sourceFile = sourceFile;
                        }

                        public char[] getFileName() {
                            return this.sourceFile.toCharArray();
                        }

                        public char[] getContents() {
                            char[] result = null;
                            try (FileInputStream is = new FileInputStream(this.sourceFile);
                                 InputStreamReader isr = new InputStreamReader((InputStream)is, JDTCompiler.this.ctxt.getOptions().getJavaEncoding());
                                 BufferedReader reader = new BufferedReader(isr);){
                                int count;
                                char[] chars = new char[8192];
                                StringBuilder buf = new StringBuilder();
                                while ((count = ((Reader)reader).read(chars, 0, chars.length)) > 0) {
                                    buf.append(chars, 0, count);
                                }
                                result = new char[buf.length()];
                                buf.getChars(0, result.length, result, 0);
                            }
                            catch (IOException e) {
                                JDTCompiler.this.log.error((Object)Localizer.getMessage("jsp.error.compilation.source", this.sourceFile), (Throwable)e);
                            }
                            return result;
                        }

                        public char[] getMainTypeName() {
                            int dot = this.className.lastIndexOf(46);
                            if (dot > 0) {
                                return this.className.substring(dot + 1).toCharArray();
                            }
                            return this.className.toCharArray();
                        }

                        public char[][] getPackageName() {
                            StringTokenizer izer = new StringTokenizer(this.className, ".");
                            char[][] result = new char[izer.countTokens() - 1][];
                            for (int i = 0; i < result.length; ++i) {
                                String tok = izer.nextToken();
                                result[i] = tok.toCharArray();
                            }
                            return result;
                        }

                        public boolean ignoreOptionalProblems() {
                            return false;
                        }
                    }
                    CompilationUnit compilationUnit = new CompilationUnit(sourceFile, className);
                    return new NameEnvironmentAnswer((ICompilationUnit)compilationUnit, null);
                }
                String resourceName = className.replace('.', '/') + ".class";
                try (InputStream is = classLoader.getResourceAsStream(resourceName);){
                    int count;
                    if (is == null) return null;
                    byte[] buf = new byte[8192];
                    ByteArrayOutputStream baos = new ByteArrayOutputStream(buf.length);
                    while ((count = is.read(buf, 0, buf.length)) > 0) {
                        baos.write(buf, 0, count);
                    }
                    baos.flush();
                    byte[] classBytes = baos.toByteArray();
                    char[] fileName = className.toCharArray();
                    ClassFileReader classFileReader = new ClassFileReader(classBytes, fileName, true);
                    NameEnvironmentAnswer nameEnvironmentAnswer = new NameEnvironmentAnswer((IBinaryType)classFileReader, null);
                    return nameEnvironmentAnswer;
                }
                catch (IOException | ClassFormatException exc) {
                    JDTCompiler.this.log.error((Object)Localizer.getMessage("jsp.error.compilation.dependent", className), exc);
                }
                return null;
            }

            private boolean isPackage(String result) {
                boolean bl;
                block9: {
                    if (result.equals(targetClassName) || result.startsWith(targetClassName + '$')) {
                        return false;
                    }
                    String resourceName = result.replace('.', '/') + ".class";
                    InputStream is = classLoader.getResourceAsStream(resourceName);
                    try {
                        boolean bl2 = bl = is == null;
                        if (is == null) break block9;
                    }
                    catch (Throwable throwable) {
                        try {
                            if (is != null) {
                                try {
                                    is.close();
                                }
                                catch (Throwable throwable2) {
                                    throwable.addSuppressed(throwable2);
                                }
                            }
                            throw throwable;
                        }
                        catch (IOException e) {
                            return false;
                        }
                    }
                    is.close();
                }
                return bl;
            }

            public boolean isPackage(char[][] parentPackageName, char[] packageName) {
                int i;
                StringBuilder result = new StringBuilder();
                if (parentPackageName != null) {
                    for (i = 0; i < parentPackageName.length; ++i) {
                        if (i > 0) {
                            result.append('.');
                        }
                        result.append(parentPackageName[i]);
                    }
                }
                if (Character.isUpperCase(packageName[0]) && !this.isPackage(result.toString())) {
                    return false;
                }
                if (i > 0) {
                    result.append('.');
                }
                result.append(packageName);
                return this.isPackage(result.toString());
            }

            public void cleanup() {
            }
        };
        IErrorHandlingPolicy policy = DefaultErrorHandlingPolicies.proceedWithAllProblems();
        HashMap<String, String> settings = new HashMap<String, String>();
        settings.put("org.eclipse.jdt.core.compiler.debug.lineNumber", "generate");
        settings.put("org.eclipse.jdt.core.compiler.debug.sourceFile", "generate");
        settings.put("org.eclipse.jdt.core.compiler.problem.deprecation", "ignore");
        if (this.ctxt.getOptions().getJavaEncoding() != null) {
            settings.put("org.eclipse.jdt.core.encoding", this.ctxt.getOptions().getJavaEncoding());
        }
        if (this.ctxt.getOptions().getClassDebugInfo()) {
            settings.put("org.eclipse.jdt.core.compiler.debug.localVariable", "generate");
        }
        if (this.ctxt.getOptions().getCompilerSourceVM() != null) {
            opt = this.ctxt.getOptions().getCompilerSourceVM();
            if (opt.equals("1.1")) {
                settings.put("org.eclipse.jdt.core.compiler.source", "1.1");
            } else if (opt.equals("1.2")) {
                settings.put("org.eclipse.jdt.core.compiler.source", "1.2");
            } else if (opt.equals("1.3")) {
                settings.put("org.eclipse.jdt.core.compiler.source", "1.3");
            } else if (opt.equals("1.4")) {
                settings.put("org.eclipse.jdt.core.compiler.source", "1.4");
            } else if (opt.equals("1.5")) {
                settings.put("org.eclipse.jdt.core.compiler.source", "1.5");
            } else if (opt.equals("1.6")) {
                settings.put("org.eclipse.jdt.core.compiler.source", "1.6");
            } else if (opt.equals("1.7")) {
                settings.put("org.eclipse.jdt.core.compiler.source", "1.7");
            } else if (opt.equals("1.8")) {
                settings.put("org.eclipse.jdt.core.compiler.source", "1.8");
            } else if (opt.equals("9") || opt.equals("1.9")) {
                settings.put("org.eclipse.jdt.core.compiler.source", "9");
            } else if (opt.equals("10")) {
                settings.put("org.eclipse.jdt.core.compiler.source", "10");
            } else if (opt.equals("11")) {
                settings.put("org.eclipse.jdt.core.compiler.source", "11");
            } else if (opt.equals("12")) {
                settings.put("org.eclipse.jdt.core.compiler.source", "12");
            } else if (opt.equals("13")) {
                settings.put("org.eclipse.jdt.core.compiler.source", "13");
            } else if (opt.equals("14")) {
                settings.put("org.eclipse.jdt.core.compiler.source", "14");
            } else if (opt.equals("15")) {
                settings.put("org.eclipse.jdt.core.compiler.source", "15");
            } else if (opt.equals("16")) {
                settings.put("org.eclipse.jdt.core.compiler.source", "16");
            } else if (opt.equals("17")) {
                settings.put("org.eclipse.jdt.core.compiler.source", "17");
            } else if (opt.equals("18")) {
                settings.put("org.eclipse.jdt.core.compiler.source", "18");
            } else if (opt.equals("19")) {
                settings.put("org.eclipse.jdt.core.compiler.source", "19");
            } else if (opt.equals("20")) {
                settings.put("org.eclipse.jdt.core.compiler.source", "20");
            } else if (opt.equals("21")) {
                settings.put("org.eclipse.jdt.core.compiler.source", "21");
            } else {
                this.log.warn((Object)Localizer.getMessage("jsp.warning.unknown.sourceVM", opt));
                settings.put("org.eclipse.jdt.core.compiler.source", "1.8");
            }
        } else {
            settings.put("org.eclipse.jdt.core.compiler.source", "1.8");
        }
        if (this.ctxt.getOptions().getCompilerTargetVM() != null) {
            opt = this.ctxt.getOptions().getCompilerTargetVM();
            if (opt.equals("1.1")) {
                settings.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.1");
            } else if (opt.equals("1.2")) {
                settings.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.2");
            } else if (opt.equals("1.3")) {
                settings.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.3");
            } else if (opt.equals("1.4")) {
                settings.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.4");
            } else if (opt.equals("1.5")) {
                settings.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.5");
                settings.put("org.eclipse.jdt.core.compiler.compliance", "1.5");
            } else if (opt.equals("1.6")) {
                settings.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.6");
                settings.put("org.eclipse.jdt.core.compiler.compliance", "1.6");
            } else if (opt.equals("1.7")) {
                settings.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.7");
                settings.put("org.eclipse.jdt.core.compiler.compliance", "1.7");
            } else if (opt.equals("1.8")) {
                settings.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.8");
                settings.put("org.eclipse.jdt.core.compiler.compliance", "1.8");
            } else if (opt.equals("9") || opt.equals("1.9")) {
                settings.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "9");
                settings.put("org.eclipse.jdt.core.compiler.compliance", "9");
            } else if (opt.equals("10")) {
                settings.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "10");
                settings.put("org.eclipse.jdt.core.compiler.compliance", "10");
            } else if (opt.equals("11")) {
                settings.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "11");
                settings.put("org.eclipse.jdt.core.compiler.compliance", "11");
            } else if (opt.equals("12")) {
                settings.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "12");
                settings.put("org.eclipse.jdt.core.compiler.compliance", "12");
            } else if (opt.equals("13")) {
                settings.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "13");
                settings.put("org.eclipse.jdt.core.compiler.compliance", "13");
            } else if (opt.equals("14")) {
                settings.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "14");
                settings.put("org.eclipse.jdt.core.compiler.compliance", "14");
            } else if (opt.equals("15")) {
                settings.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "15");
                settings.put("org.eclipse.jdt.core.compiler.compliance", "15");
            } else if (opt.equals("16")) {
                settings.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "16");
                settings.put("org.eclipse.jdt.core.compiler.compliance", "16");
            } else if (opt.equals("17")) {
                settings.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "17");
                settings.put("org.eclipse.jdt.core.compiler.compliance", "17");
            } else if (opt.equals("18")) {
                settings.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "18");
                settings.put("org.eclipse.jdt.core.compiler.compliance", "18");
            } else if (opt.equals("19")) {
                settings.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "19");
                settings.put("org.eclipse.jdt.core.compiler.compliance", "19");
            } else if (opt.equals("20")) {
                settings.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "20");
                settings.put("org.eclipse.jdt.core.compiler.compliance", "20");
            } else if (opt.equals("21")) {
                settings.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "21");
                settings.put("org.eclipse.jdt.core.compiler.compliance", "21");
            } else {
                this.log.warn((Object)Localizer.getMessage("jsp.warning.unknown.targetVM", opt));
                settings.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.8");
            }
        } else {
            settings.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.8");
            settings.put("org.eclipse.jdt.core.compiler.compliance", "1.8");
        }
        DefaultProblemFactory problemFactory = new DefaultProblemFactory(Locale.getDefault());
        ICompilerRequestor requestor = new ICompilerRequestor(){

            public void acceptResult(CompilationResult result) {
                block19: {
                    try {
                        if (result.hasProblems()) {
                            CategorizedProblem[] problems;
                            for (CategorizedProblem categorizedProblem : problems = result.getProblems()) {
                                if (!categorizedProblem.isError()) continue;
                                String name = new String(categorizedProblem.getOriginatingFileName());
                                try {
                                    problemList.add(ErrorDispatcher.createJavacError(name, JDTCompiler.this.pageNodes, new StringBuilder(categorizedProblem.getMessage()), categorizedProblem.getSourceLineNumber(), JDTCompiler.this.ctxt));
                                }
                                catch (JasperException e) {
                                    JDTCompiler.this.log.error((Object)Localizer.getMessage("jsp.error.compilation.jdtProblemError"), (Throwable)((Object)e));
                                }
                            }
                        }
                        if (!problemList.isEmpty()) break block19;
                        ClassFile[] classFiles = result.getClassFiles();
                        for (CategorizedProblem categorizedProblem : classFiles) {
                            char[][] compoundName = categorizedProblem.getCompoundName();
                            StringBuilder classFileName = new StringBuilder(outputDir).append('/');
                            for (int j = 0; j < compoundName.length; ++j) {
                                if (j > 0) {
                                    classFileName.append('/');
                                }
                                classFileName.append(compoundName[j]);
                            }
                            byte[] bytes = categorizedProblem.getBytes();
                            classFileName.append(".class");
                            try (FileOutputStream fout = new FileOutputStream(classFileName.toString());
                                 BufferedOutputStream bos = new BufferedOutputStream(fout);){
                                bos.write(bytes);
                            }
                        }
                    }
                    catch (IOException exc) {
                        JDTCompiler.this.log.error((Object)Localizer.getMessage("jsp.error.compilation.jdt"), (Throwable)exc);
                    }
                }
            }
        };
        ICompilationUnit[] compilationUnits = new ICompilationUnit[classNames.length];
        for (int i = 0; i < compilationUnits.length; ++i) {
            String className = classNames[i];
            compilationUnits[i] = new CompilationUnit(fileNames[i], className);
        }
        CompilerOptions cOptions = new CompilerOptions(settings);
        String requestedSource = this.ctxt.getOptions().getCompilerSourceVM();
        if (requestedSource != null && !requestedSource.equals(actualSource = CompilerOptions.versionFromJdkLevel((long)cOptions.sourceLevel))) {
            this.log.warn((Object)Localizer.getMessage("jsp.warning.unsupported.sourceVM", requestedSource, actualSource));
        }
        if ((requestedTarget = this.ctxt.getOptions().getCompilerTargetVM()) != null && !requestedTarget.equals(actualTarget = CompilerOptions.versionFromJdkLevel((long)cOptions.targetJDK))) {
            this.log.warn((Object)Localizer.getMessage("jsp.warning.unsupported.targetVM", requestedTarget, actualTarget));
        }
        cOptions.parseLiteralExpressionsAsConstants = true;
        org.eclipse.jdt.internal.compiler.Compiler compiler = new org.eclipse.jdt.internal.compiler.Compiler(env, policy, cOptions, requestor, (IProblemFactory)problemFactory);
        compiler.compile(compilationUnits);
        if (!this.ctxt.keepGenerated() && !(javaFile = new File(this.ctxt.getServletJavaFileName())).delete()) {
            throw new JasperException(Localizer.getMessage("jsp.warning.compiler.javafile.delete.fail", javaFile));
        }
        if (!problemList.isEmpty()) {
            JavacErrorDetail[] jeds = problemList.toArray(new JavacErrorDetail[0]);
            this.errDispatcher.javacError(jeds);
        }
        if (this.log.isDebugEnabled()) {
            long t2 = System.currentTimeMillis();
            this.log.debug((Object)("Compiled " + this.ctxt.getServletJavaFileName() + " " + (t2 - t1) + "ms"));
        }
        if (this.ctxt.isPrototypeMode()) {
            return;
        }
        if (!this.options.isSmapSuppressed()) {
            SmapUtil.installSmap(smaps);
        }
    }
}

