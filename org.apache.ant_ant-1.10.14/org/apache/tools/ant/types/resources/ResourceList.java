/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.filters.util.ChainReaderHelper;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.FilterChain;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.AppendableResourceCollection;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.Resources;
import org.apache.tools.ant.types.resources.URLResource;
import org.apache.tools.ant.types.resources.Union;

public class ResourceList
extends DataType
implements ResourceCollection {
    private final Vector<FilterChain> filterChains = new Vector();
    private final ArrayList<ResourceCollection> textDocuments = new ArrayList();
    private AppendableResourceCollection cachedResources = null;
    private String encoding = null;
    private File baseDir;
    private boolean preserveDuplicates = false;

    public void add(ResourceCollection rc) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.textDocuments.add(rc);
        this.setChecked(false);
    }

    public final void addFilterChain(FilterChain filter) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.filterChains.add(filter);
        this.setChecked(false);
    }

    public final void setEncoding(String encoding) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.encoding = encoding;
    }

    public final void setBasedir(File baseDir) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.baseDir = baseDir;
    }

    public final void setPreserveDuplicates(boolean preserveDuplicates) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.preserveDuplicates = preserveDuplicates;
    }

    @Override
    public void setRefid(Reference r) throws BuildException {
        if (this.encoding != null) {
            throw this.tooManyAttributes();
        }
        if (!this.filterChains.isEmpty() || !this.textDocuments.isEmpty()) {
            throw this.noChildrenAllowed();
        }
        super.setRefid(r);
    }

    @Override
    public final synchronized Iterator<Resource> iterator() {
        if (this.isReference()) {
            return this.getRef().iterator();
        }
        return this.cache().iterator();
    }

    @Override
    public synchronized int size() {
        if (this.isReference()) {
            return this.getRef().size();
        }
        return this.cache().size();
    }

    @Override
    public synchronized boolean isFilesystemOnly() {
        if (this.isReference()) {
            return this.getRef().isFilesystemOnly();
        }
        return this.cache().isFilesystemOnly();
    }

    @Override
    protected synchronized void dieOnCircularReference(Stack<Object> stk, Project p) throws BuildException {
        if (this.isChecked()) {
            return;
        }
        if (this.isReference()) {
            super.dieOnCircularReference(stk, p);
        } else {
            for (ResourceCollection resourceCollection : this.textDocuments) {
                if (!(resourceCollection instanceof DataType)) continue;
                ResourceList.pushAndInvokeCircularReferenceCheck((DataType)((Object)resourceCollection), stk, p);
            }
            for (FilterChain filterChain : this.filterChains) {
                ResourceList.pushAndInvokeCircularReferenceCheck(filterChain, stk, p);
            }
            this.setChecked(true);
        }
    }

    private ResourceList getRef() {
        return this.getCheckedRef(ResourceList.class);
    }

    private AppendableResourceCollection newResourceCollection() {
        if (this.preserveDuplicates) {
            Resources resources = new Resources();
            resources.setCache(true);
            return resources;
        }
        Union union = new Union();
        union.setCache(true);
        return union;
    }

    private synchronized ResourceCollection cache() {
        if (this.cachedResources == null) {
            this.dieOnCircularReference();
            this.cachedResources = this.newResourceCollection();
            this.textDocuments.stream().flatMap(ResourceCollection::stream).map(this::read).forEach(this.cachedResources::add);
        }
        return this.cachedResources;
    }

    private ResourceCollection read(Resource r) {
        AppendableResourceCollection appendableResourceCollection;
        BufferedReader reader = new BufferedReader(this.open(r));
        try {
            AppendableResourceCollection streamResources = this.newResourceCollection();
            reader.lines().map(this::parse).forEach(streamResources::add);
            appendableResourceCollection = streamResources;
        }
        catch (Throwable throwable) {
            try {
                try {
                    reader.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (IOException ioe) {
                throw new BuildException("Unable to read resource " + r.getName() + ": " + ioe, ioe, this.getLocation());
            }
        }
        reader.close();
        return appendableResourceCollection;
    }

    private Reader open(Resource r) throws IOException {
        ChainReaderHelper crh = new ChainReaderHelper();
        crh.setPrimaryReader(new InputStreamReader((InputStream)new BufferedInputStream(r.getInputStream()), this.encoding == null ? Charset.defaultCharset() : Charset.forName(this.encoding)));
        crh.setFilterChains(this.filterChains);
        crh.setProject(this.getProject());
        return crh.getAssembledReader();
    }

    private Resource parse(String line) {
        PropertyHelper propertyHelper = PropertyHelper.getPropertyHelper(this.getProject());
        Object expanded = propertyHelper.parseProperties(line);
        if (expanded instanceof Resource) {
            return (Resource)expanded;
        }
        String expandedLine = expanded.toString();
        if (expandedLine.contains(":")) {
            try {
                return new URLResource(expandedLine);
            }
            catch (BuildException buildException) {
                // empty catch block
            }
        }
        if (this.baseDir != null) {
            FileResource fr = new FileResource(this.baseDir, expandedLine);
            fr.setProject(this.getProject());
            return fr;
        }
        return new FileResource(this.getProject(), expandedLine);
    }
}

