/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.Task;

public class PropertyHelperTask
extends Task {
    private PropertyHelper propertyHelper;
    private List<Object> delegates;

    public synchronized void addConfigured(PropertyHelper propertyHelper) {
        if (this.propertyHelper != null) {
            throw new BuildException("Only one PropertyHelper can be installed");
        }
        this.propertyHelper = propertyHelper;
    }

    public synchronized void addConfigured(PropertyHelper.Delegate delegate) {
        this.getAddDelegateList().add(delegate);
    }

    public DelegateElement createDelegate() {
        DelegateElement result = new DelegateElement();
        this.getAddDelegateList().add(result);
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void execute() throws BuildException {
        if (this.getProject() == null) {
            throw new BuildException("Project instance not set");
        }
        if (this.propertyHelper == null && this.delegates == null) {
            throw new BuildException("Either a new PropertyHelper or one or more PropertyHelper delegates are required");
        }
        PropertyHelper ph = this.propertyHelper;
        ph = ph == null ? PropertyHelper.getPropertyHelper(this.getProject()) : this.propertyHelper;
        PropertyHelper propertyHelper = ph;
        synchronized (propertyHelper) {
            if (this.delegates != null) {
                for (Object o : this.delegates) {
                    PropertyHelper.Delegate delegate = o instanceof DelegateElement ? ((DelegateElement)o).resolve() : (PropertyHelper.Delegate)o;
                    this.log("Adding PropertyHelper delegate " + delegate, 4);
                    ph.add(delegate);
                }
            }
        }
        if (this.propertyHelper != null) {
            this.log("Installing PropertyHelper " + this.propertyHelper, 4);
            this.getProject().addReference("ant.PropertyHelper", this.propertyHelper);
        }
    }

    private synchronized List<Object> getAddDelegateList() {
        if (this.delegates == null) {
            this.delegates = new ArrayList<Object>();
        }
        return this.delegates;
    }

    public final class DelegateElement {
        private String refid;

        private DelegateElement() {
        }

        public String getRefid() {
            return this.refid;
        }

        public void setRefid(String refid) {
            this.refid = refid;
        }

        private PropertyHelper.Delegate resolve() {
            if (this.refid == null) {
                throw new BuildException("refid required for generic delegate");
            }
            return (PropertyHelper.Delegate)PropertyHelperTask.this.getProject().getReference(this.refid);
        }
    }
}

