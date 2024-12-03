/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.multipart.support;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

public class StringMultipartFileEditor
extends PropertyEditorSupport {
    @Nullable
    private final String charsetName;

    public StringMultipartFileEditor() {
        this.charsetName = null;
    }

    public StringMultipartFileEditor(String charsetName) {
        this.charsetName = charsetName;
    }

    @Override
    public void setAsText(String text) {
        this.setValue(text);
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof MultipartFile) {
            MultipartFile multipartFile = (MultipartFile)value;
            try {
                super.setValue(this.charsetName != null ? new String(multipartFile.getBytes(), this.charsetName) : new String(multipartFile.getBytes()));
            }
            catch (IOException ex) {
                throw new IllegalArgumentException("Cannot read contents of multipart file", ex);
            }
        } else {
            super.setValue(value);
        }
    }
}

