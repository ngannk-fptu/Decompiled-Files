/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.StringUtils
 */
package org.springframework.http;

import java.beans.PropertyEditorSupport;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

public class MediaTypeEditor
extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) {
        if (StringUtils.hasText((String)text)) {
            this.setValue(MediaType.parseMediaType(text));
        } else {
            this.setValue(null);
        }
    }

    @Override
    public String getAsText() {
        MediaType mediaType = (MediaType)this.getValue();
        return mediaType != null ? mediaType.toString() : "";
    }
}

