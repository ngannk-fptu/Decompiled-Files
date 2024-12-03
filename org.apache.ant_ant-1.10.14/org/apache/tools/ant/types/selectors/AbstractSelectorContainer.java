/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.selectors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Stack;
import java.util.Vector;
import java.util.stream.Collectors;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.selectors.AndSelector;
import org.apache.tools.ant.types.selectors.BaseSelector;
import org.apache.tools.ant.types.selectors.ContainsRegexpSelector;
import org.apache.tools.ant.types.selectors.ContainsSelector;
import org.apache.tools.ant.types.selectors.DateSelector;
import org.apache.tools.ant.types.selectors.DependSelector;
import org.apache.tools.ant.types.selectors.DepthSelector;
import org.apache.tools.ant.types.selectors.DifferentSelector;
import org.apache.tools.ant.types.selectors.ExecutableSelector;
import org.apache.tools.ant.types.selectors.ExtendSelector;
import org.apache.tools.ant.types.selectors.FileSelector;
import org.apache.tools.ant.types.selectors.FilenameSelector;
import org.apache.tools.ant.types.selectors.MajoritySelector;
import org.apache.tools.ant.types.selectors.NoneSelector;
import org.apache.tools.ant.types.selectors.NotSelector;
import org.apache.tools.ant.types.selectors.OrSelector;
import org.apache.tools.ant.types.selectors.OwnedBySelector;
import org.apache.tools.ant.types.selectors.PosixGroupSelector;
import org.apache.tools.ant.types.selectors.PosixPermissionsSelector;
import org.apache.tools.ant.types.selectors.PresentSelector;
import org.apache.tools.ant.types.selectors.ReadableSelector;
import org.apache.tools.ant.types.selectors.SelectSelector;
import org.apache.tools.ant.types.selectors.SelectorContainer;
import org.apache.tools.ant.types.selectors.SizeSelector;
import org.apache.tools.ant.types.selectors.SymlinkSelector;
import org.apache.tools.ant.types.selectors.TypeSelector;
import org.apache.tools.ant.types.selectors.WritableSelector;
import org.apache.tools.ant.types.selectors.modifiedselector.ModifiedSelector;

public abstract class AbstractSelectorContainer
extends DataType
implements Cloneable,
SelectorContainer {
    private List<FileSelector> selectorsList = Collections.synchronizedList(new ArrayList());

    @Override
    public boolean hasSelectors() {
        if (this.isReference()) {
            return this.getRef().hasSelectors();
        }
        this.dieOnCircularReference();
        return !this.selectorsList.isEmpty();
    }

    @Override
    public int selectorCount() {
        if (this.isReference()) {
            return this.getRef().selectorCount();
        }
        this.dieOnCircularReference();
        return this.selectorsList.size();
    }

    @Override
    public FileSelector[] getSelectors(Project p) {
        if (this.isReference()) {
            return this.getRef(p).getSelectors(p);
        }
        this.dieOnCircularReference(p);
        return this.selectorsList.toArray(new FileSelector[0]);
    }

    @Override
    public Enumeration<FileSelector> selectorElements() {
        if (this.isReference()) {
            return this.getRef().selectorElements();
        }
        this.dieOnCircularReference();
        return Collections.enumeration(this.selectorsList);
    }

    @Override
    public String toString() {
        return this.selectorsList.stream().map(Object::toString).collect(Collectors.joining(", "));
    }

    @Override
    public void appendSelector(FileSelector selector) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.selectorsList.add(selector);
        this.setChecked(false);
    }

    public void validate() {
        if (this.isReference()) {
            this.getRef().validate();
        }
        this.dieOnCircularReference();
        this.selectorsList.stream().filter(BaseSelector.class::isInstance).map(BaseSelector.class::cast).forEach(BaseSelector::validate);
    }

    @Override
    public void addSelector(SelectSelector selector) {
        this.appendSelector(selector);
    }

    @Override
    public void addAnd(AndSelector selector) {
        this.appendSelector(selector);
    }

    @Override
    public void addOr(OrSelector selector) {
        this.appendSelector(selector);
    }

    @Override
    public void addNot(NotSelector selector) {
        this.appendSelector(selector);
    }

    @Override
    public void addNone(NoneSelector selector) {
        this.appendSelector(selector);
    }

    @Override
    public void addMajority(MajoritySelector selector) {
        this.appendSelector(selector);
    }

    @Override
    public void addDate(DateSelector selector) {
        this.appendSelector(selector);
    }

    @Override
    public void addSize(SizeSelector selector) {
        this.appendSelector(selector);
    }

    @Override
    public void addFilename(FilenameSelector selector) {
        this.appendSelector(selector);
    }

    @Override
    public void addCustom(ExtendSelector selector) {
        this.appendSelector(selector);
    }

    @Override
    public void addContains(ContainsSelector selector) {
        this.appendSelector(selector);
    }

    @Override
    public void addPresent(PresentSelector selector) {
        this.appendSelector(selector);
    }

    @Override
    public void addDepth(DepthSelector selector) {
        this.appendSelector(selector);
    }

    @Override
    public void addDepend(DependSelector selector) {
        this.appendSelector(selector);
    }

    @Override
    public void addDifferent(DifferentSelector selector) {
        this.appendSelector(selector);
    }

    @Override
    public void addType(TypeSelector selector) {
        this.appendSelector(selector);
    }

    @Override
    public void addContainsRegexp(ContainsRegexpSelector selector) {
        this.appendSelector(selector);
    }

    @Override
    public void addModified(ModifiedSelector selector) {
        this.appendSelector(selector);
    }

    public void addReadable(ReadableSelector r) {
        this.appendSelector(r);
    }

    public void addWritable(WritableSelector w) {
        this.appendSelector(w);
    }

    public void addExecutable(ExecutableSelector e) {
        this.appendSelector(e);
    }

    public void addSymlink(SymlinkSelector e) {
        this.appendSelector(e);
    }

    public void addOwnedBy(OwnedBySelector o) {
        this.appendSelector(o);
    }

    public void addPosixGroup(PosixGroupSelector o) {
        this.appendSelector(o);
    }

    public void addPosixPermissions(PosixPermissionsSelector o) {
        this.appendSelector(o);
    }

    @Override
    public void add(FileSelector selector) {
        this.appendSelector(selector);
    }

    @Override
    protected synchronized void dieOnCircularReference(Stack<Object> stk, Project p) {
        if (this.isChecked()) {
            return;
        }
        if (this.isReference()) {
            super.dieOnCircularReference(stk, p);
        } else {
            for (FileSelector fileSelector : this.selectorsList) {
                if (!(fileSelector instanceof DataType)) continue;
                AbstractSelectorContainer.pushAndInvokeCircularReferenceCheck((DataType)((Object)fileSelector), stk, p);
            }
            this.setChecked(true);
        }
    }

    @Override
    public synchronized Object clone() {
        if (this.isReference()) {
            return this.getRef().clone();
        }
        try {
            AbstractSelectorContainer sc = (AbstractSelectorContainer)super.clone();
            sc.selectorsList = new Vector<FileSelector>(this.selectorsList);
            return sc;
        }
        catch (CloneNotSupportedException e) {
            throw new BuildException(e);
        }
    }

    private AbstractSelectorContainer getRef(Project p) {
        return this.getCheckedRef(AbstractSelectorContainer.class, this.getDataTypeName(), p);
    }

    private AbstractSelectorContainer getRef() {
        return this.getCheckedRef(AbstractSelectorContainer.class);
    }
}

