/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 */
package com.atlassian.confluence.plugins.emailgateway.attachmentconverter.instances;

import com.atlassian.confluence.plugins.emailgateway.api.AttachmentConverter;
import com.atlassian.confluence.plugins.emailgateway.api.AttachmentFile;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MultimediaAttachmentConverter
implements AttachmentConverter<MacroDefinition> {
    private static final Set<String> SUPPORTED_MULTIMEDIA_EXTENSIONS = new HashSet<String>(Arrays.asList("rm", "ram", "mpeg", "mpg", "wmv", "wma", "swf", "mov", "mp4", "mp3", "avi"));

    @Override
    public MacroDefinition convertAttachment(AttachmentFile attachmentFile) {
        if (!SUPPORTED_MULTIMEDIA_EXTENSIONS.contains(attachmentFile.getExtension())) {
            return null;
        }
        Map<String, String> params = Collections.singletonMap("name", attachmentFile.getFileName());
        return MacroDefinition.builder().withName("multimedia").withParameters(params).build();
    }

    @Override
    public Class<MacroDefinition> getConversionClass() {
        return MacroDefinition.class;
    }
}

