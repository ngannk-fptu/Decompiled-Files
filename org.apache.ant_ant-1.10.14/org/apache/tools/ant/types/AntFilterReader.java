/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.Parameter;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

public final class AntFilterReader
extends DataType {
    private String className;
    private final List<Parameter> parameters = new ArrayList<Parameter>();
    private Path classpath;

    public void setClassName(String className) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.className = className;
    }

    public String getClassName() {
        if (this.isReference()) {
            return this.getRef().getClassName();
        }
        this.dieOnCircularReference();
        return this.className;
    }

    public void addParam(Parameter param) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.parameters.add(param);
    }

    public void setClasspath(Path classpath) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        if (this.classpath == null) {
            this.classpath = classpath;
        } else {
            this.classpath.append(classpath);
        }
        this.setChecked(false);
    }

    public Path createClasspath() {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        if (this.classpath == null) {
            this.classpath = new Path(this.getProject());
        }
        this.setChecked(false);
        return this.classpath.createPath();
    }

    public Path getClasspath() {
        if (this.isReference()) {
            this.getRef().getClasspath();
        }
        this.dieOnCircularReference();
        return this.classpath;
    }

    public void setClasspathRef(Reference r) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.createClasspath().setRefid(r);
    }

    public Parameter[] getParams() {
        if (this.isReference()) {
            this.getRef().getParams();
        }
        this.dieOnCircularReference();
        return this.parameters.toArray(new Parameter[0]);
    }

    @Override
    public void setRefid(Reference r) throws BuildException {
        if (!this.parameters.isEmpty() || this.className != null || this.classpath != null) {
            throw this.tooManyAttributes();
        }
        super.setRefid(r);
    }

    @Override
    protected synchronized void dieOnCircularReference(Stack<Object> stk, Project p) throws BuildException {
        if (this.isChecked()) {
            return;
        }
        if (this.isReference()) {
            super.dieOnCircularReference(stk, p);
        } else {
            if (this.classpath != null) {
                AntFilterReader.pushAndInvokeCircularReferenceCheck(this.classpath, stk, p);
            }
            this.setChecked(true);
        }
    }

    private AntFilterReader getRef() {
        return this.getCheckedRef(AntFilterReader.class);
    }
}

