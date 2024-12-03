/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.Resources;
import org.apache.tools.ant.util.PermissionUtils;
import org.apache.tools.ant.util.StringUtils;

public class SetPermissions
extends Task {
    private final Set<PosixFilePermission> permissions = EnumSet.noneOf(PosixFilePermission.class);
    private Resources resources = null;
    private boolean failonerror = true;
    private NonPosixMode nonPosixMode = NonPosixMode.fail;

    public void setPermissions(String perms) {
        if (perms != null) {
            Arrays.stream(perms.split(",")).map(String::trim).filter(s -> !s.isEmpty()).map(s -> Enum.valueOf(PosixFilePermission.class, s)).forEach(this.permissions::add);
        }
    }

    public void setMode(String octalString) {
        int mode = Integer.parseInt(octalString, 8);
        this.permissions.addAll(PermissionUtils.permissionsFromMode(mode));
    }

    public void setFailOnError(boolean failonerror) {
        this.failonerror = failonerror;
    }

    public void setNonPosixMode(NonPosixMode m) {
        this.nonPosixMode = m;
    }

    public void add(ResourceCollection rc) {
        if (this.resources == null) {
            this.resources = new Resources();
        }
        this.resources.add(rc);
    }

    @Override
    public void execute() {
        if (this.resources == null) {
            throw new BuildException("At least one resource-collection is required");
        }
        Resource currentResource = null;
        try {
            Iterator<Resource> iterator = this.resources.iterator();
            while (iterator.hasNext()) {
                Resource r;
                currentResource = r = iterator.next();
                try {
                    PermissionUtils.setPermissions(r, this.permissions, this::posixPermissionsNotSupported);
                }
                catch (IOException ioe) {
                    this.maybeThrowException(ioe, "Failed to set permissions on '%s' due to %s", r, ioe.getMessage());
                }
            }
        }
        catch (ClassCastException cce) {
            this.maybeThrowException(null, "some specified permissions are not of type PosixFilePermission: %s", StringUtils.join(this.permissions, (CharSequence)", "));
        }
        catch (SecurityException se) {
            this.maybeThrowException(null, "the SecurityManager denies role accessUserInformation or write access for SecurityManager.checkWrite for resource '%s'", currentResource);
        }
        catch (BuildException be) {
            this.maybeThrowException(be, be.getMessage(), new Object[0]);
        }
    }

    private void maybeThrowException(Exception exc, String msgFormat, Object ... msgArgs) {
        String msg = String.format(msgFormat, msgArgs);
        if (this.failonerror) {
            if (exc instanceof BuildException) {
                throw (BuildException)exc;
            }
            throw new BuildException(msg, exc);
        }
        this.log("Warning: " + msg, 0);
    }

    private void posixPermissionsNotSupported(Path p) {
        String msg = String.format("the associated path '%s' does not support the PosixFileAttributeView", p);
        switch (this.nonPosixMode) {
            case fail: {
                throw new BuildException(msg);
            }
            case pass: {
                this.log("Warning: " + msg, 0);
                break;
            }
            case tryDosOrFail: {
                this.tryDos(p, true);
                break;
            }
            case tryDosOrPass: {
                this.tryDos(p, false);
            }
        }
    }

    private void tryDos(Path p, boolean failIfDosIsNotSupported) {
        this.log("Falling back to DosFileAttributeView", 4);
        boolean readOnly = !this.isWritable();
        DosFileAttributeView view = Files.getFileAttributeView(p, DosFileAttributeView.class, new LinkOption[0]);
        if (view != null) {
            try {
                view.setReadOnly(readOnly);
            }
            catch (IOException ioe) {
                this.maybeThrowException(ioe, "Failed to set permissions on '%s' due to %s", p, ioe.getMessage());
            }
            catch (SecurityException uoe) {
                this.maybeThrowException(null, "the SecurityManager denies role accessUserInformation or write access for SecurityManager.checkWrite for resource '%s'", p);
            }
        } else {
            String msg = String.format("the associated path '%s' does not support the DosFileAttributeView", p);
            if (failIfDosIsNotSupported) {
                throw new BuildException(msg);
            }
            this.log("Warning: " + msg, 0);
        }
    }

    private boolean isWritable() {
        return this.permissions.contains((Object)PosixFilePermission.OWNER_WRITE) || this.permissions.contains((Object)PosixFilePermission.GROUP_WRITE) || this.permissions.contains((Object)PosixFilePermission.OTHERS_WRITE);
    }

    public static enum NonPosixMode {
        fail,
        pass,
        tryDosOrFail,
        tryDosOrPass;

    }
}

