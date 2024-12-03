/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.BuildException
 *  org.apache.tools.ant.Project
 */
package org.apache.catalina.ant.jmx;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularDataSupport;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.apache.catalina.ant.BaseRedirectorHelperTask;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

public class JMXAccessorTask
extends BaseRedirectorHelperTask {
    public static final String JMX_SERVICE_PREFIX = "service:jmx:rmi:///jndi/rmi://";
    public static final String JMX_SERVICE_SUFFIX = "/jmxrmi";
    private String name = null;
    private String resultproperty;
    private String url = null;
    private String host = "localhost";
    private String port = "8050";
    private String password = null;
    private String username = null;
    private String ref = "jmx.server";
    private boolean echo = false;
    private boolean separatearrayresults = true;
    private String delimiter;
    private String unlessCondition;
    private String ifCondition;
    private final Properties properties = new Properties();

    public String getName() {
        return this.name;
    }

    public void setName(String objectName) {
        this.name = objectName;
    }

    public String getResultproperty() {
        return this.resultproperty;
    }

    public void setResultproperty(String propertyName) {
        this.resultproperty = propertyName;
    }

    public String getDelimiter() {
        return this.delimiter;
    }

    public void setDelimiter(String separator) {
        this.delimiter = separator;
    }

    public boolean isEcho() {
        return this.echo;
    }

    public void setEcho(boolean echo) {
        this.echo = echo;
    }

    public boolean isSeparatearrayresults() {
        return this.separatearrayresults;
    }

    public void setSeparatearrayresults(boolean separateArrayResults) {
        this.separatearrayresults = separateArrayResults;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return this.port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public boolean isUseRef() {
        return this.ref != null && !this.ref.isEmpty();
    }

    public String getRef() {
        return this.ref;
    }

    public void setRef(String refId) {
        this.ref = refId;
    }

    public String getIf() {
        return this.ifCondition;
    }

    public void setIf(String c) {
        this.ifCondition = c;
    }

    public String getUnless() {
        return this.unlessCondition;
    }

    public void setUnless(String c) {
        this.unlessCondition = c;
    }

    public void execute() throws BuildException {
        if (this.testIfCondition() && this.testUnlessCondition()) {
            try {
                String error = null;
                MBeanServerConnection jmxServerConnection = this.getJMXConnection();
                error = this.jmxExecute(jmxServerConnection);
                if (error != null && this.isFailOnError()) {
                    throw new BuildException(error);
                }
            }
            catch (Exception e) {
                if (this.isFailOnError()) {
                    throw new BuildException((Throwable)e);
                }
                this.handleErrorOutput(e.getMessage());
            }
            finally {
                this.closeRedirector();
            }
        }
    }

    public static MBeanServerConnection createJMXConnection(String url, String host, String port, String username, String password) throws MalformedURLException, IOException {
        String urlForJMX = url != null ? url : JMX_SERVICE_PREFIX + host + ":" + port + JMX_SERVICE_SUFFIX;
        HashMap<String, String[]> environment = null;
        if (username != null && password != null) {
            String[] credentials = new String[]{username, password};
            environment = new HashMap<String, String[]>();
            environment.put("jmx.remote.credentials", credentials);
        }
        return JMXConnectorFactory.connect(new JMXServiceURL(urlForJMX), environment).getMBeanServerConnection();
    }

    protected boolean testIfCondition() {
        if (this.ifCondition == null || "".equals(this.ifCondition)) {
            return true;
        }
        return this.getProperty(this.ifCondition) != null;
    }

    protected boolean testUnlessCondition() {
        if (this.unlessCondition == null || "".equals(this.unlessCondition)) {
            return true;
        }
        return this.getProperty(this.unlessCondition) == null;
    }

    public static MBeanServerConnection accessJMXConnection(Project project, String url, String host, String port, String username, String password, String refId) throws MalformedURLException, IOException {
        boolean isRef;
        MBeanServerConnection jmxServerConnection = null;
        boolean bl = isRef = project != null && refId != null && refId.length() > 0;
        if (isRef) {
            Object pref = project.getReference(refId);
            try {
                jmxServerConnection = (MBeanServerConnection)pref;
            }
            catch (ClassCastException cce) {
                project.log("wrong object reference " + refId + " - " + pref.getClass());
                return null;
            }
        }
        if (jmxServerConnection == null) {
            jmxServerConnection = JMXAccessorTask.createJMXConnection(url, host, port, username, password);
        }
        if (isRef && jmxServerConnection != null) {
            project.addReference(refId, (Object)jmxServerConnection);
        }
        return jmxServerConnection;
    }

    protected MBeanServerConnection getJMXConnection() throws MalformedURLException, IOException {
        MBeanServerConnection jmxServerConnection = null;
        if (this.isUseRef()) {
            Object pref = null;
            if (this.getProject() != null && (pref = this.getProject().getReference(this.getRef())) != null) {
                try {
                    jmxServerConnection = (MBeanServerConnection)pref;
                }
                catch (ClassCastException cce) {
                    this.getProject().log("Wrong object reference " + this.getRef() + " - " + pref.getClass());
                    return null;
                }
            }
            if (jmxServerConnection == null) {
                jmxServerConnection = JMXAccessorTask.accessJMXConnection(this.getProject(), this.getUrl(), this.getHost(), this.getPort(), this.getUsername(), this.getPassword(), this.getRef());
            }
        } else {
            jmxServerConnection = JMXAccessorTask.accessJMXConnection(this.getProject(), this.getUrl(), this.getHost(), this.getPort(), this.getUsername(), this.getPassword(), null);
        }
        return jmxServerConnection;
    }

    public String jmxExecute(MBeanServerConnection jmxServerConnection) throws Exception {
        if (jmxServerConnection == null) {
            throw new BuildException("Must open a connection!");
        }
        if (this.isEcho()) {
            this.handleOutput("JMX Connection ref=" + this.ref + " is open!");
        }
        return null;
    }

    protected Object convertStringToType(String value, String valueType) {
        Object convertValue;
        block26: {
            if ("java.lang.String".equals(valueType)) {
                return value;
            }
            convertValue = value;
            if ("java.lang.Integer".equals(valueType) || "int".equals(valueType)) {
                try {
                    convertValue = Integer.valueOf(value);
                }
                catch (NumberFormatException ex) {
                    if (this.isEcho()) {
                        this.handleErrorOutput("Unable to convert to integer:" + value);
                    }
                    break block26;
                }
            }
            if ("java.lang.Long".equals(valueType) || "long".equals(valueType)) {
                try {
                    convertValue = Long.valueOf(value);
                }
                catch (NumberFormatException ex) {
                    if (this.isEcho()) {
                        this.handleErrorOutput("Unable to convert to long:" + value);
                    }
                    break block26;
                }
            }
            if ("java.lang.Boolean".equals(valueType) || "boolean".equals(valueType)) {
                convertValue = Boolean.valueOf(value);
            } else {
                if ("java.lang.Float".equals(valueType) || "float".equals(valueType)) {
                    try {
                        convertValue = Float.valueOf(value);
                    }
                    catch (NumberFormatException ex) {
                        if (this.isEcho()) {
                            this.handleErrorOutput("Unable to convert to float:" + value);
                        }
                        break block26;
                    }
                }
                if ("java.lang.Double".equals(valueType) || "double".equals(valueType)) {
                    try {
                        convertValue = Double.valueOf(value);
                    }
                    catch (NumberFormatException ex) {
                        if (this.isEcho()) {
                            this.handleErrorOutput("Unable to convert to double:" + value);
                        }
                        break block26;
                    }
                }
                if ("javax.management.ObjectName".equals(valueType) || "name".equals(valueType)) {
                    try {
                        convertValue = new ObjectName(value);
                    }
                    catch (MalformedObjectNameException e) {
                        if (this.isEcho()) {
                            this.handleErrorOutput("Unable to convert to ObjectName:" + value);
                        }
                        break block26;
                    }
                }
                if ("java.net.InetAddress".equals(valueType)) {
                    try {
                        convertValue = InetAddress.getByName(value);
                    }
                    catch (UnknownHostException exc) {
                        if (!this.isEcho()) break block26;
                        this.handleErrorOutput("Unable to resolve host name:" + value);
                    }
                }
            }
        }
        return convertValue;
    }

    protected void echoResult(String name, Object result) {
        if (this.isEcho()) {
            if (result.getClass().isArray()) {
                for (int i = 0; i < Array.getLength(result); ++i) {
                    this.handleOutput(name + "." + i + "=" + Array.get(result, i));
                }
            } else {
                this.handleOutput(name + "=" + result);
            }
        }
    }

    protected void createProperty(Object result) {
        if (this.resultproperty != null) {
            this.createProperty(this.resultproperty, result);
        }
    }

    protected void createProperty(String propertyPrefix, Object result) {
        if (propertyPrefix == null) {
            propertyPrefix = "";
        }
        if (result instanceof CompositeDataSupport) {
            CompositeDataSupport data = (CompositeDataSupport)result;
            CompositeType compositeType = data.getCompositeType();
            Set<String> keys = compositeType.keySet();
            for (String key : keys) {
                Object value = data.get(key);
                OpenType<?> type = compositeType.getType(key);
                if (type instanceof SimpleType) {
                    this.setProperty(propertyPrefix + "." + key, value);
                    continue;
                }
                this.createProperty(propertyPrefix + "." + key, value);
            }
        } else if (result instanceof TabularDataSupport) {
            TabularDataSupport data = (TabularDataSupport)result;
            for (Object key : data.keySet()) {
                for (Object key1 : (List)key) {
                    CompositeData valuedata = data.get(new Object[]{key1});
                    Object value = valuedata.get("value");
                    OpenType<?> type = valuedata.getCompositeType().getType("value");
                    if (type instanceof SimpleType) {
                        this.setProperty(propertyPrefix + "." + key1, value);
                        continue;
                    }
                    this.createProperty(propertyPrefix + "." + key1, value);
                }
            }
        } else if (result.getClass().isArray()) {
            if (this.isSeparatearrayresults()) {
                int size = 0;
                for (int i = 0; i < Array.getLength(result); ++i) {
                    if (!this.setProperty(propertyPrefix + "." + size, Array.get(result, i))) continue;
                    ++size;
                }
                if (size > 0) {
                    this.setProperty(propertyPrefix + ".Length", Integer.toString(size));
                }
            }
        } else {
            String delim = this.getDelimiter();
            if (delim != null) {
                StringTokenizer tokenizer = new StringTokenizer(result.toString(), delim);
                int size = 0;
                while (tokenizer.hasMoreTokens()) {
                    String token = tokenizer.nextToken();
                    if (!this.setProperty(propertyPrefix + "." + size, token)) continue;
                    ++size;
                }
                if (size > 0) {
                    this.setProperty(propertyPrefix + ".Length", Integer.toString(size));
                }
            } else {
                this.setProperty(propertyPrefix, result.toString());
            }
        }
    }

    public String getProperty(String property) {
        Project currentProject = this.getProject();
        if (currentProject != null) {
            return currentProject.getProperty(property);
        }
        return this.properties.getProperty(property);
    }

    public boolean setProperty(String property, Object value) {
        if (property != null) {
            Project currentProject;
            if (value == null) {
                value = "";
            }
            if (this.isEcho()) {
                this.handleOutput(property + "=" + value.toString());
            }
            if ((currentProject = this.getProject()) != null) {
                currentProject.setNewProperty(property, value.toString());
            } else {
                this.properties.setProperty(property, value.toString());
            }
            return true;
        }
        return false;
    }
}

