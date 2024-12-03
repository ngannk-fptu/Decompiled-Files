/*
 * Decompiled with CFR 0.152.
 */
package groovy.text;

import groovy.lang.Closure;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.Writable;
import groovy.text.Template;
import groovy.text.TemplateEngine;
import groovy.text.TemplateExecutionException;
import groovy.text.TemplateParseException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.ErrorCollector;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.codehaus.groovy.control.messages.Message;
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;
import org.codehaus.groovy.runtime.StackTraceUtils;
import org.codehaus.groovy.syntax.SyntaxException;

public class StreamingTemplateEngine
extends TemplateEngine {
    private static final String TEMPLATE_SCRIPT_PREFIX = "StreamingTemplateScript";
    private final ClassLoader parentLoader;
    private static int counter = 1;

    public StreamingTemplateEngine() {
        this(StreamingTemplate.class.getClassLoader());
    }

    public StreamingTemplateEngine(ClassLoader parentLoader) {
        this.parentLoader = parentLoader;
    }

    @Override
    public Template createTemplate(Reader reader) throws CompilationFailedException, ClassNotFoundException, IOException {
        return new StreamingTemplate(reader, this.parentLoader);
    }

    private static class StreamingTemplate
    implements Template {
        private static final String SCRIPT_HEAD = "package groovy.tmp.templates;def getTemplate() { return { _p, _s, _b, out -> int _i = 0;try {delegate = new Binding(_b);";
        private static final String SCRIPT_TAIL = "} catch (Throwable e) { _p.error(_i, _s, e);}}.asWritable()}";
        private StringBuilder templateSource;
        private int index = 0;
        final Closure template;
        String scriptSource;
        private static final FinishedReadingException finishedReadingException;
        public static final StackTraceElement[] EMPTY_STACKTRACE;
        boolean useLastRead = false;
        private int lastRead = -1;

        private void finishStringSection(List<StringSection> sections, StringSection currentSection, StringBuilder templateExpressions, Position lastSourcePosition, Position targetPosition) {
            if (currentSection.lastSourcePosition != null) {
                return;
            }
            currentSection.lastSourcePosition = new Position(lastSourcePosition);
            sections.add(currentSection);
            this.append(templateExpressions, targetPosition, "out<<_s[_i=" + this.index++ + "];");
            currentSection.lastTargetPosition = new Position(targetPosition.row, targetPosition.column);
        }

        public void error(int index, List<StringSection> sections, Throwable e) throws Throwable {
            int i = Math.max(0, index);
            StringSection precedingSection = sections.get(i);
            int traceLine = -1;
            for (StackTraceElement element : e.getStackTrace()) {
                if (!element.getClassName().contains(StreamingTemplateEngine.TEMPLATE_SCRIPT_PREFIX)) continue;
                traceLine = element.getLineNumber();
                break;
            }
            if (traceLine != -1) {
                int actualLine = precedingSection.lastSourcePosition.row + traceLine - 1;
                String message = "Template execution error at line " + actualLine + ":\n" + this.getErrorContext(actualLine);
                TemplateExecutionException unsanitized = new TemplateExecutionException(actualLine, message, StackTraceUtils.sanitize(e));
                throw StackTraceUtils.sanitize(unsanitized);
            }
            throw e;
        }

        private int getLinesInSource() throws IOException {
            int result = 0;
            BufferedReader reader = null;
            try {
                reader = new LineNumberReader(new StringReader(this.templateSource.toString()));
                ((LineNumberReader)reader).skip(Long.MAX_VALUE);
                result = ((LineNumberReader)reader).getLineNumber();
            }
            finally {
                if (reader != null) {
                    reader.close();
                }
            }
            return result;
        }

        private String getErrorContext(int actualLine) throws IOException {
            int lineNr;
            int minLine = Math.max(0, actualLine - 1);
            int maxLine = Math.min(this.getLinesInSource(), actualLine + 1);
            LineNumberReader r = new LineNumberReader(new StringReader(this.templateSource.toString()));
            StringBuilder result = new StringBuilder();
            while ((lineNr = r.getLineNumber() + 1) <= maxLine) {
                String line = r.readLine();
                if (lineNr < minLine) continue;
                String nr = Integer.toString(lineNr);
                if (lineNr == actualLine) {
                    nr = " --> " + nr;
                }
                result.append(this.padLeft(nr, 10));
                result.append(": ");
                result.append(line);
                result.append('\n');
            }
            return result.toString();
        }

        private String padLeft(String s, int len) {
            StringBuilder b = new StringBuilder(s);
            while (b.length() < len) {
                b.insert(0, " ");
            }
            return b.toString();
        }

        StreamingTemplate(Reader source, ClassLoader parentLoader) throws CompilationFailedException, ClassNotFoundException, IOException {
            StringBuilder target = new StringBuilder();
            ArrayList<StringSection> sections = new ArrayList<StringSection>();
            Position sourcePosition = new Position(1, 1);
            Position targetPosition = new Position(1, 1);
            Position lastSourcePosition = new Position(1, 1);
            StringSection currentSection = new StringSection(sourcePosition);
            this.templateSource = new StringBuilder();
            StringBuilder lookAhead = new StringBuilder(10);
            this.append(target, targetPosition, SCRIPT_HEAD);
            try {
                int skipRead = -1;
                while (true) {
                    lastSourcePosition.set(sourcePosition);
                    int c = skipRead != -1 ? skipRead : this.read(source, sourcePosition, lookAhead);
                    skipRead = -1;
                    if (c == 92) {
                        this.handleEscaping(source, sourcePosition, currentSection, lookAhead);
                        continue;
                    }
                    if (c == 60) {
                        c = this.read(source, sourcePosition, lookAhead);
                        if (c == 37) {
                            c = this.read(source, sourcePosition);
                            StreamingTemplate.clear(lookAhead);
                            if (c == 61) {
                                this.finishStringSection(sections, currentSection, target, lastSourcePosition, targetPosition);
                                this.parseExpression(source, target, sourcePosition, targetPosition);
                                currentSection = new StringSection(sourcePosition);
                                continue;
                            }
                            this.finishStringSection(sections, currentSection, target, lastSourcePosition, targetPosition);
                            this.parseSection(c, source, target, sourcePosition, targetPosition);
                            currentSection = new StringSection(sourcePosition);
                            continue;
                        }
                        currentSection.data.append('<');
                    } else if (c == 36) {
                        c = this.read(source, sourcePosition);
                        StreamingTemplate.clear(lookAhead);
                        if (c == 123) {
                            this.finishStringSection(sections, currentSection, target, lastSourcePosition, targetPosition);
                            this.parseDollarCurlyIdentifier(source, target, sourcePosition, targetPosition);
                            currentSection = new StringSection(sourcePosition);
                            continue;
                        }
                        if (Character.isJavaIdentifierStart(c)) {
                            this.finishStringSection(sections, currentSection, target, lastSourcePosition, targetPosition);
                            skipRead = this.parseDollarIdentifier(c, source, target, sourcePosition, targetPosition);
                            currentSection = new StringSection(sourcePosition);
                            continue;
                        }
                        currentSection.data.append('$');
                    }
                    currentSection.data.append((char)c);
                    StreamingTemplate.clear(lookAhead);
                }
            }
            catch (FinishedReadingException e) {
                if (lookAhead.length() > 0) {
                    currentSection.data.append((CharSequence)lookAhead);
                }
                this.finishStringSection(sections, currentSection, target, sourcePosition, targetPosition);
                this.append(target, targetPosition, SCRIPT_TAIL);
                this.scriptSource = target.toString();
                this.template = this.createTemplateClosure(sections, parentLoader, target);
                return;
            }
        }

        private static void clear(StringBuilder lookAhead) {
            lookAhead.delete(0, lookAhead.length());
        }

        private void handleEscaping(Reader source, Position sourcePosition, StringSection currentSection, StringBuilder lookAhead) throws IOException, FinishedReadingException {
            int c = this.read(source, sourcePosition, lookAhead);
            if (c == 92) {
                source.mark(3);
                int d = this.read(source, sourcePosition, lookAhead);
                c = this.read(source, sourcePosition, lookAhead);
                StreamingTemplate.clear(lookAhead);
                if (d == 36 && c == 123 || d == 60 && c == 37) {
                    source.reset();
                    currentSection.data.append('\\');
                    return;
                }
                currentSection.data.append('\\');
                currentSection.data.append('\\');
                currentSection.data.append((char)d);
            } else if (c == 36) {
                c = this.read(source, sourcePosition, lookAhead);
                if (c == 123) {
                    currentSection.data.append('$');
                } else {
                    currentSection.data.append('\\');
                    currentSection.data.append('$');
                }
            } else if (c == 60) {
                c = this.read(source, sourcePosition, lookAhead);
                if (c == 37) {
                    currentSection.data.append('<');
                } else {
                    currentSection.data.append('\\');
                    currentSection.data.append('<');
                }
            } else {
                currentSection.data.append('\\');
            }
            currentSection.data.append((char)c);
            StreamingTemplate.clear(lookAhead);
        }

        private Closure createTemplateClosure(List<StringSection> sections, final ClassLoader parentLoader, StringBuilder target) throws ClassNotFoundException {
            Closure result;
            Class groovyClass;
            GroovyClassLoader loader = AccessController.doPrivileged(new PrivilegedAction<GroovyClassLoader>(){

                @Override
                public GroovyClassLoader run() {
                    return new GroovyClassLoader(parentLoader);
                }
            });
            try {
                groovyClass = loader.parseClass(new GroovyCodeSource(target.toString(), StreamingTemplateEngine.TEMPLATE_SCRIPT_PREFIX + counter++ + ".groovy", "x"));
            }
            catch (MultipleCompilationErrorsException e) {
                throw this.mangleMultipleCompilationErrorsException(e, sections);
            }
            catch (Exception e) {
                throw new GroovyRuntimeException("Failed to parse template script (your template may contain an error or be trying to use expressions not currently supported): " + e.getMessage());
            }
            try {
                GroovyObject object = (GroovyObject)groovyClass.newInstance();
                Closure chicken = (Closure)object.invokeMethod("getTemplate", null);
                result = chicken.curry(this, sections);
            }
            catch (InstantiationException e) {
                throw new ClassNotFoundException(e.getMessage());
            }
            catch (IllegalAccessException e) {
                throw new ClassNotFoundException(e.getMessage());
            }
            return result;
        }

        private int parseDollarIdentifier(int c, Reader reader, StringBuilder target, Position sourcePosition, Position targetPosition) throws IOException, FinishedReadingException {
            this.append(target, targetPosition, "out<<");
            this.append(target, targetPosition, (char)c);
            while (Character.isJavaIdentifierPart(c = this.read(reader, sourcePosition)) && c != 36) {
                this.append(target, targetPosition, (char)c);
            }
            this.append(target, targetPosition, ";");
            return c;
        }

        private void parseDollarCurlyIdentifier(Reader reader, StringBuilder target, Position sourcePosition, Position targetPosition) throws IOException, FinishedReadingException {
            int c;
            this.append(target, targetPosition, "out<<\"\"\"${");
            do {
                c = this.read(reader, sourcePosition);
                this.append(target, targetPosition, (char)c);
            } while (c != 125);
            this.append(target, targetPosition, "\"\"\";");
        }

        private void parseSection(int pendingC, Reader reader, StringBuilder target, Position sourcePosition, Position targetPosition) throws IOException, FinishedReadingException {
            this.append(target, targetPosition, "          ");
            this.append(target, targetPosition, (char)pendingC);
            while (true) {
                int c;
                if ((c = this.read(reader, sourcePosition)) == 37) {
                    c = this.read(reader, sourcePosition);
                    if (c == 62) break;
                    this.append(target, targetPosition, '%');
                }
                this.append(target, targetPosition, (char)c);
            }
            this.append(target, targetPosition, ';');
        }

        private void parseExpression(Reader reader, StringBuilder target, Position sourcePosition, Position targetPosition) throws IOException, FinishedReadingException {
            this.append(target, targetPosition, "out<<\"\"\"${");
            while (true) {
                int c;
                if ((c = this.read(reader, sourcePosition)) == 37) {
                    c = this.read(reader, sourcePosition);
                    if (c == 62) break;
                    this.append(target, targetPosition, '%');
                }
                this.append(target, targetPosition, (char)c);
            }
            this.append(target, targetPosition, "}\"\"\";");
        }

        @Override
        public Writable make() {
            return this.make(null);
        }

        @Override
        public Writable make(Map map) {
            Closure template = this.template.curry(new Object[]{map});
            return (Writable)((Object)template);
        }

        private RuntimeException mangleMultipleCompilationErrorsException(MultipleCompilationErrorsException e, List<StringSection> sections) {
            SyntaxException syntaxException;
            Position errorPosition;
            StringSection precedingSection;
            Message firstMessage;
            RuntimeException result = e;
            ErrorCollector collector = e.getErrorCollector();
            List errors = collector.getErrors();
            if (!errors.isEmpty() && (firstMessage = (Message)errors.get(0)) instanceof SyntaxErrorMessage && (precedingSection = this.findPrecedingSection(errorPosition = new Position((syntaxException = ((SyntaxErrorMessage)firstMessage).getCause()).getLine(), syntaxException.getStartColumn()), sections)) != null) {
                this.offsetPositionFromSection(errorPosition, precedingSection);
                if (sections.get(sections.size() - 1) == precedingSection) {
                    errorPosition.column = precedingSection.lastSourcePosition.column;
                }
                String message = this.mangleExceptionMessage(e.getMessage(), errorPosition);
                result = new TemplateParseException(message, (Throwable)e, errorPosition.row, errorPosition.column);
            }
            return result;
        }

        private String mangleExceptionMessage(String original, Position p) {
            String result = original;
            int index = result.indexOf("@ line ");
            if (index != -1) {
                result = result.substring(0, index);
            }
            int count = 0;
            index = 0;
            for (char c : result.toCharArray()) {
                if (c == ':' && ++count == 3) {
                    result = result.substring(index + 2);
                    break;
                }
                ++index;
            }
            String msg = "Template parse error '" + result + "' at line " + p.row + ", column " + p.column;
            try {
                msg = msg + "\n" + this.getErrorContext(p.row);
            }
            catch (IOException iOException) {
                // empty catch block
            }
            return msg;
        }

        private void offsetPositionFromSection(Position p, StringSection s) {
            if (p.row == s.lastTargetPosition.row) {
                p.column -= s.lastTargetPosition.column + 8;
                p.column += s.lastSourcePosition.column;
            }
            p.row += s.lastSourcePosition.row - 1;
        }

        private StringSection findPrecedingSection(Position p, List<StringSection> sections) {
            StringSection result = null;
            for (StringSection s : sections) {
                if (s.lastTargetPosition.row > p.row || s.lastTargetPosition.row == p.row && s.lastTargetPosition.column > p.column) break;
                result = s;
            }
            return result;
        }

        private void append(StringBuilder target, Position targetPosition, char c) {
            if (c == '\n') {
                ++targetPosition.row;
                targetPosition.column = 1;
            } else {
                ++targetPosition.column;
            }
            target.append(c);
        }

        private void append(StringBuilder target, Position targetPosition, String s) {
            int len = s.length();
            for (int i = 0; i < len; ++i) {
                this.append(target, targetPosition, s.charAt(i));
            }
        }

        private int read(Reader reader, Position position, StringBuilder lookAhead) throws IOException, FinishedReadingException {
            int c = this.read(reader, position);
            lookAhead.append((char)c);
            return c;
        }

        private int read(Reader reader, Position position) throws IOException, FinishedReadingException {
            int c;
            if (this.useLastRead) {
                c = this.lastRead;
                this.useLastRead = false;
                this.lastRead = -1;
            } else {
                c = this.read(reader);
                if (c == 13 && (c = this.read(reader)) != 10) {
                    this.lastRead = c;
                    this.useLastRead = true;
                    c = 13;
                }
            }
            if (c == -1) {
                throw finishedReadingException;
            }
            if (c == 10) {
                ++position.row;
                position.column = 1;
            } else {
                ++position.column;
            }
            return c;
        }

        private int read(Reader reader) throws IOException {
            int c = reader.read();
            this.templateSource.append((char)c);
            return c;
        }

        static {
            EMPTY_STACKTRACE = new StackTraceElement[0];
            finishedReadingException = new FinishedReadingException();
            finishedReadingException.setStackTrace(EMPTY_STACKTRACE);
        }

        private static final class StringSection {
            StringBuilder data = new StringBuilder();
            Position firstSourcePosition;
            Position lastSourcePosition;
            Position lastTargetPosition;

            private StringSection(Position firstSourcePosition) {
                this.firstSourcePosition = new Position(firstSourcePosition);
            }

            public String toString() {
                return this.data.toString();
            }
        }

        private static final class Position {
            public int row;
            public int column;

            private Position(int row, int column) {
                this.row = row;
                this.column = column;
            }

            private Position(Position p) {
                this.set(p);
            }

            private void set(Position p) {
                this.row = p.row;
                this.column = p.column;
            }

            public String toString() {
                return this.row + ":" + this.column;
            }
        }

        private static class FinishedReadingException
        extends Exception {
            private FinishedReadingException() {
            }
        }
    }
}

