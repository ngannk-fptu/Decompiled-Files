/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.html;

import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBufferFragment;
import com.opensymphony.module.sitemesh.html.State;

public interface HTMLProcessorContext {
    public SitemeshBuffer getSitemeshBuffer();

    public State currentState();

    public void changeState(State var1);

    public void pushBuffer(SitemeshBufferFragment.Builder var1);

    public SitemeshBufferFragment.Builder currentBuffer();

    public SitemeshBufferFragment.Builder popBuffer();
}

