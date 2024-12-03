/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.status.StatusData
 *  org.apache.logging.log4j.status.StatusListener
 *  org.apache.logging.log4j.status.StatusLogger
 */
package org.apache.logging.log4j.core.jmx;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;
import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ObjectName;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.jmx.Server;
import org.apache.logging.log4j.core.jmx.StatusLoggerAdminMBean;
import org.apache.logging.log4j.status.StatusData;
import org.apache.logging.log4j.status.StatusListener;
import org.apache.logging.log4j.status.StatusLogger;

public class StatusLoggerAdmin
extends NotificationBroadcasterSupport
implements StatusListener,
StatusLoggerAdminMBean {
    private final AtomicLong sequenceNo = new AtomicLong();
    private final ObjectName objectName;
    private final String contextName;
    private Level level = Level.WARN;

    public StatusLoggerAdmin(String contextName, Executor executor) {
        super(executor, StatusLoggerAdmin.createNotificationInfo());
        this.contextName = contextName;
        try {
            String mbeanName = String.format("org.apache.logging.log4j2:type=%s,component=StatusLogger", Server.escape(contextName));
            this.objectName = new ObjectName(mbeanName);
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
        this.removeListeners(contextName);
        StatusLogger.getLogger().registerListener((StatusListener)this);
    }

    private void removeListeners(String ctxName) {
        StatusLogger logger = StatusLogger.getLogger();
        Iterable listeners = logger.getListeners();
        for (StatusListener statusListener : listeners) {
            if (!(statusListener instanceof StatusLoggerAdmin)) continue;
            StatusLoggerAdmin adminListener = (StatusLoggerAdmin)statusListener;
            if (ctxName == null || !ctxName.equals(adminListener.contextName)) continue;
            logger.removeListener((StatusListener)adminListener);
        }
    }

    private static MBeanNotificationInfo createNotificationInfo() {
        String[] notifTypes = new String[]{"com.apache.logging.log4j.core.jmx.statuslogger.data", "com.apache.logging.log4j.core.jmx.statuslogger.message"};
        String name = Notification.class.getName();
        String description = "StatusLogger has logged an event";
        return new MBeanNotificationInfo(notifTypes, name, "StatusLogger has logged an event");
    }

    @Override
    public String[] getStatusDataHistory() {
        List<StatusData> data = this.getStatusData();
        String[] result = new String[data.size()];
        for (int i = 0; i < result.length; ++i) {
            result[i] = data.get(i).getFormattedStatus();
        }
        return result;
    }

    @Override
    public List<StatusData> getStatusData() {
        return StatusLogger.getLogger().getStatusData();
    }

    @Override
    public String getLevel() {
        return this.level.name();
    }

    public Level getStatusLevel() {
        return this.level;
    }

    @Override
    public void setLevel(String level) {
        this.level = Level.toLevel((String)level, (Level)Level.ERROR);
    }

    @Override
    public String getContextName() {
        return this.contextName;
    }

    public void log(StatusData data) {
        Notification notifMsg = new Notification("com.apache.logging.log4j.core.jmx.statuslogger.message", this.getObjectName(), this.nextSeqNo(), this.nowMillis(), data.getFormattedStatus());
        this.sendNotification(notifMsg);
        Notification notifData = new Notification("com.apache.logging.log4j.core.jmx.statuslogger.data", (Object)this.getObjectName(), this.nextSeqNo(), this.nowMillis());
        notifData.setUserData(data);
        this.sendNotification(notifData);
    }

    @Override
    public ObjectName getObjectName() {
        return this.objectName;
    }

    private long nextSeqNo() {
        return this.sequenceNo.getAndIncrement();
    }

    private long nowMillis() {
        return System.currentTimeMillis();
    }

    public void close() throws IOException {
    }
}

