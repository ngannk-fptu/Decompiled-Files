/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.provider.log;

import com.opensymphony.provider.LogProvider;
import com.opensymphony.provider.ProviderConfigurationException;

public class DefaultLogProvider
implements LogProvider {
    @Override
    public Object getContext(String name) {
        return name;
    }

    @Override
    public boolean isEnabled(Object context, int level) {
        return level == 4 || level == 5;
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init() throws ProviderConfigurationException {
    }

    @Override
    public void log(Object context, int level, Object msg, Throwable throwable) {
        if (this.isEnabled(context, level)) {
            StringBuffer l = new StringBuffer();
            l.append(level == 5 ? "[FATAL] " : "[ERROR] ");
            l.append(context);
            l.append(" : ");
            if (msg != null) {
                l.append(msg.toString());
            }
            System.err.println(l);
            if (throwable != null) {
                throwable.printStackTrace(System.err);
            }
        }
    }
}

