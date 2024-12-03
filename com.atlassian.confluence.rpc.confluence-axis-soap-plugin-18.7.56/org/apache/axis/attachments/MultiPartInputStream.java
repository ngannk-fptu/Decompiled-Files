/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.attachments;

import java.io.FilterInputStream;
import java.io.InputStream;
import java.util.Collection;
import org.apache.axis.AxisFault;
import org.apache.axis.Part;

public abstract class MultiPartInputStream
extends FilterInputStream {
    MultiPartInputStream(InputStream is) {
        super(is);
    }

    public abstract Part getAttachmentByReference(String[] var1) throws AxisFault;

    public abstract Collection getAttachments() throws AxisFault;

    public abstract String getContentLocation();

    public abstract String getContentId();
}

