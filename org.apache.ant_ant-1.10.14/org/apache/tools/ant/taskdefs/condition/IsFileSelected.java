/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.condition;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.types.selectors.AbstractSelectorContainer;
import org.apache.tools.ant.types.selectors.FileSelector;
import org.apache.tools.ant.util.FileUtils;

public class IsFileSelected
extends AbstractSelectorContainer
implements Condition {
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private File file;
    private File baseDir;

    public void setFile(File file) {
        this.file = file;
    }

    public void setBaseDir(File baseDir) {
        this.baseDir = baseDir;
    }

    @Override
    public void validate() {
        if (this.selectorCount() != 1) {
            throw new BuildException("Only one selector allowed");
        }
        super.validate();
    }

    @Override
    public boolean eval() {
        if (this.file == null) {
            throw new BuildException("file attribute not set");
        }
        this.validate();
        File myBaseDir = this.baseDir;
        if (myBaseDir == null) {
            myBaseDir = this.getProject().getBaseDir();
        }
        FileSelector f = this.getSelectors(this.getProject())[0];
        return f.isSelected(myBaseDir, FILE_UTILS.removeLeadingPath(myBaseDir, this.file), this.file);
    }
}

