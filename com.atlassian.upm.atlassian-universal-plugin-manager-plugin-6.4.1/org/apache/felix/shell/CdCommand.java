/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.shell;

import org.apache.felix.shell.Command;

public interface CdCommand
extends Command {
    public static final String BASE_URL_PROPERTY = "felix.shell.baseurl";

    public String getBaseURL();

    public void setBaseURL(String var1);
}

