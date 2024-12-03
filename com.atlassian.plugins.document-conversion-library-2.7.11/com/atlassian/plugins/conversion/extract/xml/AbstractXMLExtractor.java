/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.conversion.extract.xml;

import com.atlassian.plugins.conversion.AsposeAware;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Deprecated
public abstract class AbstractXMLExtractor
extends AsposeAware {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected static ByteArrayInputStream filterZipStream(InputStream inputStream, long maxSize) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(0x200000);
        ZipOutputStream zos = new ZipOutputStream(baos);
        ZipInputStream zis = new ZipInputStream(inputStream);
        byte[] buf = new byte[65535];
        long finalSize = 0L;
        int totalBytesRead = 0;
        try {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                int read;
                if ((finalSize += entry.getSize()) > maxSize) {
                    throw new Exception("Document too big for text extraction, bailing out");
                }
                String name = entry.getName();
                String processedName = name.trim().toLowerCase();
                if (name.contains("/media/") || processedName.endsWith(".png") || processedName.endsWith(".emf") || processedName.endsWith(".wmf") || processedName.endsWith(".jpg") || processedName.endsWith(".jpeg") || processedName.endsWith(".gif")) continue;
                zos.putNextEntry(new ZipEntry(name));
                while ((read = zis.read(buf)) > 0) {
                    if ((long)(totalBytesRead += read) > maxSize) {
                        throw new IllegalArgumentException("Number of bytes read has passed max size threshold despite checking for final size earlier. Bailing out due to possible malicious behaviour.");
                    }
                    zos.write(buf, 0, read);
                }
            }
        }
        finally {
            zis.close();
            zos.close();
        }
        return new ByteArrayInputStream(baos.toByteArray());
    }
}

