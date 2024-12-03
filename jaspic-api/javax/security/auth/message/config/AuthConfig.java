/*
 * Decompiled with CFR 0.152.
 */
package javax.security.auth.message.config;

import javax.security.auth.message.MessageInfo;

public interface AuthConfig {
    public String getMessageLayer();

    public String getAppContext();

    public String getAuthContextID(MessageInfo var1);

    public void refresh();

    public boolean isProtected();
}

