/*
 * Decompiled with CFR 0.152.
 */
package org.ungoverned.osgi.service.shell;

import org.ungoverned.osgi.service.shell.Command;

public interface CdCommand
extends Command {
    public static final String BASE_URL_PROPERTY = "felix.impl.baseurl";

    public String getBaseURL();

    public void setBaseURL(String var1);
}

