/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.io.File;
import java.io.IOException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceEditor;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

public class FileEditor
extends PropertyEditorSupport {
    private final ResourceEditor resourceEditor;

    public FileEditor() {
        this.resourceEditor = new ResourceEditor();
    }

    public FileEditor(ResourceEditor resourceEditor) {
        Assert.notNull((Object)resourceEditor, "ResourceEditor must not be null");
        this.resourceEditor = resourceEditor;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (!StringUtils.hasText(text)) {
            this.setValue(null);
            return;
        }
        File file = null;
        if (!ResourceUtils.isUrl(text) && (file = new File(text)).isAbsolute()) {
            this.setValue(file);
            return;
        }
        this.resourceEditor.setAsText(text);
        Resource resource = (Resource)this.resourceEditor.getValue();
        if (file == null || resource.exists()) {
            try {
                this.setValue(resource.getFile());
            }
            catch (IOException ex) {
                throw new IllegalArgumentException("Could not retrieve file for " + resource + ": " + ex.getMessage());
            }
        } else {
            this.setValue(file);
        }
    }

    @Override
    public String getAsText() {
        File value = (File)this.getValue();
        return value != null ? value.getPath() : "";
    }
}

