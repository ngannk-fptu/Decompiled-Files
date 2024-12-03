/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text;

import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.text.MessageFormat;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

final class VersionBean {
    public static final Version VERSION = new Version();

    VersionBean() {
    }

    void setManifest(Manifest manifest) {
        VersionBean.VERSION.processManifest(manifest);
    }

    public String getVendor() {
        return VERSION.getImplementationVendor();
    }

    public String getTitle() {
        return VERSION.getImplementationTitle();
    }

    public String getTimestamp() {
        return VERSION.getScmTimestamp();
    }

    public Version getVersion() {
        return VERSION;
    }

    public String toString() {
        return VERSION.toString();
    }

    public static class Version {
        private static final String UNKNOWN = "";
        private String implementationVendor = "";
        private String implementationVersion = "1.0.0";
        private String bundleVersion = "1.0.0";
        private String implementationTitle = "";
        private String scmTimestamp = "";
        private String fullVersionString = "";
        private boolean containsDataFromManifest = false;

        public Version() {
            this.initialize();
        }

        private String getAttributeValueOrDefault(Attributes attributes, String name) {
            String value = attributes.getValue(name);
            if (value == null) {
                value = UNKNOWN;
            }
            return value;
        }

        private void initialize() {
            try {
                Manifest manifest = this.readManifest();
                this.processManifest(manifest);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }

        private void processManifest(Manifest manifest) {
            if (manifest != null) {
                this.initializePropertiesFromManifest(manifest);
                this.initializeDerivativeProperties();
            }
        }

        private void initializePropertiesFromManifest(Manifest manifest) {
            this.containsDataFromManifest = true;
            Attributes attributes = manifest.getMainAttributes();
            this.implementationVendor = this.getAttributeValueOrDefault(attributes, "Implementation-Vendor");
            this.implementationVersion = this.getAttributeValueOrDefault(attributes, "Implementation-Version");
            this.bundleVersion = this.getAttributeValueOrDefault(attributes, "Bundle-Version");
            this.implementationTitle = this.getAttributeValueOrDefault(attributes, "Implementation-Title");
            this.scmTimestamp = this.getAttributeValueOrDefault(attributes, "SCM-Timestamp");
            if (this.isEmpty(this.implementationVersion) && !this.isEmpty(this.bundleVersion)) {
                this.implementationVersion = this.bundleVersion;
            }
        }

        private boolean isEmpty(String value) {
            return value == null || value.isEmpty();
        }

        private void initializeDerivativeProperties() {
            this.fullVersionString = MessageFormat.format("{0}", this.implementationVersion);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private Manifest readManifest() {
            URL url;
            CodeSource codeSource;
            ProtectionDomain domain = VersionBean.class.getProtectionDomain();
            if (domain != null && (codeSource = domain.getCodeSource()) != null && (url = codeSource.getLocation()) != null) {
                InputStream manifestStream = null;
                try {
                    URL manifestFileUrl;
                    Object manifestFile;
                    if ("vfs".equals(url.getProtocol())) {
                        manifestFile = String.format("%s/%s", url.toExternalForm(), "META-INF/MANIFEST.MF");
                        manifestFileUrl = new URL((String)manifestFile);
                    } else {
                        manifestFileUrl = new URL(url, "META-INF/MANIFEST.MF");
                    }
                    manifestStream = Version.urlToStream(manifestFileUrl);
                    manifestFile = new Manifest(manifestStream);
                    return manifestFile;
                }
                catch (IOException manifestFileUrl) {
                }
                finally {
                    if (manifestStream != null) {
                        try {
                            manifestStream.close();
                        }
                        catch (IOException iOException) {}
                    }
                }
                try {
                    Manifest manifest;
                    URLConnection urlConnection = url.openConnection();
                    urlConnection.setUseCaches(false);
                    if (urlConnection instanceof JarURLConnection) {
                        JarURLConnection jarUrlConnection = (JarURLConnection)urlConnection;
                        return jarUrlConnection.getManifest();
                    }
                    try (JarInputStream jis = new JarInputStream(urlConnection.getInputStream());){
                        manifest = jis.getManifest();
                    }
                    return manifest;
                }
                catch (IOException iOException) {
                    // empty catch block
                }
            }
            return null;
        }

        private static InputStream urlToStream(URL url) throws IOException {
            if (url != null) {
                URLConnection connection = url.openConnection();
                try {
                    connection.setUseCaches(false);
                }
                catch (IllegalArgumentException illegalArgumentException) {
                    // empty catch block
                }
                return connection.getInputStream();
            }
            return null;
        }

        private boolean containsDataFromManifest() {
            return this.containsDataFromManifest;
        }

        public String getVersion() {
            return this.fullVersionString;
        }

        public String getImplementationTitle() {
            return this.implementationTitle;
        }

        public String getImplementationVendor() {
            return this.implementationVendor;
        }

        public String getImplementationVersion() {
            return this.implementationVersion;
        }

        public String getScmTimestamp() {
            return this.scmTimestamp;
        }

        public String toString() {
            if (this.containsDataFromManifest()) {
                return this.getImplementationTitle() + " by " + this.getImplementationVendor() + ", version " + this.getVersion();
            }
            return this.getVersion();
        }
    }
}

