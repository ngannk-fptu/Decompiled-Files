/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.resource;

import java.io.BufferedInputStream;
import java.io.InputStream;
import org.xhtmlrenderer.resource.Resource;
import org.xml.sax.InputSource;

public abstract class AbstractResource
implements Resource {
    private InputSource inputSource;
    private long createTimeStamp = System.currentTimeMillis();
    private long elapsedLoadTime;

    private AbstractResource() {
    }

    public AbstractResource(InputSource source) {
        this();
        this.inputSource = source;
    }

    public AbstractResource(InputStream is) {
        this(is == null ? (InputSource)null : new InputSource(new BufferedInputStream(is)));
    }

    @Override
    public InputSource getResourceInputSource() {
        return this.inputSource;
    }

    @Override
    public long getResourceLoadTimeStamp() {
        return this.createTimeStamp;
    }

    public long getElapsedLoadTime() {
        return this.elapsedLoadTime;
    }

    void setElapsedLoadTime(long elapsedLoadTime) {
        this.elapsedLoadTime = elapsedLoadTime;
    }
}

