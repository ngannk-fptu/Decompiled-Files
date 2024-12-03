/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.renderer;

import com.atlassian.confluence.renderer.PageContext;
import java.util.Map;

public class WikiRendererContextKeys {
    public static final String RENDER_CONTEXT = "RENDER_CONTEXT";
    public static final String ATTACHMENTS_PATH = "ATTACHMENTS_PATH";
    public static final String EXTRACTED_EXTERNAL_REFERENCES = "EXTRACTED_EXTERNAL_REFERENCES";

    public static PageContext getPageContext(Map contextParams) {
        return (PageContext)((Object)contextParams.get(RENDER_CONTEXT));
    }
}

