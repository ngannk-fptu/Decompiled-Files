/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.protocol.util;

import java.util.Date;
import org.apache.abdera.protocol.EntityProvider;
import org.apache.abdera.util.EntityTag;

public abstract class AbstractEntityProvider
implements EntityProvider {
    public String getContentType() {
        return "application/xml";
    }

    public EntityTag getEntityTag() {
        return null;
    }

    public Date getLastModified() {
        return null;
    }
}

