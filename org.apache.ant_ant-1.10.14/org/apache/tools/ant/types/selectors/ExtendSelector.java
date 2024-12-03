/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.selectors;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Parameter;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.selectors.BaseSelector;
import org.apache.tools.ant.types.selectors.ExtendFileSelector;
import org.apache.tools.ant.types.selectors.FileSelector;

public class ExtendSelector
extends BaseSelector {
    private String classname = null;
    private FileSelector dynselector = null;
    private List<Parameter> parameters = Collections.synchronizedList(new ArrayList());
    private Path classpath = null;

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public void selectorCreate() {
        if (this.classname != null && !this.classname.isEmpty()) {
            try {
                Class<?> c;
                if (this.classpath == null) {
                    c = Class.forName(this.classname);
                } else {
                    AntClassLoader al = this.getProject().createClassLoader(this.classpath);
                    c = Class.forName(this.classname, true, al);
                }
                this.dynselector = c.asSubclass(FileSelector.class).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                Project p = this.getProject();
                if (p != null) {
                    p.setProjectReference(this.dynselector);
                }
            }
            catch (ClassNotFoundException cnfexcept) {
                this.setError("Selector " + this.classname + " not initialized, no such class");
            }
            catch (InstantiationException | NoSuchMethodException | InvocationTargetException iexcept) {
                this.setError("Selector " + this.classname + " not initialized, could not create class");
            }
            catch (IllegalAccessException iaexcept) {
                this.setError("Selector " + this.classname + " not initialized, class not accessible");
            }
        } else {
            this.setError("There is no classname specified");
        }
    }

    public void addParam(Parameter p) {
        this.parameters.add(p);
    }

    public final void setClasspath(Path classpath) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        if (this.classpath == null) {
            this.classpath = classpath;
        } else {
            this.classpath.append(classpath);
        }
    }

    public final Path createClasspath() {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        if (this.classpath == null) {
            this.classpath = new Path(this.getProject());
        }
        return this.classpath.createPath();
    }

    public final Path getClasspath() {
        return this.classpath;
    }

    public void setClasspathref(Reference r) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.createClasspath().setRefid(r);
    }

    @Override
    public void verifySettings() {
        if (this.dynselector == null) {
            this.selectorCreate();
        }
        if (this.classname == null || this.classname.length() < 1) {
            this.setError("The classname attribute is required");
        } else if (this.dynselector == null) {
            this.setError("Internal Error: The custom selector was not created");
        } else if (!(this.dynselector instanceof ExtendFileSelector) && !this.parameters.isEmpty()) {
            this.setError("Cannot set parameters on custom selector that does not implement ExtendFileSelector");
        }
    }

    @Override
    public boolean isSelected(File basedir, String filename, File file) throws BuildException {
        this.validate();
        if (!this.parameters.isEmpty() && this.dynselector instanceof ExtendFileSelector) {
            ((ExtendFileSelector)this.dynselector).setParameters(this.parameters.toArray(new Parameter[0]));
        }
        return this.dynselector.isSelected(basedir, filename, file);
    }
}

