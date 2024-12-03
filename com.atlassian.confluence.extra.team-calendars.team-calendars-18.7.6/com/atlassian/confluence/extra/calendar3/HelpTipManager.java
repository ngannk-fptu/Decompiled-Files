/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HelpTipManager {
    private final BandanaManager bandanaManager;
    private static final BandanaContext helpTipContext = new ConfluenceBandanaContext(DigestUtils.sha1Hex(HelpTipManager.class.getName()));

    @Autowired
    public HelpTipManager(@ComponentImport BandanaManager bandanaManager) {
        this.bandanaManager = bandanaManager;
    }

    public void dismissTip(User user, String id) {
        String tipId = this.getTipId(id);
        Set<String> dismissedHelpTipIds = this.getDismissedHelpTipIds(user);
        dismissedHelpTipIds.add(tipId);
        this.bandanaManager.setValue(helpTipContext, user.getName(), dismissedHelpTipIds);
    }

    public void undismissTip(User user, String id) {
        String tipId = this.getTipId(id);
        Set<String> dismissedHelpTipIds = this.getDismissedHelpTipIds(user);
        dismissedHelpTipIds.remove(tipId);
        this.bandanaManager.setValue(helpTipContext, user.getName(), dismissedHelpTipIds);
    }

    public Collection<String> getDismissedTips(User user) {
        return this.getDismissedHelpTipIds(user);
    }

    private String getTipId(String id) {
        return id;
    }

    private Set<String> getDismissedHelpTipIds(User user) {
        HashSet dismissedHelpTipIds = (HashSet)this.bandanaManager.getValue(helpTipContext, user.getName());
        if (dismissedHelpTipIds == null) {
            dismissedHelpTipIds = new HashSet();
        }
        return dismissedHelpTipIds;
    }
}

