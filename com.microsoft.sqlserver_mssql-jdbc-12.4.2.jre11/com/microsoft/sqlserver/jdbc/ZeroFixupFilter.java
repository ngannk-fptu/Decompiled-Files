/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.IntColumnFilter;

class ZeroFixupFilter
extends IntColumnFilter {
    ZeroFixupFilter() {
    }

    @Override
    int oneValueToAnother(int precl) {
        if (0 == precl) {
            return Integer.MAX_VALUE;
        }
        return precl;
    }
}

