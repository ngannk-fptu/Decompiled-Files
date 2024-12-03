/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.selectors;

import java.io.File;
import java.util.Enumeration;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.types.selectors.BaseSelectorContainer;
import org.apache.tools.ant.types.selectors.FileSelector;

public class SelectSelector
extends BaseSelectorContainer {
    private Object ifCondition;
    private Object unlessCondition;

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        if (this.hasSelectors()) {
            buf.append("{select");
            if (this.ifCondition != null) {
                buf.append(" if: ");
                buf.append(this.ifCondition);
            }
            if (this.unlessCondition != null) {
                buf.append(" unless: ");
                buf.append(this.unlessCondition);
            }
            buf.append(" ");
            buf.append(super.toString());
            buf.append("}");
        }
        return buf.toString();
    }

    private SelectSelector getRef() {
        return this.getCheckedRef(SelectSelector.class);
    }

    @Override
    public boolean hasSelectors() {
        if (this.isReference()) {
            return this.getRef().hasSelectors();
        }
        return super.hasSelectors();
    }

    @Override
    public int selectorCount() {
        if (this.isReference()) {
            return this.getRef().selectorCount();
        }
        return super.selectorCount();
    }

    @Override
    public FileSelector[] getSelectors(Project p) {
        if (this.isReference()) {
            return this.getRef().getSelectors(p);
        }
        return super.getSelectors(p);
    }

    @Override
    public Enumeration<FileSelector> selectorElements() {
        if (this.isReference()) {
            return this.getRef().selectorElements();
        }
        return super.selectorElements();
    }

    @Override
    public void appendSelector(FileSelector selector) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        super.appendSelector(selector);
    }

    @Override
    public void verifySettings() {
        int cnt = this.selectorCount();
        if (cnt < 0 || cnt > 1) {
            this.setError("Only one selector is allowed within the <selector> tag");
        }
    }

    public boolean passesConditions() {
        PropertyHelper ph = PropertyHelper.getPropertyHelper(this.getProject());
        return ph.testIfCondition(this.ifCondition) && ph.testUnlessCondition(this.unlessCondition);
    }

    public void setIf(Object ifProperty) {
        this.ifCondition = ifProperty;
    }

    public void setIf(String ifProperty) {
        this.setIf((Object)ifProperty);
    }

    public void setUnless(Object unlessProperty) {
        this.unlessCondition = unlessProperty;
    }

    public void setUnless(String unlessProperty) {
        this.setUnless((Object)unlessProperty);
    }

    @Override
    public boolean isSelected(File basedir, String filename, File file) {
        this.validate();
        if (!this.passesConditions()) {
            return false;
        }
        Enumeration<FileSelector> e = this.selectorElements();
        return !e.hasMoreElements() || e.nextElement().isSelected(basedir, filename, file);
    }
}

