/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.pats.db;

import com.atlassian.pats.db.QAOToken;
import java.util.Objects;

public final class Tables {
    public static final int BATCH_SIZE = 100;
    public static final String AO_PREFIX = Tables.getAOPrefix();
    public static final QAOToken TOKEN = new QAOToken(AO_PREFIX);

    private Tables() {
    }

    private static String getAOPrefix() {
        String aoPrefix = System.getProperty("AO_PREFIX");
        return Objects.nonNull(aoPrefix) ? aoPrefix : "AO_81F455";
    }
}

