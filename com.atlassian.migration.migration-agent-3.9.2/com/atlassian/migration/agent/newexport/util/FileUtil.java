/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.newexport.util;

import com.atlassian.migration.agent.service.ServiceInitializeException;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class FileUtil {
    private FileUtil() {
    }

    public static String createExportDirectory(String tempDirFilePath) throws AccessDeniedException {
        return FileUtil.createExportDirectory(null, tempDirFilePath);
    }

    public static String createExportDirectory(String spaceKey, String tempDirFilePath) throws AccessDeniedException {
        Date now = new Date();
        String timestamp = MessageFormat.format("{0,date,yyyyMMdd}-{1,time,HHmmss}-", now, now);
        String dirName = tempDirFilePath;
        dirName = Objects.isNull(spaceKey) ? dirName + "/global-entities-export/" + timestamp + "/" : dirName + "/space-export/" + timestamp + spaceKey + "/";
        try {
            Files.createDirectories(Paths.get(dirName, new String[0]), new FileAttribute[0]);
        }
        catch (AccessDeniedException e) {
            throw new AccessDeniedException(String.format("We don't have sufficient permissions to write the file at the path: %s. Update the file write permissions for the parent directory and any nested directories within it.", tempDirFilePath));
        }
        catch (IOException e) {
            throw new ServiceInitializeException("Failed to create export directory " + dirName, e);
        }
        return dirName;
    }

    private static <KEY extends Comparable<KEY>> void serializeIds(Set<KEY> ids, Writer writer) throws IOException {
        List sorted = ids.stream().filter(Objects::nonNull).sorted().collect(Collectors.toList());
        for (Comparable id : sorted) {
            writer.write(id + "\n");
        }
    }
}

