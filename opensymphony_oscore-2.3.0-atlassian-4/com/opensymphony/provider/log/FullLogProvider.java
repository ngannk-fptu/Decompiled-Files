/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.provider.log;

import com.opensymphony.provider.LogProvider;
import com.opensymphony.provider.ProviderConfigurationException;

public class FullLogProvider
implements LogProvider {
    @Override
    public Object getContext(String name) {
        return name;
    }

    @Override
    public boolean isEnabled(Object context, int level) {
        return true;
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init() throws ProviderConfigurationException {
    }

    @Override
    public void log(Object context, int level, Object msg, Throwable throwable) {
        StringBuffer l = new StringBuffer();
        l.append('[');
        l.append(this.getLevelDescription(level));
        l.append("] ");
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

    private String getLevelDescription(int level) {
        switch (level) {
            case 1: {
                return "DEBUG";
            }
            case 2: {
                return "INFO ";
            }
            case 3: {
                return "WARN ";
            }
            case 4: {
                return "ERROR";
            }
            case 5: {
                return "FATAL";
            }
        }
        return "?????";
    }
}

