/*
 * Decompiled with CFR 0.152.
 */
package groovy.text;

import groovy.lang.Binding;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import groovy.lang.Writable;
import groovy.text.Template;
import groovy.text.TemplateEngine;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.runtime.InvokerHelper;

public class SimpleTemplateEngine
extends TemplateEngine {
    private boolean verbose;
    private static int counter = 1;
    private GroovyShell groovyShell;

    public SimpleTemplateEngine() {
        this(GroovyShell.class.getClassLoader());
    }

    public SimpleTemplateEngine(boolean verbose) {
        this(GroovyShell.class.getClassLoader());
        this.setVerbose(verbose);
    }

    public SimpleTemplateEngine(ClassLoader parentLoader) {
        this(new GroovyShell(parentLoader));
    }

    public SimpleTemplateEngine(GroovyShell groovyShell) {
        this.groovyShell = groovyShell;
    }

    @Override
    public Template createTemplate(Reader reader) throws CompilationFailedException, IOException {
        SimpleTemplate template = new SimpleTemplate();
        String script = template.parse(reader);
        if (this.verbose) {
            System.out.println("\n-- script source --");
            System.out.print(script);
            System.out.println("\n-- script end --\n");
        }
        try {
            template.script = this.groovyShell.parse(script, "SimpleTemplateScript" + counter++ + ".groovy");
        }
        catch (Exception e) {
            throw new GroovyRuntimeException("Failed to parse template script (your template may contain an error or be trying to use expressions not currently supported): " + e.getMessage());
        }
        return template;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public boolean isVerbose() {
        return this.verbose;
    }

    private static class SimpleTemplate
    implements Template {
        protected Script script;

        private SimpleTemplate() {
        }

        @Override
        public Writable make() {
            return this.make(null);
        }

        @Override
        public Writable make(final Map map) {
            return new Writable(){

                @Override
                public Writer writeTo(Writer writer) {
                    Binding binding = map == null ? new Binding() : new Binding(map);
                    Script scriptObject = InvokerHelper.createScript(SimpleTemplate.this.script.getClass(), binding);
                    PrintWriter pw = new PrintWriter(writer);
                    scriptObject.setProperty("out", pw);
                    scriptObject.run();
                    pw.flush();
                    return writer;
                }

                public String toString() {
                    StringWriter sw = new StringWriter();
                    this.writeTo(sw);
                    return sw.toString();
                }
            };
        }

        protected String parse(Reader reader) throws IOException {
            int c;
            if (!reader.markSupported()) {
                reader = new BufferedReader(reader);
            }
            StringWriter sw = new StringWriter();
            this.startScript(sw);
            while ((c = reader.read()) != -1) {
                if (c == 60) {
                    reader.mark(1);
                    c = reader.read();
                    if (c != 37) {
                        sw.write(60);
                        reader.reset();
                        continue;
                    }
                    reader.mark(1);
                    c = reader.read();
                    if (c == 61) {
                        this.groovyExpression(reader, sw);
                        continue;
                    }
                    reader.reset();
                    this.groovySection(reader, sw);
                    continue;
                }
                if (c == 36) {
                    reader.mark(1);
                    c = reader.read();
                    if (c != 123) {
                        sw.write(36);
                        reader.reset();
                        continue;
                    }
                    reader.mark(1);
                    sw.write("${");
                    this.processGSstring(reader, sw);
                    continue;
                }
                if (c == 34) {
                    sw.write(92);
                }
                if (c == 10 || c == 13) {
                    if (c == 13) {
                        reader.mark(1);
                        c = reader.read();
                        if (c != 10) {
                            reader.reset();
                        }
                    }
                    sw.write("\n");
                    continue;
                }
                sw.write(c);
            }
            this.endScript(sw);
            return sw.toString();
        }

        private void startScript(StringWriter sw) {
            sw.write("out.print(\"\"\"");
        }

        private void endScript(StringWriter sw) {
            sw.write("\"\"\");\n");
            sw.write("\n/* Generated by SimpleTemplateEngine */");
        }

        private void processGSstring(Reader reader, StringWriter sw) throws IOException {
            int c;
            while ((c = reader.read()) != -1) {
                if (c != 10 && c != 13) {
                    sw.write(c);
                }
                if (c != 125) continue;
                break;
            }
        }

        private void groovyExpression(Reader reader, StringWriter sw) throws IOException {
            int c;
            sw.write("${");
            while ((c = reader.read()) != -1) {
                if (c == 37) {
                    c = reader.read();
                    if (c == 62) break;
                    sw.write(37);
                }
                if (c == 10 || c == 13) continue;
                sw.write(c);
            }
            sw.write("}");
        }

        private void groovySection(Reader reader, StringWriter sw) throws IOException {
            int c;
            sw.write("\"\"\");");
            while ((c = reader.read()) != -1) {
                if (c == 37) {
                    c = reader.read();
                    if (c == 62) break;
                    sw.write(37);
                }
                sw.write(c);
            }
            sw.write(";\nout.print(\"\"\"");
        }
    }
}

