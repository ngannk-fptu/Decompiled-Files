/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.guardrails.util;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipBuilder {
    private final OutputStream outputStream;
    private final List<Path> files = new ArrayList<Path>();

    public ZipBuilder(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void maybeAdd(Path path) {
        if (path != null) {
            this.add(path);
        }
    }

    public void add(Path path) {
        this.files.add(path);
    }

    public void create(boolean deleteSourceFiles) throws IOException {
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(this.outputStream));){
            for (Path path : this.files) {
                this.addToZip(zipOutputStream, path);
            }
        }
        if (deleteSourceFiles) {
            for (Path path : this.files) {
                Files.delete(path);
            }
        }
    }

    private void addToZip(ZipOutputStream zipOutputStream, Path filePath) throws IOException {
        String resolvedZipEntryName = filePath.getFileName().toString();
        zipOutputStream.putNextEntry(new ZipEntry(resolvedZipEntryName));
        Files.copy(filePath, zipOutputStream);
        zipOutputStream.closeEntry();
    }
}

