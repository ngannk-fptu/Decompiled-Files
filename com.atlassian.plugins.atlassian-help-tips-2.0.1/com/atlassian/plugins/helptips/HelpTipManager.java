/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.helptips;

import com.atlassian.plugins.helptips.dao.HelpTipsDao;
import java.util.Set;

public class HelpTipManager {
    private final HelpTipsDao helpTipsDao;

    public HelpTipManager(HelpTipsDao helpTipsDao) {
        this.helpTipsDao = helpTipsDao;
    }

    public Set<String> getDismissedTips(String userKey) {
        return this.helpTipsDao.findDismissedTips(userKey);
    }

    public void dismissTip(String userKey, String id) {
        this.helpTipsDao.saveDismissedTip(userKey, id);
    }

    public void undismissTip(String userKey, String id) {
        this.helpTipsDao.deleteDismissedTip(userKey, id);
    }
}

