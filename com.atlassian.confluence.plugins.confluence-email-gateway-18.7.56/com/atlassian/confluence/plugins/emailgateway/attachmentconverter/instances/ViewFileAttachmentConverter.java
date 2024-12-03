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
import java.util.Collections;
import java.util.Map;

public class ViewFileAttachmentConverter
implements AttachmentConverter<MacroDefinition> {
    @Override
    public MacroDefinition convertAttachment(AttachmentFile attachmentFile) {
        String type = attachmentFile.getContentType().toLowerCase();
        String macroName = this.getMacroName(type, attachmentFile.getExtension());
        if (macroName == null) {
            return null;
        }
        Map<String, String> params = Collections.singletonMap("name", attachmentFile.getFileName());
        return MacroDefinition.builder().withName(macroName).withParameters(params).build();
    }

    private String getMacroName(String contentType, String extension) {
        if ("application/pdf".equals(contentType)) {
            return "viewpdf";
        }
        if (extension.startsWith("doc")) {
            return "viewdoc";
        }
        if (extension.startsWith("xls")) {
            return "viewxls";
        }
        if (extension.startsWith("ppt")) {
            return "viewppt";
        }
        return null;
    }

    @Override
    public Class<MacroDefinition> getConversionClass() {
        return MacroDefinition.class;
    }
}

