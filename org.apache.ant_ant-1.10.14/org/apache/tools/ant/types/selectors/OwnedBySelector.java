/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.selectors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.UserPrincipal;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.selectors.FileSelector;

public class OwnedBySelector
implements FileSelector {
    private String owner;
    private boolean followSymlinks = true;

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setFollowSymlinks(boolean followSymlinks) {
        this.followSymlinks = followSymlinks;
    }

    @Override
    public boolean isSelected(File basedir, String filename, File file) {
        if (this.owner == null) {
            throw new BuildException("the owner attribute is required");
        }
        if (file != null) {
            try {
                UserPrincipal user = this.followSymlinks ? Files.getOwner(file.toPath(), new LinkOption[0]) : Files.getOwner(file.toPath(), LinkOption.NOFOLLOW_LINKS);
                return user != null && this.owner.equals(user.getName());
            }
            catch (IOException | UnsupportedOperationException exception) {
                // empty catch block
            }
        }
        return false;
    }
}

