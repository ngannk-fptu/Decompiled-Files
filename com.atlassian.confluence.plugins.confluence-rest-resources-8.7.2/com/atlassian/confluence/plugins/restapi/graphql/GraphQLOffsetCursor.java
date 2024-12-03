/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 */
package com.atlassian.confluence.plugins.restapi.graphql;

import com.google.common.base.Strings;

public class GraphQLOffsetCursor {
    public static int parseOffset(int offset, String afterOffset) {
        return Strings.isNullOrEmpty((String)afterOffset) ? offset : Integer.parseInt(afterOffset) + 1;
    }
}

