/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.filters.util;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Vector;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.filters.BaseFilterReader;
import org.apache.tools.ant.filters.ChainableReader;
import org.apache.tools.ant.types.AntFilterReader;
import org.apache.tools.ant.types.FilterChain;
import org.apache.tools.ant.types.Parameterizable;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileUtils;

public final class ChainReaderHelper {
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    public Reader primaryReader;
    public int bufferSize = 8192;
    public Vector<FilterChain> filterChains = new Vector();
    private Project project = null;

    public ChainReaderHelper() {
    }

    public ChainReaderHelper(Project project, Reader primaryReader, Iterable<FilterChain> filterChains) {
        this.withProject(project).withPrimaryReader(primaryReader).withFilterChains(filterChains);
    }

    public void setPrimaryReader(Reader rdr) {
        this.primaryReader = rdr;
    }

    public ChainReaderHelper withPrimaryReader(Reader rdr) {
        this.setPrimaryReader(rdr);
        return this;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public ChainReaderHelper withProject(Project project) {
        this.setProject(project);
        return this;
    }

    public Project getProject() {
        return this.project;
    }

    public void setBufferSize(int size) {
        this.bufferSize = size;
    }

    public ChainReaderHelper withBufferSize(int size) {
        this.setBufferSize(size);
        return this;
    }

    public void setFilterChains(Vector<FilterChain> fchain) {
        this.filterChains = fchain;
    }

    public ChainReaderHelper withFilterChains(Iterable<FilterChain> filterChains) {
        Vector<FilterChain> fcs;
        if (filterChains instanceof Vector) {
            fcs = (Vector<FilterChain>)filterChains;
        } else {
            fcs = new Vector<FilterChain>();
            filterChains.forEach(fcs::add);
        }
        this.setFilterChains(fcs);
        return this;
    }

    public ChainReaderHelper with(Consumer<ChainReaderHelper> consumer) {
        consumer.accept(this);
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ChainReader getAssembledReader() throws BuildException {
        if (this.primaryReader == null) {
            throw new BuildException("primaryReader must not be null.");
        }
        Reader instream = this.primaryReader;
        ArrayList<AntClassLoader> classLoadersToCleanUp = new ArrayList<AntClassLoader>();
        List finalFilters = this.filterChains.stream().map(FilterChain::getFilterReaders).flatMap(Collection::stream).collect(Collectors.toList());
        if (!finalFilters.isEmpty()) {
            boolean success = false;
            try {
                for (Object o : finalFilters) {
                    if (o instanceof AntFilterReader) {
                        instream = this.expandReader((AntFilterReader)o, instream, classLoadersToCleanUp);
                        continue;
                    }
                    if (!(o instanceof ChainableReader)) continue;
                    this.setProjectOnObject(o);
                    instream = ((ChainableReader)o).chain(instream);
                    this.setProjectOnObject(instream);
                }
                success = true;
            }
            finally {
                if (!success && !classLoadersToCleanUp.isEmpty()) {
                    ChainReaderHelper.cleanUpClassLoaders(classLoadersToCleanUp);
                }
            }
        }
        return new ChainReader(instream, classLoadersToCleanUp);
    }

    private void setProjectOnObject(Object obj) {
        if (this.project == null) {
            return;
        }
        if (obj instanceof BaseFilterReader) {
            ((BaseFilterReader)obj).setProject(this.project);
            return;
        }
        this.project.setProjectReference(obj);
    }

    private static void cleanUpClassLoaders(List<AntClassLoader> loaders) {
        loaders.forEach(AntClassLoader::cleanup);
    }

    public String readFully(Reader rdr) throws IOException {
        return FileUtils.readFully(rdr, this.bufferSize);
    }

    private Reader expandReader(AntFilterReader filter, Reader ancestor, List<AntClassLoader> classLoadersToCleanUp) {
        String className = filter.getClassName();
        Path classpath = filter.getClasspath();
        if (className != null) {
            try {
                Class<FilterReader> clazz;
                try {
                    if (classpath == null) {
                        clazz = Class.forName(className).asSubclass(FilterReader.class);
                    } else {
                        AntClassLoader al = filter.getProject().createClassLoader(classpath);
                        classLoadersToCleanUp.add(al);
                        clazz = Class.forName(className, true, al).asSubclass(FilterReader.class);
                    }
                }
                catch (ClassCastException ex) {
                    throw new BuildException("%s does not extend %s", className, FilterReader.class.getName());
                }
                Optional<Constructor> ctor = Stream.of(clazz.getConstructors()).filter(c -> c.getParameterCount() == 1 && c.getParameterTypes()[0].isAssignableFrom(Reader.class)).findFirst();
                Object instream = ctor.orElseThrow(() -> new BuildException("%s does not define a public constructor that takes in a %s as its single argument.", className, Reader.class.getSimpleName())).newInstance(ancestor);
                this.setProjectOnObject(instream);
                if (Parameterizable.class.isAssignableFrom(clazz)) {
                    ((Parameterizable)instream).setParameters(filter.getParams());
                }
                return (Reader)instream;
            }
            catch (ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException ex) {
                throw new BuildException(ex);
            }
        }
        return ancestor;
    }

    public class ChainReader
    extends FilterReader {
        private List<AntClassLoader> cleanupLoaders;

        private ChainReader(Reader in, List<AntClassLoader> cleanupLoaders) {
            super(in);
            this.cleanupLoaders = cleanupLoaders;
        }

        public String readFully() throws IOException {
            return ChainReaderHelper.this.readFully(this);
        }

        @Override
        public void close() throws IOException {
            ChainReaderHelper.cleanUpClassLoaders(this.cleanupLoaders);
            super.close();
        }

        protected void finalize() throws Throwable {
            try {
                this.close();
            }
            finally {
                super.finalize();
            }
        }
    }
}

