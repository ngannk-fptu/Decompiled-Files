/*
 * Decompiled with CFR 0.152.
 */
package com.benryan.conversion;

import com.benryan.conversion.FilePathAwareConversionStore;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.UUID;

public class LocalFilePathAwareConversionStore
implements FilePathAwareConversionStore {
    private final String localPath;

    public LocalFilePathAwareConversionStore(String localPath) {
        this.localPath = localPath;
    }

    @Override
    public Path getFilePath(UUID fileStoreId) {
        return Paths.get(this.localPath, fileStoreId.toString());
    }

    public OutputStream createFile(UUID uuid) {
        OutputStream fileOutputStream = null;
        try {
            Files.createDirectories(Paths.get(this.localPath, new String[0]), new FileAttribute[0]);
            fileOutputStream = Files.newOutputStream(Paths.get(this.localPath, uuid.toString()), new OpenOption[0]);
        }
        catch (IOException var6) {
            var6.printStackTrace();
        }
        return fileOutputStream;
    }

    public InputStream readFile(UUID uuid) {
        InputStream fileInputStream = null;
        try {
            fileInputStream = Files.newInputStream(this.getFilePath(uuid), new OpenOption[0]);
        }
        catch (IOException var5) {
            var5.printStackTrace();
        }
        return fileInputStream;
    }
}

