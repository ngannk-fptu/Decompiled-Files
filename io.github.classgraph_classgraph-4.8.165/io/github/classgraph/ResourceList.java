/*
 * Decompiled with CFR 0.152.
 */
package io.github.classgraph;

import io.github.classgraph.PotentiallyUnmodifiableList;
import io.github.classgraph.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import nonapi.io.github.classgraph.utils.CollectionUtils;

public class ResourceList
extends PotentiallyUnmodifiableList<Resource>
implements AutoCloseable {
    static final long serialVersionUID = 1L;
    static final ResourceList EMPTY_LIST = new ResourceList();
    private static final ResourceFilter CLASSFILE_FILTER;

    public static ResourceList emptyList() {
        return EMPTY_LIST;
    }

    public ResourceList() {
    }

    public ResourceList(int sizeHint) {
        super(sizeHint);
    }

    public ResourceList(Collection<Resource> resourceCollection) {
        super(resourceCollection);
    }

    public ResourceList get(String resourcePath) {
        boolean hasResourceWithPath = false;
        for (Resource res : this) {
            if (!res.getPath().equals(resourcePath)) continue;
            hasResourceWithPath = true;
            break;
        }
        if (!hasResourceWithPath) {
            return EMPTY_LIST;
        }
        ResourceList matchingResources = new ResourceList(2);
        for (Resource res : this) {
            if (!res.getPath().equals(resourcePath)) continue;
            matchingResources.add(res);
        }
        return matchingResources;
    }

    public List<String> getPaths() {
        ArrayList<String> resourcePaths = new ArrayList<String>(this.size());
        for (Resource resource : this) {
            resourcePaths.add(resource.getPath());
        }
        return resourcePaths;
    }

    public List<String> getPathsRelativeToClasspathElement() {
        ArrayList<String> resourcePaths = new ArrayList<String>(this.size());
        for (Resource resource : this) {
            resourcePaths.add(resource.getPath());
        }
        return resourcePaths;
    }

    public List<URL> getURLs() {
        ArrayList<URL> resourceURLs = new ArrayList<URL>(this.size());
        for (Resource resource : this) {
            resourceURLs.add(resource.getURL());
        }
        return resourceURLs;
    }

    public List<URI> getURIs() {
        ArrayList<URI> resourceURLs = new ArrayList<URI>(this.size());
        for (Resource resource : this) {
            resourceURLs.add(resource.getURI());
        }
        return resourceURLs;
    }

    public ResourceList classFilesOnly() {
        return this.filter(CLASSFILE_FILTER);
    }

    public ResourceList nonClassFilesOnly() {
        return this.filter(new ResourceFilter(){

            @Override
            public boolean accept(Resource resource) {
                return !CLASSFILE_FILTER.accept(resource);
            }
        });
    }

    public Map<String, ResourceList> asMap() {
        HashMap<String, ResourceList> pathToResourceList = new HashMap<String, ResourceList>();
        for (Resource resource : this) {
            String path = resource.getPath();
            ResourceList resourceList = (ResourceList)pathToResourceList.get(path);
            if (resourceList == null) {
                resourceList = new ResourceList(1);
                pathToResourceList.put(path, resourceList);
            }
            resourceList.add(resource);
        }
        return pathToResourceList;
    }

    public List<Map.Entry<String, ResourceList>> findDuplicatePaths() {
        ArrayList<Map.Entry<String, ResourceList>> duplicatePaths = new ArrayList<Map.Entry<String, ResourceList>>();
        for (Map.Entry<String, ResourceList> pathAndResourceList : this.asMap().entrySet()) {
            if (pathAndResourceList.getValue().size() <= 1) continue;
            duplicatePaths.add(new AbstractMap.SimpleEntry<String, ResourceList>(pathAndResourceList.getKey(), pathAndResourceList.getValue()));
        }
        CollectionUtils.sortIfNotEmpty(duplicatePaths, new Comparator<Map.Entry<String, ResourceList>>(){

            @Override
            public int compare(Map.Entry<String, ResourceList> o1, Map.Entry<String, ResourceList> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });
        return duplicatePaths;
    }

    public ResourceList filter(ResourceFilter filter) {
        ResourceList resourcesFiltered = new ResourceList();
        for (Resource resource : this) {
            if (!filter.accept(resource)) continue;
            resourcesFiltered.add(resource);
        }
        return resourcesFiltered;
    }

    @Deprecated
    public void forEachByteArray(ByteArrayConsumer byteArrayConsumer, boolean ignoreIOExceptions) {
        for (Resource resource : this) {
            try {
                Resource resourceToClose = resource;
                try {
                    byteArrayConsumer.accept(resourceToClose, resourceToClose.load());
                }
                finally {
                    if (resourceToClose == null) continue;
                    resourceToClose.close();
                }
            }
            catch (IOException e) {
                if (ignoreIOExceptions) continue;
                throw new IllegalArgumentException("Could not load resource " + resource, e);
            }
        }
    }

    @Deprecated
    public void forEachByteArray(ByteArrayConsumer byteArrayConsumer) {
        this.forEachByteArray(byteArrayConsumer, false);
    }

    public void forEachByteArrayIgnoringIOException(ByteArrayConsumer byteArrayConsumer) {
        for (Resource resource : this) {
            try {
                Resource resourceToClose = resource;
                try {
                    byteArrayConsumer.accept(resourceToClose, resourceToClose.load());
                }
                finally {
                    if (resourceToClose == null) continue;
                    resourceToClose.close();
                }
            }
            catch (IOException iOException) {}
        }
    }

    public void forEachByteArrayThrowingIOException(ByteArrayConsumerThrowsIOException byteArrayConsumerThrowsIOException) throws IOException {
        Iterator iterator = this.iterator();
        while (iterator.hasNext()) {
            Resource resource;
            Resource resourceToClose = resource = (Resource)iterator.next();
            try {
                byteArrayConsumerThrowsIOException.accept(resourceToClose, resourceToClose.load());
            }
            finally {
                if (resourceToClose == null) continue;
                resourceToClose.close();
            }
        }
    }

    @Deprecated
    public void forEachInputStream(InputStreamConsumer inputStreamConsumer, boolean ignoreIOExceptions) {
        for (Resource resource : this) {
            try {
                Resource resourceToClose = resource;
                try {
                    inputStreamConsumer.accept(resourceToClose, resourceToClose.open());
                }
                finally {
                    if (resourceToClose == null) continue;
                    resourceToClose.close();
                }
            }
            catch (IOException e) {
                if (ignoreIOExceptions) continue;
                throw new IllegalArgumentException("Could not load resource " + resource, e);
            }
        }
    }

    @Deprecated
    public void forEachInputStream(InputStreamConsumer inputStreamConsumer) {
        this.forEachInputStream(inputStreamConsumer, false);
    }

    public void forEachInputStreamIgnoringIOException(InputStreamConsumer inputStreamConsumer) {
        for (Resource resource : this) {
            try {
                Resource resourceToClose = resource;
                try {
                    inputStreamConsumer.accept(resourceToClose, resourceToClose.open());
                }
                finally {
                    if (resourceToClose == null) continue;
                    resourceToClose.close();
                }
            }
            catch (IOException iOException) {}
        }
    }

    public void forEachInputStreamThrowingIOException(InputStreamConsumerThrowsIOException inputStreamConsumerThrowsIOException) throws IOException {
        Iterator iterator = this.iterator();
        while (iterator.hasNext()) {
            Resource resource;
            Resource resourceToClose = resource = (Resource)iterator.next();
            try {
                inputStreamConsumerThrowsIOException.accept(resourceToClose, resourceToClose.open());
            }
            finally {
                if (resourceToClose == null) continue;
                resourceToClose.close();
            }
        }
    }

    @Deprecated
    public void forEachByteBuffer(ByteBufferConsumer byteBufferConsumer, boolean ignoreIOExceptions) {
        for (Resource resource : this) {
            try {
                Resource resourceToClose = resource;
                try {
                    byteBufferConsumer.accept(resourceToClose, resourceToClose.read());
                }
                finally {
                    if (resourceToClose == null) continue;
                    resourceToClose.close();
                }
            }
            catch (IOException e) {
                if (ignoreIOExceptions) continue;
                throw new IllegalArgumentException("Could not load resource " + resource, e);
            }
        }
    }

    @Deprecated
    public void forEachByteBuffer(ByteBufferConsumer byteBufferConsumer) {
        this.forEachByteBuffer(byteBufferConsumer, false);
    }

    public void forEachByteBufferIgnoringIOException(ByteBufferConsumer byteBufferConsumer) {
        for (Resource resource : this) {
            try {
                Resource resourceToClose = resource;
                try {
                    byteBufferConsumer.accept(resourceToClose, resourceToClose.read());
                }
                finally {
                    if (resourceToClose == null) continue;
                    resourceToClose.close();
                }
            }
            catch (IOException iOException) {}
        }
    }

    public void forEachByteBufferThrowingIOException(ByteBufferConsumerThrowsIOException byteBufferConsumerThrowsIOException) throws IOException {
        Iterator iterator = this.iterator();
        while (iterator.hasNext()) {
            Resource resource;
            Resource resourceToClose = resource = (Resource)iterator.next();
            try {
                byteBufferConsumerThrowsIOException.accept(resourceToClose, resourceToClose.read());
            }
            finally {
                if (resourceToClose == null) continue;
                resourceToClose.close();
            }
        }
    }

    @Override
    public void close() {
        for (Resource resource : this) {
            resource.close();
        }
    }

    static {
        EMPTY_LIST.makeUnmodifiable();
        CLASSFILE_FILTER = new ResourceFilter(){

            @Override
            public boolean accept(Resource resource) {
                String path = resource.getPath();
                if (!path.endsWith(".class") || path.length() < 7) {
                    return false;
                }
                char c = path.charAt(path.length() - 7);
                return c != '/' && c != '.';
            }
        };
    }

    @FunctionalInterface
    public static interface ByteBufferConsumerThrowsIOException {
        public void accept(Resource var1, ByteBuffer var2) throws IOException;
    }

    @FunctionalInterface
    public static interface ByteBufferConsumer {
        public void accept(Resource var1, ByteBuffer var2);
    }

    @FunctionalInterface
    public static interface InputStreamConsumerThrowsIOException {
        public void accept(Resource var1, InputStream var2) throws IOException;
    }

    @FunctionalInterface
    public static interface InputStreamConsumer {
        public void accept(Resource var1, InputStream var2);
    }

    @FunctionalInterface
    public static interface ByteArrayConsumerThrowsIOException {
        public void accept(Resource var1, byte[] var2) throws IOException;
    }

    @FunctionalInterface
    public static interface ByteArrayConsumer {
        public void accept(Resource var1, byte[] var2);
    }

    @FunctionalInterface
    public static interface ResourceFilter {
        public boolean accept(Resource var1);
    }
}

