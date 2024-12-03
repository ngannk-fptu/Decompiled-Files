/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.upgrade.dml;

import com.atlassian.confluence.upgrade.dml.DmlStatement;

public interface DmlCommand {
    public DmlStatement getStatement();
}

