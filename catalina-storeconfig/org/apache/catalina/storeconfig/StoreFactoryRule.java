/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.digester.Rule
 */
package org.apache.catalina.storeconfig;

import org.apache.catalina.storeconfig.IStoreFactory;
import org.apache.catalina.storeconfig.StoreAppender;
import org.apache.catalina.storeconfig.StoreDescription;
import org.apache.catalina.storeconfig.StoreRegistry;
import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;

public class StoreFactoryRule
extends Rule {
    private String attributeName;
    private String appenderAttributeName;
    private String storeFactoryClass;
    private String storeAppenderClass;

    public StoreFactoryRule(String storeFactoryClass, String attributeName, String storeAppenderClass, String appenderAttributeName) {
        this.storeFactoryClass = storeFactoryClass;
        this.attributeName = attributeName;
        this.appenderAttributeName = appenderAttributeName;
        this.storeAppenderClass = storeAppenderClass;
    }

    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        IStoreFactory factory = (IStoreFactory)this.newInstance(this.attributeName, this.storeFactoryClass, attributes);
        StoreAppender storeAppender = (StoreAppender)this.newInstance(this.appenderAttributeName, this.storeAppenderClass, attributes);
        factory.setStoreAppender(storeAppender);
        StoreDescription desc = (StoreDescription)this.digester.peek(0);
        StoreRegistry registry = (StoreRegistry)this.digester.peek(1);
        factory.setRegistry(registry);
        desc.setStoreFactory(factory);
    }

    protected Object newInstance(String attr, String defaultName, Attributes attributes) throws ReflectiveOperationException {
        String value;
        String className = defaultName;
        if (attr != null && (value = attributes.getValue(attr)) != null) {
            className = value;
        }
        Class<?> clazz = Class.forName(className);
        return clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
    }
}

