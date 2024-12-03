/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceEntityObject
 */
package com.atlassian.confluence.plugin.copyspace.util;

import com.atlassian.confluence.core.ConfluenceEntityObject;

public class MetadataCopier {
    public static void copyEntityMetadata(ConfluenceEntityObject from, ConfluenceEntityObject to) {
        to.setCreationDate(from.getCreationDate());
        to.setLastModificationDate(from.getLastModificationDate());
        to.setCreator(from.getCreator());
        to.setLastModifier(from.getLastModifier());
    }
}

