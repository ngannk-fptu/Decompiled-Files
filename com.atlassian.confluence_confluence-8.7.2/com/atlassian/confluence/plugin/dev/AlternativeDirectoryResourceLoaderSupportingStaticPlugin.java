/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.elements.ResourceLocation
 *  com.atlassian.plugin.impl.StaticPlugin
 *  com.atlassian.plugin.util.resource.AlternativeDirectoryResourceLoader
 *  com.atlassian.plugin.webresource.WebResourceModuleDescriptor
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  io.atlassian.util.concurrent.Lazy
 */
package com.atlassian.confluence.plugin.dev;

import com.atlassian.confluence.plugin.dev.ClassLoaderDelegate;
import com.atlassian.confluence.plugin.dev.ResourceLocationDelegate;
import com.atlassian.confluence.plugin.dev.WebResourceModuleDescriptorDelegate;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.elements.ResourceLocation;
import com.atlassian.plugin.impl.StaticPlugin;
import com.atlassian.plugin.util.resource.AlternativeDirectoryResourceLoader;
import com.atlassian.plugin.webresource.WebResourceModuleDescriptor;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.atlassian.util.concurrent.Lazy;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class AlternativeDirectoryResourceLoaderSupportingStaticPlugin
extends StaticPlugin {
    private static final Supplier<AlternativeDirectoryResourceLoader> alternativeDirectory = Lazy.supplier(AlternativeDirectoryResourceLoader::new);

    public ClassLoader getClassLoader() {
        return new ClassLoaderDelegate(super.getClassLoader()){

            @Override
            public Enumeration<URL> getResources(String name) throws IOException {
                Enumeration<URL> originalResources = super.getResources(name);
                URL alternativeResource = alternativeDirectory.get().getResource(name);
                if (alternativeResource != null) {
                    ArrayList mergedResources = Lists.newArrayList((Object[])new URL[]{alternativeResource});
                    mergedResources.addAll(Collections.list(originalResources));
                    return Collections.enumeration(mergedResources);
                }
                return originalResources;
            }

            @Override
            public URL getResource(String name) {
                return AlternativeDirectoryResourceLoaderSupportingStaticPlugin.either(alternativeDirectory.get().getResource(name), super.getResource(name));
            }

            @Override
            public InputStream getResourceAsStream(String name) {
                return AlternativeDirectoryResourceLoaderSupportingStaticPlugin.either(alternativeDirectory.get().getResourceAsStream(name), super.getResourceAsStream(name));
            }
        };
    }

    public URL getResource(String name) {
        return AlternativeDirectoryResourceLoaderSupportingStaticPlugin.either(alternativeDirectory.get().getResource(name), super.getResource(name));
    }

    public InputStream getResourceAsStream(String name) {
        return AlternativeDirectoryResourceLoaderSupportingStaticPlugin.either(alternativeDirectory.get().getResourceAsStream(name), super.getResourceAsStream(name));
    }

    public Collection<ModuleDescriptor<?>> getModuleDescriptors() {
        return Lists.transform(new LinkedList(super.getModuleDescriptors()), descriptor -> AlternativeDirectoryResourceLoaderSupportingStaticPlugin.decorateModuleDescriptor(descriptor));
    }

    public <T> List<ModuleDescriptor<T>> getModuleDescriptorsByModuleClass(Class<T> aClass) {
        return Lists.transform((List)super.getModuleDescriptorsByModuleClass(aClass), descriptor -> AlternativeDirectoryResourceLoaderSupportingStaticPlugin.decorateModuleDescriptor(descriptor));
    }

    public ModuleDescriptor<?> getModuleDescriptor(String key) {
        return AlternativeDirectoryResourceLoaderSupportingStaticPlugin.decorateModuleDescriptor(super.getModuleDescriptor(key));
    }

    private static <T> ModuleDescriptor<T> decorateModuleDescriptor(ModuleDescriptor<T> descriptor) {
        if (descriptor instanceof WebResourceModuleDescriptor) {
            return new WebResourceModuleDescriptorDelegate((WebResourceModuleDescriptor)descriptor){

                @Override
                public ResourceLocation getResourceLocation(String type, String name) {
                    ResourceLocation originalResourceLocation = super.getResourceLocation(type, name);
                    if (originalResourceLocation != null) {
                        return new ResourceLocationDelegate(originalResourceLocation){

                            @Override
                            public String getParameter(String key) {
                                return this.substituteParameter(key, super.getParameter(key));
                            }

                            @Override
                            public Map<String, String> getParams() {
                                return Maps.transformEntries(super.getParams(), (key1, value) -> this.substituteParameter((String)key1, (String)value));
                            }

                            private String substituteParameter(String key, String value) {
                                if ("source".equals(key)) {
                                    return null;
                                }
                                return value;
                            }
                        };
                    }
                    return originalResourceLocation;
                }
            };
        }
        return descriptor;
    }

    private static <T> T either(T primary, T secondary) {
        return primary != null ? primary : secondary;
    }
}

