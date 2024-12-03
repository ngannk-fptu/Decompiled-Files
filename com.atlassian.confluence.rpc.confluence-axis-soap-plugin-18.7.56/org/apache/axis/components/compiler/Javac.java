/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.components.compiler;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import org.apache.axis.components.compiler.AbstractCompiler;
import org.apache.axis.components.compiler.CompilerError;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public class Javac
extends AbstractCompiler {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$components$compiler$Javac == null ? (class$org$apache$axis$components$compiler$Javac = Javac.class$("org.apache.axis.components.compiler.Javac")) : class$org$apache$axis$components$compiler$Javac).getName());
    public static final String CLASSIC_CLASS = "sun.tools.javac.Main";
    public static final String MODERN_CLASS = "com.sun.tools.javac.main.Main";
    private boolean modern = false;
    static /* synthetic */ Class class$org$apache$axis$components$compiler$Javac;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$java$io$PrintWriter;
    static /* synthetic */ Class class$java$io$OutputStream;
    static /* synthetic */ Class array$Ljava$lang$String;

    public Javac() {
        ClassLoader cl = this.getClassLoader();
        try {
            ClassUtils.forName(MODERN_CLASS, true, cl);
            this.modern = true;
        }
        catch (ClassNotFoundException e) {
            log.debug((Object)Messages.getMessage("noModernCompiler"));
            try {
                ClassUtils.forName(CLASSIC_CLASS, true, cl);
                this.modern = false;
            }
            catch (Exception ex) {
                log.error((Object)Messages.getMessage("noCompiler00"), (Throwable)ex);
                throw new RuntimeException(Messages.getMessage("noCompiler00"));
            }
        }
        log.debug((Object)Messages.getMessage("compilerClass", this.modern ? MODERN_CLASS : CLASSIC_CLASS));
    }

    private ClassLoader getClassLoader() {
        File f;
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL toolsURL = null;
        String tools = System.getProperty("java.home");
        if (tools != null && (f = new File(tools + "/../lib/tools.jar")).exists()) {
            try {
                toolsURL = f.toURL();
                cl = new URLClassLoader(new URL[]{toolsURL}, cl);
            }
            catch (MalformedURLException e) {
                // empty catch block
            }
        }
        return cl;
    }

    public boolean compile() throws IOException {
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        boolean result = false;
        try {
            Object compiler;
            Class c = ClassUtils.forName(this.modern ? MODERN_CLASS : CLASSIC_CLASS, true, this.getClassLoader());
            if (this.modern) {
                PrintWriter pw = new PrintWriter(new OutputStreamWriter(err));
                Constructor cons = c.getConstructor(class$java$lang$String == null ? (class$java$lang$String = Javac.class$("java.lang.String")) : class$java$lang$String, class$java$io$PrintWriter == null ? (class$java$io$PrintWriter = Javac.class$("java.io.PrintWriter")) : class$java$io$PrintWriter);
                compiler = cons.newInstance("javac", pw);
            } else {
                Constructor cons = c.getConstructor(class$java$io$OutputStream == null ? (class$java$io$OutputStream = Javac.class$("java.io.OutputStream")) : class$java$io$OutputStream, class$java$lang$String == null ? (class$java$lang$String = Javac.class$("java.lang.String")) : class$java$lang$String);
                compiler = cons.newInstance(err, "javac");
            }
            Method compile = c.getMethod("compile", array$Ljava$lang$String == null ? (array$Ljava$lang$String = Javac.class$("[Ljava.lang.String;")) : array$Ljava$lang$String);
            if (this.modern) {
                int compilationResult = (Integer)compile.invoke(compiler, new Object[]{this.toStringArray(this.fillArguments(new ArrayList()))});
                result = compilationResult == 0;
                log.debug((Object)("Compilation Returned: " + Integer.toString(compilationResult)));
            } else {
                Boolean ok = (Boolean)compile.invoke(compiler, new Object[]{this.toStringArray(this.fillArguments(new ArrayList()))});
                result = ok;
            }
        }
        catch (Exception cnfe) {
            log.error((Object)Messages.getMessage("noCompiler00"), (Throwable)cnfe);
            throw new RuntimeException(Messages.getMessage("noCompiler00"));
        }
        this.errors = new ByteArrayInputStream(err.toByteArray());
        return result;
    }

    protected List parseStream(BufferedReader input) throws IOException {
        if (this.modern) {
            return this.parseModernStream(input);
        }
        return this.parseClassicStream(input);
    }

    protected List parseModernStream(BufferedReader input) throws IOException {
        ArrayList<CompilerError> errors = new ArrayList<CompilerError>();
        String line = null;
        StringBuffer buffer = null;
        while (true) {
            buffer = new StringBuffer();
            do {
                if ((line = input.readLine()) == null) {
                    if (buffer.length() > 0) {
                        errors.add(new CompilerError("\n" + buffer.toString()));
                    }
                    return errors;
                }
                log.debug((Object)line);
                buffer.append(line);
                buffer.append('\n');
            } while (!line.endsWith("^"));
            errors.add(this.parseModernError(buffer.toString()));
        }
    }

    private CompilerError parseModernError(String error) {
        StringTokenizer tokens = new StringTokenizer(error, ":");
        try {
            String pointer;
            int startcolumn;
            String file = tokens.nextToken();
            if (file.length() == 1) {
                file = file + ":" + tokens.nextToken();
            }
            int line = Integer.parseInt(tokens.nextToken());
            String message = tokens.nextToken("\n").substring(1);
            String context = tokens.nextToken("\n");
            int endcolumn = context.indexOf(" ", startcolumn = (pointer = tokens.nextToken("\n")).indexOf("^"));
            if (endcolumn == -1) {
                endcolumn = context.length();
            }
            return new CompilerError(file, false, line, startcolumn, line, endcolumn, message);
        }
        catch (NoSuchElementException nse) {
            return new CompilerError(Messages.getMessage("noMoreTokens", error));
        }
        catch (Exception nse) {
            return new CompilerError(Messages.getMessage("cantParse", error));
        }
    }

    protected List parseClassicStream(BufferedReader input) throws IOException {
        ArrayList<CompilerError> errors = null;
        String line = null;
        StringBuffer buffer = null;
        while (true) {
            buffer = new StringBuffer();
            for (int i = 0; i < 3; ++i) {
                line = input.readLine();
                if (line == null) {
                    return errors;
                }
                log.debug((Object)line);
                buffer.append(line);
                buffer.append('\n');
            }
            if (errors == null) {
                errors = new ArrayList<CompilerError>();
            }
            errors.add(this.parseClassicError(buffer.toString()));
        }
    }

    private CompilerError parseClassicError(String error) {
        StringTokenizer tokens = new StringTokenizer(error, ":");
        try {
            String pointer;
            int startcolumn;
            String file = tokens.nextToken();
            if (file.length() == 1) {
                file = file + ":" + tokens.nextToken();
            }
            int line = Integer.parseInt(tokens.nextToken());
            String last = tokens.nextToken();
            while (tokens.hasMoreElements()) {
                last = last + tokens.nextToken();
            }
            tokens = new StringTokenizer(last.trim(), "\n");
            String message = tokens.nextToken();
            String context = tokens.nextToken();
            int endcolumn = context.indexOf(" ", startcolumn = (pointer = tokens.nextToken()).indexOf("^"));
            if (endcolumn == -1) {
                endcolumn = context.length();
            }
            return new CompilerError(this.srcDir + File.separator + file, true, line, startcolumn, line, endcolumn, message);
        }
        catch (NoSuchElementException nse) {
            return new CompilerError(Messages.getMessage("noMoreTokens", error));
        }
        catch (Exception nse) {
            return new CompilerError(Messages.getMessage("cantParse", error));
        }
    }

    public String toString() {
        return Messages.getMessage("sunJavac");
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

