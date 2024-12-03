/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources.selectors;

import java.util.Stack;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Comparison;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.Quantifier;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.types.resources.comparators.DelegatedResourceComparator;
import org.apache.tools.ant.types.resources.comparators.ResourceComparator;
import org.apache.tools.ant.types.resources.selectors.ResourceSelector;

public class Compare
extends DataType
implements ResourceSelector {
    private DelegatedResourceComparator comp = new DelegatedResourceComparator();
    private Quantifier against = Quantifier.ALL;
    private Comparison when = Comparison.EQUAL;
    private Union control;

    public synchronized void add(ResourceComparator c) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.comp.add(c);
        this.setChecked(false);
    }

    public synchronized void setAgainst(Quantifier against) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.against = against;
    }

    public synchronized void setWhen(Comparison when) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.when = when;
    }

    public synchronized ResourceCollection createControl() {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        if (this.control != null) {
            throw this.oneControl();
        }
        this.control = new Union();
        this.setChecked(false);
        return this.control;
    }

    @Override
    public synchronized boolean isSelected(Resource r) {
        if (this.isReference()) {
            return this.getRef().isSelected(r);
        }
        if (this.control == null) {
            throw this.oneControl();
        }
        this.dieOnCircularReference();
        int t = 0;
        int f = 0;
        for (Resource res : this.control) {
            if (this.when.evaluate(this.comp.compare(r, res))) {
                ++t;
                continue;
            }
            ++f;
        }
        return this.against.evaluate(t, f);
    }

    @Override
    protected synchronized void dieOnCircularReference(Stack<Object> stk, Project p) throws BuildException {
        if (this.isChecked()) {
            return;
        }
        if (this.isReference()) {
            super.dieOnCircularReference(stk, p);
        } else {
            if (this.control != null) {
                DataType.pushAndInvokeCircularReferenceCheck(this.control, stk, p);
            }
            DataType.pushAndInvokeCircularReferenceCheck(this.comp, stk, p);
            this.setChecked(true);
        }
    }

    private ResourceSelector getRef() {
        return this.getCheckedRef(ResourceSelector.class);
    }

    private BuildException oneControl() {
        return new BuildException("%s the <control> element should be specified exactly once.", super.toString());
    }
}

