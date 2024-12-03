/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.maven.model.Resource
 *  org.apache.maven.plugin.AbstractMojo
 *  org.apache.maven.plugin.MojoExecutionException
 *  org.apache.maven.plugin.MojoFailureException
 *  org.apache.maven.plugins.annotations.LifecyclePhase
 *  org.apache.maven.plugins.annotations.Mojo
 *  org.apache.maven.plugins.annotations.Parameter
 *  org.apache.maven.project.MavenProject
 */
package org.apache.xmlbeans.impl.tool;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.impl.tool.Extension;
import org.apache.xmlbeans.impl.tool.MavenPluginResolver;
import org.apache.xmlbeans.impl.tool.Parameters;
import org.apache.xmlbeans.impl.tool.SchemaCompiler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Mojo(name="compile", defaultPhase=LifecyclePhase.GENERATE_SOURCES)
public class MavenPlugin
extends AbstractMojo {
    @Parameter(readonly=true, defaultValue="${project}")
    private MavenProject project;
    @Parameter(defaultValue="${project.basedir}/src/main/schema")
    private String sourceDir;
    @Parameter(defaultValue="*.xsd,*.wsdl,*.java")
    private String sourceSchemas;
    @Parameter(defaultValue="${project.basedir}/src/schema/xmlconfig.xml")
    private String xmlConfigs;
    @Parameter(defaultValue="${project.basedir}/target/generated-sources")
    private String javaTargetDir;
    @Parameter(defaultValue="${project.basedir}/target/generated-resources")
    private String classTargetDir;
    @Parameter
    private String catalogLocation;
    @Parameter
    private String classPath;
    @Parameter
    private List<Resource> resources;
    @Parameter(defaultValue="true")
    private boolean buildSchemas;
    @Parameter(defaultValue="schemaorg_apache_xmlbeans/src")
    private String baseSchemaLocation;
    @Parameter(defaultValue="${project.artifactId}")
    private String name;
    @Parameter(defaultValue="false")
    private boolean verbose;
    @Parameter(defaultValue="true")
    private boolean quiet;
    @Parameter(defaultValue="true")
    private boolean quite;
    @Parameter(defaultValue="false")
    private boolean noUpa;
    @Parameter(defaultValue="false")
    private boolean noPvr;
    @Parameter(defaultValue="false")
    private boolean noAnn;
    @Parameter(defaultValue="false")
    private boolean noVDoc;
    @Parameter(defaultValue="${project.groupId}.${project.artifactId}.metadata")
    private String repackage;
    @Parameter
    private List<String> mdefNamespaces;
    @Parameter
    private String partialMethods;
    @Parameter(defaultValue="false")
    private boolean download;
    @Parameter(defaultValue="true")
    private boolean sourceOnly;
    @Parameter
    private File basedir;
    @Parameter
    private String compiler;
    @Parameter(defaultValue="8m")
    private String memoryInitialSize;
    @Parameter(defaultValue="256m")
    private String memoryMaximumSize;
    @Parameter(defaultValue="${project.basedir}/target/${project.artifactId}-${project.version}-xmltypes.jar")
    private File outputJar;
    @Parameter(defaultValue="false")
    private boolean debug;
    @Parameter(defaultValue="false")
    private boolean copyAnn;
    @Parameter
    private List<Extension> extensions;

    public void execute() throws MojoExecutionException, MojoFailureException {
        File[] schemaFiles;
        if (this.sourceDir == null || this.sourceDir.isEmpty() || !new File(this.sourceDir).isDirectory()) {
            throw new MojoFailureException("Set configuration <sourceDir> (='" + this.sourceDir + "') to a valid directory containing *.xsd,*.wsdl files.");
        }
        if (this.baseSchemaLocation == null || this.baseSchemaLocation.isEmpty()) {
            throw new MojoFailureException("baseSchemaLocation is empty");
        }
        if (this.sourceSchemas == null) {
            this.getLog().debug((CharSequence)"sourceSchemas is null");
        }
        if (this.classPath == null) {
            this.getLog().debug((CharSequence)"classPath is null");
        }
        ArrayList<File> xsds = new ArrayList<File>();
        ArrayList<File> wsdls = new ArrayList<File>();
        ArrayList<File> javas = new ArrayList<File>();
        File base = new File(this.sourceDir);
        Resource resource = new Resource();
        resource.setDirectory(this.sourceDir);
        resource.setTargetPath(this.baseSchemaLocation);
        Pattern pat = Pattern.compile(this.sourceSchemas == null ? ".*" : "(" + this.sourceSchemas.replace(",", "|").replace(".", "\\.").replace("*", ".*") + ")");
        for (File file : schemaFiles = Objects.requireNonNull(base.listFiles((dir, name) -> !name.endsWith(".xsdconfig") && pat.matcher(name).matches()))) {
            String name2 = file.getName();
            switch (name2.replaceAll(".*\\.", "")) {
                case "wsdl": {
                    wsdls.add(file);
                    break;
                }
                case "java": {
                    javas.add(file);
                    break;
                }
                default: {
                    xsds.add(file);
                }
            }
            resource.addInclude(name2);
        }
        this.resources = Collections.singletonList(resource);
        if (this.buildSchemas) {
            List<File> configs = this.xmlConfigs == null || this.xmlConfigs.isEmpty() ? Collections.emptyList() : Stream.of(this.xmlConfigs.split(",")).flatMap(s -> Stream.of(new File((String)s), new File(base, (String)s)).filter(File::exists)).collect(Collectors.toList());
            ArrayList<File> classPathList = new ArrayList<File>();
            ArrayList<URL> urls = new ArrayList<URL>();
            if (this.classPath != null) {
                for (String classpathElement : this.classPath.split(",")) {
                    File file = new File(classpathElement);
                    classPathList.add(file);
                    try {
                        urls.add(file.toURI().toURL());
                    }
                    catch (MalformedURLException e) {
                        throw new MojoFailureException("invalid classpath: " + file, (Throwable)e);
                    }
                }
            }
            URLClassLoader uRLClassLoader = new URLClassLoader(urls.toArray(new URL[0]));
            EntityResolver entityResolver = MavenPluginResolver.getResolver(this.catalogLocation);
            URI sourceDirURI = new File(this.sourceDir).toURI();
            entityResolver = new PassThroughResolver(uRLClassLoader, entityResolver, sourceDirURI, this.baseSchemaLocation);
            Parameters params = new Parameters();
            params.setXsdFiles(MavenPlugin.files(xsds));
            params.setWsdlFiles(MavenPlugin.files(wsdls));
            params.setJavaFiles(MavenPlugin.files(javas));
            params.setConfigFiles(MavenPlugin.files(configs));
            params.setClasspath(MavenPlugin.files(classPathList));
            params.setName(this.name);
            params.setSrcDir(new File(this.javaTargetDir));
            params.setClassesDir(new File(this.classTargetDir));
            params.setNojavac(this.sourceOnly);
            params.setVerbose(this.verbose);
            params.setEntityResolver(entityResolver);
            params.setQuiet(this.quiet && this.quite);
            params.setNoUpa(this.noUpa);
            params.setNoPvr(this.noPvr);
            params.setNoAnn(this.noAnn);
            params.setCopyAnn(this.copyAnn);
            params.setNoVDoc(this.noVDoc);
            if (this.repackage != null && !this.repackage.isEmpty()) {
                params.setRepackage("org.apache.xmlbeans.metadata:" + this.repackage);
            }
            if (this.mdefNamespaces != null && !this.mdefNamespaces.isEmpty()) {
                params.setMdefNamespaces(new HashSet<String>(this.mdefNamespaces));
            }
            ArrayList<XmlError> errorList = new ArrayList<XmlError>();
            params.setErrorListener(errorList);
            if (this.partialMethods != null && !this.partialMethods.isEmpty()) {
                params.setPartialMethods(SchemaCompiler.parsePartialMethods(this.partialMethods));
            }
            params.setDownload(this.download);
            params.setBaseDir(this.basedir);
            params.setCompiler(this.compiler);
            params.setMemoryInitialSize(this.memoryInitialSize);
            params.setMemoryMaximumSize(this.memoryMaximumSize);
            params.setOutputJar(this.outputJar);
            params.setDebug(this.debug);
            params.setExtensions(this.extensions);
            boolean result = SchemaCompiler.compile(params);
            if (!result) {
                throw new MojoFailureException("Schema compilation failed!\n" + errorList.stream().map(XmlError::toString).collect(Collectors.joining("\n")));
            }
            Resource genResource = new Resource();
            genResource.setDirectory(this.classTargetDir);
            this.project.addResource(genResource);
            this.project.addCompileSourceRoot(this.javaTargetDir);
        }
    }

    private static File[] files(List<File> files) {
        return files == null || files.isEmpty() ? null : files.toArray(new File[0]);
    }

    private static class PassThroughResolver
    implements EntityResolver {
        private final ClassLoader cl;
        private final EntityResolver delegate;
        private final URI sourceDir;
        private final String baseSchemaLocation;

        public PassThroughResolver(ClassLoader cl, EntityResolver delegate, URI sourceDir, String baseSchemaLocation) {
            this.cl = cl;
            this.delegate = delegate;
            this.sourceDir = sourceDir;
            this.baseSchemaLocation = baseSchemaLocation + "/";
        }

        @Override
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            String localSystemId;
            InputSource is;
            if (this.delegate != null && (is = this.delegate.resolveEntity(publicId, systemId)) != null) {
                return is;
            }
            System.out.println("Could not resolve publicId: " + publicId + ", systemId: " + systemId + " from catalog");
            try {
                localSystemId = this.sourceDir.relativize(new URI(systemId)).toString();
            }
            catch (URISyntaxException e) {
                throw new IOException("Could not relativeize systemId", e);
            }
            InputStream in = this.cl.getResourceAsStream(localSystemId);
            if (in != null) {
                System.out.println("found in classpath at: " + localSystemId);
                return new InputSource(in);
            }
            in = this.cl.getResourceAsStream(this.baseSchemaLocation + localSystemId);
            if (in != null) {
                System.out.println("found in classpath at: META-INF/" + localSystemId);
                return new InputSource(in);
            }
            System.out.println("Not found in classpath, looking in current directory: " + systemId);
            return new InputSource(systemId);
        }
    }
}

