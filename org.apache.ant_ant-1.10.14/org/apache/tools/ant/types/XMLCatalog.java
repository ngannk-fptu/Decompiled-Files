/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.Stack;
import java.util.Vector;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.ResourceLocation;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.JAXPUtils;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class XMLCatalog
extends DataType
implements EntityResolver,
URIResolver {
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private Vector<ResourceLocation> elements = new Vector();
    private Path classpath;
    private Path catalogPath;
    public static final String APACHE_RESOLVER = "org.apache.tools.ant.types.resolver.ApacheCatalogResolver";
    public static final String CATALOG_RESOLVER = "org.apache.xml.resolver.tools.CatalogResolver";
    private CatalogResolver catalogResolver = null;

    public XMLCatalog() {
        this.setChecked(false);
    }

    private Vector<ResourceLocation> getElements() {
        return this.getRef().elements;
    }

    private Path getClasspath() {
        return this.getRef().classpath;
    }

    public Path createClasspath() {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        if (this.classpath == null) {
            this.classpath = new Path(this.getProject());
        }
        this.setChecked(false);
        return this.classpath.createPath();
    }

    public void setClasspath(Path classpath) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        if (this.classpath == null) {
            this.classpath = classpath;
        } else {
            this.classpath.append(classpath);
        }
        this.setChecked(false);
    }

    public void setClasspathRef(Reference r) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.createClasspath().setRefid(r);
        this.setChecked(false);
    }

    public Path createCatalogPath() {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        if (this.catalogPath == null) {
            this.catalogPath = new Path(this.getProject());
        }
        this.setChecked(false);
        return this.catalogPath.createPath();
    }

    public void setCatalogPathRef(Reference r) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.createCatalogPath().setRefid(r);
        this.setChecked(false);
    }

    public Path getCatalogPath() {
        return this.getRef().catalogPath;
    }

    public void addDTD(ResourceLocation dtd) throws BuildException {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.getElements().addElement(dtd);
        this.setChecked(false);
    }

    public void addEntity(ResourceLocation entity) throws BuildException {
        this.addDTD(entity);
    }

    public void addConfiguredXMLCatalog(XMLCatalog catalog) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.getElements().addAll(catalog.getElements());
        Path nestedClasspath = catalog.getClasspath();
        this.createClasspath().append(nestedClasspath);
        Path nestedCatalogPath = catalog.getCatalogPath();
        this.createCatalogPath().append(nestedCatalogPath);
        this.setChecked(false);
    }

    @Override
    public void setRefid(Reference r) throws BuildException {
        if (!this.elements.isEmpty()) {
            throw this.tooManyAttributes();
        }
        super.setRefid(r);
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        if (this.isReference()) {
            return this.getRef().resolveEntity(publicId, systemId);
        }
        this.dieOnCircularReference();
        this.log("resolveEntity: '" + publicId + "': '" + systemId + "'", 4);
        InputSource inputSource = this.getCatalogResolver().resolveEntity(publicId, systemId);
        if (inputSource == null) {
            this.log("No matching catalog entry found, parser will use: '" + systemId + "'", 4);
        }
        return inputSource;
    }

    @Override
    public Source resolve(String href, String base) throws TransformerException {
        if (this.isReference()) {
            return this.getRef().resolve(href, base);
        }
        this.dieOnCircularReference();
        SAXSource source = null;
        String uri = this.removeFragment(href);
        this.log("resolve: '" + uri + "' with base: '" + base + "'", 4);
        source = (SAXSource)this.getCatalogResolver().resolve(uri, base);
        if (source == null) {
            this.log("No matching catalog entry found, parser will use: '" + href + "'", 4);
            source = new SAXSource();
            try {
                URL baseURL = base == null ? FILE_UTILS.getFileURL(this.getProject().getBaseDir()) : new URL(base);
                URL url = uri.isEmpty() ? baseURL : new URL(baseURL, uri);
                source.setInputSource(new InputSource(url.toString()));
            }
            catch (MalformedURLException ex) {
                source.setInputSource(new InputSource(uri));
            }
        }
        this.setEntityResolver(source);
        return source;
    }

    @Override
    protected synchronized void dieOnCircularReference(Stack<Object> stk, Project p) throws BuildException {
        if (this.isChecked()) {
            return;
        }
        if (this.isReference()) {
            super.dieOnCircularReference(stk, p);
        } else {
            if (this.classpath != null) {
                XMLCatalog.pushAndInvokeCircularReferenceCheck(this.classpath, stk, p);
            }
            if (this.catalogPath != null) {
                XMLCatalog.pushAndInvokeCircularReferenceCheck(this.catalogPath, stk, p);
            }
            this.setChecked(true);
        }
    }

    private XMLCatalog getRef() {
        if (!this.isReference()) {
            return this;
        }
        return this.getCheckedRef(XMLCatalog.class);
    }

    private CatalogResolver getCatalogResolver() {
        if (this.catalogResolver == null) {
            AntClassLoader loader = this.getProject().createClassLoader(Path.systemClasspath);
            try {
                Class<?> clazz = Class.forName(APACHE_RESOLVER, true, loader);
                ClassLoader apacheResolverLoader = clazz.getClassLoader();
                Class<?> baseResolverClass = Class.forName(CATALOG_RESOLVER, true, apacheResolverLoader);
                ClassLoader baseResolverLoader = baseResolverClass.getClassLoader();
                clazz = Class.forName(APACHE_RESOLVER, true, baseResolverLoader);
                Object obj = clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                this.catalogResolver = new ExternalResolver(clazz, obj);
            }
            catch (Throwable ex) {
                this.catalogResolver = new InternalResolver();
                if (this.getCatalogPath() != null && this.getCatalogPath().list().length != 0) {
                    this.log("Warning: XML resolver not found; external catalogs will be ignored", 1);
                }
                this.log("Failed to load Apache resolver: " + ex, 4);
            }
        }
        return this.catalogResolver;
    }

    private void setEntityResolver(SAXSource source) throws TransformerException {
        XMLReader reader = source.getXMLReader();
        if (reader == null) {
            SAXParserFactory spFactory = SAXParserFactory.newInstance();
            spFactory.setNamespaceAware(true);
            try {
                reader = spFactory.newSAXParser().getXMLReader();
            }
            catch (ParserConfigurationException | SAXException ex) {
                throw new TransformerException(ex);
            }
        }
        reader.setEntityResolver(this);
        source.setXMLReader(reader);
    }

    private ResourceLocation findMatchingEntry(String publicId) {
        return this.getElements().stream().filter(e -> e.getPublicId().equals(publicId)).findFirst().orElse(null);
    }

    private String removeFragment(String uri) {
        String result = uri;
        int hashPos = uri.indexOf("#");
        if (hashPos >= 0) {
            result = uri.substring(0, hashPos);
        }
        return result;
    }

    private InputSource filesystemLookup(ResourceLocation matchingEntry) {
        String fileName;
        URL baseURL;
        String uri = matchingEntry.getLocation();
        uri = uri.replace(File.separatorChar, '/');
        if (matchingEntry.getBase() != null) {
            baseURL = matchingEntry.getBase();
        } else {
            try {
                baseURL = FILE_UTILS.getFileURL(this.getProject().getBaseDir());
            }
            catch (MalformedURLException ex) {
                throw new BuildException("Project basedir cannot be converted to a URL");
            }
        }
        URL url = null;
        try {
            url = new URL(baseURL, uri);
        }
        catch (MalformedURLException ex) {
            File testFile = new File(uri);
            if (testFile.exists() && testFile.canRead()) {
                this.log("uri : '" + uri + "' matches a readable file", 4);
                try {
                    url = FILE_UTILS.getFileURL(testFile);
                }
                catch (MalformedURLException ex1) {
                    throw new BuildException("could not find an URL for :" + testFile.getAbsolutePath());
                }
            }
            this.log("uri : '" + uri + "' does not match a readable file", 4);
        }
        InputSource source = null;
        if (url != null && "file".equals(url.getProtocol()) && (fileName = FILE_UTILS.fromURI(url.toString())) != null) {
            this.log("fileName " + fileName, 4);
            File resFile = new File(fileName);
            if (resFile.exists() && resFile.canRead()) {
                try {
                    source = new InputSource(Files.newInputStream(resFile.toPath(), new OpenOption[0]));
                    String sysid = JAXPUtils.getSystemId(resFile);
                    source.setSystemId(sysid);
                    this.log("catalog entry matched a readable file: '" + sysid + "'", 4);
                }
                catch (IOException iOException) {
                    // empty catch block
                }
            }
        }
        return source;
    }

    private InputSource classpathLookup(ResourceLocation matchingEntry) {
        InputSource source = null;
        Path cp = this.classpath;
        cp = cp != null ? this.classpath.concatSystemClasspath("ignore") : new Path(this.getProject()).concatSystemClasspath("last");
        AntClassLoader loader = this.getProject().createClassLoader(cp);
        InputStream is = loader.getResourceAsStream(matchingEntry.getLocation());
        if (is != null) {
            source = new InputSource(is);
            URL entryURL = loader.getResource(matchingEntry.getLocation());
            String sysid = entryURL.toExternalForm();
            source.setSystemId(sysid);
            this.log("catalog entry matched a resource in the classpath: '" + sysid + "'", 4);
        }
        return source;
    }

    private InputSource urlLookup(ResourceLocation matchingEntry) {
        URL url;
        URL baseURL;
        String uri = matchingEntry.getLocation();
        if (matchingEntry.getBase() != null) {
            baseURL = matchingEntry.getBase();
        } else {
            try {
                baseURL = FILE_UTILS.getFileURL(this.getProject().getBaseDir());
            }
            catch (MalformedURLException ex) {
                throw new BuildException("Project basedir cannot be converted to a URL");
            }
        }
        try {
            url = new URL(baseURL, uri);
        }
        catch (MalformedURLException ex) {
            url = null;
        }
        InputSource source = null;
        if (url != null) {
            try {
                InputStream is = null;
                URLConnection conn = url.openConnection();
                if (conn != null) {
                    conn.setUseCaches(false);
                    is = conn.getInputStream();
                }
                if (is != null) {
                    source = new InputSource(is);
                    String sysid = url.toExternalForm();
                    source.setSystemId(sysid);
                    this.log("catalog entry matched as a URL: '" + sysid + "'", 4);
                }
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        return source;
    }

    private static interface CatalogResolver
    extends URIResolver,
    EntityResolver {
        @Override
        public InputSource resolveEntity(String var1, String var2);
    }

    private class ExternalResolver
    implements CatalogResolver {
        private Method setXMLCatalog = null;
        private Method parseCatalog = null;
        private Method resolveEntity = null;
        private Method resolve = null;
        private Object resolverImpl = null;
        private boolean externalCatalogsProcessed = false;

        public ExternalResolver(Class<?> resolverImplClass, Object resolverImpl) {
            this.resolverImpl = resolverImpl;
            try {
                this.setXMLCatalog = resolverImplClass.getMethod("setXMLCatalog", XMLCatalog.class);
                this.parseCatalog = resolverImplClass.getMethod("parseCatalog", String.class);
                this.resolveEntity = resolverImplClass.getMethod("resolveEntity", String.class, String.class);
                this.resolve = resolverImplClass.getMethod("resolve", String.class, String.class);
            }
            catch (NoSuchMethodException ex) {
                throw new BuildException(ex);
            }
            XMLCatalog.this.log("Apache resolver library found, xml-commons resolver will be used", 3);
        }

        @Override
        public InputSource resolveEntity(String publicId, String systemId) {
            InputSource result;
            this.processExternalCatalogs();
            ResourceLocation matchingEntry = XMLCatalog.this.findMatchingEntry(publicId);
            if (matchingEntry != null) {
                XMLCatalog.this.log("Matching catalog entry found for publicId: '" + matchingEntry.getPublicId() + "' location: '" + matchingEntry.getLocation() + "'", 4);
                result = XMLCatalog.this.filesystemLookup(matchingEntry);
                if (result == null) {
                    result = XMLCatalog.this.classpathLookup(matchingEntry);
                }
                if (result == null) {
                    try {
                        result = (InputSource)this.resolveEntity.invoke(this.resolverImpl, publicId, systemId);
                    }
                    catch (Exception ex) {
                        throw new BuildException(ex);
                    }
                }
            } else {
                try {
                    result = (InputSource)this.resolveEntity.invoke(this.resolverImpl, publicId, systemId);
                }
                catch (Exception ex) {
                    throw new BuildException(ex);
                }
            }
            return result;
        }

        @Override
        public Source resolve(String href, String base) throws TransformerException {
            SAXSource result;
            this.processExternalCatalogs();
            ResourceLocation matchingEntry = XMLCatalog.this.findMatchingEntry(href);
            if (matchingEntry != null) {
                XMLCatalog.this.log("Matching catalog entry found for uri: '" + matchingEntry.getPublicId() + "' location: '" + matchingEntry.getLocation() + "'", 4);
                ResourceLocation entryCopy = matchingEntry;
                if (base != null) {
                    try {
                        URL baseURL = new URL(base);
                        entryCopy = new ResourceLocation();
                        entryCopy.setBase(baseURL);
                    }
                    catch (MalformedURLException baseURL) {
                        // empty catch block
                    }
                }
                entryCopy.setPublicId(matchingEntry.getPublicId());
                entryCopy.setLocation(matchingEntry.getLocation());
                InputSource source = XMLCatalog.this.filesystemLookup(entryCopy);
                if (source == null) {
                    source = XMLCatalog.this.classpathLookup(entryCopy);
                }
                if (source != null) {
                    result = new SAXSource(source);
                } else {
                    try {
                        result = (SAXSource)this.resolve.invoke(this.resolverImpl, href, base);
                    }
                    catch (Exception ex) {
                        throw new BuildException(ex);
                    }
                }
            } else {
                if (base == null) {
                    try {
                        base = FILE_UTILS.getFileURL(XMLCatalog.this.getProject().getBaseDir()).toString();
                    }
                    catch (MalformedURLException x) {
                        throw new TransformerException(x);
                    }
                }
                try {
                    result = (SAXSource)this.resolve.invoke(this.resolverImpl, href, base);
                }
                catch (Exception ex) {
                    throw new BuildException(ex);
                }
            }
            return result;
        }

        private void processExternalCatalogs() {
            if (!this.externalCatalogsProcessed) {
                try {
                    this.setXMLCatalog.invoke(this.resolverImpl, XMLCatalog.this);
                }
                catch (Exception ex) {
                    throw new BuildException(ex);
                }
                Path catPath = XMLCatalog.this.getCatalogPath();
                if (catPath != null) {
                    XMLCatalog.this.log("Using catalogpath '" + XMLCatalog.this.getCatalogPath() + "'", 4);
                    for (String catFileName : XMLCatalog.this.getCatalogPath().list()) {
                        File catFile = new File(catFileName);
                        XMLCatalog.this.log("Parsing " + catFile, 4);
                        try {
                            this.parseCatalog.invoke(this.resolverImpl, catFile.getPath());
                        }
                        catch (Exception ex) {
                            throw new BuildException(ex);
                        }
                    }
                }
            }
            this.externalCatalogsProcessed = true;
        }
    }

    private class InternalResolver
    implements CatalogResolver {
        public InternalResolver() {
            XMLCatalog.this.log("Apache resolver library not found, internal resolver will be used", 3);
        }

        @Override
        public InputSource resolveEntity(String publicId, String systemId) {
            InputSource result = null;
            ResourceLocation matchingEntry = XMLCatalog.this.findMatchingEntry(publicId);
            if (matchingEntry != null) {
                XMLCatalog.this.log("Matching catalog entry found for publicId: '" + matchingEntry.getPublicId() + "' location: '" + matchingEntry.getLocation() + "'", 4);
                result = XMLCatalog.this.filesystemLookup(matchingEntry);
                if (result == null) {
                    result = XMLCatalog.this.classpathLookup(matchingEntry);
                }
                if (result == null) {
                    result = XMLCatalog.this.urlLookup(matchingEntry);
                }
            }
            return result;
        }

        @Override
        public Source resolve(String href, String base) throws TransformerException {
            SAXSource result = null;
            InputSource source = null;
            ResourceLocation matchingEntry = XMLCatalog.this.findMatchingEntry(href);
            if (matchingEntry != null) {
                XMLCatalog.this.log("Matching catalog entry found for uri: '" + matchingEntry.getPublicId() + "' location: '" + matchingEntry.getLocation() + "'", 4);
                ResourceLocation entryCopy = matchingEntry;
                if (base != null) {
                    try {
                        URL baseURL = new URL(base);
                        entryCopy = new ResourceLocation();
                        entryCopy.setBase(baseURL);
                    }
                    catch (MalformedURLException malformedURLException) {
                        // empty catch block
                    }
                }
                entryCopy.setPublicId(matchingEntry.getPublicId());
                entryCopy.setLocation(matchingEntry.getLocation());
                source = XMLCatalog.this.filesystemLookup(entryCopy);
                if (source == null) {
                    source = XMLCatalog.this.classpathLookup(entryCopy);
                }
                if (source == null) {
                    source = XMLCatalog.this.urlLookup(entryCopy);
                }
                if (source != null) {
                    result = new SAXSource(source);
                }
            }
            return result;
        }
    }
}

