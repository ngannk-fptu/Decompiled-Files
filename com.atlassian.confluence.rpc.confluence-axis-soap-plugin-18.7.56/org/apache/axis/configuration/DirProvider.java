/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axis.configuration;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.axis.AxisEngine;
import org.apache.axis.ConfigurationException;
import org.apache.axis.Handler;
import org.apache.axis.WSDDEngineConfiguration;
import org.apache.axis.deployment.wsdd.WSDDDeployment;
import org.apache.axis.deployment.wsdd.WSDDDocument;
import org.apache.axis.deployment.wsdd.WSDDGlobalConfiguration;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DirProvider
implements WSDDEngineConfiguration {
    protected static Log log = LogFactory.getLog((String)(class$org$apache$axis$configuration$DirProvider == null ? (class$org$apache$axis$configuration$DirProvider = DirProvider.class$("org.apache.axis.configuration.DirProvider")) : class$org$apache$axis$configuration$DirProvider).getName());
    private WSDDDeployment deployment = null;
    private String configFile;
    private File dir;
    private static final String SERVER_CONFIG_FILE = "server-config.wsdd";
    static /* synthetic */ Class class$org$apache$axis$configuration$DirProvider;

    public DirProvider(String basepath) throws ConfigurationException {
        this(basepath, SERVER_CONFIG_FILE);
    }

    public DirProvider(String basepath, String configFile) throws ConfigurationException {
        File dir = new File(basepath);
        if (!(dir.exists() && dir.isDirectory() && dir.canRead())) {
            throw new ConfigurationException(Messages.getMessage("invalidConfigFilePath", basepath));
        }
        this.dir = dir;
        this.configFile = configFile;
    }

    public WSDDDeployment getDeployment() {
        return this.deployment;
    }

    public void configureEngine(AxisEngine engine) throws ConfigurationException {
        this.deployment = new WSDDDeployment();
        WSDDGlobalConfiguration config = new WSDDGlobalConfiguration();
        config.setOptionsHashtable(new Hashtable());
        this.deployment.setGlobalConfiguration(config);
        File[] dirs = this.dir.listFiles(new DirFilter());
        for (int i = 0; i < dirs.length; ++i) {
            this.processWSDD(dirs[i]);
        }
        this.deployment.configureEngine(engine);
        engine.refreshGlobalOptions();
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void processWSDD(File dir) throws ConfigurationException {
        File file = new File(dir, this.configFile);
        if (!file.exists()) {
            return;
        }
        log.debug((Object)("Loading service configuration from file: " + file));
        FileInputStream in = null;
        try {
            try {
                in = new FileInputStream(file);
                WSDDDocument doc = new WSDDDocument(XMLUtils.newDocument(in));
                doc.deploy(this.deployment);
            }
            catch (Exception e) {
                throw new ConfigurationException(e);
            }
            Object var6_6 = null;
            if (in == null) return;
        }
        catch (Throwable throwable) {
            Object var6_7 = null;
            if (in == null) throw throwable;
            try {
                ((InputStream)in).close();
                throw throwable;
            }
            catch (IOException e) {
                // empty catch block
            }
            throw throwable;
        }
        try {}
        catch (IOException e) {}
        ((InputStream)in).close();
        return;
    }

    public void writeEngineConfig(AxisEngine engine) throws ConfigurationException {
    }

    public Handler getHandler(QName qname) throws ConfigurationException {
        return this.deployment.getHandler(qname);
    }

    public SOAPService getService(QName qname) throws ConfigurationException {
        SOAPService service = this.deployment.getService(qname);
        if (service == null) {
            throw new ConfigurationException(Messages.getMessage("noService10", qname.toString()));
        }
        return service;
    }

    public SOAPService getServiceByNamespaceURI(String namespace) throws ConfigurationException {
        return this.deployment.getServiceByNamespaceURI(namespace);
    }

    public Handler getTransport(QName qname) throws ConfigurationException {
        return this.deployment.getTransport(qname);
    }

    public TypeMappingRegistry getTypeMappingRegistry() throws ConfigurationException {
        return this.deployment.getTypeMappingRegistry();
    }

    public Handler getGlobalRequest() throws ConfigurationException {
        return this.deployment.getGlobalRequest();
    }

    public Handler getGlobalResponse() throws ConfigurationException {
        return this.deployment.getGlobalResponse();
    }

    public Hashtable getGlobalOptions() throws ConfigurationException {
        WSDDGlobalConfiguration globalConfig = this.deployment.getGlobalConfiguration();
        if (globalConfig != null) {
            return globalConfig.getParametersTable();
        }
        return null;
    }

    public Iterator getDeployedServices() throws ConfigurationException {
        return this.deployment.getDeployedServices();
    }

    public List getRoles() {
        return this.deployment.getRoles();
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    private static class DirFilter
    implements FileFilter {
        private DirFilter() {
        }

        public boolean accept(File path) {
            return path.isDirectory();
        }
    }
}

