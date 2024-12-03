/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.FileUtils
 *  com.google.common.base.Preconditions
 *  io.atlassian.fugue.Either
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util.io;

import com.atlassian.confluence.impl.pages.attachments.filesystem.model.AttachmentRef;
import com.atlassian.confluence.schedule.jobs.filedeletion.DeferredFileDeletionQueue;
import com.atlassian.core.util.FileUtils;
import com.google.common.base.Preconditions;
import io.atlassian.fugue.Either;
import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceFileUtils {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceFileUtils.class);

    public static void moveDir(File srcDir, File destDir) throws IOException {
        boolean created;
        boolean deleted;
        Preconditions.checkArgument((boolean)srcDir.exists(), (Object)("Source dir " + srcDir + "does not exist"));
        File canonicalSrcDir = srcDir.getCanonicalFile();
        File canonicalDestDir = destDir.getCanonicalFile();
        if (canonicalSrcDir.equals(canonicalDestDir)) {
            return;
        }
        Preconditions.checkArgument((!ConfluenceFileUtils.destinationContainsSource(canonicalSrcDir, canonicalDestDir) ? 1 : 0) != 0, (Object)("Cannot move source dir " + canonicalSrcDir + " into destination dir " + canonicalDestDir + " since the destination contains the source"));
        Preconditions.checkArgument((!ConfluenceFileUtils.sourceContainsDestination(canonicalSrcDir, canonicalDestDir) ? 1 : 0) != 0, (Object)("Cannot move source dir " + canonicalSrcDir + " into destination dir " + canonicalDestDir + " since the source contains the destination"));
        File destParent = new File(canonicalDestDir.getParent());
        if (canonicalDestDir.exists() && !(deleted = FileUtils.deleteDir((File)canonicalDestDir))) {
            throw new IOException("Unable to delete destination dir " + canonicalDestDir);
        }
        if (!destParent.exists() && !(created = destParent.mkdirs())) {
            throw new IOException("Unable to create parent destination dir " + destParent);
        }
        if (!canonicalSrcDir.renameTo(canonicalDestDir)) {
            try {
                Thread.sleep(500L);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
            if (!FileUtils.moveDir((File)canonicalSrcDir, (File)canonicalDestDir)) {
                throw new IOException("Unable to move directory " + srcDir.toString() + " to " + destDir.toString() + " - on Windows this is most likely due to a file lock still being held.");
            }
        }
    }

    public static void moveDirWithCopyFallback(Either<AttachmentRef, AttachmentRef.Container> container, File srcDir, File destDir, DeferredFileDeletionQueue deferredFileDeletionQueue) throws IOException {
        try {
            ConfluenceFileUtils.moveDir(srcDir, destDir);
        }
        catch (IOException e) {
            log.debug("Failed to move {} to {}. Falling back to copying. {} is scheduled for later deletion.", new Object[]{srcDir, destDir, srcDir, e});
            FileUtils.copyDirectory((File)srcDir, (File)destDir, (boolean)true);
            deferredFileDeletionQueue.offer(container, srcDir);
        }
    }

    public static void moveDirWithCopyFallback(File srcDir, File destDir, DeferredFileDeletionQueue deferredFileDeletionQueue) throws IOException {
        ConfluenceFileUtils.moveDirWithCopyFallback(null, srcDir, destDir, deferredFileDeletionQueue);
    }

    private static boolean sourceContainsDestination(File absSrcDir, File absDestDir) {
        return ConfluenceFileUtils.dirContainsDir(absSrcDir, absDestDir);
    }

    private static boolean destinationContainsSource(File absSrcDir, File absDestDir) {
        return ConfluenceFileUtils.dirContainsDir(absDestDir, absSrcDir);
    }

    private static boolean dirContainsDir(File dirToFind, File pathToSearch) {
        for (File parent = pathToSearch.getParentFile(); parent != null; parent = parent.getParentFile()) {
            if (!parent.equals(dirToFind)) continue;
            return true;
        }
        return false;
    }

    public static boolean isChildOf(File dir, File child) {
        try {
            File dirCanonical = dir.getCanonicalFile();
            File targetCanonical = child.getCanonicalFile();
            for (File parent = targetCanonical.getParentFile(); parent != null; parent = parent.getParentFile()) {
                if (!dirCanonical.equals(parent)) continue;
                return true;
            }
        }
        catch (IOException e) {
            log.debug("Unable to construct the canonical path of file when trying to determine isChildOf", (Throwable)e);
        }
        return false;
    }

    public static String extractFileName(String pathname) {
        if (pathname == null) {
            return null;
        }
        return new File(pathname).getName();
    }
}

