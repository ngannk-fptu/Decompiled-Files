/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 */
package com.atlassian.confluence.plugins.graphql.providers;

import com.google.common.base.Strings;

class GraphQLOffsetCursor {
    GraphQLOffsetCursor() {
    }

    static int parseOffset(int offset, String afterOffset) {
        return Strings.isNullOrEmpty((String)afterOffset) ? offset : Integer.parseInt(afterOffset) + 1;
    }
}

