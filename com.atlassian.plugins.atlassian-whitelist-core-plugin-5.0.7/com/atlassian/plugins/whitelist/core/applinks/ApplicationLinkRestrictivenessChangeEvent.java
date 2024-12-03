/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.whitelist.core.applinks;

public class ApplicationLinkRestrictivenessChangeEvent {
    private static ApplicationLinkRestrictivenessChangeEvent instance;

    public static ApplicationLinkRestrictivenessChangeEvent getInstance() {
        if (instance == null) {
            instance = new ApplicationLinkRestrictivenessChangeEvent();
        }
        return instance;
    }
}

