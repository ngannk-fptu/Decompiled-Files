/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.util.resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.resource.EmptyResource;
import org.eclipse.jetty.util.resource.Resource;

public class ResourceCollection
extends Resource {
    private List<Resource> _resources;

    public ResourceCollection() {
        this._resources = new ArrayList<Resource>();
    }

    public ResourceCollection(Resource ... resources) {
        this(Arrays.asList(resources));
    }

    public ResourceCollection(Collection<Resource> resources) {
        this._resources = new ArrayList<Resource>();
        for (Resource r : resources) {
            if (r == null) continue;
            if (r instanceof ResourceCollection) {
                this._resources.addAll(((ResourceCollection)r).getResources());
                continue;
            }
            this.assertResourceValid(r);
            this._resources.add(r);
        }
    }

    public ResourceCollection(String[] resources) {
        this._resources = new ArrayList<Resource>();
        if (resources == null || resources.length == 0) {
            return;
        }
        try {
            for (String strResource : resources) {
                if (strResource == null || strResource.length() == 0) {
                    throw new IllegalArgumentException("empty/null resource path not supported");
                }
                Resource resource = Resource.newResource(strResource);
                this.assertResourceValid(resource);
                this._resources.add(resource);
            }
            if (this._resources.isEmpty()) {
                throw new IllegalArgumentException("resources cannot be empty or null");
            }
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResourceCollection(String csvResources) throws IOException {
        this.setResources(csvResources);
    }

    public List<Resource> getResources() {
        return this._resources;
    }

    public void setResources(List<Resource> res) {
        this._resources = new ArrayList<Resource>();
        if (res.isEmpty()) {
            return;
        }
        this._resources.addAll(res);
    }

    public void setResources(Resource[] resources) {
        if (resources == null || resources.length == 0) {
            this._resources = null;
            return;
        }
        ArrayList<Resource> res = new ArrayList<Resource>();
        for (Resource resource : resources) {
            this.assertResourceValid(resource);
            res.add(resource);
        }
        this.setResources(res);
    }

    public void setResources(String resources) throws IOException {
        if (StringUtil.isBlank(resources)) {
            throw new IllegalArgumentException("String is blank");
        }
        List<Resource> list = Resource.fromList(resources, false);
        if (list.isEmpty()) {
            throw new IllegalArgumentException("String contains no entries");
        }
        ArrayList<Resource> ret = new ArrayList<Resource>();
        for (Resource resource : list) {
            this.assertResourceValid(resource);
            ret.add(resource);
        }
        this.setResources(ret);
    }

    @Override
    public Resource addPath(String path) throws IOException {
        this.assertResourcesSet();
        if (path == null) {
            throw new MalformedURLException("null path");
        }
        if (path.length() == 0 || "/".equals(path)) {
            return this;
        }
        ArrayList<Resource> resources = null;
        Resource addedResource = null;
        for (Resource res : this._resources) {
            addedResource = res.addPath(path);
            if (!addedResource.exists()) continue;
            if (!addedResource.isDirectory()) {
                return addedResource;
            }
            if (resources == null) {
                resources = new ArrayList<Resource>();
            }
            resources.add(addedResource);
        }
        if (resources == null) {
            if (addedResource != null) {
                return addedResource;
            }
            return EmptyResource.INSTANCE;
        }
        if (resources.size() == 1) {
            return (Resource)resources.get(0);
        }
        return new ResourceCollection((Collection<Resource>)resources);
    }

    @Override
    public boolean delete() throws SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean exists() {
        this.assertResourcesSet();
        for (Resource r : this._resources) {
            if (!r.exists()) continue;
            return true;
        }
        return false;
    }

    @Override
    public File getFile() throws IOException {
        this.assertResourcesSet();
        for (Resource r : this._resources) {
            File f = r.getFile();
            if (f == null) continue;
            return f;
        }
        return null;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        this.assertResourcesSet();
        for (Resource r : this._resources) {
            InputStream is;
            if (!r.exists() || (is = r.getInputStream()) == null) continue;
            return is;
        }
        throw new FileNotFoundException("Resource does not exist");
    }

    @Override
    public ReadableByteChannel getReadableByteChannel() throws IOException {
        this.assertResourcesSet();
        for (Resource r : this._resources) {
            ReadableByteChannel channel = r.getReadableByteChannel();
            if (channel == null) continue;
            return channel;
        }
        return null;
    }

    @Override
    public String getName() {
        this.assertResourcesSet();
        for (Resource r : this._resources) {
            String name = r.getName();
            if (name == null) continue;
            return name;
        }
        return null;
    }

    @Override
    public URI getURI() {
        this.assertResourcesSet();
        for (Resource r : this._resources) {
            URI uri = r.getURI();
            if (uri == null) continue;
            return uri;
        }
        return null;
    }

    @Override
    public boolean isDirectory() {
        this.assertResourcesSet();
        return true;
    }

    @Override
    public long lastModified() {
        this.assertResourcesSet();
        for (Resource r : this._resources) {
            long lm = r.lastModified();
            if (lm == -1L) continue;
            return lm;
        }
        return -1L;
    }

    @Override
    public long length() {
        return -1L;
    }

    @Override
    public String[] list() {
        this.assertResourcesSet();
        HashSet set = new HashSet();
        for (Resource r : this._resources) {
            String[] list = r.list();
            if (list == null) continue;
            Collections.addAll(set, list);
        }
        Object[] result = set.toArray(new String[0]);
        Arrays.sort(result);
        return result;
    }

    @Override
    public void close() {
        this.assertResourcesSet();
        for (Resource r : this._resources) {
            r.close();
        }
    }

    @Override
    public boolean renameTo(Resource dest) throws SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void copyTo(File destination) throws IOException {
        this.assertResourcesSet();
        int r = this._resources.size();
        while (r-- > 0) {
            this._resources.get(r).copyTo(destination);
        }
    }

    public String toString() {
        if (this._resources.isEmpty()) {
            return "[]";
        }
        return String.valueOf(this._resources);
    }

    @Override
    public boolean isContainedIn(Resource r) {
        return false;
    }

    private void assertResourcesSet() {
        if (this._resources == null || this._resources.isEmpty()) {
            throw new IllegalStateException("*resources* not set.");
        }
    }

    private void assertResourceValid(Resource resource) {
        if (resource == null) {
            throw new IllegalStateException("Null resource not supported");
        }
        if (!resource.exists() || !resource.isDirectory()) {
            throw new IllegalArgumentException(resource + " is not an existing directory.");
        }
    }
}

