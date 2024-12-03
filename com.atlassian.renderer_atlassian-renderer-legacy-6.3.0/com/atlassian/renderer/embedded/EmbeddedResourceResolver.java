/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.embedded;

import com.atlassian.renderer.embedded.EmbeddedAudio;
import com.atlassian.renderer.embedded.EmbeddedFlash;
import com.atlassian.renderer.embedded.EmbeddedImage;
import com.atlassian.renderer.embedded.EmbeddedObject;
import com.atlassian.renderer.embedded.EmbeddedQuicktime;
import com.atlassian.renderer.embedded.EmbeddedRealMedia;
import com.atlassian.renderer.embedded.EmbeddedResource;
import com.atlassian.renderer.embedded.EmbeddedResourceParser;
import com.atlassian.renderer.embedded.EmbeddedWindowsMedia;
import com.atlassian.renderer.embedded.UnembeddableObject;

public class EmbeddedResourceResolver {
    public static EmbeddedResource create(String str) {
        EmbeddedResourceParser parser = new EmbeddedResourceParser(str);
        if (EmbeddedResource.matchesType(parser)) {
            return new EmbeddedResource(parser);
        }
        if (EmbeddedImage.matchesType(parser)) {
            return new EmbeddedImage(parser);
        }
        if (EmbeddedFlash.matchesType(parser)) {
            return new EmbeddedFlash(parser);
        }
        if (EmbeddedQuicktime.matchesType(parser)) {
            return new EmbeddedQuicktime(parser);
        }
        if (EmbeddedWindowsMedia.matchesType(parser)) {
            return new EmbeddedWindowsMedia(parser);
        }
        if (EmbeddedAudio.matchesType(parser)) {
            return new EmbeddedAudio(parser);
        }
        if (EmbeddedRealMedia.matchesType(parser)) {
            return new EmbeddedRealMedia(parser);
        }
        if (UnembeddableObject.matchesType(parser)) {
            return new UnembeddableObject(parser);
        }
        return new EmbeddedObject(parser);
    }
}

