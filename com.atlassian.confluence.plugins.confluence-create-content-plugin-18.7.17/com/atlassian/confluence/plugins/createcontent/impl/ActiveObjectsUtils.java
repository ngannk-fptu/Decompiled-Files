/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  net.java.ao.DBParam
 */
package com.atlassian.confluence.plugins.createcontent.impl;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.plugins.createcontent.activeobjects.UuidBackedAo;
import java.util.UUID;
import net.java.ao.DBParam;

public class ActiveObjectsUtils {
    static <T extends UuidBackedAo> T createWithUuid(ActiveObjects activeObjects, Class<T> clazz) {
        UuidBackedAo newBp = (UuidBackedAo)activeObjects.create(clazz, new DBParam[0]);
        newBp.setUuid(UUID.randomUUID().toString());
        return (T)newBp;
    }
}

