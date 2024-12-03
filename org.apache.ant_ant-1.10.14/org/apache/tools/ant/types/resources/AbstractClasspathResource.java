/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.JavaResource;
import org.apache.tools.ant.util.FileUtils;

public abstract class AbstractClasspathResource
extends Resource {
    private Path classpath;
    private Reference loader;
    private boolean parentFirst = true;

    public void setClasspath(Path classpath) {
        this.checkAttributesAllowed();
        if (this.classpath == null) {
            this.classpath = classpath;
        } else {
            this.classpath.append(classpath);
        }
        this.setChecked(false);
    }

    public Path createClasspath() {
        this.checkChildrenAllowed();
        if (this.classpath == null) {
            this.classpath = new Path(this.getProject());
        }
        this.setChecked(false);
        return this.classpath.createPath();
    }

    public void setClasspathRef(Reference r) {
        this.checkAttributesAllowed();
        this.createClasspath().setRefid(r);
    }

    public Path getClasspath() {
        if (this.isReference()) {
            return this.getRef().getClasspath();
        }
        this.dieOnCircularReference();
        return this.classpath;
    }

    public Reference getLoader() {
        if (this.isReference()) {
            return this.getRef().getLoader();
        }
        this.dieOnCircularReference();
        return this.loader;
    }

    public void setLoaderRef(Reference r) {
        this.checkAttributesAllowed();
        this.loader = r;
    }

    public void setParentFirst(boolean b) {
        this.parentFirst = b;
    }

    @Override
    public void setRefid(Reference r) {
        if (this.loader != null || this.classpath != null) {
            throw this.tooManyAttributes();
        }
        super.setRefid(r);
    }

    @Override
    public boolean isExists() {
        boolean bl;
        block9: {
            if (this.isReference()) {
                return this.getRef().isExists();
            }
            this.dieOnCircularReference();
            InputStream is = this.getInputStream();
            try {
                boolean bl2 = bl = is != null;
                if (is == null) break block9;
            }
            catch (Throwable throwable) {
                try {
                    if (is != null) {
                        try {
                            is.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException ex) {
                    return false;
                }
            }
            is.close();
        }
        return bl;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (this.isReference()) {
            return this.getRef().getInputStream();
        }
        this.dieOnCircularReference();
        final ClassLoaderWithFlag classLoader = this.getClassLoader();
        return !classLoader.needsCleanup() ? this.openInputStream(classLoader.getLoader()) : new FilterInputStream(this.openInputStream(classLoader.getLoader())){

            @Override
            public void close() throws IOException {
                FileUtils.close(this.in);
                classLoader.cleanup();
            }

            protected void finalize() throws Throwable {
                try {
                    this.close();
                }
                finally {
                    super.finalize();
                }
            }
        };
    }

    protected ClassLoaderWithFlag getClassLoader() {
        ClassLoader cl = null;
        if (this.loader != null) {
            cl = (ClassLoader)this.loader.getReferencedObject();
        }
        boolean clNeedsCleanup = false;
        if (cl == null) {
            if (this.getClasspath() != null) {
                Path p = this.getClasspath().concatSystemClasspath("ignore");
                cl = this.parentFirst ? this.getProject().createClassLoader(p) : AntClassLoader.newAntClassLoader(this.getProject().getCoreLoader(), this.getProject(), p, false);
                clNeedsCleanup = this.loader == null;
            } else {
                cl = JavaResource.class.getClassLoader();
            }
            if (this.loader != null && cl != null) {
                this.getProject().addReference(this.loader.getRefId(), cl);
            }
        }
        return new ClassLoaderWithFlag(cl, clNeedsCleanup);
    }

    protected abstract InputStream openInputStream(ClassLoader var1) throws IOException;

    @Override
    protected synchronized void dieOnCircularReference(Stack<Object> stk, Project p) {
        if (this.isChecked()) {
            return;
        }
        if (this.isReference()) {
            super.dieOnCircularReference(stk, p);
        } else {
            if (this.classpath != null) {
                AbstractClasspathResource.pushAndInvokeCircularReferenceCheck(this.classpath, stk, p);
            }
            this.setChecked(true);
        }
    }

    @Override
    protected AbstractClasspathResource getRef() {
        return this.getCheckedRef(AbstractClasspathResource.class);
    }

    public static class ClassLoaderWithFlag {
        private final ClassLoader loader;
        private final boolean cleanup;

        ClassLoaderWithFlag(ClassLoader l, boolean needsCleanup) {
            this.loader = l;
            this.cleanup = needsCleanup && l instanceof AntClassLoader;
        }

        public ClassLoader getLoader() {
            return this.loader;
        }

        public boolean needsCleanup() {
            return this.cleanup;
        }

        public void cleanup() {
            if (this.cleanup) {
                ((AntClassLoader)this.loader).cleanup();
            }
        }
    }
}

