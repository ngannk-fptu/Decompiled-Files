/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  com.google.common.io.Resources
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.google.template.soy.data;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.io.Resources;
import com.google.template.soy.data.Dir;
import com.google.template.soy.data.SanitizedContent;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class SanitizedContents {
    private SanitizedContents() {
    }

    public static SanitizedContent emptyString(SanitizedContent.ContentKind kind) {
        return new SanitizedContent("", kind, Dir.NEUTRAL);
    }

    public static SanitizedContent unsanitizedText(String text, @Nullable Dir dir) {
        return new SanitizedContent(text, SanitizedContent.ContentKind.TEXT, dir);
    }

    public static SanitizedContent unsanitizedText(String text) {
        return SanitizedContents.unsanitizedText(text, null);
    }

    public static SanitizedContent concatHtml(SanitizedContent ... contents) {
        for (SanitizedContent content : contents) {
            Preconditions.checkArgument((content.getContentKind() == SanitizedContent.ContentKind.HTML ? 1 : 0) != 0, (Object)"Can only concat HTML");
        }
        StringBuilder combined = new StringBuilder();
        Dir dir = Dir.NEUTRAL;
        for (SanitizedContent content : contents) {
            combined.append(content.getContent());
            if (dir == Dir.NEUTRAL) {
                dir = content.getContentDirection();
                continue;
            }
            if (content.getContentDirection() == dir || content.getContentDirection() == Dir.NEUTRAL) continue;
            dir = null;
        }
        return new SanitizedContent(combined.toString(), SanitizedContent.ContentKind.HTML, dir);
    }

    public static SanitizedContent fromResource(Class<?> contextClass, String resourceName, Charset charset, SanitizedContent.ContentKind kind) throws IOException {
        SanitizedContents.pretendValidateResource(resourceName, kind);
        return new SanitizedContent(Resources.toString((URL)Resources.getResource(contextClass, (String)resourceName), (Charset)charset), kind, SanitizedContents.getDefaultDir(kind));
    }

    public static SanitizedContent fromResource(String resourceName, Charset charset, SanitizedContent.ContentKind kind) throws IOException {
        SanitizedContents.pretendValidateResource(resourceName, kind);
        return new SanitizedContent(Resources.toString((URL)Resources.getResource((String)resourceName), (Charset)charset), kind, SanitizedContents.getDefaultDir(kind));
    }

    @VisibleForTesting
    static void pretendValidateResource(String resourceName, SanitizedContent.ContentKind kind) {
        int index = resourceName.lastIndexOf(46);
        Preconditions.checkArgument((index >= 0 ? 1 : 0) != 0, (Object)"Currently, we only validate resources with explicit extensions.");
        String fileExtension = resourceName.substring(index + 1).toLowerCase();
        switch (kind) {
            case JS: {
                Preconditions.checkArgument((boolean)fileExtension.equals("js"));
                break;
            }
            case HTML: {
                Preconditions.checkArgument((boolean)fileExtension.equals("html"));
                break;
            }
            case CSS: {
                Preconditions.checkArgument((boolean)fileExtension.equals("css"));
                break;
            }
            default: {
                throw new IllegalArgumentException("Don't know how to validate resources of kind " + (Object)((Object)kind));
            }
        }
    }

    @VisibleForTesting
    static Dir getDefaultDir(SanitizedContent.ContentKind kind) {
        switch (kind) {
            case JS: 
            case CSS: 
            case URI: 
            case ATTRIBUTES: {
                return Dir.LTR;
            }
        }
        return null;
    }
}

