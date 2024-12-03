/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types;

import java.util.Stack;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.IdentityStack;

public abstract class DataType
extends ProjectComponent
implements Cloneable {
    @Deprecated
    protected Reference ref;
    @Deprecated
    protected boolean checked = true;

    public boolean isReference() {
        return this.ref != null;
    }

    public void setRefid(Reference ref) {
        this.ref = ref;
        this.checked = false;
    }

    protected String getDataTypeName() {
        return ComponentHelper.getElementName(this.getProject(), this, true);
    }

    protected void dieOnCircularReference() {
        this.dieOnCircularReference(this.getProject());
    }

    protected void dieOnCircularReference(Project p) {
        if (this.checked || !this.isReference()) {
            return;
        }
        this.dieOnCircularReference(new IdentityStack<Object>(this), p);
    }

    protected void dieOnCircularReference(Stack<Object> stack, Project project) throws BuildException {
        if (this.checked || !this.isReference()) {
            return;
        }
        Object o = this.ref.getReferencedObject(project);
        if (o instanceof DataType) {
            IdentityStack<Object> id = IdentityStack.getInstance(stack);
            if (id.contains(o)) {
                throw this.circularReference();
            }
            id.push(o);
            ((DataType)o).dieOnCircularReference(id, project);
            id.pop();
        }
        this.checked = true;
    }

    public static void invokeCircularReferenceCheck(DataType dt, Stack<Object> stk, Project p) {
        dt.dieOnCircularReference(stk, p);
    }

    public static void pushAndInvokeCircularReferenceCheck(DataType dt, Stack<Object> stk, Project p) {
        stk.push(dt);
        dt.dieOnCircularReference(stk, p);
        stk.pop();
    }

    @Deprecated
    protected <T> T getCheckedRef() {
        return this.getCheckedRef(this.getProject());
    }

    protected <T> T getCheckedRef(Class<T> requiredClass) {
        return this.getCheckedRef(requiredClass, this.getDataTypeName(), this.getProject());
    }

    @Deprecated
    protected <T> T getCheckedRef(Project p) {
        return (T)this.getCheckedRef(this.getClass(), this.getDataTypeName(), p);
    }

    protected <T> T getCheckedRef(Class<T> requiredClass, String dataTypeName) {
        return this.getCheckedRef(requiredClass, dataTypeName, this.getProject());
    }

    protected <T> T getCheckedRef(Class<T> requiredClass, String dataTypeName, Project project) {
        if (project == null) {
            throw new BuildException("No Project specified");
        }
        this.dieOnCircularReference(project);
        Object o = this.ref.getReferencedObject(project);
        if (requiredClass.isAssignableFrom(o.getClass())) {
            return o;
        }
        this.log("Class " + this.displayName(o.getClass()) + " is not a subclass of " + this.displayName(requiredClass), 3);
        throw new BuildException(this.ref.getRefId() + " doesn't denote a " + dataTypeName);
    }

    protected BuildException tooManyAttributes() {
        return new BuildException("You must not specify more than one attribute when using refid");
    }

    protected BuildException noChildrenAllowed() {
        return new BuildException("You must not specify nested elements when using refid");
    }

    protected BuildException circularReference() {
        return new BuildException("This data type contains a circular reference.");
    }

    protected boolean isChecked() {
        return this.checked;
    }

    protected void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Reference getRefid() {
        return this.ref;
    }

    protected void checkAttributesAllowed() {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
    }

    protected void checkChildrenAllowed() {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
    }

    public String toString() {
        String d = this.getDescription();
        return d == null ? this.getDataTypeName() : this.getDataTypeName() + " " + d;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        DataType dt = (DataType)super.clone();
        dt.setDescription(this.getDescription());
        if (this.getRefid() != null) {
            dt.setRefid(this.getRefid());
        }
        dt.setChecked(this.isChecked());
        return dt;
    }

    private String displayName(Class<?> clazz) {
        return clazz.getName() + " (loaded via " + clazz.getClassLoader() + ")";
    }
}

