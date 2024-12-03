/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.debug;

import com.mchange.v1.io.WriterUtils;
import com.mchange.v1.lang.BooleanUtils;
import com.mchange.v1.util.SetUtils;
import com.mchange.v1.util.StringTokenizerUtils;
import com.mchange.v2.cmdline.CommandLineUtils;
import com.mchange.v2.cmdline.ParsedCommandLine;
import com.mchange.v2.debug.DebugConstants;
import com.mchange.v2.io.DirectoryDescentUtils;
import com.mchange.v2.io.FileIterator;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

public final class DebugGen
implements DebugConstants {
    static final String[] VALID = new String[]{"codebase", "packages", "trace", "debug", "recursive", "javac", "noclobber", "classname", "skipdirs", "outputbase"};
    static final String[] REQUIRED = new String[]{"codebase", "packages", "trace", "debug"};
    static final String[] ARGS = new String[]{"codebase", "packages", "trace", "debug", "classname", "outputbase"};
    static final String EOL = System.getProperty("line.separator");
    static int trace_level;
    static boolean debug;
    static boolean recursive;
    static String classname;
    static boolean clobber;
    static Set skipDirs;

    public static final synchronized void main(String[] stringArray) {
        try {
            int n;
            File file;
            String string;
            ParsedCommandLine parsedCommandLine = CommandLineUtils.parse(stringArray, "--", VALID, REQUIRED, ARGS);
            String string2 = parsedCommandLine.getSwitchArg("codebase");
            string2 = DebugGen.platify(string2);
            if (!string2.endsWith(File.separator)) {
                string2 = string2 + File.separator;
            }
            if ((string = parsedCommandLine.getSwitchArg("outputbase")) != null) {
                if (!(string = DebugGen.platify(string)).endsWith(File.separator)) {
                    string = string + File.separator;
                }
            } else {
                string = string2;
            }
            if ((file = new File(string)).exists()) {
                if (!file.isDirectory()) {
                    System.exit(-1);
                } else if (!file.canWrite()) {
                    System.err.println("Output Base '" + file.getPath() + "' is not writable!");
                    System.exit(-1);
                }
            } else if (!file.mkdirs()) {
                System.err.println("Output Base directory '" + file.getPath() + "' does not exist, and could not be created!");
                System.exit(-1);
            }
            String[] stringArray2 = StringTokenizerUtils.tokenizeToArray(parsedCommandLine.getSwitchArg("packages"), ", \t");
            File[] fileArray = new File[stringArray2.length];
            int n2 = stringArray2.length;
            for (int i = 0; i < n2; ++i) {
                fileArray[i] = new File(string2 + DebugGen.sepify(stringArray2[i]));
            }
            trace_level = Integer.parseInt(parsedCommandLine.getSwitchArg("trace"));
            debug = BooleanUtils.parseBoolean(parsedCommandLine.getSwitchArg("debug"));
            classname = parsedCommandLine.getSwitchArg("classname");
            if (classname == null) {
                classname = "Debug";
            }
            recursive = parsedCommandLine.includesSwitch("recursive");
            clobber = !parsedCommandLine.includesSwitch("noclobber");
            String string3 = parsedCommandLine.getSwitchArg("skipdirs");
            if (string3 != null) {
                Object[] objectArray = StringTokenizerUtils.tokenizeToArray(string3, ", \t");
                skipDirs = SetUtils.setFromArray(objectArray);
            } else {
                skipDirs = new HashSet();
                skipDirs.add("CVS");
            }
            if (parsedCommandLine.includesSwitch("javac")) {
                System.err.println("autorecompilation of packages not yet implemented.");
            }
            int n3 = fileArray.length;
            for (n = 0; n < n3; ++n) {
                if (recursive) {
                    if (DebugGen.recursivePrecheckPackages(string2, fileArray[n], string, file)) continue;
                    System.err.println("One or more of the specifies packages could not be processed. Aborting. No files have been modified.");
                    System.exit(-1);
                    continue;
                }
                if (DebugGen.precheckPackage(string2, fileArray[n], string, file)) continue;
                System.err.println("One or more of the specifies packages could not be processed. Aborting. No files have been modified.");
                System.exit(-1);
            }
            n3 = fileArray.length;
            for (n = 0; n < n3; ++n) {
                if (recursive) {
                    DebugGen.recursiveWriteDebugFiles(string2, fileArray[n], string, file);
                    continue;
                }
                DebugGen.writeDebugFile(string, fileArray[n]);
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            System.err.println();
            DebugGen.usage();
        }
    }

    private static void usage() {
        System.err.println("java " + DebugGen.class.getName() + " \\");
        System.err.println("\t--codebase=<directory under which packages live> \\  (no default)");
        System.err.println("\t--packages=<comma separated list of packages>    \\  (no default)");
        System.err.println("\t--debug=<true|false>                             \\  (no default)");
        System.err.println("\t--trace=<an int between 0 and 10>                \\  (no default)");
        System.err.println("\t--outputdir=<directory under which to generate>  \\  (defaults to same dir as codebase)");
        System.err.println("\t--recursive                                      \\  (no args)");
        System.err.println("\t--noclobber                                      \\  (no args)");
        System.err.println("\t--classname=<class to generate>                  \\  (defaults to Debug)");
        System.err.println("\t--skipdirs=<directories that should be skipped>  \\  (defaults to CVS)");
    }

    private static String ify(String string, char c, char c2) {
        if (c == c2) {
            return string;
        }
        StringBuffer stringBuffer = new StringBuffer(string);
        int n = stringBuffer.length();
        for (int i = 0; i < n; ++i) {
            if (stringBuffer.charAt(i) != c) continue;
            stringBuffer.setCharAt(i, c2);
        }
        return stringBuffer.toString();
    }

    private static String platify(String string) {
        String string2 = DebugGen.ify(string, '/', File.separatorChar);
        string2 = DebugGen.ify(string2, '\\', File.separatorChar);
        string2 = DebugGen.ify(string2, ':', File.separatorChar);
        return string2;
    }

    private static String dottify(String string) {
        return DebugGen.ify(string, File.separatorChar, '.');
    }

    private static String sepify(String string) {
        return DebugGen.ify(string, '.', File.separatorChar);
    }

    private static boolean recursivePrecheckPackages(String string, File file, String string2, File file2) throws IOException {
        FileIterator fileIterator = DirectoryDescentUtils.depthFirstEagerDescent(file);
        while (fileIterator.hasNext()) {
            File file3 = fileIterator.nextFile();
            if (!file3.isDirectory() || skipDirs.contains(file3.getName())) continue;
            File file4 = DebugGen.outputDir(string, file3, string2, file2);
            if (!file4.exists() && !file4.mkdirs()) {
                System.err.println("Required output dir: '" + file4 + "' does not exist, and could not be created.");
                return false;
            }
            if (DebugGen.precheckOutputPackageDir(file4)) continue;
            return false;
        }
        return true;
    }

    private static File outputDir(String string, File file, String string2, File file2) {
        if (string.equals(string2)) {
            return file;
        }
        String string3 = file.getPath();
        if (!string3.startsWith(string)) {
            System.err.println(DebugGen.class.getName() + ": program bug. Source package path '" + string3 + "' does not begin with codebase '" + string + "'.");
            System.exit(-1);
        }
        return new File(file2, string3.substring(string.length()));
    }

    private static boolean precheckPackage(String string, File file, String string2, File file2) throws IOException {
        return DebugGen.precheckOutputPackageDir(DebugGen.outputDir(string, file, string2, file2));
    }

    private static boolean precheckOutputPackageDir(File file) throws IOException {
        File file2 = new File(file, classname + ".java");
        if (!file.canWrite()) {
            System.err.println("File '" + file2.getPath() + "' is not writable.");
            return false;
        }
        if (!clobber && file2.exists()) {
            System.err.println("File '" + file2.getPath() + "' exists, and we are in noclobber mode.");
            return false;
        }
        return true;
    }

    private static void recursiveWriteDebugFiles(String string, File file, String string2, File file2) throws IOException {
        FileIterator fileIterator = DirectoryDescentUtils.depthFirstEagerDescent(DebugGen.outputDir(string, file, string2, file2));
        while (fileIterator.hasNext()) {
            File file3 = fileIterator.nextFile();
            if (!file3.isDirectory() || skipDirs.contains(file3.getName())) continue;
            DebugGen.writeDebugFile(string2, file3);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void writeDebugFile(String string, File file) throws IOException {
        File file2 = new File(file, classname + ".java");
        String string2 = DebugGen.dottify(file.getPath().substring(string.length()));
        System.err.println("Writing file: " + file2.getPath());
        OutputStreamWriter outputStreamWriter = null;
        try {
            outputStreamWriter = new OutputStreamWriter((OutputStream)new BufferedOutputStream(new FileOutputStream(file2)), "UTF8");
            outputStreamWriter.write("/********************************************************************" + EOL);
            outputStreamWriter.write(" * This class generated by " + DebugGen.class.getName() + EOL);
            outputStreamWriter.write(" * and will probably be overwritten by the same! Edit at" + EOL);
            outputStreamWriter.write(" * YOUR PERIL!!! Hahahahaha." + EOL);
            outputStreamWriter.write(" ********************************************************************/" + EOL);
            outputStreamWriter.write(EOL);
            outputStreamWriter.write("package " + string2 + ';' + EOL);
            outputStreamWriter.write(EOL);
            outputStreamWriter.write("import com.mchange.v2.debug.DebugConstants;" + EOL);
            outputStreamWriter.write(EOL);
            outputStreamWriter.write("final class " + classname + " implements DebugConstants" + EOL);
            outputStreamWriter.write("{" + EOL);
            outputStreamWriter.write("\tfinal static boolean DEBUG = " + debug + ';' + EOL);
            outputStreamWriter.write("\tfinal static int     TRACE = " + DebugGen.traceStr(trace_level) + ';' + EOL);
            outputStreamWriter.write(EOL);
            outputStreamWriter.write("\tprivate " + classname + "()" + EOL);
            outputStreamWriter.write("\t{}" + EOL);
            outputStreamWriter.write("}" + EOL);
            outputStreamWriter.write(EOL);
            ((Writer)outputStreamWriter).flush();
        }
        catch (Throwable throwable) {
            WriterUtils.attemptClose(outputStreamWriter);
            throw throwable;
        }
        WriterUtils.attemptClose(outputStreamWriter);
    }

    private static String traceStr(int n) {
        if (n == 0) {
            return "TRACE_NONE";
        }
        if (n == 5) {
            return "TRACE_MED";
        }
        if (n == 10) {
            return "TRACE_MAX";
        }
        return String.valueOf(n);
    }
}

