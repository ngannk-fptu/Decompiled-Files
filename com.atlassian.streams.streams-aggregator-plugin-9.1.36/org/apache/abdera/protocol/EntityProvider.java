/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.protocol;

import java.util.Date;
import org.apache.abdera.util.EntityTag;
import org.apache.abdera.writer.StreamWriter;

public interface EntityProvider {
    public void writeTo(StreamWriter var1);

    public boolean isRepeatable();

    public String getContentType();

    public EntityTag getEntityTag();

    public Date getLastModified();
}

