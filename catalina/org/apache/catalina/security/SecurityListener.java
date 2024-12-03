/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.buf.StringUtils
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.security;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Server;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.StringUtils;
import org.apache.tomcat.util.res.StringManager;

public class SecurityListener
implements LifecycleListener {
    private static final Log log = LogFactory.getLog(SecurityListener.class);
    private static final StringManager sm = StringManager.getManager((String)"org.apache.catalina.security");
    private static final String UMASK_PROPERTY_NAME = "org.apache.catalina.security.SecurityListener.UMASK";
    private static final String UMASK_FORMAT = "%04o";
    private final Set<String> checkedOsUsers = new HashSet<String>();
    private Integer minimumUmask = 7;

    public SecurityListener() {
        this.checkedOsUsers.add("root");
    }

    @Override
    public void lifecycleEvent(LifecycleEvent event) {
        if (event.getType().equals("before_init")) {
            if (!(event.getLifecycle() instanceof Server)) {
                log.warn((Object)sm.getString("listener.notServer", new Object[]{event.getLifecycle().getClass().getSimpleName()}));
            }
            this.doChecks();
        }
    }

    public void setCheckedOsUsers(String userNameList) {
        if (userNameList == null || userNameList.length() == 0) {
            this.checkedOsUsers.clear();
        } else {
            String[] userNames;
            for (String userName : userNames = userNameList.split(",")) {
                if (userName.length() <= 0) continue;
                this.checkedOsUsers.add(userName.toLowerCase(Locale.getDefault()));
            }
        }
    }

    public String getCheckedOsUsers() {
        return StringUtils.join(this.checkedOsUsers);
    }

    public void setMinimumUmask(String umask) {
        this.minimumUmask = umask == null || umask.length() == 0 ? Integer.valueOf(0) : Integer.valueOf(umask, 8);
    }

    public String getMinimumUmask() {
        return String.format(UMASK_FORMAT, this.minimumUmask);
    }

    protected void doChecks() {
        this.checkOsUser();
        this.checkUmask();
    }

    protected void checkOsUser() {
        String userNameLC;
        String userName = System.getProperty("user.name");
        if (userName != null && this.checkedOsUsers.contains(userNameLC = userName.toLowerCase(Locale.getDefault()))) {
            throw new Error(sm.getString("SecurityListener.checkUserWarning", new Object[]{userName}));
        }
    }

    protected void checkUmask() {
        String prop = System.getProperty(UMASK_PROPERTY_NAME);
        Integer umask = null;
        if (prop != null) {
            try {
                umask = Integer.valueOf(prop, 8);
            }
            catch (NumberFormatException nfe) {
                log.warn((Object)sm.getString("SecurityListener.checkUmaskParseFail", new Object[]{prop}));
            }
        }
        if (umask == null) {
            if ("\r\n".equals(System.lineSeparator())) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("SecurityListener.checkUmaskSkip"));
                }
                return;
            }
            if (this.minimumUmask > 0) {
                log.warn((Object)sm.getString("SecurityListener.checkUmaskNone", new Object[]{UMASK_PROPERTY_NAME, this.getMinimumUmask()}));
            }
            return;
        }
        if ((umask & this.minimumUmask) != this.minimumUmask) {
            throw new Error(sm.getString("SecurityListener.checkUmaskFail", new Object[]{String.format(UMASK_FORMAT, umask), this.getMinimumUmask()}));
        }
    }
}

