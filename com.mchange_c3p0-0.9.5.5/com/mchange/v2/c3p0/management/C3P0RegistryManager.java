/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.c3p0.management;

import com.mchange.v2.c3p0.C3P0Registry;
import com.mchange.v2.c3p0.management.C3P0RegistryManagerMBean;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Set;

public class C3P0RegistryManager
implements C3P0RegistryManagerMBean {
    @Override
    public String[] getAllIdentityTokens() {
        Set tokens = C3P0Registry.allIdentityTokens();
        return tokens.toArray(new String[tokens.size()]);
    }

    @Override
    public Set getAllIdentityTokenized() {
        return C3P0Registry.allIdentityTokenized();
    }

    @Override
    public Set getAllPooledDataSources() {
        return C3P0Registry.allPooledDataSources();
    }

    @Override
    public int getAllIdentityTokenCount() {
        return C3P0Registry.allIdentityTokens().size();
    }

    @Override
    public int getAllIdentityTokenizedCount() {
        return C3P0Registry.allIdentityTokenized().size();
    }

    @Override
    public int getAllPooledDataSourcesCount() {
        return C3P0Registry.allPooledDataSources().size();
    }

    @Override
    public String[] getAllIdentityTokenizedStringified() {
        return this.stringifySet(C3P0Registry.allIdentityTokenized());
    }

    @Override
    public String[] getAllPooledDataSourcesStringified() {
        return this.stringifySet(C3P0Registry.allPooledDataSources());
    }

    @Override
    public int getNumPooledDataSources() throws SQLException {
        return C3P0Registry.getNumPooledDataSources();
    }

    @Override
    public int getNumPoolsAllDataSources() throws SQLException {
        return C3P0Registry.getNumPoolsAllDataSources();
    }

    @Override
    public String getC3p0Version() {
        return "0.9.5.5";
    }

    private String[] stringifySet(Set s) {
        String[] out = new String[s.size()];
        int i = 0;
        Iterator ii = s.iterator();
        while (ii.hasNext()) {
            out[i++] = ii.next().toString();
        }
        return out;
    }
}

