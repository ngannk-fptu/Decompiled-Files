/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aQute.bnd.annotation.component.Component
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.setup.xstream.XStreamManager
 *  com.atlassian.spring.container.ContainerManager
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.compat.setup.xstream;

import aQute.bnd.annotation.component.Component;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.compat.setup.xstream.ConfluenceXStreamCompat;
import com.atlassian.confluence.compat.setup.xstream.XStream111Compat;
import com.atlassian.confluence.compat.setup.xstream.XStreamCompat;
import com.atlassian.confluence.setup.xstream.XStreamManager;
import com.atlassian.spring.container.ContainerManager;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class XStreamManagerCompat {
    private static final Logger log = LoggerFactory.getLogger(XStreamManagerCompat.class);
    private final Supplier<XStreamCompat> delegate = Suppliers.memoize(() -> this.initialiseXStreamCompat(xStreamManager, classLoader));

    public XStreamManagerCompat() {
        this((XStreamManager)ContainerManager.getComponent((String)"xStreamManager"), XStreamManagerCompat.class.getClassLoader());
    }

    public XStreamManagerCompat(XStreamManager xStreamManager, ClassLoader classLoader) {
    }

    private XStreamCompat initialiseXStreamCompat(XStreamManager xStreamManager, ClassLoader classLoader) {
        XStreamCompat internalDelegate;
        try {
            Object pluginXStream = this.getConfluenceXStream(xStreamManager, classLoader);
            internalDelegate = new ConfluenceXStreamCompat(pluginXStream);
        }
        catch (ClassNotFoundException e) {
            log.debug("Could not find Confluence XStream, falling back to Confluence 7.9 or prior XStream impl.", (Throwable)e);
            XStream xStream = new XStream();
            xStream.setClassLoader(classLoader);
            internalDelegate = new XStream111Compat(xStream);
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException("Confluence XStream couldn't be initialized.", (Throwable)e);
        }
        return internalDelegate;
    }

    public String toXML(Object obj) {
        return ((XStreamCompat)this.delegate.get()).toXML(obj);
    }

    public void toXML(Object obj, Writer writer) {
        ((XStreamCompat)this.delegate.get()).toXML(obj, writer);
    }

    public Object fromXML(String xml) {
        return ((XStreamCompat)this.delegate.get()).fromXML(xml);
    }

    public Object fromXML(Reader reader) {
        return ((XStreamCompat)this.delegate.get()).fromXML(reader);
    }

    public XStream getXStream() {
        return ((XStreamCompat)this.delegate.get()).getXStream();
    }

    public void registerConverter(Converter converter, Integer priority) {
        ((XStreamCompat)this.delegate.get()).registerConverter(converter, priority);
    }

    public void alias(String name, Class<?> type) {
        ((XStreamCompat)this.delegate.get()).alias(name, type);
    }

    private Object getConfluenceXStream(XStreamManager xStreamManager, ClassLoader classLoader) throws ReflectiveOperationException {
        Method getPluginXStreamMethod = Class.forName("com.atlassian.confluence.setup.xstream.ConfluenceXStreamManager", true, classLoader).getMethod("getPluginXStream", ClassLoader.class);
        return getPluginXStreamMethod.invoke((Object)xStreamManager, classLoader);
    }
}

