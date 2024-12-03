/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.labels.Labelable
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.spaces.Space
 */
package com.atlassian.confluence.plugin.copyspace.service;

import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.Labelable;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.plugin.copyspace.context.CopySpaceContext;
import com.atlassian.confluence.spaces.Space;
import java.util.List;

public interface LabelService {
    public void copyBlogPostLabels(long var1, BlogPost var3);

    public void copyAttachmentLabels(List<Attachment> var1, List<Attachment> var2, CopySpaceContext var3);

    public void addSpaceLabel(Space var1, Label var2);

    public void addLabel(Labelable var1, Label var2);
}

