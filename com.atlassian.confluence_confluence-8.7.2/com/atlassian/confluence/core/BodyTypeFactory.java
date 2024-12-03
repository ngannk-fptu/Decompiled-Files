/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.core.BodyType;

public interface BodyTypeFactory {
    public void initialiseBodyTypes();

    public BodyType getXhtmlBodyType();

    public BodyType getMailBodyType();

    public BodyType getWikiBodyType();

    public BodyType getBodyType(String var1);
}

