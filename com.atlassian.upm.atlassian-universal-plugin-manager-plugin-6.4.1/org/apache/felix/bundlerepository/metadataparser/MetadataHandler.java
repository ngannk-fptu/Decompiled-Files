/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.bundlerepository.metadataparser;

import java.io.InputStream;
import java.lang.reflect.Method;
import org.apache.felix.bundlerepository.Logger;
import org.apache.felix.bundlerepository.metadataparser.XmlCommonHandler;

public abstract class MetadataHandler {
    protected XmlCommonHandler m_handler;

    public MetadataHandler(Logger logger) {
        this.m_handler = new XmlCommonHandler(logger);
    }

    public abstract void parse(InputStream var1) throws Exception;

    public final Object getMetadata() {
        return this.m_handler.getRoot();
    }

    public final void addType(String qname, Object instanceFactory) throws Exception {
        this.m_handler.addType(qname, instanceFactory, null, null);
    }

    public final void addType(String qname, Object instanceFactory, Class castClass) throws Exception {
        this.m_handler.addType(qname, instanceFactory, castClass, null);
    }

    public final void addType(String qname, Object instanceFactory, Class castClass, Method defaultAddMethod) throws Exception {
        this.m_handler.addType(qname, instanceFactory, castClass, defaultAddMethod);
    }

    public final void setDefaultType(Object instanceFactory) throws Exception {
        this.m_handler.setDefaultType(instanceFactory, null, null);
    }

    public final void setDefaultType(Object instanceFactory, Class castClass) throws Exception {
        this.m_handler.setDefaultType(instanceFactory, castClass, null);
    }

    public final void setDefaultType(Object instanceFactory, Class castClass, Method defaultAddMethod) throws Exception {
        this.m_handler.setDefaultType(instanceFactory, castClass, defaultAddMethod);
    }

    public final void addPI(String piname, Class clazz) {
        this.m_handler.addPI(piname, clazz);
    }

    public final void setMissingPIExceptionFlag(boolean flag) {
        this.m_handler.setMissingPIExceptionFlag(flag);
    }

    public final void setTrace(boolean trace) {
        this.m_handler.setTrace(trace);
    }
}

