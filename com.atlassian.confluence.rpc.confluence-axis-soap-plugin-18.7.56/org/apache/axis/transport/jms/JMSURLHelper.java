/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.transport.jms;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

public class JMSURLHelper {
    private URL url;
    private String destination;
    private HashMap properties;
    private Vector requiredProperties;
    private Vector appProperties;

    public JMSURLHelper(URL url) throws MalformedURLException {
        this(url, null);
    }

    public JMSURLHelper(URL url, String[] requiredProperties) throws MalformedURLException {
        this.url = url;
        this.properties = new HashMap();
        this.appProperties = new Vector();
        this.destination = url.getPath();
        if (this.destination.startsWith("/")) {
            this.destination = this.destination.substring(1);
        }
        if (this.destination == null || this.destination.trim().length() < 1) {
            throw new MalformedURLException("Missing destination in URL");
        }
        String query = url.getQuery();
        StringTokenizer st = new StringTokenizer(query, "&;");
        while (st.hasMoreTokens()) {
            String keyValue = st.nextToken();
            int eqIndex = keyValue.indexOf("=");
            if (eqIndex <= 0) continue;
            String key = keyValue.substring(0, eqIndex);
            String value = keyValue.substring(eqIndex + 1);
            if (key.startsWith("msgProp.")) {
                key = key.substring("msgProp.".length());
                this.addApplicationProperty(key);
            }
            this.properties.put(key, value);
        }
        this.addRequiredProperties(requiredProperties);
        this.validateURL();
    }

    public String getDestination() {
        return this.destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getVendor() {
        return this.getPropertyValue("vendor");
    }

    public String getDomain() {
        return this.getPropertyValue("domain");
    }

    public HashMap getProperties() {
        return this.properties;
    }

    public String getPropertyValue(String property) {
        return (String)this.properties.get(property);
    }

    public void addRequiredProperties(String[] properties) {
        if (properties == null) {
            return;
        }
        for (int i = 0; i < properties.length; ++i) {
            this.addRequiredProperty(properties[i]);
        }
    }

    public void addRequiredProperty(String property) {
        if (property == null) {
            return;
        }
        if (this.requiredProperties == null) {
            this.requiredProperties = new Vector();
        }
        this.requiredProperties.addElement(property);
    }

    public Vector getRequiredProperties() {
        return this.requiredProperties;
    }

    public void addApplicationProperty(String property) {
        if (property == null) {
            return;
        }
        if (this.appProperties == null) {
            this.appProperties = new Vector();
        }
        this.appProperties.addElement(property);
    }

    public void addApplicationProperty(String property, String value) {
        if (property == null) {
            return;
        }
        if (this.appProperties == null) {
            this.appProperties = new Vector();
        }
        this.properties.put(property, value);
        this.appProperties.addElement(property);
    }

    public Vector getApplicationProperties() {
        return this.appProperties;
    }

    public String getURLString() {
        StringBuffer text = new StringBuffer("jms:/");
        text.append(this.getDestination());
        text.append("?");
        Map props = (Map)this.properties.clone();
        boolean firstEntry = true;
        Iterator itr = this.properties.keySet().iterator();
        while (itr.hasNext()) {
            String key = (String)itr.next();
            if (!firstEntry) {
                text.append("&");
            }
            if (this.appProperties.contains(key)) {
                text.append("msgProp.");
            }
            text.append(key);
            text.append("=");
            text.append(props.get(key));
            firstEntry = false;
        }
        return text.toString();
    }

    public String toString() {
        return this.getURLString();
    }

    private void validateURL() throws MalformedURLException {
        Vector required = this.getRequiredProperties();
        if (required == null) {
            return;
        }
        for (int i = 0; i < required.size(); ++i) {
            String key = (String)required.elementAt(i);
            if (this.properties.get(key) != null) continue;
            throw new MalformedURLException();
        }
    }
}

