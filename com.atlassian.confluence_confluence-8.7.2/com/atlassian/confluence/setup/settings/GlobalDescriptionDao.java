/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.core.persistence.ObjectDao
 */
package com.atlassian.confluence.setup.settings;

import bucket.core.persistence.ObjectDao;
import com.atlassian.confluence.setup.settings.GlobalDescription;

public interface GlobalDescriptionDao
extends ObjectDao {
    public GlobalDescription getGlobalDescription();

    public GlobalDescription getGlobalDescriptionById(long var1);
}

