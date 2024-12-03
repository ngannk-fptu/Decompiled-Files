/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.xmlimport;

@Deprecated
public interface Operation {
    public void execute() throws Exception;

    public String getDescription() throws Exception;
}

