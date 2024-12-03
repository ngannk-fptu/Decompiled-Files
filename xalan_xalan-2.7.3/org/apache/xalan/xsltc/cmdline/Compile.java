/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.cmdline;

import java.io.File;
import java.net.URL;
import java.util.Vector;
import org.apache.xalan.xsltc.cmdline.getopt.GetOpt;
import org.apache.xalan.xsltc.cmdline.getopt.GetOptsException;
import org.apache.xalan.xsltc.compiler.XSLTC;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;

public final class Compile {
    private static int VERSION_MAJOR = 1;
    private static int VERSION_MINOR = 4;
    private static int VERSION_DELTA = 0;

    public static void printUsage() {
        StringBuffer vers = new StringBuffer("XSLTC version " + VERSION_MAJOR + "." + VERSION_MINOR + (VERSION_DELTA > 0 ? "." + VERSION_DELTA : ""));
        System.err.println(vers + "\n" + new ErrorMsg("COMPILE_USAGE_STR"));
    }

    public static void main(String[] args) {
        try {
            boolean compileOK;
            int c;
            boolean inputIsURL = false;
            boolean useStdIn = false;
            boolean classNameSet = false;
            GetOpt getopt = new GetOpt(args, "o:d:j:p:uxhsinv");
            if (args.length < 1) {
                Compile.printUsage();
            }
            XSLTC xsltc = new XSLTC();
            xsltc.init();
            block13: while ((c = getopt.getNextOption()) != -1) {
                switch (c) {
                    case 105: {
                        useStdIn = true;
                        continue block13;
                    }
                    case 111: {
                        xsltc.setClassName(getopt.getOptionArg());
                        classNameSet = true;
                        continue block13;
                    }
                    case 100: {
                        xsltc.setDestDirectory(getopt.getOptionArg());
                        continue block13;
                    }
                    case 112: {
                        xsltc.setPackageName(getopt.getOptionArg());
                        continue block13;
                    }
                    case 106: {
                        xsltc.setJarFileName(getopt.getOptionArg());
                        continue block13;
                    }
                    case 120: {
                        xsltc.setDebug(true);
                        continue block13;
                    }
                    case 117: {
                        inputIsURL = true;
                        continue block13;
                    }
                    case 110: {
                        xsltc.setTemplateInlining(true);
                        continue block13;
                    }
                }
                Compile.printUsage();
            }
            if (useStdIn) {
                if (!classNameSet) {
                    System.err.println(new ErrorMsg("COMPILE_STDIN_ERR"));
                }
                compileOK = xsltc.compile(System.in, xsltc.getClassName());
            } else {
                String[] stylesheetNames = getopt.getCmdArgs();
                Vector<URL> stylesheetVector = new Vector<URL>();
                for (int i = 0; i < stylesheetNames.length; ++i) {
                    String name = stylesheetNames[i];
                    URL url = inputIsURL ? new URL(name) : new File(name).toURL();
                    stylesheetVector.addElement(url);
                }
                compileOK = xsltc.compile(stylesheetVector);
            }
            if (compileOK) {
                xsltc.printWarnings();
                if (xsltc.getJarFileName() != null) {
                    xsltc.outputToJar();
                }
            } else {
                xsltc.printWarnings();
                xsltc.printErrors();
            }
        }
        catch (GetOptsException ex) {
            System.err.println(ex);
            Compile.printUsage();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

