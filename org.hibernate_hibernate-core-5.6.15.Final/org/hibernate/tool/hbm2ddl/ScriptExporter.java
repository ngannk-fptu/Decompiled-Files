/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.hbm2ddl;

import org.hibernate.internal.build.AllowSysOut;
import org.hibernate.tool.hbm2ddl.Exporter;

@Deprecated
class ScriptExporter
implements Exporter {
    ScriptExporter() {
    }

    @Override
    public boolean acceptsImportScripts() {
        return false;
    }

    @Override
    @AllowSysOut
    public void export(String string) throws Exception {
        System.out.println(string);
    }

    @Override
    public void release() throws Exception {
    }
}

