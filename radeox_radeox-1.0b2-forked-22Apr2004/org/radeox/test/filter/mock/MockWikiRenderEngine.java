/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.test.filter.mock;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import org.radeox.api.engine.RenderEngine;
import org.radeox.api.engine.WikiRenderEngine;
import org.radeox.api.engine.context.RenderContext;
import org.radeox.util.Encoder;

public class MockWikiRenderEngine
implements RenderEngine,
WikiRenderEngine {
    public boolean exists(String name, int linkType) {
        return name.equals("SnipSnap") || name.equals("stephan");
    }

    public void appendExternalLink(StringBuffer buffer, String name, String view, String anchor, String tipText, int linkType) {
    }

    public void appendExternalLink(StringBuffer buffer, String name, String view, String tipText, int linkType) {
    }

    public boolean isExternal(String name, int linkType) {
        return false;
    }

    public boolean showCreate(int linkType) {
        return true;
    }

    public void appendLink(StringBuffer buffer, String name, String view, String anchor, String tipText, int linkType) {
        buffer.append("link:" + name + "|" + view + "#" + anchor);
    }

    public void appendLink(StringBuffer buffer, String name, String view, String tipText, int linkType) {
        buffer.append("link:" + name + "|" + view);
    }

    public void appendCreateLink(StringBuffer buffer, String name, String view, String tipText, int linkType) {
        buffer.append("'").append(name).append("' - ");
        buffer.append("'").append(Encoder.escape(name)).append("'");
    }

    public String getName() {
        return "mock-wiki";
    }

    public String render(String content, RenderContext context) {
        return null;
    }

    public void render(Writer out, String content, RenderContext context) throws IOException {
    }

    public String render(Reader in, RenderContext context) throws IOException {
        return null;
    }
}

