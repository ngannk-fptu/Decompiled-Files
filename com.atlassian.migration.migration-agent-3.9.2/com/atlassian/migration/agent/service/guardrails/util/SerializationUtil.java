/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 */
package com.atlassian.migration.agent.service.guardrails.util;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.migration.agent.json.JsonSerializingException;
import com.atlassian.migration.agent.json.Jsons;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;

public final class SerializationUtil {
    @VisibleForTesting
    static final String TMP_SUFFIX = ".tmp";

    private SerializationUtil() {
    }

    public static void saveJson(File filePath, Object content) throws IOException {
        File parent = filePath.getParentFile();
        Path parentPath = parent.toPath();
        if (!parent.exists()) {
            Files.createDirectories(parentPath, new FileAttribute[0]);
        }
        Path tmpFile = parentPath.resolve(filePath.getName() + TMP_SUFFIX);
        SerializationUtil.delete(filePath.toPath());
        SerializationUtil.delete(tmpFile);
        try (OutputStream out = Files.newOutputStream(tmpFile, new OpenOption[0]);){
            Jsons.OBJECT_MAPPER.writeValue(out, content);
        }
        if (!tmpFile.toFile().renameTo(filePath)) {
            throw new IOException("Can't rename temporary file");
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static <T> T deserializeJson(File file, Class<T> clazz) {
        try (FileInputStream in = new FileInputStream(file);){
            T t = Jsons.readValue((InputStream)in, clazz);
            return t;
        }
        catch (IOException e) {
            throw new JsonSerializingException("Failed to read a value from a JSON string", e);
        }
    }

    private static void delete(Path filePath) throws IOException {
        if (Files.exists(filePath, new LinkOption[0])) {
            Files.delete(filePath);
        }
    }

    public static boolean isTemporaryFileName(String name) {
        return name.endsWith(TMP_SUFFIX);
    }
}

