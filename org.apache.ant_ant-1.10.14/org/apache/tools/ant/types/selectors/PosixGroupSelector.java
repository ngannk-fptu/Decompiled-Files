/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.selectors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributes;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.selectors.FileSelector;

public class PosixGroupSelector
implements FileSelector {
    private String group;
    private boolean followSymlinks = true;

    public void setGroup(String group) {
        this.group = group;
    }

    public void setFollowSymlinks(boolean followSymlinks) {
        this.followSymlinks = followSymlinks;
    }

    @Override
    public boolean isSelected(File basedir, String filename, File file) {
        if (this.group == null) {
            throw new BuildException("the group attribute is required");
        }
        try {
            GroupPrincipal actualGroup = this.followSymlinks ? Files.readAttributes(file.toPath(), PosixFileAttributes.class, new LinkOption[0]).group() : Files.readAttributes(file.toPath(), PosixFileAttributes.class, LinkOption.NOFOLLOW_LINKS).group();
            return actualGroup != null && actualGroup.getName().equals(this.group);
        }
        catch (IOException iOException) {
            return false;
        }
    }
}

