/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources;

import java.io.File;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Spliterators;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.AppendableResourceCollection;
import org.apache.tools.ant.types.resources.FailFast;

public class Resources
extends DataType
implements AppendableResourceCollection {
    public static final ResourceCollection NONE = new ResourceCollection(){

        @Override
        public boolean isFilesystemOnly() {
            return true;
        }

        @Override
        public Iterator<Resource> iterator() {
            return EMPTY_ITERATOR;
        }

        @Override
        public int size() {
            return 0;
        }
    };
    public static final Iterator<Resource> EMPTY_ITERATOR = Collections.emptyIterator();
    private List<ResourceCollection> rc;
    private Optional<Collection<Resource>> cacheColl = Optional.empty();
    private volatile boolean cache = false;

    public Resources() {
    }

    public Resources(Project project) {
        this.setProject(project);
    }

    public void setCache(boolean b) {
        this.cache = b;
    }

    @Override
    public synchronized void add(ResourceCollection c) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        if (c == null) {
            return;
        }
        if (this.rc == null) {
            this.rc = Collections.synchronizedList(new ArrayList());
        }
        this.rc.add(c);
        this.invalidateExistingIterators();
        this.cacheColl = Optional.empty();
        this.setChecked(false);
    }

    @Override
    public synchronized Iterator<Resource> iterator() {
        if (this.isReference()) {
            return this.getRef().iterator();
        }
        this.validate();
        return new FailFast(this, this.cacheColl.map(Iterable::iterator).orElseGet(() -> new MyIterator()));
    }

    @Override
    public synchronized int size() {
        if (this.isReference()) {
            return this.getRef().size();
        }
        this.validate();
        return this.cacheColl.isPresent() ? this.cacheColl.get().size() : (int)this.internalResources().count();
    }

    @Override
    public boolean isFilesystemOnly() {
        if (this.isReference()) {
            return this.getRef().isFilesystemOnly();
        }
        this.validate();
        return this.getNested().stream().allMatch(ResourceCollection::isFilesystemOnly);
    }

    @Override
    public synchronized String toString() {
        if (this.isReference()) {
            return this.getRef().toString();
        }
        this.validate();
        Stream stream = this.cache ? this.cacheColl.get().stream() : this.getNested().stream();
        return stream.map(String::valueOf).collect(Collectors.joining(File.pathSeparator));
    }

    @Override
    protected void dieOnCircularReference(Stack<Object> stk, Project p) throws BuildException {
        if (this.isReference()) {
            super.dieOnCircularReference(stk, p);
            return;
        }
        if (!this.isChecked()) {
            this.getNested().stream().filter(DataType.class::isInstance).map(DataType.class::cast).forEach(dt -> Resources.pushAndInvokeCircularReferenceCheck(dt, stk, p));
            this.setChecked(true);
        }
    }

    protected void invalidateExistingIterators() {
        FailFast.invalidate(this);
    }

    private ResourceCollection getRef() {
        return this.getCheckedRef(ResourceCollection.class);
    }

    private synchronized void validate() {
        this.dieOnCircularReference();
        if (this.cache && !this.cacheColl.isPresent()) {
            this.cacheColl = Optional.of(new MyCollection());
        }
    }

    private synchronized List<ResourceCollection> getNested() {
        return this.rc == null ? Collections.emptyList() : this.rc;
    }

    private synchronized Stream<Resource> internalResources() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new MyIterator(), 256), false);
    }

    private class MyCollection
    extends AbstractCollection<Resource> {
        private volatile Collection<Resource> cached;

        private MyCollection() {
        }

        @Override
        public int size() {
            return this.getCache().size();
        }

        @Override
        public Iterator<Resource> iterator() {
            return this.getCache().iterator();
        }

        private synchronized Collection<Resource> getCache() {
            if (this.cached == null) {
                this.cached = Resources.this.internalResources().collect(Collectors.toList());
            }
            return this.cached;
        }
    }

    private class MyIterator
    implements Iterator<Resource> {
        private Iterator<ResourceCollection> rci;
        private Iterator<Resource> ri;

        private MyIterator() {
            this.rci = Resources.this.getNested().iterator();
        }

        @Override
        public boolean hasNext() {
            boolean result;
            boolean bl = result = this.ri != null && this.ri.hasNext();
            while (!result && this.rci.hasNext()) {
                this.ri = this.rci.next().iterator();
                result = this.ri.hasNext();
            }
            return result;
        }

        @Override
        public Resource next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            return this.ri.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

