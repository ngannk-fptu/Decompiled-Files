/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.tool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.attribute.FileAttribute;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.xmlbeans.SystemProperties;
import org.apache.xmlbeans.impl.common.IOUtil;

public class CodeGenUtil {
    public static final String DEFAULT_MEM_START = "8m";
    public static final String DEFAULT_MEM_MAX = "256m";
    public static final String DEFAULT_COMPILER = "javac";

    public static URI resolve(URI base, URI child) {
        URI ruri = base.resolve(child);
        if ("file".equals(ruri.getScheme()) && !child.equals(ruri) && base.getPath().startsWith("//") && !ruri.getPath().startsWith("//")) {
            String path = "///".concat(ruri.getPath());
            try {
                ruri = new URI("file", null, path, ruri.getQuery(), ruri.getFragment());
            }
            catch (URISyntaxException uRISyntaxException) {
                // empty catch block
            }
        }
        return ruri;
    }

    static void addAllJavaFiles(List<File> srcFiles, List<String> args) {
        for (File f : srcFiles) {
            if (f.isDirectory()) {
                File[] files = f.listFiles(file -> file.isFile() && file.getName().endsWith(".java") || file.isDirectory());
                if (files == null) continue;
                CodeGenUtil.addAllJavaFiles(Arrays.asList(files), args);
                continue;
            }
            args.add(CodeGenUtil.quoteAndEscapeFilename(f.getAbsolutePath()));
        }
    }

    private static String quoteAndEscapeFilename(String filename) {
        if (!filename.contains(" ")) {
            return filename;
        }
        return "\"" + filename.replaceAll("\\\\", "\\\\\\\\") + "\"";
    }

    public static boolean externalCompile(List<File> srcFiles, File outdir, File[] cp, boolean debug) {
        return CodeGenUtil.externalCompile(srcFiles, outdir, cp, debug, DEFAULT_COMPILER, null, DEFAULT_MEM_START, DEFAULT_MEM_MAX, false, false);
    }

    public static boolean externalCompile(List<File> srcFiles, File outdir, File[] cp, boolean debug, String javacPath, String memStart, String memMax, boolean quiet, boolean verbose) {
        return CodeGenUtil.externalCompile(srcFiles, outdir, cp, debug, javacPath, null, memStart, memMax, quiet, verbose);
    }

    public static boolean externalCompile(List<File> srcFiles, File outdir, File[] cp, boolean debug, String javacPath, String genver, String memStart, String memMax, boolean quiet, boolean verbose) {
        ArrayList<String> args = new ArrayList<String>();
        File javac = CodeGenUtil.findJavaTool(javacPath == null ? DEFAULT_COMPILER : javacPath);
        assert (javac.exists()) : "compiler not found " + javac;
        args.add(javac.getAbsolutePath());
        if (outdir == null) {
            outdir = new File(".");
        } else {
            args.add("-d");
            args.add(CodeGenUtil.quoteAndEscapeFilename(outdir.getAbsolutePath()));
        }
        if (cp == null) {
            cp = CodeGenUtil.systemClasspath();
        }
        if (cp.length > 0) {
            StringBuilder classPath = new StringBuilder();
            classPath.append(outdir.getAbsolutePath());
            for (File file : cp) {
                classPath.append(File.pathSeparator);
                classPath.append(file.getAbsolutePath());
            }
            args.add("-classpath");
            args.add(CodeGenUtil.quoteAndEscapeFilename(classPath.toString()));
        }
        if (genver == null) {
            genver = "1.8";
        }
        args.add("-source");
        args.add(genver);
        args.add("-target");
        args.add(genver);
        args.add(debug ? "-g" : "-g:none");
        if (verbose) {
            args.add("-verbose");
        }
        CodeGenUtil.addAllJavaFiles(srcFiles, args);
        File clFile = null;
        try {
            clFile = Files.createTempFile(IOUtil.getTempDir(), DEFAULT_COMPILER, ".tmp", new FileAttribute[0]).toFile();
            try (BufferedWriter fw = Files.newBufferedWriter(clFile.toPath(), StandardCharsets.ISO_8859_1, new OpenOption[0]);){
                Iterator i = args.iterator();
                i.next();
                while (i.hasNext()) {
                    String arg = (String)i.next();
                    fw.write(arg);
                    ((Writer)fw).write(10);
                }
            }
            ArrayList newargs = new ArrayList();
            newargs.add(args.get(0));
            if (memStart != null && memStart.length() != 0) {
                newargs.add("-J-Xms" + memStart);
            }
            if (memMax != null && memMax.length() != 0) {
                newargs.add("-J-Xmx" + memMax);
            }
            newargs.add("@" + clFile.getAbsolutePath());
            args = newargs;
        }
        catch (Exception e) {
            System.err.println("Could not create command-line file for javac");
        }
        try {
            String[] strArgs = args.toArray(new String[0]);
            if (verbose) {
                System.out.print("compile command:");
                for (String strArg : strArgs) {
                    System.out.print(" " + strArg);
                }
                System.out.println();
            }
            Process proc = Runtime.getRuntime().exec(strArgs);
            StringBuilder errorBuffer = new StringBuilder();
            StringBuilder outputBuffer = new StringBuilder();
            Thread out = CodeGenUtil.copy(proc.getInputStream(), outputBuffer);
            Thread err = CodeGenUtil.copy(proc.getErrorStream(), errorBuffer);
            proc.waitFor();
            if (verbose || proc.exitValue() != 0) {
                if (outputBuffer.length() > 0) {
                    System.out.println(outputBuffer.toString());
                    System.out.flush();
                }
                if (errorBuffer.length() > 0) {
                    System.err.println(errorBuffer.toString());
                    System.err.flush();
                }
                if (proc.exitValue() != 0) {
                    return false;
                }
            }
        }
        catch (Throwable e) {
            System.err.println(e.toString());
            System.err.println(e.getCause());
            e.printStackTrace(System.err);
            return false;
        }
        if (clFile != null) {
            clFile.delete();
        }
        return true;
    }

    public static File[] systemClasspath() {
        ArrayList<File> cp = new ArrayList<File>();
        CodeSource cs = CodeGenUtil.class.getProtectionDomain().getCodeSource();
        if (cs != null) {
            cp.add(new File(cs.getLocation().getPath()));
        } else {
            System.err.println("Can't determine path of xmlbeans-*.jar - specify classpath explicitly!");
        }
        String jcp = SystemProperties.getProperty("java.class.path");
        if (jcp != null) {
            String[] systemcp;
            for (String s : systemcp = jcp.split(File.pathSeparator)) {
                cp.add(new File(s));
            }
        }
        return cp.toArray(new File[0]);
    }

    private static File findJavaTool(String tool) {
        File toolFile = new File(tool);
        if (toolFile.isFile()) {
            return toolFile;
        }
        File result = new File(tool + ".exe");
        if (result.isFile()) {
            return result;
        }
        String home = SystemProperties.getProperty("java.home");
        String sep = File.separator;
        result = new File(home + sep + ".." + sep + "bin", tool);
        if (result.isFile()) {
            return result;
        }
        if ((result = new File(result.getPath() + ".exe")).isFile()) {
            return result;
        }
        result = new File(home + sep + "bin", tool);
        if (result.isFile()) {
            return result;
        }
        if ((result = new File(result.getPath() + ".exe")).isFile()) {
            return result;
        }
        return toolFile;
    }

    private static Thread copy(InputStream stream, StringBuilder output) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.ISO_8859_1));
        Thread readerThread = new Thread(() -> reader.lines().forEach(s -> output.append((String)s).append("\n")));
        readerThread.start();
        return readerThread;
    }
}

