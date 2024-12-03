/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceEditor;
import org.springframework.util.Assert;

public class PathEditor
extends PropertyEditorSupport {
    private final ResourceEditor resourceEditor;

    public PathEditor() {
        this.resourceEditor = new ResourceEditor();
    }

    public PathEditor(ResourceEditor resourceEditor) {
        Assert.notNull((Object)resourceEditor, "ResourceEditor must not be null");
        this.resourceEditor = resourceEditor;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        boolean nioPathCandidate;
        boolean bl = nioPathCandidate = !text.startsWith("classpath:");
        if (nioPathCandidate && !text.startsWith("/")) {
            try {
                URI uri = new URI(text);
                if (uri.getScheme() != null) {
                    nioPathCandidate = false;
                    this.setValue(Paths.get(uri).normalize());
                    return;
                }
            }
            catch (URISyntaxException ex) {
                nioPathCandidate = !text.startsWith("file:");
            }
            catch (FileSystemNotFoundException ex) {
                // empty catch block
            }
        }
        this.resourceEditor.setAsText(text);
        Resource resource = (Resource)this.resourceEditor.getValue();
        if (resource == null) {
            this.setValue(null);
        } else if (nioPathCandidate && !resource.exists()) {
            this.setValue(Paths.get(text, new String[0]).normalize());
        } else {
            try {
                this.setValue(resource.getFile().toPath());
            }
            catch (IOException ex) {
                throw new IllegalArgumentException("Failed to retrieve file for " + resource, ex);
            }
        }
    }

    @Override
    public String getAsText() {
        Path value = (Path)this.getValue();
        return value != null ? value.toString() : "";
    }
}

