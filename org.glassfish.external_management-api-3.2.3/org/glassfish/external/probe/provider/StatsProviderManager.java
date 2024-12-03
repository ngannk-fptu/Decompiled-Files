/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.external.probe.provider;

import java.util.ArrayList;
import org.glassfish.external.probe.provider.PluginPoint;
import org.glassfish.external.probe.provider.StatsProviderInfo;
import org.glassfish.external.probe.provider.StatsProviderManagerDelegate;

public class StatsProviderManager {
    static StatsProviderManagerDelegate spmd;
    private static ArrayList<StatsProviderInfo> toBeRegistered;

    private StatsProviderManager() {
    }

    public static boolean register(String configElement, PluginPoint pp, String subTreeRoot, Object statsProvider) {
        return StatsProviderManager.register(pp, configElement, subTreeRoot, statsProvider, null);
    }

    public static boolean register(PluginPoint pp, String configElement, String subTreeRoot, Object statsProvider, String invokerId) {
        StatsProviderInfo spInfo = new StatsProviderInfo(configElement, pp, subTreeRoot, statsProvider, invokerId);
        return StatsProviderManager.registerStatsProvider(spInfo);
    }

    public static boolean register(String configElement, PluginPoint pp, String subTreeRoot, Object statsProvider, String configLevelStr) {
        return StatsProviderManager.register(configElement, pp, subTreeRoot, statsProvider, configLevelStr, null);
    }

    public static boolean register(String configElement, PluginPoint pp, String subTreeRoot, Object statsProvider, String configLevelStr, String invokerId) {
        StatsProviderInfo spInfo = new StatsProviderInfo(configElement, pp, subTreeRoot, statsProvider, invokerId);
        spInfo.setConfigLevel(configLevelStr);
        return StatsProviderManager.registerStatsProvider(spInfo);
    }

    private static synchronized boolean registerStatsProvider(StatsProviderInfo spInfo) {
        if (spmd != null) {
            spmd.register(spInfo);
            return true;
        }
        toBeRegistered.add(spInfo);
        return false;
    }

    public static synchronized boolean unregister(Object statsProvider) {
        if (spmd == null) {
            for (StatsProviderInfo spInfo : toBeRegistered) {
                if (spInfo.getStatsProvider() != statsProvider) continue;
                toBeRegistered.remove(spInfo);
                break;
            }
        } else {
            spmd.unregister(statsProvider);
            return true;
        }
        return false;
    }

    public static boolean hasListeners(String probeStr) {
        if (spmd == null) {
            return false;
        }
        return spmd.hasListeners(probeStr);
    }

    public static synchronized void setStatsProviderManagerDelegate(StatsProviderManagerDelegate lspmd) {
        if (lspmd == null) {
            return;
        }
        spmd = lspmd;
        for (StatsProviderInfo spInfo : toBeRegistered) {
            spmd.register(spInfo);
        }
        toBeRegistered.clear();
    }

    static {
        toBeRegistered = new ArrayList();
    }
}

