/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.ProjectComponent
 *  org.apache.tools.ant.taskdefs.condition.Condition
 */
package org.apache.catalina.ant.jmx;

import java.io.IOException;
import java.net.MalformedURLException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import org.apache.catalina.ant.jmx.JMXAccessorTask;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.taskdefs.condition.Condition;

public abstract class JMXAccessorConditionBase
extends ProjectComponent
implements Condition {
    private String url = null;
    private String host = "localhost";
    private String port = "8050";
    private String password = null;
    private String username = null;
    private String name = null;
    private String attribute;
    private String value;
    private String ref = "jmx.server";

    public String getAttribute() {
        return this.attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String objectName) {
        this.name = objectName;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPort() {
        return this.port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRef() {
        return this.ref;
    }

    public void setRef(String refId) {
        this.ref = refId;
    }

    protected MBeanServerConnection getJMXConnection() throws MalformedURLException, IOException {
        return JMXAccessorTask.accessJMXConnection(this.getProject(), this.getUrl(), this.getHost(), this.getPort(), this.getUsername(), this.getPassword(), this.ref);
    }

    protected String accessJMXValue() {
        try {
            Object result = this.getJMXConnection().getAttribute(new ObjectName(this.name), this.attribute);
            if (result != null) {
                return result.toString();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return null;
    }
}

