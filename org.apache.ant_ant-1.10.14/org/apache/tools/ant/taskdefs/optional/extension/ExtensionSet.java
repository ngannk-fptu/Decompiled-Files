/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.extension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.optional.extension.Extension;
import org.apache.tools.ant.taskdefs.optional.extension.ExtensionAdapter;
import org.apache.tools.ant.taskdefs.optional.extension.ExtensionUtil;
import org.apache.tools.ant.taskdefs.optional.extension.LibFileSet;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Reference;

public class ExtensionSet
extends DataType {
    private final List<ExtensionAdapter> extensions = new ArrayList<ExtensionAdapter>();
    private final List<FileSet> extensionsFilesets = new ArrayList<FileSet>();

    public void addExtension(ExtensionAdapter extensionAdapter) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.setChecked(false);
        this.extensions.add(extensionAdapter);
    }

    public void addLibfileset(LibFileSet fileSet) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.setChecked(false);
        this.extensionsFilesets.add(fileSet);
    }

    public void addFileset(FileSet fileSet) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.setChecked(false);
        this.extensionsFilesets.add(fileSet);
    }

    public Extension[] toExtensions(Project proj) throws BuildException {
        if (this.isReference()) {
            return this.getRef().toExtensions(proj);
        }
        this.dieOnCircularReference();
        ArrayList<Extension> extensionsList = ExtensionUtil.toExtensions(this.extensions);
        ExtensionUtil.extractExtensions(proj, extensionsList, this.extensionsFilesets);
        return extensionsList.toArray(new Extension[0]);
    }

    @Override
    public void setRefid(Reference reference) throws BuildException {
        if (!this.extensions.isEmpty() || !this.extensionsFilesets.isEmpty()) {
            throw this.tooManyAttributes();
        }
        super.setRefid(reference);
    }

    @Override
    protected synchronized void dieOnCircularReference(Stack<Object> stk, Project p) throws BuildException {
        if (this.isChecked()) {
            return;
        }
        if (this.isReference()) {
            super.dieOnCircularReference(stk, p);
        } else {
            for (ExtensionAdapter extensionAdapter : this.extensions) {
                ExtensionSet.pushAndInvokeCircularReferenceCheck(extensionAdapter, stk, p);
            }
            for (FileSet fileSet : this.extensionsFilesets) {
                ExtensionSet.pushAndInvokeCircularReferenceCheck(fileSet, stk, p);
            }
            this.setChecked(true);
        }
    }

    private ExtensionSet getRef() {
        return this.getCheckedRef(ExtensionSet.class);
    }

    @Override
    public String toString() {
        return "ExtensionSet" + Arrays.asList(this.toExtensions(this.getProject()));
    }
}

