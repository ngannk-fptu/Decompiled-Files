/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.digester.Digester
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.authenticator.jaspic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.res.StringManager;
import org.xml.sax.SAXException;

public final class PersistentProviderRegistrations {
    private static final Log log = LogFactory.getLog(PersistentProviderRegistrations.class);
    private static final StringManager sm = StringManager.getManager(PersistentProviderRegistrations.class);

    private PersistentProviderRegistrations() {
    }

    static Providers loadProviders(File configFile) {
        Providers providers;
        FileInputStream is = new FileInputStream(configFile);
        try {
            Digester digester = new Digester();
            try {
                digester.setFeature("http://apache.org/xml/features/allow-java-encodings", true);
            }
            catch (SAXException se) {
                log.warn((Object)sm.getString("persistentProviderRegistrations.xmlFeatureEncoding"), (Throwable)se);
            }
            digester.setValidating(true);
            digester.setNamespaceAware(true);
            Providers result = new Providers();
            digester.push((Object)result);
            digester.addObjectCreate("jaspic-providers/provider", Provider.class.getName());
            digester.addSetProperties("jaspic-providers/provider");
            digester.addSetNext("jaspic-providers/provider", "addProvider", Provider.class.getName());
            digester.addObjectCreate("jaspic-providers/provider/property", Property.class.getName());
            digester.addSetProperties("jaspic-providers/provider/property");
            digester.addSetNext("jaspic-providers/provider/property", "addProperty", Property.class.getName());
            digester.parse((InputStream)is);
            providers = result;
        }
        catch (Throwable throwable) {
            try {
                try {
                    ((InputStream)is).close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (IOException | ParserConfigurationException | SAXException e) {
                throw new SecurityException(e);
            }
        }
        ((InputStream)is).close();
        return providers;
    }

    static void writeProviders(Providers providers, File configFile) {
        File configFileOld = new File(configFile.getAbsolutePath() + ".old");
        File configFileNew = new File(configFile.getAbsolutePath() + ".new");
        if (configFileOld.exists() && configFileOld.delete()) {
            throw new SecurityException(sm.getString("persistentProviderRegistrations.existsDeleteFail", new Object[]{configFileOld.getAbsolutePath()}));
        }
        if (configFileNew.exists() && configFileNew.delete()) {
            throw new SecurityException(sm.getString("persistentProviderRegistrations.existsDeleteFail", new Object[]{configFileNew.getAbsolutePath()}));
        }
        try (FileOutputStream fos = new FileOutputStream(configFileNew);
             OutputStreamWriter writer = new OutputStreamWriter((OutputStream)fos, StandardCharsets.UTF_8);){
            writer.write("<?xml version='1.0' encoding='utf-8'?>\n<jaspic-providers\n    xmlns=\"http://tomcat.apache.org/xml\"\n    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n    xsi:schemaLocation=\"http://tomcat.apache.org/xml jaspic-providers.xsd\"\n    version=\"1.0\">\n");
            for (Provider provider : providers.providers) {
                writer.write("  <provider");
                PersistentProviderRegistrations.writeOptional("className", provider.getClassName(), writer);
                PersistentProviderRegistrations.writeOptional("layer", provider.getLayer(), writer);
                PersistentProviderRegistrations.writeOptional("appContext", provider.getAppContext(), writer);
                PersistentProviderRegistrations.writeOptional("description", provider.getDescription(), writer);
                writer.write(">\n");
                for (Map.Entry<String, String> entry : provider.getProperties().entrySet()) {
                    writer.write("    <property name=\"");
                    writer.write(entry.getKey());
                    writer.write("\" value=\"");
                    writer.write(entry.getValue());
                    writer.write("\"/>\n");
                }
                writer.write("  </provider>\n");
            }
            writer.write("</jaspic-providers>\n");
        }
        catch (IOException e) {
            if (!configFileNew.delete()) {
                Log log = LogFactory.getLog(PersistentProviderRegistrations.class);
                log.warn((Object)sm.getString("persistentProviderRegistrations.deleteFail", new Object[]{configFileNew.getAbsolutePath()}));
            }
            throw new SecurityException(e);
        }
        if (configFile.isFile() && !configFile.renameTo(configFileOld)) {
            throw new SecurityException(sm.getString("persistentProviderRegistrations.moveFail", new Object[]{configFile.getAbsolutePath(), configFileOld.getAbsolutePath()}));
        }
        if (!configFileNew.renameTo(configFile)) {
            throw new SecurityException(sm.getString("persistentProviderRegistrations.moveFail", new Object[]{configFileNew.getAbsolutePath(), configFile.getAbsolutePath()}));
        }
        if (configFileOld.exists() && !configFileOld.delete()) {
            Log log = LogFactory.getLog(PersistentProviderRegistrations.class);
            log.warn((Object)sm.getString("persistentProviderRegistrations.deleteFail", new Object[]{configFileOld.getAbsolutePath()}));
        }
    }

    private static void writeOptional(String name, String value, Writer writer) throws IOException {
        if (value != null) {
            writer.write(" " + name + "=\"");
            writer.write(value);
            writer.write("\"");
        }
    }

    public static class Providers {
        private final List<Provider> providers = new ArrayList<Provider>();

        public void addProvider(Provider provider) {
            this.providers.add(provider);
        }

        public List<Provider> getProviders() {
            return this.providers;
        }
    }

    public static class Provider {
        private String className;
        private String layer;
        private String appContext;
        private String description;
        private final Map<String, String> properties = new HashMap<String, String>();

        public String getClassName() {
            return this.className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getLayer() {
            return this.layer;
        }

        public void setLayer(String layer) {
            this.layer = layer;
        }

        public String getAppContext() {
            return this.appContext;
        }

        public void setAppContext(String appContext) {
            this.appContext = appContext;
        }

        public String getDescription() {
            return this.description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void addProperty(Property property) {
            this.properties.put(property.getName(), property.getValue());
        }

        public void setProperty(String name, String value) {
            this.addProperty(name, value);
        }

        void addProperty(String name, String value) {
            this.properties.put(name, value);
        }

        public Map<String, String> getProperties() {
            return this.properties;
        }
    }

    public static class Property {
        private String name;
        private String value;

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return this.value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}

