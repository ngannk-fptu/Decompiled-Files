/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.stp.action;

import com.atlassian.troubleshooting.stp.action.SupportToolsAction;
import java.util.List;

public interface SupportActionFactory {
    public SupportToolsAction getAction(String var1);

    public List<String> getActionCategories();

    public List<SupportToolsAction> getActionsByCategory(String var1);

    public List<SupportToolsAction> getActions();
}

