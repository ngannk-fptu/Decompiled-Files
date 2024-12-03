/*
 * Decompiled with CFR 0.152.
 */
package groovy.text;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.Writable;
import groovy.text.Template;
import groovy.text.TemplateEngine;
import java.io.IOException;
import java.io.Reader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.codehaus.groovy.control.CompilationFailedException;

public class GStringTemplateEngine
extends TemplateEngine {
    private final ClassLoader parentLoader;
    private static AtomicInteger counter = new AtomicInteger();
    private static final boolean reuseClassLoader = Boolean.getBoolean("groovy.GStringTemplateEngine.reuseClassLoader");

    public GStringTemplateEngine() {
        this(GStringTemplate.class.getClassLoader());
    }

    public GStringTemplateEngine(ClassLoader parentLoader) {
        this.parentLoader = parentLoader;
    }

    @Override
    public Template createTemplate(Reader reader) throws CompilationFailedException, ClassNotFoundException, IOException {
        return new GStringTemplate(reader, this.parentLoader);
    }

    private static class GStringTemplate
    implements Template {
        final Closure template;

        GStringTemplate(Reader reader, final ClassLoader parentLoader) throws CompilationFailedException, ClassNotFoundException, IOException {
            Class groovyClass;
            int c;
            StringBuilder templateExpressions = new StringBuilder("package groovy.tmp.templates\n def getTemplate() { return { out -> out << \"\"\"");
            boolean writingString = true;
            while ((c = reader.read()) != -1) {
                if (c == 60) {
                    c = reader.read();
                    if (c == 37) {
                        c = reader.read();
                        if (c == 61) {
                            GStringTemplate.parseExpression(reader, writingString, templateExpressions);
                            writingString = true;
                            continue;
                        }
                        GStringTemplate.parseSection(c, reader, writingString, templateExpressions);
                        writingString = false;
                        continue;
                    }
                    GStringTemplate.appendCharacter('<', templateExpressions, writingString);
                    writingString = true;
                } else if (c == 34) {
                    GStringTemplate.appendCharacter('\\', templateExpressions, writingString);
                    writingString = true;
                } else if (c == 36) {
                    GStringTemplate.appendCharacter('$', templateExpressions, writingString);
                    writingString = true;
                    c = reader.read();
                    if (c == 123) {
                        GStringTemplate.appendCharacter('{', templateExpressions, writingString);
                        writingString = true;
                        this.parseGSstring(reader, writingString, templateExpressions);
                        writingString = true;
                        continue;
                    }
                }
                GStringTemplate.appendCharacter((char)c, templateExpressions, writingString);
                writingString = true;
            }
            if (writingString) {
                templateExpressions.append("\"\"\"");
            }
            templateExpressions.append("}}");
            GroovyClassLoader loader = reuseClassLoader && parentLoader instanceof GroovyClassLoader ? (GroovyClassLoader)parentLoader : (GroovyClassLoader)AccessController.doPrivileged(new PrivilegedAction(){

                public Object run() {
                    return new GroovyClassLoader(parentLoader);
                }
            });
            try {
                groovyClass = loader.parseClass(new GroovyCodeSource(templateExpressions.toString(), "GStringTemplateScript" + counter.incrementAndGet() + ".groovy", "x"));
            }
            catch (Exception e) {
                throw new GroovyRuntimeException("Failed to parse template script (your template may contain an error or be trying to use expressions not currently supported): " + e.getMessage());
            }
            try {
                GroovyObject script = (GroovyObject)groovyClass.newInstance();
                this.template = (Closure)script.invokeMethod("getTemplate", null);
                this.template.setResolveStrategy(1);
            }
            catch (InstantiationException e) {
                throw new ClassNotFoundException(e.getMessage());
            }
            catch (IllegalAccessException e) {
                throw new ClassNotFoundException(e.getMessage());
            }
        }

        private static void appendCharacter(char c, StringBuilder templateExpressions, boolean writingString) {
            if (!writingString) {
                templateExpressions.append("out << \"\"\"");
            }
            templateExpressions.append(c);
        }

        private void parseGSstring(Reader reader, boolean writingString, StringBuilder templateExpressions) throws IOException {
            int c;
            if (!writingString) {
                templateExpressions.append("\"\"\"; ");
            }
            while ((c = reader.read()) != -1) {
                templateExpressions.append((char)c);
                if (c != 125) continue;
                break;
            }
        }

        private static void parseSection(int pendingC, Reader reader, boolean writingString, StringBuilder templateExpressions) throws IOException {
            int c;
            if (writingString) {
                templateExpressions.append("\"\"\"; ");
            }
            templateExpressions.append((char)pendingC);
            while ((c = reader.read()) != -1) {
                if (c == 37) {
                    c = reader.read();
                    if (c == 62) break;
                    templateExpressions.append('%');
                }
                templateExpressions.append((char)c);
            }
            templateExpressions.append(";\n ");
        }

        private static void parseExpression(Reader reader, boolean writingString, StringBuilder templateExpressions) throws IOException {
            int c;
            if (!writingString) {
                templateExpressions.append("out << \"\"\"");
            }
            templateExpressions.append("${");
            while ((c = reader.read()) != -1) {
                if (c == 37) {
                    c = reader.read();
                    if (c == 62) break;
                    templateExpressions.append('%');
                }
                templateExpressions.append((char)c);
            }
            templateExpressions.append('}');
        }

        @Override
        public Writable make() {
            return this.make(null);
        }

        @Override
        public Writable make(Map map) {
            Closure template = ((Closure)this.template.clone()).asWritable();
            Binding binding = new Binding(map);
            template.setDelegate(binding);
            return (Writable)((Object)template);
        }
    }
}

