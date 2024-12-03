/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.configuration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.axis.AxisEngine;
import org.apache.axis.ConfigurationException;
import org.apache.axis.Handler;
import org.apache.axis.WSDDEngineConfiguration;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.deployment.wsdd.WSDDDeployment;
import org.apache.axis.deployment.wsdd.WSDDDocument;
import org.apache.axis.deployment.wsdd.WSDDGlobalConfiguration;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.utils.Admin;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;
import org.w3c.dom.Document;

public class FileProvider
implements WSDDEngineConfiguration {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$configuration$FileProvider == null ? (class$org$apache$axis$configuration$FileProvider = FileProvider.class$("org.apache.axis.configuration.FileProvider")) : class$org$apache$axis$configuration$FileProvider).getName());
    private WSDDDeployment deployment = null;
    private String filename;
    private File configFile = null;
    private InputStream myInputStream = null;
    private boolean readOnly = true;
    private boolean searchClasspath = true;
    static /* synthetic */ Class class$org$apache$axis$configuration$FileProvider;

    public FileProvider(String filename) {
        this.filename = filename;
        this.configFile = new File(filename);
        this.check();
    }

    public FileProvider(String basepath, String filename) throws ConfigurationException {
        this.filename = filename;
        File dir = new File(basepath);
        if (!(dir.exists() && dir.isDirectory() && dir.canRead())) {
            throw new ConfigurationException(Messages.getMessage("invalidConfigFilePath", basepath));
        }
        this.configFile = new File(basepath, filename);
        this.check();
    }

    private void check() {
        try {
            this.readOnly = this.configFile.canRead() & !this.configFile.canWrite();
        }
        catch (SecurityException se) {
            this.readOnly = true;
        }
        if (this.readOnly) {
            log.info((Object)Messages.getMessage("readOnlyConfigFile"));
        }
    }

    public FileProvider(InputStream is) {
        this.setInputStream(is);
    }

    public void setInputStream(InputStream is) {
        this.myInputStream = is;
    }

    private InputStream getInputStream() {
        return this.myInputStream;
    }

    public WSDDDeployment getDeployment() {
        return this.deployment;
    }

    public void setDeployment(WSDDDeployment deployment) {
        this.deployment = deployment;
    }

    public void setSearchClasspath(boolean searchClasspath) {
        this.searchClasspath = searchClasspath;
    }

    public void configureEngine(AxisEngine engine) throws ConfigurationException {
        try {
            block6: {
                if (this.getInputStream() == null) {
                    try {
                        this.setInputStream(new FileInputStream(this.configFile));
                    }
                    catch (Exception e) {
                        if (!this.searchClasspath) break block6;
                        this.setInputStream(ClassUtils.getResourceAsStream(engine.getClass(), this.filename, true));
                    }
                }
            }
            if (this.getInputStream() == null) {
                throw new ConfigurationException(Messages.getMessage("noConfigFile"));
            }
            WSDDDocument doc = new WSDDDocument(XMLUtils.newDocument(this.getInputStream()));
            this.deployment = doc.getDeployment();
            this.deployment.configureEngine(engine);
            engine.refreshGlobalOptions();
            this.setInputStream(null);
        }
        catch (Exception e) {
            throw new ConfigurationException(e);
        }
    }

    public void writeEngineConfig(AxisEngine engine) throws ConfigurationException {
        if (!this.readOnly) {
            try {
                Document doc = Admin.listConfig(engine);
                OutputStreamWriter osWriter = new OutputStreamWriter((OutputStream)new FileOutputStream(this.configFile), XMLUtils.getEncoding());
                PrintWriter writer = new PrintWriter(new BufferedWriter(osWriter));
                XMLUtils.DocumentToWriter(doc, writer);
                writer.println();
                writer.close();
            }
            catch (Exception e) {
                throw new ConfigurationException(e);
            }
        }
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
}

