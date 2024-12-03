/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.digester.Digester
 *  org.apache.tomcat.util.digester.Rule
 *  org.apache.tomcat.util.file.ConfigFileLoader
 *  org.apache.tomcat.util.file.ConfigurationSource$Resource
 */
package org.apache.catalina.storeconfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.apache.catalina.storeconfig.StoreFactoryRule;
import org.apache.catalina.storeconfig.StoreRegistry;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.digester.Rule;
import org.apache.tomcat.util.file.ConfigFileLoader;
import org.apache.tomcat.util.file.ConfigurationSource;

public class StoreLoader {
    protected static final Digester digester = StoreLoader.createDigester();
    private StoreRegistry registry;
    private URL registryResource;

    public StoreRegistry getRegistry() {
        return this.registry;
    }

    public void setRegistry(StoreRegistry registry) {
        this.registry = registry;
    }

    protected static Digester createDigester() {
        Digester digester = new Digester();
        digester.setValidating(false);
        digester.setClassLoader(StoreRegistry.class.getClassLoader());
        digester.addObjectCreate("Registry", "org.apache.catalina.storeconfig.StoreRegistry", "className");
        digester.addSetProperties("Registry");
        digester.addObjectCreate("Registry/Description", "org.apache.catalina.storeconfig.StoreDescription", "className");
        digester.addSetProperties("Registry/Description");
        digester.addRule("Registry/Description", (Rule)new StoreFactoryRule("org.apache.catalina.storeconfig.StoreFactoryBase", "storeFactoryClass", "org.apache.catalina.storeconfig.StoreAppender", "storeAppenderClass"));
        digester.addSetNext("Registry/Description", "registerDescription", "org.apache.catalina.storeconfig.StoreDescription");
        digester.addCallMethod("Registry/Description/TransientAttribute", "addTransientAttribute", 0);
        digester.addCallMethod("Registry/Description/TransientChild", "addTransientChild", 0);
        return digester;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void load(String path) throws Exception {
        try (ConfigurationSource.Resource resource = path == null ? ConfigFileLoader.getSource().getConfResource("server-registry.xml") : ConfigFileLoader.getSource().getResource(path);
             InputStream is = resource.getInputStream();){
            this.registryResource = resource.getURI().toURL();
            Digester digester = StoreLoader.digester;
            synchronized (digester) {
                this.registry = (StoreRegistry)StoreLoader.digester.parse(is);
            }
        }
        catch (IOException e) {
            try (InputStream is2 = StoreLoader.class.getResourceAsStream("/org/apache/catalina/storeconfig/server-registry.xml");){
                if (is2 != null) {
                    this.registryResource = StoreLoader.class.getResource("/org/apache/catalina/storeconfig/server-registry.xml");
                    Digester digester = StoreLoader.digester;
                    synchronized (digester) {
                        this.registry = (StoreRegistry)StoreLoader.digester.parse(is2);
                    }
                }
                throw e;
            }
        }
    }

    public URL getRegistryResource() {
        return this.registryResource;
    }
}

