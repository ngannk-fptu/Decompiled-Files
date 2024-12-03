/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.ssi;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

public interface SSIExternalResolver {
    public void addVariableNames(Collection<String> var1);

    public String getVariableValue(String var1);

    public void setVariableValue(String var1, String var2);

    public Date getCurrentDate();

    public long getFileSize(String var1, boolean var2) throws IOException;

    public long getFileLastModified(String var1, boolean var2) throws IOException;

    public String getFileText(String var1, boolean var2) throws IOException;

    public void log(String var1, Throwable var2);
}

