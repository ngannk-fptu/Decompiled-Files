/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.BuildException
 *  org.apache.tools.ant.taskdefs.Javac
 *  org.apache.tools.ant.taskdefs.compilers.DefaultCompilerAdapter
 *  org.apache.tools.ant.types.Commandline
 *  org.apache.tools.ant.types.Commandline$Argument
 *  org.apache.tools.ant.types.Path
 *  org.apache.tools.ant.util.JavaEnvUtils
 */
package org.eclipse.jdt.core;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.taskdefs.compilers.DefaultCompilerAdapter;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.JavaEnvUtils;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.antadapter.AntAdapterMessages;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.util.SuffixConstants;
import org.eclipse.jdt.internal.compiler.util.Util;

public class JDTCompilerAdapter
extends DefaultCompilerAdapter {
    private static final char[] SEPARATOR_CHARS = new char[]{'/', '\\'};
    private static final char[] ADAPTER_PREFIX = "#ADAPTER#".toCharArray();
    private static final char[] ADAPTER_ENCODING = "ENCODING#".toCharArray();
    private static final char[] ADAPTER_ACCESS = "ACCESS#".toCharArray();
    private static String compilerClass = "org.eclipse.jdt.internal.compiler.batch.Main";
    String logFileName;
    Map customDefaultOptions;
    private Map fileEncodings = null;
    private Map dirEncodings = null;
    private List accessRules = null;

    public boolean execute() throws BuildException {
        this.attributes.log(AntAdapterMessages.getString("ant.jdtadapter.info.usingJDTCompiler"), 3);
        Commandline cmd = this.setupJavacCommand();
        try {
            Class<?> c = Class.forName(compilerClass);
            Constructor<?> batchCompilerConstructor = c.getConstructor(PrintWriter.class, PrintWriter.class, Boolean.TYPE, Map.class);
            Object batchCompilerInstance = batchCompilerConstructor.newInstance(new PrintWriter(System.out), new PrintWriter(System.err), Boolean.TRUE, this.customDefaultOptions);
            Method compile = c.getMethod("compile", String[].class);
            Object result = compile.invoke(batchCompilerInstance, new Object[]{cmd.getArguments()});
            boolean resultValue = (Boolean)result;
            if (!resultValue && this.logFileName != null) {
                this.attributes.log(AntAdapterMessages.getString("ant.jdtadapter.error.compilationFailed", this.logFileName));
            }
            return resultValue;
        }
        catch (ClassNotFoundException cnfe) {
            throw new BuildException(AntAdapterMessages.getString("ant.jdtadapter.error.cannotFindJDTCompiler"), (Throwable)cnfe);
        }
        catch (Exception ex) {
            throw new BuildException((Throwable)ex);
        }
    }

    protected Commandline setupJavacCommand() throws BuildException {
        int length;
        String source;
        String memoryParameterPrefix;
        Commandline cmd = new Commandline();
        this.customDefaultOptions = new CompilerOptions().getMap();
        Class<Javac> javacClass = Javac.class;
        String[] compilerArgs = this.processCompilerArguments(javacClass);
        cmd.createArgument().setValue("-noExit");
        if (this.bootclasspath != null) {
            cmd.createArgument().setValue("-bootclasspath");
            if (this.bootclasspath.size() != 0) {
                cmd.createArgument().setPath(this.bootclasspath);
            } else {
                cmd.createArgument().setValue(Util.EMPTY_STRING);
            }
        }
        if (this.extdirs != null) {
            cmd.createArgument().setValue("-extdirs");
            cmd.createArgument().setPath(this.extdirs);
        }
        Path classpath = new Path(this.project);
        classpath.append(this.getCompileClasspath());
        cmd.createArgument().setValue("-classpath");
        this.createClasspathArgument(cmd, classpath);
        Path sourcepath = null;
        Method getSourcepathMethod = null;
        try {
            getSourcepathMethod = javacClass.getMethod("getSourcepath", null);
        }
        catch (NoSuchMethodException noSuchMethodException) {}
        Path compileSourcePath = null;
        if (getSourcepathMethod != null) {
            try {
                compileSourcePath = (Path)getSourcepathMethod.invoke((Object)this.attributes, null);
            }
            catch (IllegalAccessException | InvocationTargetException reflectiveOperationException) {}
        }
        sourcepath = compileSourcePath != null ? compileSourcePath : this.src;
        cmd.createArgument().setValue("-sourcepath");
        this.createClasspathArgument(cmd, sourcepath);
        String javaVersion = JavaEnvUtils.getJavaVersion();
        String string = memoryParameterPrefix = javaVersion.equals("1.1") ? "-J-" : "-J-X";
        if (this.memoryInitialSize != null) {
            if (!this.attributes.isForkedJavac()) {
                this.attributes.log(AntAdapterMessages.getString("ant.jdtadapter.info.ignoringMemoryInitialSize"), 1);
            } else {
                cmd.createArgument().setValue(String.valueOf(memoryParameterPrefix) + "ms" + this.memoryInitialSize);
            }
        }
        if (this.memoryMaximumSize != null) {
            if (!this.attributes.isForkedJavac()) {
                this.attributes.log(AntAdapterMessages.getString("ant.jdtadapter.info.ignoringMemoryMaximumSize"), 1);
            } else {
                cmd.createArgument().setValue(String.valueOf(memoryParameterPrefix) + "mx" + this.memoryMaximumSize);
            }
        }
        if (this.debug) {
            Method getDebugLevelMethod = null;
            try {
                getDebugLevelMethod = javacClass.getMethod("getDebugLevel", null);
            }
            catch (NoSuchMethodException noSuchMethodException) {}
            String debugLevel = null;
            if (getDebugLevelMethod != null) {
                try {
                    debugLevel = (String)getDebugLevelMethod.invoke((Object)this.attributes, null);
                }
                catch (IllegalAccessException | InvocationTargetException reflectiveOperationException) {}
            }
            if (debugLevel != null) {
                this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.debug.localVariable", "do not generate");
                this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.debug.lineNumber", "do not generate");
                this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.debug.sourceFile", "do not generate");
                if (debugLevel.length() != 0) {
                    if (debugLevel.indexOf("vars") != -1) {
                        this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.debug.localVariable", "generate");
                    }
                    if (debugLevel.indexOf("lines") != -1) {
                        this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.debug.lineNumber", "generate");
                    }
                    if (debugLevel.indexOf("source") != -1) {
                        this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.debug.sourceFile", "generate");
                    }
                }
            } else {
                this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.debug.localVariable", "generate");
                this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.debug.lineNumber", "generate");
                this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.debug.sourceFile", "generate");
            }
        } else {
            this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.debug.localVariable", "do not generate");
            this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.debug.lineNumber", "do not generate");
            this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.debug.sourceFile", "do not generate");
        }
        if (this.attributes.getNowarn()) {
            Object[] entries = this.customDefaultOptions.entrySet().toArray();
            int i = 0;
            int max = entries.length;
            while (i < max) {
                Map.Entry entry = (Map.Entry)entries[i];
                if (entry.getKey() instanceof String && entry.getValue() instanceof String && ((String)entry.getValue()).equals("warning")) {
                    this.customDefaultOptions.put(entry.getKey(), "ignore");
                }
                ++i;
            }
            this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.taskTags", Util.EMPTY_STRING);
            if (this.deprecation) {
                this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.problem.deprecation", "warning");
                this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.problem.deprecationInDeprecatedCode", "enabled");
                this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.problem.deprecationWhenOverridingDeprecatedMethod", "enabled");
            }
        } else if (this.deprecation) {
            this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.problem.deprecation", "warning");
            this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.problem.deprecationInDeprecatedCode", "enabled");
            this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.problem.deprecationWhenOverridingDeprecatedMethod", "enabled");
        } else {
            this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.problem.deprecation", "ignore");
            this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.problem.deprecationInDeprecatedCode", "disabled");
            this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.problem.deprecationWhenOverridingDeprecatedMethod", "disabled");
        }
        if (this.destDir != null) {
            cmd.createArgument().setValue("-d");
            cmd.createArgument().setFile(this.destDir.getAbsoluteFile());
        }
        if (this.verbose) {
            cmd.createArgument().setValue("-verbose");
        }
        if (!this.attributes.getFailonerror()) {
            cmd.createArgument().setValue("-proceedOnError");
        }
        if (this.target != null) {
            this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", this.target);
        }
        if ((source = this.attributes.getSource()) != null) {
            this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.source", source);
            this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.compliance", source);
        }
        if (this.release != null) {
            this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.release", this.release);
        }
        if (compilerArgs != null && (length = compilerArgs.length) != 0) {
            int i = 0;
            int max = length;
            while (i < max) {
                String arg = compilerArgs[i];
                if (this.logFileName == null && "-log".equals(arg) && i + 1 < max) {
                    this.logFileName = compilerArgs[i + 1];
                }
                cmd.createArgument().setValue(arg);
                ++i;
            }
        }
        if (this.encoding != null) {
            cmd.createArgument().setValue("-encoding");
            cmd.createArgument().setValue(this.encoding);
        }
        this.logAndAddFilesToCompile(cmd);
        return cmd;
    }

    private String[] processCompilerArguments(Class javacClass) {
        Method getCurrentCompilerArgsMethod = null;
        try {
            getCurrentCompilerArgsMethod = javacClass.getMethod("getCurrentCompilerArgs", null);
        }
        catch (NoSuchMethodException noSuchMethodException) {}
        String[] compilerArgs = null;
        if (getCurrentCompilerArgsMethod != null) {
            try {
                compilerArgs = (String[])getCurrentCompilerArgsMethod.invoke((Object)this.attributes, null);
            }
            catch (IllegalAccessException | InvocationTargetException reflectiveOperationException) {}
        }
        if (compilerArgs != null) {
            this.checkCompilerArgs(compilerArgs);
        }
        return compilerArgs;
    }

    private void checkCompilerArgs(String[] args) {
        int i = 0;
        while (i < args.length) {
            if (args[i].charAt(0) == '@') {
                try {
                    char[] content = Util.getFileCharContent(new File(args[i].substring(1)), null);
                    int offset = 0;
                    int prefixLength = ADAPTER_PREFIX.length;
                    while ((offset = CharOperation.indexOf(ADAPTER_PREFIX, content, true, offset)) > -1) {
                        int start = offset + prefixLength;
                        int end = CharOperation.indexOf('\n', content, start);
                        if (end == -1) {
                            end = content.length;
                        }
                        while (CharOperation.isWhitespace(content[end])) {
                            --end;
                        }
                        if (CharOperation.equals(ADAPTER_ENCODING, content, start, start + ADAPTER_ENCODING.length)) {
                            CharOperation.replace(content, SEPARATOR_CHARS, File.separatorChar, start, end + 1);
                            int encodeStart = CharOperation.lastIndexOf('[', content, start += ADAPTER_ENCODING.length, end);
                            if (start < encodeStart && encodeStart < end) {
                                boolean isFile = CharOperation.equals(SuffixConstants.SUFFIX_java, content, encodeStart - 5, encodeStart, false);
                                String str = String.valueOf(content, start, encodeStart - start);
                                String enc = String.valueOf(content, encodeStart, end - encodeStart + 1);
                                if (isFile) {
                                    if (this.fileEncodings == null) {
                                        this.fileEncodings = new HashMap();
                                    }
                                    this.fileEncodings.put(str, enc);
                                } else {
                                    if (this.dirEncodings == null) {
                                        this.dirEncodings = new HashMap();
                                    }
                                    this.dirEncodings.put(str, enc);
                                }
                            }
                        } else if (CharOperation.equals(ADAPTER_ACCESS, content, start, start + ADAPTER_ACCESS.length)) {
                            int accessStart = CharOperation.indexOf('[', content, start += ADAPTER_ACCESS.length, end);
                            CharOperation.replace(content, SEPARATOR_CHARS, File.separatorChar, start, accessStart);
                            if (start < accessStart && accessStart < end) {
                                String path = String.valueOf(content, start, accessStart - start);
                                String access = String.valueOf(content, accessStart, end - accessStart + 1);
                                if (this.accessRules == null) {
                                    this.accessRules = new ArrayList();
                                }
                                this.accessRules.add(path);
                                this.accessRules.add(access);
                            }
                        }
                        offset = end;
                    }
                }
                catch (IOException iOException) {}
            }
            ++i;
        }
    }

    private void createClasspathArgument(Commandline cmd, Path classpath) {
        Commandline.Argument arg = cmd.createArgument();
        String[] pathElements = classpath.list();
        if (pathElements.length == 0) {
            arg.setValue(Util.EMPTY_STRING);
            return;
        }
        if (this.accessRules == null) {
            arg.setPath(classpath);
            return;
        }
        int rulesLength = this.accessRules.size();
        String[] rules = this.accessRules.toArray(new String[rulesLength]);
        int nextRule = 0;
        StringBuffer result = new StringBuffer();
        int i = 0;
        int max = pathElements.length;
        while (i < max) {
            if (i > 0) {
                result.append(File.pathSeparatorChar);
            }
            String pathElement = pathElements[i];
            result.append(pathElement);
            int j = nextRule;
            while (j < rulesLength) {
                int ruleLength;
                String rule = rules[j];
                if (pathElement.endsWith(rule)) {
                    result.append(rules[j + 1]);
                    nextRule = j + 2;
                    break;
                }
                if (rule.endsWith(File.separator)) {
                    ruleLength = rule.length();
                    if (pathElement.regionMatches(false, pathElement.length() - ruleLength + 1, rule, 0, ruleLength - 1)) {
                        result.append(rules[j + 1]);
                        nextRule = j + 2;
                        break;
                    }
                } else if (pathElement.endsWith(File.separator)) {
                    ruleLength = rule.length();
                    if (pathElement.regionMatches(false, pathElement.length() - ruleLength - 1, rule, 0, ruleLength)) {
                        result.append(rules[j + 1]);
                        nextRule = j + 2;
                        break;
                    }
                }
                j += 2;
            }
            ++i;
        }
        arg.setValue(result.toString());
    }

    protected void logAndAddFilesToCompile(Commandline cmd) {
        this.attributes.log("Compilation " + cmd.describeArguments(), 3);
        StringBuffer niceSourceList = new StringBuffer("File");
        if (this.compileList.length != 1) {
            niceSourceList.append("s");
        }
        niceSourceList.append(" to be compiled:");
        niceSourceList.append(System.lineSeparator());
        String[] encodedFiles = null;
        String[] encodedDirs = null;
        int encodedFilesLength = 0;
        int encodedDirsLength = 0;
        if (this.fileEncodings != null) {
            encodedFilesLength = this.fileEncodings.size();
            encodedFiles = new String[encodedFilesLength];
            this.fileEncodings.keySet().toArray(encodedFiles);
        }
        if (this.dirEncodings != null) {
            encodedDirsLength = this.dirEncodings.size();
            encodedDirs = new String[encodedDirsLength];
            this.dirEncodings.keySet().toArray(encodedDirs);
            Comparator comparator = new Comparator(){

                public int compare(Object o1, Object o2) {
                    return ((String)o2).length() - ((String)o1).length();
                }
            };
            Arrays.sort(encodedDirs, comparator);
        }
        int i = 0;
        while (i < this.compileList.length) {
            int j;
            String arg = this.compileList[i].getAbsolutePath();
            boolean encoded = false;
            if (encodedFiles != null) {
                j = 0;
                while (j < encodedFilesLength) {
                    if (arg.endsWith(encodedFiles[j])) {
                        arg = String.valueOf(arg) + (String)this.fileEncodings.get(encodedFiles[j]);
                        if (j < encodedFilesLength - 1) {
                            System.arraycopy(encodedFiles, j + 1, encodedFiles, j, encodedFilesLength - j - 1);
                        }
                        encodedFiles[--encodedFilesLength] = null;
                        encoded = true;
                        break;
                    }
                    ++j;
                }
            }
            if (!encoded && encodedDirs != null) {
                j = 0;
                while (j < encodedDirsLength) {
                    if (arg.lastIndexOf(encodedDirs[j]) != -1) {
                        arg = String.valueOf(arg) + (String)this.dirEncodings.get(encodedDirs[j]);
                        break;
                    }
                    ++j;
                }
            }
            cmd.createArgument().setValue(arg);
            niceSourceList.append("    " + arg + System.lineSeparator());
            ++i;
        }
        this.attributes.log(niceSourceList.toString(), 3);
    }
}

