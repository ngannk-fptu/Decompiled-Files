/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.listener;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.DefaultLogger;

public class ProfileLogger
extends DefaultLogger {
    private Map<Object, Date> profileData = new ConcurrentHashMap<Object, Date>();

    @Override
    public void targetStarted(BuildEvent event) {
        Date now = new Date();
        String name = "Target " + event.getTarget().getName();
        this.logStart(event, now, name);
        this.profileData.put(event.getTarget(), now);
    }

    @Override
    public void targetFinished(BuildEvent event) {
        Date start = this.profileData.remove(event.getTarget());
        String name = "Target " + event.getTarget().getName();
        this.logFinish(event, start, name);
    }

    @Override
    public void taskStarted(BuildEvent event) {
        String name = event.getTask().getTaskName();
        Date now = new Date();
        this.logStart(event, now, name);
        this.profileData.put(event.getTask(), now);
    }

    @Override
    public void taskFinished(BuildEvent event) {
        Date start = this.profileData.remove(event.getTask());
        String name = event.getTask().getTaskName();
        this.logFinish(event, start, name);
    }

    private void logFinish(BuildEvent event, Date start, String name) {
        String msg;
        Date now = new Date();
        if (start != null) {
            long diff = now.getTime() - start.getTime();
            msg = String.format("%n%s: finished %s (%d)", name, now, diff);
        } else {
            msg = String.format("%n%s: finished %s (unknown duration, start not detected)", name, now);
        }
        this.printMessage(msg, this.out, event.getPriority());
        this.log(msg);
    }

    private void logStart(BuildEvent event, Date start, String name) {
        String msg = String.format("%n%s: started %s", name, start);
        this.printMessage(msg, this.out, event.getPriority());
        this.log(msg);
    }
}

