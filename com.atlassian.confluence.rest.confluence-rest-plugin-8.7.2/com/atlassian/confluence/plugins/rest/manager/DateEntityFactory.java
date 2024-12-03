/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.rest.manager;

import com.atlassian.confluence.plugins.rest.entities.DateEntity;
import java.util.Date;

public interface DateEntityFactory {
    public DateEntity buildDateEntity(Date var1);
}

