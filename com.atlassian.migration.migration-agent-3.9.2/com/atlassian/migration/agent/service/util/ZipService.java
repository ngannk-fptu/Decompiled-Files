/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipService {
    public final FileInputStream fileInputStream;

    public ZipService(FileInputStream fileInputStream) {
        this.fileInputStream = fileInputStream;
    }

    public ZipOutputStream createZip(OutputStream outputStream) {
        return new ZipOutputStream(new BufferedOutputStream(outputStream));
    }

    public void addFileToZip(ZipOutputStream zipOut, String pathInsideZip, File file) throws IOException {
        try (FileInputStream inputStream = this.fileInputStream;){
            this.addToZip(zipOut, pathInsideZip, inputStream);
        }
    }

    public void addToZip(ZipOutputStream zipOut, String pathInsideZip, InputStream inputStream) throws IOException {
        int length;
        byte[] bytes = new byte[1024];
        ZipEntry zipEntry = new ZipEntry(pathInsideZip);
        zipOut.putNextEntry(zipEntry);
        while ((length = inputStream.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        zipOut.closeEntry();
    }
}

