/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.settings.ConfluenceDirectories
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.core.io.Resource
 *  org.springframework.stereotype.Component
 */
package com.benryan.conversion;

import com.atlassian.confluence.setup.settings.ConfluenceDirectories;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.benryan.conversion.AttachmentDataTempFile;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class AttachmentTempFileSupplier {
    private final ConfluenceDirectories confluenceDirectories;

    public AttachmentTempFileSupplier(@ComponentImport ConfluenceDirectories confluenceDirectories) {
        this.confluenceDirectories = confluenceDirectories;
    }

    public AttachmentDataTempFile createAttachmentTempFile(Resource attachmentResource) throws IOException {
        Path tempDataFile = Files.createTempFile(Files.createDirectories(this.confluenceDirectories.getTempDirectory(), new FileAttribute[0]), attachmentResource.getFilename(), null, new FileAttribute[0]);
        try (InputStream inputStream = attachmentResource.getInputStream();){
            Files.copy(inputStream, tempDataFile, StandardCopyOption.REPLACE_EXISTING);
        }
        return new AttachmentDataTempFile(tempDataFile);
    }
}

