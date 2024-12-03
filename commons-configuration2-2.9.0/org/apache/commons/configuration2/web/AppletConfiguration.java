/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.web;

import java.applet.Applet;
import java.util.Arrays;
import java.util.Iterator;
import org.apache.commons.configuration2.web.BaseWebConfiguration;

public class AppletConfiguration
extends BaseWebConfiguration {
    protected Applet applet;

    public AppletConfiguration(Applet applet) {
        this.applet = applet;
    }

    @Override
    protected Object getPropertyInternal(String key) {
        return this.handleDelimiters(this.applet.getParameter(key));
    }

    @Override
    protected Iterator<String> getKeysInternal() {
        String[][] paramsInfo = this.applet.getParameterInfo();
        String[] keys = new String[paramsInfo != null ? paramsInfo.length : 0];
        if (paramsInfo != null) {
            for (int i = 0; i < keys.length; ++i) {
                keys[i] = paramsInfo[i][0];
            }
        }
        return Arrays.asList(keys).iterator();
    }
}

