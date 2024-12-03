/*
 * Decompiled with CFR 0.152.
 */
package groovy.text.markup;

import groovy.lang.Closure;
import groovy.lang.Writable;
import groovy.text.Template;
import groovy.text.markup.DelegatingIndentWriter;
import groovy.text.markup.MarkupTemplateEngine;
import groovy.text.markup.TemplateConfiguration;
import groovy.xml.XmlUtil;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.codehaus.groovy.control.io.NullWriter;
import org.codehaus.groovy.runtime.ExceptionUtils;
import org.codehaus.groovy.runtime.ResourceGroovyMethods;

public abstract class BaseTemplate
implements Writable {
    private static final Map EMPTY_MODEL = Collections.emptyMap();
    private final Map model;
    private final Map<String, String> modelTypes;
    private final MarkupTemplateEngine engine;
    private final TemplateConfiguration configuration;
    private final Map<String, Template> cachedFragments;
    private Writer out;
    private boolean doWriteIndent;

    public BaseTemplate(MarkupTemplateEngine templateEngine, Map model, Map<String, String> modelTypes, TemplateConfiguration configuration) {
        this.model = model == null ? EMPTY_MODEL : model;
        this.engine = templateEngine;
        this.configuration = configuration;
        this.modelTypes = modelTypes;
        this.cachedFragments = new LinkedHashMap<String, Template>();
    }

    public Map getModel() {
        return this.model;
    }

    public abstract Object run();

    public BaseTemplate yieldUnescaped(Object obj) throws IOException {
        this.writeIndent();
        this.out.write(obj.toString());
        return this;
    }

    public BaseTemplate yield(Object obj) throws IOException {
        this.writeIndent();
        this.out.write(XmlUtil.escapeXml(obj.toString()));
        return this;
    }

    public String stringOf(Closure cl) throws IOException {
        Writer old = this.out;
        StringWriter stringWriter = new StringWriter(32);
        this.out = stringWriter;
        Object result = cl.call();
        if (result != null && result != this) {
            stringWriter.append(result.toString());
        }
        this.out = old;
        return stringWriter.toString();
    }

    public BaseTemplate comment(Object cs) throws IOException {
        this.writeIndent();
        this.out.write("<!--");
        this.out.write(cs.toString());
        this.out.write("-->");
        return this;
    }

    public BaseTemplate xmlDeclaration() throws IOException {
        this.out.write("<?xml ");
        this.writeAttribute("version", "1.0");
        if (this.configuration.getDeclarationEncoding() != null) {
            this.writeAttribute(" encoding", this.configuration.getDeclarationEncoding());
        }
        this.out.write("?>");
        this.out.write(this.configuration.getNewLineString());
        return this;
    }

    public BaseTemplate pi(Map<?, ?> attrs) throws IOException {
        for (Map.Entry<?, ?> entry : attrs.entrySet()) {
            Object target = entry.getKey();
            Object instruction = entry.getValue();
            this.out.write("<?");
            if (instruction instanceof Map) {
                this.out.write(target.toString());
                this.writeAttributes((Map)instruction);
            } else {
                this.out.write(target.toString());
                this.out.write(" ");
                this.out.write(instruction.toString());
            }
            this.out.write("?>");
            this.out.write(this.configuration.getNewLineString());
        }
        return this;
    }

    private void writeAttribute(String attName, String value) throws IOException {
        this.out.write(attName);
        this.out.write("=");
        this.writeQt();
        this.out.write(this.escapeQuotes(value));
        this.writeQt();
    }

    private void writeQt() throws IOException {
        if (this.configuration.isUseDoubleQuotes()) {
            this.out.write(34);
        } else {
            this.out.write(39);
        }
    }

    private void writeIndent() throws IOException {
        if (this.out instanceof DelegatingIndentWriter && this.doWriteIndent) {
            ((DelegatingIndentWriter)this.out).writeIndent();
            this.doWriteIndent = false;
        }
    }

    private String escapeQuotes(String str) {
        String quote = this.configuration.isUseDoubleQuotes() ? "\"" : "'";
        String escape = this.configuration.isUseDoubleQuotes() ? "&quote;" : "&apos;";
        return str.replace(quote, escape);
    }

    public Object methodMissing(String tagName, Object args) throws IOException {
        Object o = this.model.get(tagName);
        if (o instanceof Closure) {
            if (args instanceof Object[]) {
                this.yieldUnescaped(((Closure)o).call((Object[])args));
                return this;
            }
            this.yieldUnescaped(((Closure)o).call(args));
            return this;
        }
        if (args instanceof Object[]) {
            Writer wrt = this.out;
            TagData tagData = new TagData(args).invoke();
            Object body = tagData.getBody();
            this.writeIndent();
            wrt.write(60);
            wrt.write(tagName);
            this.writeAttributes(tagData.getAttributes());
            if (body != null) {
                wrt.write(62);
                this.writeBody(body);
                this.writeIndent();
                wrt.write("</");
                wrt.write(tagName);
                wrt.write(62);
            } else if (this.configuration.isExpandEmptyElements()) {
                wrt.write("></");
                wrt.write(tagName);
                wrt.write(62);
            } else {
                wrt.write("/>");
            }
        }
        return this;
    }

    private void writeBody(Object body) throws IOException {
        boolean indent = this.out instanceof DelegatingIndentWriter;
        if (body instanceof Closure) {
            if (indent) {
                ((DelegatingIndentWriter)this.out).next();
            }
            ((Closure)body).call();
            if (indent) {
                ((DelegatingIndentWriter)this.out).previous();
            }
        } else {
            this.out.write(body.toString());
        }
    }

    private void writeAttributes(Map<?, ?> attributes) throws IOException {
        if (attributes == null) {
            return;
        }
        Writer wrt = this.out;
        for (Map.Entry<?, ?> entry : attributes.entrySet()) {
            wrt.write(32);
            String attName = entry.getKey().toString();
            String value = entry.getValue() == null ? "" : entry.getValue().toString();
            this.writeAttribute(attName, value);
        }
    }

    public void includeGroovy(String templatePath) throws IOException, ClassNotFoundException {
        URL resource = this.engine.resolveTemplate(templatePath);
        this.engine.createTypeCheckedModelTemplate(resource, this.modelTypes).make(this.model).writeTo(this.out);
    }

    public void includeEscaped(String templatePath) throws IOException {
        URL resource = this.engine.resolveTemplate(templatePath);
        this.yield(ResourceGroovyMethods.getText(resource, this.engine.getCompilerConfiguration().getSourceEncoding()));
    }

    public void includeUnescaped(String templatePath) throws IOException {
        URL resource = this.engine.resolveTemplate(templatePath);
        this.yieldUnescaped(ResourceGroovyMethods.getText(resource, this.engine.getCompilerConfiguration().getSourceEncoding()));
    }

    public Object tryEscape(Object contents) {
        if (contents instanceof CharSequence) {
            return XmlUtil.escapeXml(contents.toString());
        }
        return contents;
    }

    public Writer getOut() {
        return this.out;
    }

    public void newLine() throws IOException {
        this.yieldUnescaped(this.configuration.getNewLineString());
        this.doWriteIndent = true;
    }

    public Object fragment(Map model, String templateText) throws IOException, ClassNotFoundException {
        Template template = this.cachedFragments.get(templateText);
        if (template == null) {
            template = this.engine.createTemplate(new StringReader(templateText));
            this.cachedFragments.put(templateText, template);
        }
        template.make(model).writeTo(this.out);
        return this;
    }

    public Object layout(Map model, String templateName) throws IOException, ClassNotFoundException {
        return this.layout(model, templateName, false);
    }

    public Object layout(Map model, String templateName, boolean inheritModel) throws IOException, ClassNotFoundException {
        Map submodel = inheritModel ? this.forkModel(model) : model;
        URL resource = this.engine.resolveTemplate(templateName);
        this.engine.createTypeCheckedModelTemplate(resource, this.modelTypes).make(submodel).writeTo(this.out);
        return this;
    }

    private Map forkModel(Map m) {
        HashMap result = new HashMap();
        result.putAll(this.model);
        result.putAll(m);
        return result;
    }

    public Closure contents(final Closure cl) {
        return new Closure(cl.getOwner(), cl.getThisObject()){

            @Override
            public Object call() {
                cl.call();
                return "";
            }

            public Object call(Object ... args) {
                cl.call(args);
                return "";
            }

            public Object call(Object arguments) {
                cl.call(arguments);
                return "";
            }
        };
    }

    @Override
    public Writer writeTo(Writer out) throws IOException {
        if (this.out != null) {
            return NullWriter.DEFAULT;
        }
        try {
            this.out = this.createWriter(out);
            this.run();
            Writer writer = out;
            return writer;
        }
        finally {
            if (this.out != null) {
                this.out.flush();
            }
            this.out = null;
        }
    }

    private Writer createWriter(Writer out) {
        return this.configuration.isAutoIndent() && !(out instanceof DelegatingIndentWriter) ? new DelegatingIndentWriter(out, this.configuration.getAutoIndentString()) : out;
    }

    public String toString() {
        StringWriter wrt = new StringWriter(512);
        try {
            this.writeTo(wrt);
        }
        catch (IOException e) {
            ExceptionUtils.sneakyThrow(e);
        }
        return wrt.toString();
    }

    private static class TagData {
        private final Object[] array;
        private Map attributes;
        private Object body;

        public TagData(Object args) {
            this.array = (Object[])args;
        }

        public Map getAttributes() {
            return this.attributes;
        }

        public Object getBody() {
            return this.body;
        }

        public TagData invoke() {
            this.attributes = null;
            this.body = null;
            for (Object o : this.array) {
                if (o instanceof Map) {
                    this.attributes = (Map)o;
                    continue;
                }
                this.body = o;
            }
            return this;
        }
    }
}

