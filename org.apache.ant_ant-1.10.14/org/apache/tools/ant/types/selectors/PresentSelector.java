/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.selectors;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.types.selectors.BaseSelector;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.IdentityMapper;

public class PresentSelector
extends BaseSelector {
    private File targetdir = null;
    private Mapper mapperElement = null;
    private FileNameMapper map = null;
    private boolean destmustexist = true;

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder("{presentselector targetdir: ");
        if (this.targetdir == null) {
            buf.append("NOT YET SET");
        } else {
            buf.append(this.targetdir.getName());
        }
        buf.append(" present: ");
        if (this.destmustexist) {
            buf.append("both");
        } else {
            buf.append("srconly");
        }
        if (this.map != null) {
            buf.append(this.map.toString());
        } else if (this.mapperElement != null) {
            buf.append(this.mapperElement.toString());
        }
        buf.append("}");
        return buf.toString();
    }

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

    public void setPresent(FilePresence fp) {
        if (fp.getIndex() == 0) {
            this.destmustexist = false;
        }
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
            throw new BuildException("Invalid destination file results for " + this.targetdir + " with filename " + filename);
        }
        String destname = destfiles[0];
        File destfile = FileUtils.getFileUtils().resolveFile(this.targetdir, destname);
        return destfile.exists() == this.destmustexist;
    }

    public static class FilePresence
    extends EnumeratedAttribute {
        @Override
        public String[] getValues() {
            return new String[]{"srconly", "both"};
        }
    }
}

