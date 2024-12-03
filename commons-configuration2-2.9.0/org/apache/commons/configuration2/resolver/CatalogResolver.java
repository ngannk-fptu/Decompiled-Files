/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xml.resolver.Catalog
 *  org.apache.xml.resolver.CatalogException
 *  org.apache.xml.resolver.CatalogManager
 *  org.apache.xml.resolver.readers.CatalogReader
 *  org.apache.xml.resolver.tools.CatalogResolver
 */
package org.apache.commons.configuration2.resolver;

import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.interpol.ConfigurationInterpolator;
import org.apache.commons.configuration2.io.ConfigurationLogger;
import org.apache.commons.configuration2.io.FileLocatorUtils;
import org.apache.commons.configuration2.io.FileSystem;
import org.apache.xml.resolver.CatalogException;
import org.apache.xml.resolver.readers.CatalogReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class CatalogResolver
implements EntityResolver {
    private static final int DEBUG_ALL = 9;
    private static final int DEBUG_NORMAL = 4;
    private static final int DEBUG_NONE = 0;
    private final CatalogManager manager = new CatalogManager();
    private FileSystem fs = FileLocatorUtils.DEFAULT_FILE_SYSTEM;
    private org.apache.xml.resolver.tools.CatalogResolver resolver;
    private ConfigurationLogger log;

    public CatalogResolver() {
        this.manager.setIgnoreMissingProperties(true);
        this.manager.setUseStaticCatalog(false);
        this.manager.setFileSystem(this.fs);
        this.initLogger(null);
    }

    public void setCatalogFiles(String catalogs) {
        this.manager.setCatalogFiles(catalogs);
    }

    public void setFileSystem(FileSystem fileSystem) {
        this.fs = fileSystem;
        this.manager.setFileSystem(fileSystem);
    }

    public void setBaseDir(String baseDir) {
        this.manager.setBaseDir(baseDir);
    }

    public void setInterpolator(ConfigurationInterpolator ci) {
        this.manager.setInterpolator(ci);
    }

    public void setDebug(boolean debug) {
        this.manager.setVerbosity(debug ? 9 : 0);
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
        String resolved = this.getResolver().getResolvedEntity(publicId, systemId);
        if (resolved != null) {
            String badFilePrefix = "file://";
            String correctFilePrefix = "file:///";
            if (resolved.startsWith("file://") && !resolved.startsWith("file:///")) {
                resolved = "file:///" + resolved.substring("file://".length());
            }
            try {
                URL url = CatalogResolver.locate(this.fs, null, resolved);
                if (url == null) {
                    throw new ConfigurationException("Could not locate " + resolved);
                }
                InputStream inputStream = this.fs.getInputStream(url);
                InputSource inputSource = new InputSource(resolved);
                inputSource.setPublicId(publicId);
                inputSource.setByteStream(inputStream);
                return inputSource;
            }
            catch (Exception e) {
                this.log.warn("Failed to create InputSource for " + resolved, e);
            }
        }
        return null;
    }

    public ConfigurationLogger getLogger() {
        return this.log;
    }

    public void setLogger(ConfigurationLogger log) {
        this.initLogger(log);
    }

    private void initLogger(ConfigurationLogger log) {
        this.log = log != null ? log : ConfigurationLogger.newDummyLogger();
    }

    private synchronized org.apache.xml.resolver.tools.CatalogResolver getResolver() {
        if (this.resolver == null) {
            this.resolver = new org.apache.xml.resolver.tools.CatalogResolver((org.apache.xml.resolver.CatalogManager)this.manager);
        }
        return this.resolver;
    }

    private static URL locate(FileSystem fs, String basePath, String name) {
        return FileLocatorUtils.locate(FileLocatorUtils.fileLocator().fileSystem(fs).basePath(basePath).fileName(name).create());
    }

    public static class Catalog
    extends org.apache.xml.resolver.Catalog {
        private FileSystem fs;
        private final FileNameMap fileNameMap = URLConnection.getFileNameMap();

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void loadSystemCatalogs() throws IOException {
            this.fs = ((CatalogManager)this.catalogManager).getFileSystem();
            String base = ((CatalogManager)this.catalogManager).getBaseDir();
            Vector catalogs = this.catalogManager.getCatalogFiles();
            if (catalogs != null) {
                for (int count = 0; count < catalogs.size(); ++count) {
                    String fileName = (String)catalogs.elementAt(count);
                    URL url = null;
                    InputStream inputStream = null;
                    try {
                        url = CatalogResolver.locate(this.fs, base, fileName);
                        if (url != null) {
                            inputStream = this.fs.getInputStream(url);
                        }
                    }
                    catch (ConfigurationException ce) {
                        String name = url.toString();
                        this.catalogManager.debug.message(9, "Unable to get input stream for " + name + ". " + ce.getMessage());
                    }
                    if (inputStream != null) {
                        String mimeType = this.fileNameMap.getContentTypeFor(fileName);
                        try {
                            if (mimeType != null) {
                                this.parseCatalog(mimeType, inputStream);
                                continue;
                            }
                        }
                        catch (Exception ex) {
                            this.catalogManager.debug.message(9, "Exception caught parsing input stream for " + fileName + ". " + ex.getMessage());
                        }
                        finally {
                            inputStream.close();
                        }
                    }
                    this.parseCatalog(base, fileName);
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void parseCatalog(String baseDir, String fileName) throws IOException {
            this.catalogCwd = this.base = CatalogResolver.locate(this.fs, baseDir, fileName);
            this.default_override = this.catalogManager.getPreferPublic();
            this.catalogManager.debug.message(4, "Parse catalog: " + fileName);
            boolean parsed = false;
            for (int count = 0; !parsed && count < this.readerArr.size(); ++count) {
                InputStream inputStream;
                CatalogReader reader = (CatalogReader)this.readerArr.get(count);
                try {
                    inputStream = this.fs.getInputStream(this.base);
                }
                catch (Exception ex) {
                    this.catalogManager.debug.message(4, "Unable to access " + this.base + ex.getMessage());
                    break;
                }
                try {
                    reader.readCatalog((org.apache.xml.resolver.Catalog)this, inputStream);
                    parsed = true;
                    continue;
                }
                catch (CatalogException ce) {
                    this.catalogManager.debug.message(4, "Parse failed for " + fileName + ce.getMessage());
                    if (ce.getExceptionType() != 7) continue;
                    break;
                }
                finally {
                    try {
                        inputStream.close();
                    }
                    catch (IOException ioe) {
                        inputStream = null;
                    }
                }
            }
            if (parsed) {
                this.parsePendingCatalogs();
            }
        }

        protected String normalizeURI(String uriref) {
            ConfigurationInterpolator ci = ((CatalogManager)this.catalogManager).getInterpolator();
            String resolved = ci != null ? String.valueOf(ci.interpolate(uriref)) : uriref;
            return super.normalizeURI(resolved);
        }
    }

    public static class CatalogManager
    extends org.apache.xml.resolver.CatalogManager {
        private static org.apache.xml.resolver.Catalog staticCatalog;
        private FileSystem fs;
        private String baseDir = System.getProperty("user.dir");
        private ConfigurationInterpolator interpolator;

        public void setFileSystem(FileSystem fileSystem) {
            this.fs = fileSystem;
        }

        public FileSystem getFileSystem() {
            return this.fs;
        }

        public void setBaseDir(String baseDir) {
            if (baseDir != null) {
                this.baseDir = baseDir;
            }
        }

        public String getBaseDir() {
            return this.baseDir;
        }

        public void setInterpolator(ConfigurationInterpolator configurationInterpolator) {
            this.interpolator = configurationInterpolator;
        }

        public ConfigurationInterpolator getInterpolator() {
            return this.interpolator;
        }

        public org.apache.xml.resolver.Catalog getPrivateCatalog() {
            org.apache.xml.resolver.Catalog catalog = staticCatalog;
            if (catalog == null || !this.getUseStaticCatalog()) {
                try {
                    catalog = new Catalog();
                    catalog.setCatalogManager((org.apache.xml.resolver.CatalogManager)this);
                    catalog.setupReaders();
                    catalog.loadSystemCatalogs();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
                if (this.getUseStaticCatalog()) {
                    staticCatalog = catalog;
                }
            }
            return catalog;
        }

        public org.apache.xml.resolver.Catalog getCatalog() {
            return this.getPrivateCatalog();
        }
    }
}

