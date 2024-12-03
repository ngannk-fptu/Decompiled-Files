/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.control;

import groovy.lang.GroovyClassLoader;
import groovyjarjarantlr.MismatchedCharException;
import groovyjarjarantlr.MismatchedTokenException;
import groovyjarjarantlr.NoViableAltException;
import groovyjarjarantlr.NoViableAltForCharException;
import groovyjarjarantlr.Token;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.ErrorCollector;
import org.codehaus.groovy.control.Janitor;
import org.codehaus.groovy.control.ParserPlugin;
import org.codehaus.groovy.control.ProcessingUnit;
import org.codehaus.groovy.control.XStreamUtils;
import org.codehaus.groovy.control.io.FileReaderSource;
import org.codehaus.groovy.control.io.ReaderSource;
import org.codehaus.groovy.control.io.StringReaderSource;
import org.codehaus.groovy.control.io.URLReaderSource;
import org.codehaus.groovy.control.messages.Message;
import org.codehaus.groovy.control.messages.SimpleMessage;
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;
import org.codehaus.groovy.syntax.Reduction;
import org.codehaus.groovy.syntax.SyntaxException;
import org.codehaus.groovy.tools.Utilities;

public class SourceUnit
extends ProcessingUnit {
    private ParserPlugin parserPlugin;
    protected ReaderSource source;
    protected String name;
    protected Reduction cst;
    protected ModuleNode ast;

    public SourceUnit(String name, ReaderSource source, CompilerConfiguration flags, GroovyClassLoader loader, ErrorCollector er) {
        super(flags, loader, er);
        this.name = name;
        this.source = source;
    }

    public SourceUnit(File source, CompilerConfiguration configuration, GroovyClassLoader loader, ErrorCollector er) {
        this(source.getPath(), new FileReaderSource(source, configuration), configuration, loader, er);
    }

    public SourceUnit(URL source, CompilerConfiguration configuration, GroovyClassLoader loader, ErrorCollector er) {
        this(source.toExternalForm(), new URLReaderSource(source, configuration), configuration, loader, er);
    }

    public SourceUnit(String name, String source, CompilerConfiguration configuration, GroovyClassLoader loader, ErrorCollector er) {
        this(name, new StringReaderSource(source, configuration), configuration, loader, er);
    }

    public String getName() {
        return this.name;
    }

    public Reduction getCST() {
        return this.cst;
    }

    public ModuleNode getAST() {
        return this.ast;
    }

    public boolean failedWithUnexpectedEOF() {
        if (this.getErrorCollector().hasErrors()) {
            Message last = this.getErrorCollector().getLastError();
            Throwable cause = null;
            if (last instanceof SyntaxErrorMessage) {
                cause = ((SyntaxErrorMessage)last).getCause().getCause();
            }
            if (cause != null) {
                if (cause instanceof NoViableAltException) {
                    return this.isEofToken(((NoViableAltException)cause).token);
                }
                if (cause instanceof NoViableAltForCharException) {
                    char badChar = ((NoViableAltForCharException)cause).foundChar;
                    return badChar == '\uffff';
                }
                if (cause instanceof MismatchedCharException) {
                    char badChar = (char)((MismatchedCharException)cause).foundChar;
                    return badChar == '\uffff';
                }
                if (cause instanceof MismatchedTokenException) {
                    return this.isEofToken(((MismatchedTokenException)cause).token);
                }
            }
        }
        return false;
    }

    protected boolean isEofToken(Token token) {
        return token.getType() == 1;
    }

    public static SourceUnit create(String name, String source) {
        CompilerConfiguration configuration = new CompilerConfiguration();
        configuration.setTolerance(1);
        return new SourceUnit(name, source, configuration, null, new ErrorCollector(configuration));
    }

    public static SourceUnit create(String name, String source, int tolerance) {
        CompilerConfiguration configuration = new CompilerConfiguration();
        configuration.setTolerance(tolerance);
        return new SourceUnit(name, source, configuration, null, new ErrorCollector(configuration));
    }

    public void parse() throws CompilationFailedException {
        if (this.phase > 2) {
            throw new GroovyBugError("parsing is already complete");
        }
        if (this.phase == 1) {
            this.nextPhase();
        }
        Reader reader = null;
        try {
            reader = this.source.getReader();
            this.parserPlugin = this.getConfiguration().getPluginFactory().createParserPlugin();
            this.cst = this.parserPlugin.parseCST(this, reader);
            reader.close();
        }
        catch (IOException e) {
            this.getErrorCollector().addFatalError(new SimpleMessage(e.getMessage(), this));
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException iOException) {}
            }
        }
    }

    public void convert() throws CompilationFailedException {
        if (this.phase == 2 && this.phaseComplete) {
            this.gotoPhase(3);
        }
        if (this.phase != 3) {
            throw new GroovyBugError("SourceUnit not ready for convert()");
        }
        try {
            this.ast = this.parserPlugin.buildAST(this, this.classLoader, this.cst);
            this.ast.setDescription(this.name);
        }
        catch (SyntaxException e) {
            if (this.ast == null) {
                this.ast = new ModuleNode(this);
            }
            this.getErrorCollector().addError(new SyntaxErrorMessage(e, this));
        }
        String property = (String)AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                return System.getProperty("groovy.ast");
            }
        });
        if ("xml".equals(property)) {
            SourceUnit.saveAsXML(this.name, this.ast);
        }
    }

    private static void saveAsXML(String name, ModuleNode ast) {
        XStreamUtils.serialize(name, ast);
    }

    public String getSample(int line, int column, Janitor janitor) {
        String sample = null;
        String text = this.source.getLine(line, janitor);
        if (text != null) {
            if (column > 0) {
                String marker = Utilities.repeatString(" ", column - 1) + "^";
                if (column > 40) {
                    int start = column - 30 - 1;
                    int end = column + 10 > text.length() ? text.length() : column + 10 - 1;
                    sample = "   " + text.substring(start, end) + Utilities.eol() + "   " + marker.substring(start, marker.length());
                } else {
                    sample = "   " + text + Utilities.eol() + "   " + marker;
                }
            } else {
                sample = text;
            }
        }
        return sample;
    }

    public void addException(Exception e) throws CompilationFailedException {
        this.getErrorCollector().addException(e, this);
    }

    public void addError(SyntaxException se) throws CompilationFailedException {
        this.getErrorCollector().addError(se, this);
    }

    public ReaderSource getSource() {
        return this.source;
    }
}

