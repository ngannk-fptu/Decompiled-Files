/*
 * Decompiled with CFR 0.152.
 */
package groovy.text.markup;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import groovy.lang.Writable;
import groovy.text.Template;
import groovy.text.TemplateEngine;
import groovy.text.markup.AutoNewLineTransformer;
import groovy.text.markup.BaseTemplate;
import groovy.text.markup.TemplateASTTransformer;
import groovy.text.markup.TemplateConfiguration;
import groovy.text.markup.TemplateResolver;
import groovy.transform.CompileStatic;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.classgen.asm.BytecodeDumper;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

public class MarkupTemplateEngine
extends TemplateEngine {
    static final ClassNode MARKUPTEMPLATEENGINE_CLASSNODE = ClassHelper.make(MarkupTemplateEngine.class);
    static final String MODELTYPES_ASTKEY = "MTE.modelTypes";
    private static final Pattern LOCALIZED_RESOURCE_PATTERN = Pattern.compile("(.+?)(?:_([a-z]{2}(?:_[A-Z]{2,3})))?\\.([\\p{Alnum}.]+)$");
    private static final boolean DEBUG_BYTECODE = Boolean.valueOf(System.getProperty("markuptemplateengine.compiler.debug", "false"));
    private static final AtomicLong counter = new AtomicLong();
    private final TemplateGroovyClassLoader groovyClassLoader;
    private final CompilerConfiguration compilerConfiguration;
    private final TemplateConfiguration templateConfiguration;
    private final Map<String, GroovyCodeSource> codeSourceCache = new LinkedHashMap<String, GroovyCodeSource>();
    private final TemplateResolver templateResolver;

    public MarkupTemplateEngine() {
        this(new TemplateConfiguration());
    }

    public MarkupTemplateEngine(TemplateConfiguration tplConfig) {
        this(MarkupTemplateEngine.class.getClassLoader(), tplConfig);
    }

    public MarkupTemplateEngine(ClassLoader parentLoader, TemplateConfiguration tplConfig) {
        this(parentLoader, tplConfig, null);
    }

    public MarkupTemplateEngine(final ClassLoader parentLoader, TemplateConfiguration tplConfig, TemplateResolver resolver) {
        this.compilerConfiguration = new CompilerConfiguration();
        this.templateConfiguration = tplConfig;
        this.compilerConfiguration.addCompilationCustomizers(new TemplateASTTransformer(tplConfig));
        this.compilerConfiguration.addCompilationCustomizers(new ASTTransformationCustomizer(Collections.singletonMap("extensions", "groovy.text.markup.MarkupTemplateTypeCheckingExtension"), CompileStatic.class));
        if (this.templateConfiguration.isAutoNewLine()) {
            this.compilerConfiguration.addCompilationCustomizers(new CompilationCustomizer(CompilePhase.CONVERSION){

                @Override
                public void call(SourceUnit source, GeneratorContext context, ClassNode classNode) throws CompilationFailedException {
                    new AutoNewLineTransformer(source).visitClass(classNode);
                }
            });
        }
        this.groovyClassLoader = AccessController.doPrivileged(new PrivilegedAction<TemplateGroovyClassLoader>(){

            @Override
            public TemplateGroovyClassLoader run() {
                return new TemplateGroovyClassLoader(parentLoader, MarkupTemplateEngine.this.compilerConfiguration);
            }
        });
        if (DEBUG_BYTECODE) {
            this.compilerConfiguration.setBytecodePostprocessor(BytecodeDumper.STANDARD_ERR);
        }
        this.templateResolver = resolver == null ? new DefaultTemplateResolver() : resolver;
        this.templateResolver.configure(this.groovyClassLoader, this.templateConfiguration);
    }

    public MarkupTemplateEngine(final ClassLoader parentLoader, final File templateDirectory, TemplateConfiguration tplConfig) {
        this((ClassLoader)AccessController.doPrivileged(new PrivilegedAction<URLClassLoader>(){

            @Override
            public URLClassLoader run() {
                return new URLClassLoader(MarkupTemplateEngine.buildURLs(templateDirectory), parentLoader);
            }
        }), tplConfig, null);
    }

    private static URL[] buildURLs(File templateDirectory) {
        try {
            return new URL[]{templateDirectory.toURI().toURL()};
        }
        catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid directory", e);
        }
    }

    @Override
    public Template createTemplate(Reader reader) throws CompilationFailedException, ClassNotFoundException, IOException {
        return new MarkupTemplateMaker(reader, null, null);
    }

    public Template createTemplate(Reader reader, String sourceName) throws CompilationFailedException, ClassNotFoundException, IOException {
        return new MarkupTemplateMaker(reader, sourceName, null);
    }

    public Template createTemplateByPath(String templatePath) throws CompilationFailedException, ClassNotFoundException, IOException {
        return new MarkupTemplateMaker(this.resolveTemplate(templatePath), null);
    }

    public Template createTypeCheckedModelTemplate(String source, Map<String, String> modelTypes) throws CompilationFailedException, ClassNotFoundException, IOException {
        return new MarkupTemplateMaker(new StringReader(source), null, modelTypes);
    }

    public Template createTypeCheckedModelTemplate(String source, String sourceName, Map<String, String> modelTypes) throws CompilationFailedException, ClassNotFoundException, IOException {
        return new MarkupTemplateMaker(new StringReader(source), sourceName, modelTypes);
    }

    public Template createTypeCheckedModelTemplate(Reader reader, Map<String, String> modelTypes) throws CompilationFailedException, ClassNotFoundException, IOException {
        return new MarkupTemplateMaker(reader, null, modelTypes);
    }

    public Template createTypeCheckedModelTemplate(Reader reader, String sourceName, Map<String, String> modelTypes) throws CompilationFailedException, ClassNotFoundException, IOException {
        return new MarkupTemplateMaker(reader, sourceName, modelTypes);
    }

    public Template createTypeCheckedModelTemplateByPath(String templatePath, Map<String, String> modelTypes) throws CompilationFailedException, ClassNotFoundException, IOException {
        return new MarkupTemplateMaker(this.resolveTemplate(templatePath), modelTypes);
    }

    @Override
    public Template createTemplate(URL resource) throws CompilationFailedException, ClassNotFoundException, IOException {
        return new MarkupTemplateMaker(resource, null);
    }

    public Template createTypeCheckedModelTemplate(URL resource, Map<String, String> modelTypes) throws CompilationFailedException, ClassNotFoundException, IOException {
        return new MarkupTemplateMaker(resource, modelTypes);
    }

    public GroovyClassLoader getTemplateLoader() {
        return this.groovyClassLoader;
    }

    public CompilerConfiguration getCompilerConfiguration() {
        return this.compilerConfiguration;
    }

    public TemplateConfiguration getTemplateConfiguration() {
        return this.templateConfiguration;
    }

    public URL resolveTemplate(String templatePath) throws IOException {
        return this.templateResolver.resolveTemplate(templatePath);
    }

    public static class CachingTemplateResolver
    extends DefaultTemplateResolver {
        protected final Map<String, URL> cache;
        protected boolean useCache = false;

        public CachingTemplateResolver(Map<String, URL> cache) {
            this.cache = cache;
        }

        public CachingTemplateResolver() {
            this(new ConcurrentHashMap<String, URL>());
        }

        @Override
        public void configure(ClassLoader templateClassLoader, TemplateConfiguration configuration) {
            super.configure(templateClassLoader, configuration);
            this.useCache = configuration.isCacheTemplates();
        }

        @Override
        public URL resolveTemplate(String templatePath) throws IOException {
            URL cachedURL;
            if (this.useCache && (cachedURL = this.cache.get(templatePath)) != null) {
                return cachedURL;
            }
            URL url = super.resolveTemplate(templatePath);
            if (this.useCache) {
                this.cache.put(templatePath, url);
            }
            return url;
        }
    }

    public static class DefaultTemplateResolver
    implements TemplateResolver {
        private TemplateConfiguration templateConfiguration;
        private ClassLoader templateClassLoader;

        @Override
        public void configure(ClassLoader templateClassLoader, TemplateConfiguration configuration) {
            this.templateClassLoader = templateClassLoader;
            this.templateConfiguration = configuration;
        }

        @Override
        public URL resolveTemplate(String templatePath) throws IOException {
            URL resource;
            TemplateResource templateResource = TemplateResource.parse(templatePath);
            String configurationLocale = this.templateConfiguration.getLocale().toString().replace("-", "_");
            URL uRL = resource = templateResource.hasLocale() ? this.templateClassLoader.getResource(templateResource.toString()) : null;
            if (resource == null) {
                resource = this.templateClassLoader.getResource(templateResource.withLocale(configurationLocale).toString());
            }
            if (resource == null) {
                resource = this.templateClassLoader.getResource(templateResource.withLocale(null).toString());
            }
            if (resource == null) {
                throw new IOException("Unable to load template:" + templatePath);
            }
            return resource;
        }
    }

    public static class TemplateResource {
        private final String baseName;
        private final String locale;
        private final String extension;

        public static TemplateResource parse(String fullPath) {
            Matcher matcher = LOCALIZED_RESOURCE_PATTERN.matcher(fullPath);
            if (!matcher.find()) {
                throw new IllegalArgumentException("Illegal template path: " + fullPath);
            }
            return new TemplateResource(matcher.group(1), matcher.group(2), matcher.group(3));
        }

        private TemplateResource(String baseName, String locale, String extension) {
            this.baseName = baseName;
            this.locale = locale;
            this.extension = extension;
        }

        public TemplateResource withLocale(String locale) {
            return new TemplateResource(this.baseName, locale, this.extension);
        }

        public String toString() {
            return this.baseName + (this.locale != null ? "_" + this.locale : "") + "." + this.extension;
        }

        public boolean hasLocale() {
            return this.locale != null && !"".equals(this.locale);
        }
    }

    static class TemplateGroovyClassLoader
    extends GroovyClassLoader {
        static final ThreadLocal<Map<String, String>> modelTypes = new ThreadLocal();

        public TemplateGroovyClassLoader(ClassLoader parentLoader, CompilerConfiguration compilerConfiguration) {
            super(parentLoader, compilerConfiguration);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public Class parseClass(GroovyCodeSource codeSource, Map<String, String> hints) throws CompilationFailedException {
            modelTypes.set(hints);
            try {
                Class clazz = super.parseClass(codeSource);
                return clazz;
            }
            finally {
                modelTypes.set(null);
            }
        }
    }

    private class MarkupTemplateMaker
    implements Template {
        final Class<BaseTemplate> templateClass;
        final Map<String, String> modeltypes;

        public MarkupTemplateMaker(Reader reader, String sourceName, Map<String, String> modelTypes) {
            String name = sourceName != null ? sourceName : "GeneratedMarkupTemplate" + counter.getAndIncrement();
            this.templateClass = MarkupTemplateEngine.this.groovyClassLoader.parseClass(new GroovyCodeSource(reader, name, "x"), modelTypes);
            this.modeltypes = modelTypes;
        }

        public MarkupTemplateMaker(URL resource, Map<String, String> modelTypes) throws IOException {
            GroovyCodeSource codeSource;
            boolean cache = MarkupTemplateEngine.this.templateConfiguration.isCacheTemplates();
            if (cache) {
                String key = resource.toExternalForm();
                codeSource = (GroovyCodeSource)MarkupTemplateEngine.this.codeSourceCache.get(key);
                if (codeSource == null) {
                    codeSource = new GroovyCodeSource(resource);
                    MarkupTemplateEngine.this.codeSourceCache.put(key, codeSource);
                }
            } else {
                codeSource = new GroovyCodeSource(resource);
            }
            codeSource.setCachable(cache);
            this.templateClass = MarkupTemplateEngine.this.groovyClassLoader.parseClass(codeSource, modelTypes);
            this.modeltypes = modelTypes;
        }

        @Override
        public Writable make() {
            return this.make(Collections.emptyMap());
        }

        @Override
        public Writable make(Map binding) {
            return DefaultGroovyMethods.newInstance(this.templateClass, new Object[]{MarkupTemplateEngine.this, binding, this.modeltypes, MarkupTemplateEngine.this.templateConfiguration});
        }
    }
}

