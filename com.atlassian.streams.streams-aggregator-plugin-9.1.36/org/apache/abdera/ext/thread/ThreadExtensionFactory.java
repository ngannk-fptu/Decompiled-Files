/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.ext.thread;

import org.apache.abdera.ext.thread.InReplyTo;
import org.apache.abdera.ext.thread.ThreadConstants;
import org.apache.abdera.ext.thread.Total;
import org.apache.abdera.util.AbstractExtensionFactory;

public final class ThreadExtensionFactory
extends AbstractExtensionFactory
implements ThreadConstants {
    public ThreadExtensionFactory() {
        super("http://purl.org/syndication/thread/1.0");
        this.addImpl(IN_REPLY_TO, InReplyTo.class);
        this.addImpl(THRTOTAL, Total.class);
    }
}

