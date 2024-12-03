/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.AttachmentUpload
 *  com.atlassian.confluence.api.service.content.AttachmentService
 *  com.atlassian.confluence.spaces.Space
 *  com.google.common.collect.Lists
 *  org.apache.commons.io.IOUtils
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.confluence.plugins.softwareproject.components;

import com.atlassian.confluence.api.model.content.AttachmentUpload;
import com.atlassian.confluence.api.service.content.AttachmentService;
import com.atlassian.confluence.spaces.Space;
import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class SampleAttachmentCreator {
    private static final String[] PERSONA_NAMES = new String[]{"Alana-persona", "Harvey-persona", "Mia-persona"};
    private final AttachmentService attachmentService;

    @Autowired
    public SampleAttachmentCreator(AttachmentService attachmentService) {
        this.attachmentService = Objects.requireNonNull(attachmentService);
    }

    public void addSampleAttachmentsToHomePage(Space space) {
        try {
            this.attachmentService.addAttachments(space.getHomePage().getContentId(), this.getSampleAttachmentUploads());
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    private Collection<AttachmentUpload> getSampleAttachmentUploads() throws IOException {
        ArrayList uploads = Lists.newArrayList();
        for (String personaName : PERSONA_NAMES) {
            uploads.add(new AttachmentUpload(this.readPersonaImage(personaName + ".png"), personaName, "image/png", "", true));
        }
        return uploads;
    }

    private File readPersonaImage(String imageFileName) throws IOException {
        File tempFile = File.createTempFile("attachment-", ".png");
        tempFile.deleteOnExit();
        InputStream imageInputStream = this.getClass().getClassLoader().getResourceAsStream("images/" + imageFileName);
        FileOutputStream out = new FileOutputStream(tempFile);
        IOUtils.copy((InputStream)imageInputStream, (OutputStream)out);
        return tempFile;
    }
}

