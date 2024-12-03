/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.server.sysinterface;

import java.io.Serializable;
import net.fortuna.ical4j.vcard.Property;
import net.fortuna.ical4j.vcard.VCard;
import net.fortuna.ical4j.vcard.property.N;
import org.bedework.access.AccessPrincipal;
import org.bedework.webdav.servlet.shared.WebdavException;

public class CalPrincipalInfo
implements Serializable {
    public AccessPrincipal principal;
    public VCard card;
    private String cardStr;
    public String userHomePath;
    public String defaultCalendarPath;
    public String inboxPath;
    public String outboxPath;
    public String notificationsPath;
    private long quota;

    public CalPrincipalInfo(AccessPrincipal principal, VCard card, String cardStr, String userHomePath, String defaultCalendarPath, String inboxPath, String outboxPath, String notificationsPath, long quota) {
        this.principal = principal;
        this.card = card;
        this.cardStr = cardStr;
        this.userHomePath = userHomePath;
        this.defaultCalendarPath = defaultCalendarPath;
        this.inboxPath = inboxPath;
        this.outboxPath = outboxPath;
        this.notificationsPath = notificationsPath;
        this.quota = quota;
    }

    public VCard getCard() {
        return this.card;
    }

    public String getCardStr() {
        return this.cardStr;
    }

    public long getQuota() {
        return this.quota;
    }

    public String getDisplayname() throws WebdavException {
        if (this.card == null) {
            return null;
        }
        String nn = this.propertyVal(Property.Id.NICKNAME);
        if (nn != null) {
            return nn;
        }
        N n = (N)this.card.getProperty(Property.Id.N);
        if (n == null) {
            return null;
        }
        return this.notNull(n.getGivenName()) + " " + this.notNull(n.getFamilyName());
    }

    private String notNull(String val) {
        if (val == null) {
            return "";
        }
        return val;
    }

    private String propertyVal(Property.Id id) {
        Property p = this.card.getProperty(id);
        if (p == null) {
            return null;
        }
        return p.getValue();
    }
}

