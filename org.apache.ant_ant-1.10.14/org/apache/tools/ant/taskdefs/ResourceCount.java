/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.types.Comparison;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.ResourceCollection;

public class ResourceCount
extends Task
implements Condition {
    private static final String ONE_NESTED_MESSAGE = "ResourceCount can count resources from exactly one nested ResourceCollection.";
    private static final String COUNT_REQUIRED = "Use of the ResourceCount condition requires that the count attribute be set.";
    private ResourceCollection rc;
    private Comparison when = Comparison.EQUAL;
    private Integer count;
    private String property;

    public void add(ResourceCollection r) {
        if (this.rc != null) {
            throw new BuildException(ONE_NESTED_MESSAGE);
        }
        this.rc = r;
    }

    public void setRefid(Reference r) {
        Object o = r.getReferencedObject();
        if (!(o instanceof ResourceCollection)) {
            throw new BuildException("%s doesn't denote a ResourceCollection", r.getRefId());
        }
        this.add((ResourceCollection)o);
    }

    @Override
    public void execute() {
        if (this.rc == null) {
            throw new BuildException(ONE_NESTED_MESSAGE);
        }
        if (this.property == null) {
            this.log("resource count = " + this.rc.size());
        } else {
            this.getProject().setNewProperty(this.property, Integer.toString(this.rc.size()));
        }
    }

    @Override
    public boolean eval() {
        if (this.rc == null) {
            throw new BuildException(ONE_NESTED_MESSAGE);
        }
        if (this.count == null) {
            throw new BuildException(COUNT_REQUIRED);
        }
        return this.when.evaluate(Integer.valueOf(this.rc.size()).compareTo(this.count));
    }

    public void setCount(int c) {
        this.count = c;
    }

    public void setWhen(Comparison c) {
        this.when = c;
    }

    public void setProperty(String p) {
        this.property = p;
    }
}

