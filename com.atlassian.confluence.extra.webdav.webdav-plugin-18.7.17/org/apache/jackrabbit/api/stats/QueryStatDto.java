/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.api.stats;

import java.io.Serializable;

public interface QueryStatDto
extends Serializable {
    public long getDuration();

    public String getLanguage();

    public String getStatement();

    public String getCreationTime();

    public int getOccurrenceCount();

    public long getPosition();

    public void setPosition(long var1);
}

