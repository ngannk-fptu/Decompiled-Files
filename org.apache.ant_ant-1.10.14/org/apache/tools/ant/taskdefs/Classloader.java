/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.File;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.StringUtils;

public class Classloader
extends Task {
    public static final String SYSTEM_LOADER_REF = "ant.coreLoader";
    private String name = null;
    private Path classpath;
    private boolean reset = false;
    private boolean parentFirst = true;
    private String parentName = null;

    public void setName(String name) {
        this.name = name;
    }

    public void setReset(boolean b) {
        this.reset = b;
    }

    @Deprecated
    public void setReverse(boolean b) {
        this.parentFirst = !b;
    }

    public void setParentFirst(boolean b) {
        this.parentFirst = b;
    }

    public void setParentName(String name) {
        this.parentName = name;
    }

    public void setClasspathRef(Reference pathRef) throws BuildException {
        this.classpath = (Path)pathRef.getReferencedObject(this.getProject());
    }

    public void setClasspath(Path classpath) {
        if (this.classpath == null) {
            this.classpath = classpath;
        } else {
            this.classpath.append(classpath);
        }
    }

    public Path createClasspath() {
        if (this.classpath == null) {
            this.classpath = new Path(null);
        }
        return this.classpath.createPath();
    }

    @Override
    public void execute() {
        try {
            boolean existingLoader;
            if ("only".equals(this.getProject().getProperty("build.sysclasspath")) && (this.name == null || SYSTEM_LOADER_REF.equals(this.name))) {
                this.log("Changing the system loader is disabled by build.sysclasspath=only", 1);
                return;
            }
            String loaderName = this.name == null ? SYSTEM_LOADER_REF : this.name;
            Object obj = this.getProject().getReference(loaderName);
            if (this.reset) {
                obj = null;
            }
            if (obj != null && !(obj instanceof AntClassLoader)) {
                this.log("Referenced object is not an AntClassLoader", 0);
                return;
            }
            AntClassLoader acl = (AntClassLoader)obj;
            boolean bl = existingLoader = acl != null;
            if (acl == null) {
                ClassLoader parent = null;
                if (this.parentName != null && !((parent = (ClassLoader)this.getProject().getReference(this.parentName)) instanceof ClassLoader)) {
                    parent = null;
                }
                if (parent == null) {
                    parent = this.getClass().getClassLoader();
                }
                if (this.name == null) {
                    // empty if block
                }
                this.getProject().log("Setting parent loader " + this.name + " " + parent + " " + this.parentFirst, 4);
                acl = AntClassLoader.newAntClassLoader(parent, this.getProject(), this.classpath, this.parentFirst);
                this.getProject().addReference(loaderName, acl);
                if (this.name == null) {
                    acl.addLoaderPackageRoot("org.apache.tools.ant.taskdefs.optional");
                    this.getProject().setCoreLoader(acl);
                }
            }
            if (existingLoader && this.classpath != null) {
                for (String path : this.classpath.list()) {
                    File f = new File(path);
                    if (!f.exists()) continue;
                    this.log("Adding to class loader " + acl + " " + f.getAbsolutePath(), 4);
                    acl.addPathElement(f.getAbsolutePath());
                }
            }
        }
        catch (Exception ex) {
            this.log(StringUtils.getStackTrace(ex), 0);
        }
    }
}

