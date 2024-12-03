/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentRepresentation
 *  com.atlassian.confluence.core.DataSourceFactory
 *  com.atlassian.confluence.mail.embed.MimeBodyPartRecorder
 *  com.atlassian.soy.renderer.SoyServerFunction
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.plugins.email.soy;

import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.core.DataSourceFactory;
import com.atlassian.confluence.mail.embed.MimeBodyPartRecorder;
import com.atlassian.confluence.plugins.email.soy.ResourceImageFunction;
import com.atlassian.soy.renderer.SoyServerFunction;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

public class AttachInlineTaskImagesFunction
implements SoyServerFunction<String> {
    private static final String CID_INLINE_TASK_DIFF_UNCHECKED_ICON = "<span class=\"inline-task\">";
    private static final String CID_INLINE_TASK_DIFF_CHECKED_ICON = "<span class=\"inline-task checked\">";
    private static final String CID_INLINE_TASK_RENDER_CHECKED_ICON = "li class=\"checked\" data-inline-task-id";
    private static final String CID_INLINE_TASK_RENDER_UNCHECKED_ICON = "<li data-inline-task-id";
    private static final String CID_INLINE_TASK_CALENDAR_ICON = "<time datetime=";
    private final ResourceImageFunction resourceImageFunction;

    public AttachInlineTaskImagesFunction(DataSourceFactory dataSourceFactory, MimeBodyPartRecorder bodyPartRecorder) {
        this.resourceImageFunction = new ResourceImageFunction(dataSourceFactory, bodyPartRecorder);
    }

    public String apply(Object ... objects) {
        String string;
        if (objects.length != 1) {
            return "";
        }
        String content = "";
        Object contentBody = objects[0];
        if (contentBody instanceof String) {
            content = (String)objects[0];
        } else if (contentBody instanceof ContentRepresentation) {
            content = ((ContentRepresentation)contentBody).getRepresentation();
        }
        if (content.contains(CID_INLINE_TASK_DIFF_UNCHECKED_ICON) || content.contains(CID_INLINE_TASK_RENDER_UNCHECKED_ICON)) {
            string = this.resourceImageFunction.apply("com.atlassian.confluence.plugins.confluence-inline-tasks:inline-task-mail-resources", "inline-task-unchecked-icon");
        }
        if (content.contains(CID_INLINE_TASK_DIFF_CHECKED_ICON) || content.contains(CID_INLINE_TASK_RENDER_CHECKED_ICON)) {
            string = this.resourceImageFunction.apply("com.atlassian.confluence.plugins.confluence-inline-tasks:inline-task-mail-resources", "inline-task-checked-icon");
        }
        if (content.contains(CID_INLINE_TASK_CALENDAR_ICON)) {
            string = this.resourceImageFunction.apply("com.atlassian.confluence.plugins.confluence-inline-tasks:inline-task-mail-resources", "inline-task-calendar-icon");
        }
        return content;
    }

    public String getName() {
        return "attachInlineTaskImages";
    }

    public Set<Integer> validArgSizes() {
        return ImmutableSet.of((Object)1);
    }
}

