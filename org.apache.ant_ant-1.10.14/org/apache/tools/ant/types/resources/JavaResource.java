/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.AbstractClasspathResource;
import org.apache.tools.ant.types.resources.URLProvider;

public class JavaResource
extends AbstractClasspathResource
implements URLProvider {
    public JavaResource() {
    }

    public JavaResource(String name, Path path) {
        this.setName(name);
        this.setClasspath(path);
    }

    @Override
    protected InputStream openInputStream(ClassLoader cl) throws IOException {
        InputStream inputStream;
        if (cl == null) {
            inputStream = ClassLoader.getSystemResourceAsStream(this.getName());
            if (inputStream == null) {
                throw new FileNotFoundException("No resource " + this.getName() + " on Ant's classpath");
            }
        } else {
            inputStream = cl.getResourceAsStream(this.getName());
            if (inputStream == null) {
                throw new FileNotFoundException("No resource " + this.getName() + " on the classpath " + cl);
            }
        }
        return inputStream;
    }

    @Override
    public URL getURL() {
        if (this.isReference()) {
            return this.getRef().getURL();
        }
        AbstractClasspathResource.ClassLoaderWithFlag classLoader = this.getClassLoader();
        if (classLoader.getLoader() == null) {
            return ClassLoader.getSystemResource(this.getName());
        }
        try {
            URL uRL = classLoader.getLoader().getResource(this.getName());
            return uRL;
        }
        finally {
            classLoader.cleanup();
        }
    }

    @Override
    public int compareTo(Resource another) {
        if (this.isReference()) {
            return this.getRef().compareTo(another);
        }
        if (another.getClass().equals(this.getClass())) {
            Path op;
            JavaResource otherjr = (JavaResource)another;
            if (!this.getName().equals(otherjr.getName())) {
                return this.getName().compareTo(otherjr.getName());
            }
            if (this.getLoader() != otherjr.getLoader()) {
                if (this.getLoader() == null) {
                    return -1;
                }
                if (otherjr.getLoader() == null) {
                    return 1;
                }
                return this.getLoader().getRefId().compareTo(otherjr.getLoader().getRefId());
            }
            Path p = this.getClasspath();
            if (p != (op = otherjr.getClasspath())) {
                if (p == null) {
                    return -1;
                }
                if (op == null) {
                    return 1;
                }
                return p.toString().compareTo(op.toString());
            }
            return 0;
        }
        return super.compareTo(another);
    }

    @Override
    protected JavaResource getRef() {
        return this.getCheckedRef(JavaResource.class);
    }
}

