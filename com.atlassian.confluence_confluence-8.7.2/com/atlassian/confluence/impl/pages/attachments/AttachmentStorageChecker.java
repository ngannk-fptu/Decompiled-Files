/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.dc.filestore.api.FileStore$Path
 *  com.atlassian.dc.filestore.api.FileStore$Reader
 *  com.atlassian.dc.filestore.api.FileStore$Writer
 */
package com.atlassian.confluence.impl.pages.attachments;

import com.atlassian.confluence.impl.pages.attachments.AttachmentV4Helper;
import com.atlassian.dc.filestore.api.FileStore;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class AttachmentStorageChecker {
    private static final Logger log = Logger.getLogger(AttachmentV4Helper.class.getSimpleName());
    private static final byte[] dummyContent = "Confluence".getBytes(StandardCharsets.UTF_8);

    private AttachmentStorageChecker() {
    }

    public static void testOperations(FileStore.Path rootPath) throws IOException {
        String dateTime = Instant.now().atZone(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss"));
        String testFile = String.format("connectivity-test-%s.txt", dateTime);
        FileStore.Path tempFilePath = rootPath.path(new String[]{testFile});
        AttachmentStorageChecker.testWrite(tempFilePath);
        AttachmentStorageChecker.testRead(tempFilePath);
        AttachmentStorageChecker.testList(rootPath, tempFilePath);
        AttachmentStorageChecker.testDelete(tempFilePath);
    }

    private static void testWrite(FileStore.Path tempFilePath) throws IOException {
        try {
            FileStore.Writer writer = tempFilePath.fileWriter();
            writer.write(dummyContent);
        }
        catch (IOException exception) {
            throw new IOException("Error performing write operation", exception);
        }
    }

    private static void testRead(FileStore.Path tempFilePath) throws IOException {
        try {
            FileStore.Reader reader = tempFilePath.fileReader();
            try (InputStream inputStream = reader.openInputStream();){
                byte[] readBackContent = inputStream.readAllBytes();
                if (!Arrays.equals(dummyContent, readBackContent)) {
                    log.warning("Attachment storage check read back unexpected file contents");
                }
            }
        }
        catch (IOException exception) {
            throw new IOException("Error performing read operation", exception);
        }
    }

    private static void testList(FileStore.Path rootPath, FileStore.Path tempFilePath) throws IOException {
        try {
            Stream descendents = rootPath.getFileDescendents();
            if (descendents.noneMatch(path -> path.equals(tempFilePath))) {
                log.warning("Attachment storage check could not find temporary file");
            }
        }
        catch (IOException exception) {
            throw new IOException("Error performing list operation", exception);
        }
    }

    private static void testDelete(FileStore.Path tempFilePath) throws IOException {
        try {
            tempFilePath.deleteFile();
        }
        catch (IOException exception) {
            throw new IOException("Error performing delete operation", exception);
        }
    }
}

