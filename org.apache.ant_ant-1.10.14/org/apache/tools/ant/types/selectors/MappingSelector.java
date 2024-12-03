/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.selectors;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.types.selectors.BaseSelector;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.IdentityMapper;

public abstract class MappingSelector
extends BaseSelector {
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    protected File targetdir = null;
    protected Mapper mapperElement = null;
    protected FileNameMapper map = null;
    protected int granularity = (int)FILE_UTILS.getFileTimestampGranularity();

    public void setTargetdir(File targetdir) {
        this.targetdir = targetdir;
    }

    public Mapper createMapper() throws BuildException {
        if (this.map != null || this.mapperElement != null) {
            throw new BuildException("Cannot define more than one mapper");
        }
        this.mapperElement = new Mapper(this.getProject());
        return this.mapperElement;
    }

    public void addConfigured(FileNameMapper fileNameMapper) {
        if (this.map != null || this.mapperElement != null) {
            throw new BuildException("Cannot define more than one mapper");
        }
        this.map = fileNameMapper;
    }

    @Override
    public void verifySettings() {
        if (this.targetdir == null) {
            this.setError("The targetdir attribute is required.");
        }
        if (this.map == null) {
            if (this.mapperElement == null) {
                this.map = new IdentityMapper();
            } else {
                this.map = this.mapperElement.getImplementation();
                if (this.map == null) {
                    this.setError("Could not set <mapper> element.");
                }
            }
        }
    }

    @Override
    public boolean isSelected(File basedir, String filename, File file) {
        this.validate();
        String[] destfiles = this.map.mapFileName(filename);
        if (destfiles == null) {
            return false;
        }
        if (destfiles.length != 1 || destfiles[0] == null) {
            throw new BuildException("Invalid destination file results for " + this.targetdir.getName() + " with filename " + filename);
        }
        String destname = destfiles[0];
        File destfile = FILE_UTILS.resolveFile(this.targetdir, destname);
        return this.selectionTest(file, destfile);
    }

    protected abstract boolean selectionTest(File var1, File var2);

    public void setGranularity(int granularity) {
        this.granularity = granularity;
    }
}

