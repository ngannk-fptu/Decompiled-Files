/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.providers.XmlDocConfigurationProvider;
import com.opensymphony.xwork2.config.providers.XmlHelper;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.ClassPathFinder;
import com.opensymphony.xwork2.util.DomHelper;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Vector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

public abstract class XmlConfigurationProvider
extends XmlDocConfigurationProvider {
    private static final Logger LOG = LogManager.getLogger(XmlConfigurationProvider.class);
    private final String configFileName;
    private final Set<String> loadedFileUrls = new HashSet<String>();
    private Set<String> includedFileNames;
    protected FileManager fileManager;

    @Inject
    public void setFileManagerFactory(FileManagerFactory fileManagerFactory) {
        this.fileManager = fileManagerFactory.getFileManager();
    }

    public XmlConfigurationProvider() {
        this("struts.xml");
    }

    public XmlConfigurationProvider(String filename) {
        super(new Document[0]);
        this.configFileName = filename;
    }

    @Deprecated
    public XmlConfigurationProvider(String filename, @Deprecated boolean notUsed) {
        this(filename);
    }

    @Override
    public void init(Configuration configuration) {
        super.init(configuration);
        this.includedFileNames = configuration.getLoadedFileNames();
        this.documents = this.parseFile(this.configFileName);
    }

    @Override
    public void loadPackages() throws ConfigurationException {
        super.loadPackages();
        this.documents = Collections.emptyList();
    }

    @Override
    public void register(ContainerBuilder containerBuilder, LocatableProperties props) throws ConfigurationException {
        LOG.trace("Parsing configuration file [{}]", (Object)this.configFileName);
        super.register(containerBuilder, props);
    }

    @Override
    public boolean needsReload() {
        return this.loadedFileUrls.stream().anyMatch(url -> this.fileManager.fileNeedsReloading((String)url));
    }

    protected List<Document> parseFile(String configFileName) {
        try {
            this.loadedFileUrls.clear();
            return this.loadConfigurationFiles(configFileName, null);
        }
        catch (ConfigurationException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ConfigurationException("Error loading configuration file " + configFileName, e);
        }
    }

    protected List<Document> loadConfigurationFiles(String fileName, Element includeElement) {
        if (this.includedFileNames.contains(fileName)) {
            return Collections.emptyList();
        }
        LOG.debug("Loading action configurations from: {}", (Object)fileName);
        this.includedFileNames.add(fileName);
        Iterator<URL> urls = this.getURLs(fileName);
        if (urls == null) {
            return Collections.emptyList();
        }
        List<Document> docs = this.getDocs(urls, fileName, includeElement);
        List<Document> finalDocs = this.getFinalDocs(docs);
        LOG.debug("Loaded action configuration from: {}", (Object)fileName);
        return finalDocs;
    }

    protected Iterator<URL> getURLs(String fileName) {
        Iterator<URL> urls = null;
        try {
            urls = this.getConfigurationUrls(fileName);
        }
        catch (IOException ex) {
            LOG.debug("Ignoring file that does not exist: " + fileName, (Throwable)ex);
        }
        if (urls != null && !urls.hasNext()) {
            LOG.debug("Ignoring file that has no URLs: " + fileName);
            urls = null;
        }
        return urls;
    }

    protected Iterator<URL> getConfigurationUrls(String fileName) throws IOException {
        return ClassLoaderUtil.getResources(fileName, XmlConfigurationProvider.class, false);
    }

    protected List<Document> getDocs(Iterator<URL> urls, String fileName, Element includeElement) {
        ArrayList<Document> docs = new ArrayList<Document>();
        while (urls.hasNext()) {
            InputStream is = null;
            URL url = null;
            try {
                url = urls.next();
                is = this.fileManager.loadFile(url);
                InputSource in = new InputSource(is);
                in.setSystemId(url.toString());
                Document helperDoc = DomHelper.parse(in, this.dtdMappings);
                if (helperDoc != null) {
                    docs.add(helperDoc);
                }
                this.loadedFileUrls.add(url.toString());
            }
            catch (StrutsException e) {
                if (includeElement != null) {
                    throw new ConfigurationException("Unable to load " + url, e, includeElement);
                }
                throw new ConfigurationException("Unable to load " + url, e);
            }
            catch (Exception e) {
                throw new ConfigurationException("Caught exception while loading file " + fileName, e, includeElement);
            }
            finally {
                if (is == null) continue;
                try {
                    is.close();
                }
                catch (IOException e) {
                    LOG.error("Unable to close input stream", (Throwable)e);
                }
            }
        }
        return docs;
    }

    protected List<Document> getFinalDocs(List<Document> docs) {
        ArrayList<Document> finalDocs = new ArrayList<Document>();
        docs.sort(Comparator.comparing(XmlHelper::getLoadOrder));
        for (Document doc : docs) {
            XmlConfigurationProvider.iterateElementChildren(doc, (Element child) -> {
                if (!"include".equals(child.getNodeName())) {
                    return;
                }
                String includeFileName = child.getAttribute("file");
                if (includeFileName.indexOf(42) == -1) {
                    finalDocs.addAll(this.loadConfigurationFiles(includeFileName, (Element)child));
                    return;
                }
                ClassPathFinder wildcardFinder = new ClassPathFinder();
                wildcardFinder.setPattern(includeFileName);
                Vector<String> wildcardMatches = wildcardFinder.findMatches();
                for (String match : wildcardMatches) {
                    finalDocs.addAll(this.loadConfigurationFiles(match, (Element)child));
                }
            });
            finalDocs.add(doc);
        }
        return finalDocs;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof XmlConfigurationProvider)) {
            return false;
        }
        XmlConfigurationProvider xmlConfigurationProvider = (XmlConfigurationProvider)o;
        return Objects.equals(this.configFileName, xmlConfigurationProvider.configFileName);
    }

    public int hashCode() {
        return this.configFileName != null ? this.configFileName.hashCode() : 0;
    }

    public String toString() {
        return String.format("XmlConfigurationProvider{configFileName='%s'}", this.configFileName);
    }
}

