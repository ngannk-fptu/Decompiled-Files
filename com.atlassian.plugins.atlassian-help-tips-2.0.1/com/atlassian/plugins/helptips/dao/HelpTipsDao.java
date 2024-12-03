/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.tx.Transactional
 */
package com.atlassian.plugins.helptips.dao;

import com.atlassian.activeobjects.tx.Transactional;
import java.util.Set;

@Transactional
public interface HelpTipsDao {
    public Set<String> findDismissedTips(String var1);

    public void saveDismissedTip(String var1, String var2);

    public void deleteDismissedTip(String var1, String var2);
}

