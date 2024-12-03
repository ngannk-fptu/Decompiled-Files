/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Pair
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.elements.ResourceDescriptor
 *  com.google.common.base.Function
 *  com.google.common.base.MoreObjects
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Iterables
 *  io.atlassian.util.concurrent.Lazy
 *  io.atlassian.util.concurrent.LazyReference
 *  javax.activation.DataSource
 *  javax.activation.FileTypeMap
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.atlassian.confluence.core.InputStreamExceptionDecorator;
import com.atlassian.confluence.core.PluginDataSourceFactory;
import com.atlassian.fugue.Pair;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.elements.ResourceDescriptor;
import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import io.atlassian.util.concurrent.Lazy;
import io.atlassian.util.concurrent.LazyReference;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.activation.DataSource;
import javax.activation.FileTypeMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.checkerframework.checker.nullness.qual.Nullable;

public class DefaultPluginDataSourceFactory
implements PluginDataSourceFactory {
    private static final String PARAMETER_KEY_CONTENT_TYPE = "content-type";
    private final Plugin plugin;
    private final FileTypeMap fileTypeMap;
    private final Function<Pair<Plugin, PluginDataSourceFactory.ResourceView>, InputStream> resourceStreamFactory;
    private static final Supplier<InputStreamFactory> DEFAULT_INPUT_STREAM_FACTORY = Lazy.supplier(() -> new InputStreamFactory());
    private static final LoadingCache<String, InputStreamFactory> INPUT_STREAM_FACTORIES_BY_CONTENT_TYPE = CacheBuilder.newBuilder().build((CacheLoader)new CacheLoader<String, InputStreamFactory>(){

        private String format(Plugin plugin, ResourceDescriptorView resource, String comment) {
            String moduleFragment = resource.moduleDescriptor == null ? "" : ", module: " + resource.moduleDescriptor.getKey();
            String resourceName = resource.resourceDescriptor.getName();
            return String.format("%s [resource: %s%s, plugin: %s]", comment, resourceName, moduleFragment, plugin.getKey());
        }

        public InputStreamFactory load(String contentType) throws Exception {
            InputStreamFactory specificInputStreamFactory = null;
            if (ConfluenceSystemProperties.isDevMode()) {
                specificInputStreamFactory = this.loadForContentType(contentType);
            }
            return (InputStreamFactory)MoreObjects.firstNonNull((Object)specificInputStreamFactory, (Object)DEFAULT_INPUT_STREAM_FACTORY.get());
        }

        private InputStreamFactory loadForContentType(String key) {
            if ("text/css".equals(key)) {
                return new InputStreamFactory(){

                    @Override
                    protected String comment(Plugin plugin, ResourceDescriptorView resource, String comment) {
                        return "\n/* " + this.format(plugin, resource, comment) + " */\n";
                    }
                };
            }
            return null;
        }
    });

    public DefaultPluginDataSourceFactory(FileTypeMap fileTypeMap, Function<Pair<Plugin, PluginDataSourceFactory.ResourceView>, InputStream> resourceStreamFactory, Plugin plugin) {
        this.plugin = plugin;
        this.fileTypeMap = fileTypeMap;
        this.resourceStreamFactory = resourceStreamFactory;
    }

    @Override
    public Optional<Iterable<DataSource>> getResourcesFromModules(String moduleKey) {
        return this.getResourcesFromModules(moduleKey, null);
    }

    @Override
    public Optional<Iterable<DataSource>> getResourcesFromModules(String moduleKey, @Nullable Predicate<PluginDataSourceFactory.ResourceView> filter) {
        Iterable resourceDescriptors;
        ModuleDescriptor moduleDescriptor = this.plugin.getModuleDescriptor(moduleKey);
        if (moduleDescriptor == null) {
            resourceDescriptors = this.plugin.getResourceDescriptors().stream().filter(resourceDescriptor -> resourceDescriptor.getName().equals(moduleKey)).collect(Collectors.toList());
            if (Iterables.isEmpty((Iterable)resourceDescriptors)) {
                return Optional.empty();
            }
        } else {
            resourceDescriptors = moduleDescriptor.getResourceDescriptors();
        }
        Iterable<ResourceDescriptorView> resources = this.resources(resourceDescriptors, filter, moduleDescriptor);
        return Optional.of((Iterable)StreamSupport.stream(resources.spliterator(), false).map(this::createDataSource).collect(Collectors.toList()));
    }

    @Override
    public Optional<DataSource> getResourceFromModuleByName(String moduleKey, String resourceName) {
        Optional<Iterable<DataSource>> resources = this.getResourcesFromModules(moduleKey, resource -> resource.name().equals(resourceName));
        if (resources.isEmpty()) {
            return Optional.empty();
        }
        Iterator<DataSource> resourceIterator = resources.get().iterator();
        if (!resourceIterator.hasNext()) {
            return Optional.empty();
        }
        return Optional.of(resourceIterator.next());
    }

    private Iterable<ResourceDescriptorView> resources(Iterable<ResourceDescriptor> resourceDescriptors, @Nullable Predicate<PluginDataSourceFactory.ResourceView> filter, @Nullable ModuleDescriptor<?> moduleDescriptor) {
        Iterable resources = StreamSupport.stream(resourceDescriptors.spliterator(), false).map(resourceDescriptor -> new ResourceDescriptorView(this.fileTypeMap, (ResourceDescriptor)resourceDescriptor, this.plugin, moduleDescriptor)).collect(Collectors.toList());
        if (filter == null) {
            return resources;
        }
        return StreamSupport.stream(resources.spliterator(), false).filter(filter).collect(Collectors.toList());
    }

    private DataSource createDataSource(ResourceDescriptorView resource) {
        return new ResourceDataSource(resource);
    }

    private static InputStream toInputStream(ImmutableList.Builder<InputStream> builder) {
        ImmutableList inputStreams = builder.build();
        if (inputStreams.size() == 1) {
            return (InputStream)inputStreams.get(0);
        }
        return new SequenceInputStream(Collections.enumeration(inputStreams));
    }

    private class ResourceDataSource
    implements DataSource {
        private final ResourceDescriptorView resource;

        public ResourceDataSource(ResourceDescriptorView resource) {
            this.resource = resource;
        }

        public String getName() {
            return this.resource.name();
        }

        public InputStream getInputStream() throws IOException {
            InputStreamFactory factory = (InputStreamFactory)INPUT_STREAM_FACTORIES_BY_CONTENT_TYPE.getUnchecked((Object)this.getContentType());
            Objects.requireNonNull(factory);
            return factory.create(DefaultPluginDataSourceFactory.this.resourceStreamFactory, DefaultPluginDataSourceFactory.this.plugin, this.resource);
        }

        public String getContentType() {
            return this.resource.contentType();
        }

        public OutputStream getOutputStream() throws IOException {
            throw new UnsupportedOperationException();
        }
    }

    private static class InputStreamFactory {
        private InputStreamFactory() {
        }

        public InputStream create(Function<Pair<Plugin, PluginDataSourceFactory.ResourceView>, InputStream> resourceStreamFactory, Plugin plugin, ResourceDescriptorView resource) throws IOException {
            InputStream inputStream;
            ImmutableList.Builder builder = ImmutableList.builder();
            String start = this.comment(plugin, resource, "start");
            if (StringUtils.isNotBlank((CharSequence)start)) {
                builder.add((Object)new ByteArrayInputStream(start.getBytes()));
            }
            if ((inputStream = (InputStream)resourceStreamFactory.apply((Object)new Pair((Object)plugin, (Object)resource))) == null) {
                String moduleFragment = resource.moduleDescriptor == null ? "" : " for module [" + resource.moduleDescriptor.getKey() + "]";
                String format = String.format("Resource [%s]%s in plugin [%s] could not be found under path [%s]", resource.name(), moduleFragment, plugin.getKey(), resource.location());
                throw new RuntimeException(format);
            }
            builder.add((Object)new InputStreamExceptionDecorator(inputStream, (Function<IOException, IOException>)((Function)exception -> {
                String moduleFragment = resource.moduleDescriptor == null ? "" : " for module [" + resource.moduleDescriptor.getKey() + "]";
                return new IOException(String.format("Resource [%s]%s in plugin [%s] failed to stream from path [%s], see cause", resource.name(), moduleFragment, plugin.getKey(), resource.location()), (Throwable)exception);
            })));
            String end = this.comment(plugin, resource, "end");
            if (StringUtils.isNotBlank((CharSequence)end)) {
                builder.add((Object)new ByteArrayInputStream(end.getBytes()));
            }
            return DefaultPluginDataSourceFactory.toInputStream((ImmutableList.Builder<InputStream>)builder);
        }

        protected String comment(Plugin plugin, ResourceDescriptorView resource, String comment) {
            return null;
        }
    }

    private static class ResourceDescriptorView
    implements PluginDataSourceFactory.ResourceView {
        private final FileTypeMap fileTypeMap;
        private final ResourceDescriptor resourceDescriptor;
        private final Plugin plugin;
        private @Nullable ModuleDescriptor<?> moduleDescriptor;
        private Supplier<Map<String, String>> parameters = new LazyReference<Map<String, String>>(){

            protected Map<String, String> create() throws Exception {
                Map parameters = resourceDescriptor.getParameters();
                ImmutableMap.Builder parameterBuilder = ImmutableMap.builder().putAll(parameters);
                if (parameters.get(DefaultPluginDataSourceFactory.PARAMETER_KEY_CONTENT_TYPE) == null) {
                    parameterBuilder.put((Object)DefaultPluginDataSourceFactory.PARAMETER_KEY_CONTENT_TYPE, (Object)fileTypeMap.getContentType(resourceDescriptor.getLocation()));
                }
                return parameterBuilder.build();
            }
        };

        public ResourceDescriptorView(FileTypeMap fileTypeMap, ResourceDescriptor resourceDescriptor, Plugin plugin, @Nullable ModuleDescriptor<?> moduleDescriptor) {
            this.fileTypeMap = fileTypeMap;
            this.resourceDescriptor = resourceDescriptor;
            this.plugin = plugin;
            this.moduleDescriptor = moduleDescriptor;
        }

        @Override
        public String name() {
            if ("embedded".equals(this.type())) {
                return (this.moduleDescriptor == null ? this.plugin.getKey() : this.moduleDescriptor.getCompleteKey()) + ":" + this.resourceDescriptor.getName();
            }
            return this.resourceDescriptor.getName();
        }

        @Override
        public String type() {
            return this.resourceDescriptor.getType();
        }

        @Override
        public String location() {
            return this.resourceDescriptor.getLocation();
        }

        @Override
        public Map<String, String> params() {
            return this.parameters.get();
        }

        @Override
        public String contentType() {
            return this.params().get(DefaultPluginDataSourceFactory.PARAMETER_KEY_CONTENT_TYPE);
        }

        public String toString() {
            ToStringBuilder toStringBuilder = new ToStringBuilder((Object)this);
            if (this.moduleDescriptor != null) {
                toStringBuilder.append("plugin", (Object)this.moduleDescriptor.getPlugin().getKey()).append("module", (Object)this.moduleDescriptor.getKey());
            }
            toStringBuilder.append("name", (Object)this.name()).append("location", (Object)this.location()).append("contentType", (Object)this.contentType());
            if (this.type() != null) {
                toStringBuilder.append("type", (Object)this.type());
            }
            return toStringBuilder.toString();
        }
    }
}

