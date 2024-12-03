/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.xml.security.utils.resolver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.ClassLoaderUtils;
import org.apache.xml.security.utils.JavaUtils;
import org.apache.xml.security.utils.resolver.ResourceResolverContext;
import org.apache.xml.security.utils.resolver.ResourceResolverException;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;
import org.apache.xml.security.utils.resolver.implementations.ResolverFragment;
import org.apache.xml.security.utils.resolver.implementations.ResolverXPointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceResolver {
    private static final Logger LOG = LoggerFactory.getLogger(ResourceResolver.class);
    private static final List<ResourceResolverSpi> resolverList = new CopyOnWriteArrayList<ResourceResolverSpi>();
    private static final AtomicBoolean defaultResolversAdded = new AtomicBoolean();

    public static void register(String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        JavaUtils.checkRegisterPermission();
        Class<?> resourceResolverClass = ClassLoaderUtils.loadClass(className, ResourceResolver.class);
        ResourceResolver.register((ResourceResolverSpi)JavaUtils.newInstanceWithEmptyConstructor(resourceResolverClass), false);
    }

    public static void registerAtStart(String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        JavaUtils.checkRegisterPermission();
        Class<?> resourceResolverClass = ClassLoaderUtils.loadClass(className, ResourceResolver.class);
        ResourceResolver.register((ResourceResolverSpi)JavaUtils.newInstanceWithEmptyConstructor(resourceResolverClass), true);
    }

    public static void register(ResourceResolverSpi resourceResolverSpi, boolean start) {
        JavaUtils.checkRegisterPermission();
        if (start) {
            resolverList.add(0, resourceResolverSpi);
        } else {
            resolverList.add(resourceResolverSpi);
        }
        LOG.debug("Registered resolver: {}", (Object)resourceResolverSpi.toString());
    }

    public static void registerClassNames(List<String> classNames) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        JavaUtils.checkRegisterPermission();
        ArrayList<ResourceResolverSpi> resourceResolversToAdd = new ArrayList<ResourceResolverSpi>(classNames.size());
        for (String className : classNames) {
            ResourceResolverSpi resourceResolverSpi = (ResourceResolverSpi)JavaUtils.newInstanceWithEmptyConstructor(ClassLoaderUtils.loadClass(className, ResourceResolver.class));
            resourceResolversToAdd.add(resourceResolverSpi);
        }
        resolverList.addAll(resourceResolversToAdd);
    }

    public static void registerDefaultResolvers() {
        if (defaultResolversAdded.compareAndSet(false, true)) {
            ArrayList<ResourceResolverSpi> resourceResolversToAdd = new ArrayList<ResourceResolverSpi>();
            resourceResolversToAdd.add(new ResolverFragment());
            resourceResolversToAdd.add(new ResolverXPointer());
            resolverList.addAll(resourceResolversToAdd);
        }
    }

    public static XMLSignatureInput resolve(ResourceResolverContext context) throws ResourceResolverException {
        for (ResourceResolverSpi resolver : resolverList) {
            LOG.debug("check resolvability by class {}", (Object)resolver.getClass().getName());
            if (!resolver.engineCanResolveURI(context)) continue;
            return resolver.engineResolveURI(context);
        }
        Object[] exArgs = new Object[]{context.uriToResolve != null ? context.uriToResolve : "null", context.baseUri};
        throw new ResourceResolverException("utils.resolver.noClass", exArgs, context.uriToResolve, context.baseUri);
    }

    public static XMLSignatureInput resolve(List<ResourceResolverSpi> individualResolvers, ResourceResolverContext context) throws ResourceResolverException {
        LOG.debug("I was asked to create a ResourceResolver and got {}", (Object)(individualResolvers == null ? 0 : individualResolvers.size()));
        if (individualResolvers != null) {
            for (ResourceResolverSpi resolver : individualResolvers) {
                String currentClass = resolver.getClass().getName();
                LOG.debug("check resolvability by class {}", (Object)currentClass);
                if (!resolver.engineCanResolveURI(context)) continue;
                return resolver.engineResolveURI(context);
            }
        }
        return ResourceResolver.resolve(context);
    }
}

