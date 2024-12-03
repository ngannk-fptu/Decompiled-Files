/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.selectors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.PosixFilePermissions;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.selectors.FileSelector;
import org.apache.tools.ant.util.PermissionUtils;

public class PosixPermissionsSelector
implements FileSelector {
    private String permissions;
    private boolean followSymlinks = true;

    public void setPermissions(String permissions) {
        if (permissions.length() == 3 && permissions.matches("^[0-7]+$")) {
            this.permissions = PosixFilePermissions.toString(PermissionUtils.permissionsFromMode(Integer.parseInt(permissions, 8)));
            return;
        }
        try {
            this.permissions = PosixFilePermissions.toString(PosixFilePermissions.fromString(permissions));
        }
        catch (IllegalArgumentException ex) {
            throw new BuildException("the permissions attribute " + permissions + " is invalid", ex);
        }
    }

    public void setFollowSymlinks(boolean followSymlinks) {
        this.followSymlinks = followSymlinks;
    }

    @Override
    public boolean isSelected(File basedir, String filename, File file) {
        if (this.permissions == null) {
            throw new BuildException("the permissions attribute is required");
        }
        try {
            return PosixFilePermissions.toString(this.followSymlinks ? Files.getPosixFilePermissions(file.toPath(), new LinkOption[0]) : Files.getPosixFilePermissions(file.toPath(), LinkOption.NOFOLLOW_LINKS)).equals(this.permissions);
        }
        catch (IOException iOException) {
            return false;
        }
    }
}

