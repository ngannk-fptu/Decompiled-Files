/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.test.filter.mock;

import org.radeox.test.filter.mock.MockWikiRenderEngine;

public class MockOldWikiRenderEngine
extends MockWikiRenderEngine {
    public void appendLink(StringBuffer buffer, String name, String view, String anchor, String tipText, int linkType) {
        buffer.append("link:" + view + "#" + anchor);
    }

    public void appendLink(StringBuffer buffer, String name, String view, String tipText, int linkType) {
        buffer.append("link:" + view);
    }

    public void appendCreateLink(StringBuffer buffer, String name, String view, String tipText, int linkType) {
        buffer.append(view);
        buffer.append("?");
    }

    public String getName() {
        return "mock-old-wiki";
    }
}

