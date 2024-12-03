/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.util.FileUtils;

@Deprecated
public class SymbolicLinkUtils {
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private static final SymbolicLinkUtils PRIMARY_INSTANCE = new SymbolicLinkUtils();

    public static SymbolicLinkUtils getSymbolicLinkUtils() {
        return PRIMARY_INSTANCE;
    }

    protected SymbolicLinkUtils() {
    }

    public boolean isSymbolicLink(File file) throws IOException {
        return this.isSymbolicLink(file.getParentFile(), file.getName());
    }

    public boolean isSymbolicLink(String name) throws IOException {
        return this.isSymbolicLink(new File(name));
    }

    public boolean isSymbolicLink(File parent, String name) throws IOException {
        File toTest = parent != null ? new File(parent.getCanonicalPath(), name) : new File(name);
        return !toTest.getAbsolutePath().equals(toTest.getCanonicalPath());
    }

    public boolean isDanglingSymbolicLink(String name) throws IOException {
        return this.isDanglingSymbolicLink(new File(name));
    }

    public boolean isDanglingSymbolicLink(File file) throws IOException {
        return this.isDanglingSymbolicLink(file.getParentFile(), file.getName());
    }

    public boolean isDanglingSymbolicLink(File parent, String name) throws IOException {
        File f = new File(parent, name);
        if (!f.exists()) {
            String localName = f.getName();
            String[] c = parent.list((d, n) -> localName.equals(n));
            return c != null && c.length > 0;
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void deleteSymbolicLink(File link, Task task) throws IOException {
        if (this.isDanglingSymbolicLink(link)) {
            if (!link.delete()) {
                throw new IOException("failed to remove dangling symbolic link " + link);
            }
            return;
        }
        if (!this.isSymbolicLink(link)) {
            return;
        }
        if (!link.exists()) {
            throw new FileNotFoundException("No such symbolic link: " + link);
        }
        File target = link.getCanonicalFile();
        if (task == null || target.getParentFile().canWrite()) {
            Project project = task == null ? null : task.getProject();
            File temp = FILE_UTILS.createTempFile(project, "symlink", ".tmp", target.getParentFile(), false, false);
            if (FILE_UTILS.isLeadingPath(target, link)) {
                link = new File(temp, FILE_UTILS.removeLeadingPath(target, link));
            }
            boolean renamedTarget = false;
            boolean success = false;
            try {
                try {
                    FILE_UTILS.rename(target, temp);
                    renamedTarget = true;
                }
                catch (IOException e) {
                    throw new IOException("Couldn't rename resource when attempting to delete '" + link + "'.  Reason: " + e.getMessage());
                }
                if (!link.delete()) {
                    throw new IOException("Couldn't delete symlink: " + link + " (was it a real file? is this not a UNIX system?)");
                }
                success = true;
            }
            finally {
                if (renamedTarget) {
                    try {
                        FILE_UTILS.rename(temp, target);
                    }
                    catch (IOException e) {
                        String msg = "Couldn't return resource " + temp + " to its original name: " + target.getAbsolutePath() + ". Reason: " + e.getMessage() + "\n THE RESOURCE'S NAME ON DISK HAS BEEN CHANGED BY THIS ERROR!\n";
                        if (success) {
                            throw new IOException(msg);
                        }
                        System.err.println(msg);
                    }
                }
            }
        }
        Execute.runCommand(task, "rm", link.getAbsolutePath());
    }
}

