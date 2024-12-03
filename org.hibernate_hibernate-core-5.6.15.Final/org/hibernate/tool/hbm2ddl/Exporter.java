/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.hbm2ddl;

@Deprecated
interface Exporter {
    public boolean acceptsImportScripts();

    public void export(String var1) throws Exception;

    public void release() throws Exception;
}

