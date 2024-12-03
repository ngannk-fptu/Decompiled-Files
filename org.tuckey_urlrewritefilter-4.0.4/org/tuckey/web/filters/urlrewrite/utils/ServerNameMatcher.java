/*
 * Decompiled with CFR 0.152.
 */
package org.tuckey.web.filters.urlrewrite.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.tuckey.web.filters.urlrewrite.utils.Log;
import org.tuckey.web.filters.urlrewrite.utils.StringUtils;
import org.tuckey.web.filters.urlrewrite.utils.WildcardHelper;

public class ServerNameMatcher {
    private static Log log = Log.getLog(ServerNameMatcher.class);
    private List patterns = new ArrayList();
    WildcardHelper wh = new WildcardHelper();

    public ServerNameMatcher(String options) {
        String[] enableOnHostsArr = options.split(",");
        for (int i = 0; i < enableOnHostsArr.length; ++i) {
            String s = enableOnHostsArr[i];
            if (StringUtils.isBlank(s)) continue;
            String rawPattern = StringUtils.trim(enableOnHostsArr[i]).toLowerCase();
            int[] compiledPattern = this.wh.compilePattern(rawPattern);
            this.patterns.add(compiledPattern);
        }
    }

    public boolean isMatch(String serverName) {
        log.debug("looking for hostname match on current server name " + serverName);
        if (this.patterns == null || StringUtils.isBlank(serverName)) {
            return false;
        }
        serverName = StringUtils.trim(serverName).toLowerCase();
        for (int i = 0; i < this.patterns.size(); ++i) {
            HashMap map = new HashMap();
            int[] compiledPattern = (int[])this.patterns.get(i);
            if (!this.wh.match(map, serverName, compiledPattern)) continue;
            return true;
        }
        return false;
    }
}

