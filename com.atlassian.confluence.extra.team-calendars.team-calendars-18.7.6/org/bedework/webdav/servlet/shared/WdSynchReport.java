/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.webdav.servlet.shared;

import java.util.List;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavNsNode;

public class WdSynchReport {
    public List<WdSynchReportItem> items;
    public boolean truncated;
    public String token;

    public static class WdSynchReportItem
    implements Comparable<WdSynchReportItem> {
        private String token;
        private WebdavNsNode node;
        private boolean canSync;

        public WdSynchReportItem(WebdavNsNode node, String token, boolean canSync) throws WebdavException {
            this.node = node;
            this.token = token;
            this.canSync = canSync;
        }

        public WebdavNsNode getNode() {
            return this.node;
        }

        public boolean getCanSync() {
            return this.canSync;
        }

        @Override
        public int compareTo(WdSynchReportItem that) {
            return this.token.compareTo(that.token);
        }

        public int hashCode() {
            return this.token.hashCode();
        }

        public boolean equals(Object o) {
            return this.compareTo((WdSynchReportItem)o) == 0;
        }
    }
}

