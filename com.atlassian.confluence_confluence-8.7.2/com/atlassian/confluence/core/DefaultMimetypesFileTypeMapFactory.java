/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ClassLoaderUtils
 *  javax.activation.MimetypesFileTypeMap
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.core.MimetypesFileTypeMapFactory;
import com.atlassian.core.util.ClassLoaderUtils;
import javax.activation.MimetypesFileTypeMap;

public class DefaultMimetypesFileTypeMapFactory
implements MimetypesFileTypeMapFactory {
    @Override
    public MimetypesFileTypeMap getMimetypesFileTypeMap() {
        return new MimetypesFileTypeMap(ClassLoaderUtils.getResourceAsStream((String)"mime.types", this.getClass()));
    }
}

