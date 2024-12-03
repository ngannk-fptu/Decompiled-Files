/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginRestartState
 */
package com.atlassian.upm.core.rest.representations;

import com.atlassian.plugin.PluginRestartState;

public final class RestartState {
    public static String toString(PluginRestartState restartState) {
        switch (restartState) {
            case NONE: {
                return null;
            }
        }
        return restartState.toString().toLowerCase();
    }
}

