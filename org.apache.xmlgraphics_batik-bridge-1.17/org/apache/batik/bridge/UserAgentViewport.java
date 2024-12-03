/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.bridge;

import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.Viewport;

public class UserAgentViewport
implements Viewport {
    private UserAgent userAgent;

    public UserAgentViewport(UserAgent userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public float getWidth() {
        return (float)this.userAgent.getViewportSize().getWidth();
    }

    @Override
    public float getHeight() {
        return (float)this.userAgent.getViewportSize().getHeight();
    }
}

