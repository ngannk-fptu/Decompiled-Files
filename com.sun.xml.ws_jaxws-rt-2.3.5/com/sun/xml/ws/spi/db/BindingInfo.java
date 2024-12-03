/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.spi.db;

import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.spi.db.TypeInfo;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BindingInfo {
    private String databindingMode;
    private String defaultNamespace;
    private Collection<Class> contentClasses = new ArrayList<Class>();
    private Collection<TypeInfo> typeInfos = new ArrayList<TypeInfo>();
    private Map<Class, Class> subclassReplacements = new HashMap<Class, Class>();
    private Map<String, Object> properties = new HashMap<String, Object>();
    protected ClassLoader classLoader;
    private SEIModel seiModel;
    private URL wsdlURL;

    public String getDatabindingMode() {
        return this.databindingMode;
    }

    public void setDatabindingMode(String databindingMode) {
        this.databindingMode = databindingMode;
    }

    public String getDefaultNamespace() {
        return this.defaultNamespace;
    }

    public void setDefaultNamespace(String defaultNamespace) {
        this.defaultNamespace = defaultNamespace;
    }

    public Collection<Class> contentClasses() {
        return this.contentClasses;
    }

    public Collection<TypeInfo> typeInfos() {
        return this.typeInfos;
    }

    public Map<Class, Class> subclassReplacements() {
        return this.subclassReplacements;
    }

    public Map<String, Object> properties() {
        return this.properties;
    }

    public SEIModel getSEIModel() {
        return this.seiModel;
    }

    public void setSEIModel(SEIModel seiModel) {
        this.seiModel = seiModel;
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public URL getWsdlURL() {
        return this.wsdlURL;
    }

    public void setWsdlURL(URL wsdlURL) {
        this.wsdlURL = wsdlURL;
    }
}

