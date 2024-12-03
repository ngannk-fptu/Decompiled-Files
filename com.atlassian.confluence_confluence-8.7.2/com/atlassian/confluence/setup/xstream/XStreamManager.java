/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.core.exception.InfrastructureException
 *  com.atlassian.core.util.ClassLoaderUtils
 *  com.atlassian.spring.container.ContainerManager
 *  com.thoughtworks.xstream.XStream
 *  com.thoughtworks.xstream.converters.Converter
 *  com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider
 *  com.thoughtworks.xstream.converters.reflection.ReflectionProvider
 *  com.thoughtworks.xstream.core.ClassLoaderReference
 *  com.thoughtworks.xstream.io.HierarchicalStreamDriver
 *  com.thoughtworks.xstream.io.xml.XppDriver
 *  io.atlassian.util.concurrent.ResettableLazyReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.setup.xstream;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.impl.xstream.ConfluenceXStreamImpl;
import com.atlassian.confluence.impl.xstream.XStream111;
import com.atlassian.confluence.impl.xstream.security.XStreamSecurityConfigurator;
import com.atlassian.confluence.setup.xstream.AtomicReferenceConverter;
import com.atlassian.confluence.setup.xstream.ConfluenceXStream;
import com.atlassian.confluence.setup.xstream.ConfluenceXStreamInternal;
import com.atlassian.confluence.setup.xstream.ConfluenceXStreamManager;
import com.atlassian.core.exception.InfrastructureException;
import com.atlassian.core.util.ClassLoaderUtils;
import com.atlassian.spring.container.ContainerManager;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.core.ClassLoaderReference;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.xml.XppDriver;
import io.atlassian.util.concurrent.ResettableLazyReference;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class XStreamManager
implements ConfluenceXStreamManager {
    private static final Logger log = LoggerFactory.getLogger(XStreamManager.class);
    private final ResettableLazyReference<ConfluenceXStreamInternal> confluenceXStreamReference;
    private final Map<String, String> aliases;
    private final Map<Converter, Integer> converters;
    private final ClassLoader defaultClassLoader;
    private final XStreamSecurityConfigurator securityConfigurator;
    private final boolean isAllowListEnabled;

    public XStreamManager(Map aliases) {
        this(aliases, null);
    }

    public XStreamManager(Map<String, String> aliases, ClassLoader classLoader, XStreamSecurityConfigurator securityConfigurator) {
        this(aliases, Collections.emptyMap(), classLoader, securityConfigurator);
    }

    @Deprecated
    public XStreamManager(Map<String, String> aliases, ClassLoader classLoader) {
        this(aliases, Collections.emptyMap(), classLoader);
    }

    @Deprecated
    public XStreamManager(Map<String, String> aliases, Map<Converter, Integer> converters, ClassLoader classLoader) {
        this(aliases, converters, classLoader, (XStreamSecurityConfigurator)ContainerManager.getComponent((String)"xStreamSecurityConfigurator"));
    }

    @Internal
    XStreamManager(Map<String, String> aliases, Map<Converter, Integer> converters, ClassLoader classLoader, XStreamSecurityConfigurator securityConfigurator) {
        this.aliases = aliases;
        this.converters = converters;
        this.defaultClassLoader = classLoader == null ? XStreamManager.class.getClassLoader() : classLoader;
        this.securityConfigurator = securityConfigurator;
        this.isAllowListEnabled = this.isAllowListEnabled();
        this.confluenceXStreamReference = new ResettableLazyReference<ConfluenceXStreamInternal>(){

            protected ConfluenceXStreamInternal create() {
                return XStreamManager.this.createAndInitializeXStream(XStreamManager.this.defaultClassLoader);
            }
        };
        this.resetXStream();
    }

    public XStream getXstream() {
        return ((ConfluenceXStreamInternal)this.confluenceXStreamReference.get()).getXStream();
    }

    @Override
    public ConfluenceXStream getPluginXStream(ClassLoader classLoader) {
        return this.createAndInitializeXStream(classLoader);
    }

    @Override
    public ConfluenceXStream getConfluenceXStream() {
        return (ConfluenceXStream)this.confluenceXStreamReference.get();
    }

    @Override
    public void resetXStream() {
        this.confluenceXStreamReference.reset();
    }

    private ConfluenceXStreamInternal createAndInitializeXStream(ClassLoader classLoader) {
        PureJavaReflectionProvider reflectionProvider = new PureJavaReflectionProvider();
        ConfluenceXStreamInternal confluenceXStream = this.createNewConfluenceXStream((ReflectionProvider)reflectionProvider, classLoader, Boolean.getBoolean("xstream.11.storage"));
        if (!this.isAllowListEnabled) {
            log.error("Now xstream support only allowList as default and xstream blockList functionality is completely removed form 8.0");
        }
        this.securityConfigurator.configureXStreamSecurity(confluenceXStream);
        this.converters.forEach(confluenceXStream::registerConverter);
        this.aliases.forEach((alias, aliasClassName) -> {
            try {
                Class aliasClass = ClassLoaderUtils.loadClass((String)aliasClassName, XStreamManager.class);
                confluenceXStream.alias((String)alias, aliasClass);
            }
            catch (ClassNotFoundException e) {
                throw new InfrastructureException("Error loading XStreamManager - could not find class : " + aliasClassName + " for alias " + alias, (Throwable)e);
            }
        });
        return confluenceXStream;
    }

    private ConfluenceXStreamInternal createNewConfluenceXStream(ReflectionProvider reflectProvider, ClassLoader classLoader, boolean is11StorageEnabled) {
        ClassLoaderReference classLoaderReference = new ClassLoaderReference(this.defaultClassLoader);
        XStream111 xStream111 = new XStream111(reflectProvider, classLoaderReference);
        xStream111.registerConverter(new AtomicReferenceConverter(xStream111.getMapper()), 10000);
        XStream xStreamBundled = new XStream(reflectProvider, (HierarchicalStreamDriver)new XppDriver(), classLoaderReference);
        xStreamBundled.registerConverter((Converter)new AtomicReferenceConverter(xStreamBundled.getMapper()), 10000);
        ConfluenceXStreamImpl confluenceXStream = is11StorageEnabled ? new ConfluenceXStreamImpl(xStream111, xStreamBundled) : new ConfluenceXStreamImpl(xStreamBundled, xStream111);
        classLoaderReference.setReference(classLoader);
        return confluenceXStream;
    }

    private boolean isAllowListEnabled() {
        if (!Objects.isNull(System.getProperty("xstream.allowlist.enable"))) {
            if (Boolean.getBoolean("xstream.allowlist.enable")) {
                log.warn("XStream's default behaviour is allowList, no need to use xstream.whitelist.enable");
            }
            return Boolean.getBoolean("xstream.allowlist.enable");
        }
        if (!Objects.isNull(System.getProperty("xstream.whitelist.enable"))) {
            if (Boolean.getBoolean("xstream.whitelist.enable")) {
                log.warn("XStream's default behaviour is allowList, no need to use xstream.whitelist.enable");
            }
            return Boolean.getBoolean("xstream.whitelist.enable");
        }
        return true;
    }
}

