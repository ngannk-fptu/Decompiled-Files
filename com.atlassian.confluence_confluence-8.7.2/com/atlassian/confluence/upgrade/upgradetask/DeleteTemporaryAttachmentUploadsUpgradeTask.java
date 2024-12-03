/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.AbstractUpgradeTask
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.confluence.upgrade.AbstractUpgradeTask;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteTemporaryAttachmentUploadsUpgradeTask
extends AbstractUpgradeTask {
    private static final String BUILD_NUMBER = "8401";
    private static final Pattern FILE_PATTERN = Pattern.compile("^attachment-.*\\.tmp$");
    private static final Logger logger = LoggerFactory.getLogger(DeleteTemporaryAttachmentUploadsUpgradeTask.class);

    public String getBuildNumber() {
        return BUILD_NUMBER;
    }

    public void doUpgrade() {
        try {
            String tempDirProperty = System.getProperty("java.io.tmpdir");
            if (tempDirProperty == null) {
                logger.warn("Wasn't able to get temp java directory - skipping *.tmp file removal");
                return;
            }
            File tempDir = new File(tempDirProperty);
            if (!tempDir.exists() || !tempDir.isDirectory()) {
                logger.warn("Unable to proceed because temp directory doesn't exist or is not a directory");
                return;
            }
            try (Stream<Path> filesStream = Files.list(Paths.get(tempDirProperty, new String[0]));){
                Set filesToRemove = filesStream.filter(p -> FILE_PATTERN.matcher(p.toFile().getName()).matches()).filter(x$0 -> Files.isRegularFile(x$0, new LinkOption[0])).collect(Collectors.toSet());
                logger.info("Deleting {} files matching pattern {} in directory {}. It might take some time.", new Object[]{filesToRemove.size(), FILE_PATTERN, tempDirProperty});
                int failedRemovals = 0;
                for (Path pathToRemove : filesToRemove) {
                    try {
                        Files.deleteIfExists(pathToRemove);
                    }
                    catch (IOException e) {
                        ++failedRemovals;
                        logger.debug("Failed to remove file {}", (Object)pathToRemove, (Object)e);
                    }
                }
                logger.info("Successfully deleted {} files and failed on removal of {} files", (Object)filesToRemove.size(), (Object)failedRemovals);
            }
        }
        catch (Exception e) {
            logger.warn("Failed to remove temporary attachment files in upgrade task. This is not critical for the system, therefore this upgrade task will finish successfully.", (Throwable)e);
        }
    }

    public boolean breaksBackwardCompatibility() {
        return false;
    }

    public boolean runOnSpaceImport() {
        return false;
    }
}

