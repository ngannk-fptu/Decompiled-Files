/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.johnson.event;

import com.atlassian.johnson.event.EventLevel;
import com.atlassian.johnson.event.EventType;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Event {
    private final Map<Object, Object> attributes;
    private String date;
    private String desc;
    private String exception;
    private EventType key;
    private EventLevel level;
    private int progress;

    public Event(EventType key, String desc) {
        this(key, desc, null, null);
    }

    public Event(EventType key, String desc, String exception) {
        this(key, desc, exception, null);
    }

    public Event(EventType key, String desc, EventLevel level) {
        this(key, desc, null, level);
    }

    public Event(EventType key, String desc, String exception, EventLevel level) {
        this.desc = desc;
        this.exception = exception;
        this.key = key;
        this.level = level;
        this.attributes = new HashMap<Object, Object>();
        this.date = Event.getFormattedDate();
        this.progress = -1;
    }

    public static String toString(Throwable t) {
        StringWriter string = new StringWriter();
        PrintWriter writer = new PrintWriter(string);
        writer.println(t);
        t.printStackTrace(writer);
        while ((t = t.getCause()) != null) {
            writer.println("Caused by: " + t);
            t.printStackTrace(writer);
        }
        writer.flush();
        return string.toString();
    }

    public void addAttribute(Object key, Object value) {
        this.attributes.put(key, value);
    }

    public Object getAttribute(Object key) {
        return this.attributes.get(key);
    }

    public Map getAttributes() {
        return Collections.unmodifiableMap(this.attributes);
    }

    public String getDate() {
        return this.date;
    }

    public String getDesc() {
        return this.desc;
    }

    public String getException() {
        return this.exception;
    }

    public EventType getKey() {
        return this.key;
    }

    public EventLevel getLevel() {
        return this.level;
    }

    public int getProgress() {
        return this.progress;
    }

    public boolean hasProgress() {
        return this.progress != -1;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public void setKey(EventType name) {
        this.key = name;
    }

    public void setLevel(EventLevel level) {
        this.level = level;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Event)) {
            return false;
        }
        Event e = (Event)o;
        return Objects.equals(this.getDate(), e.getDate()) && Objects.equals(this.getDesc(), e.getDesc()) && Objects.equals(this.getException(), e.getException()) && Objects.equals(this.getKey(), e.getKey()) && Objects.equals(this.getLevel(), e.getLevel());
    }

    public int hashCode() {
        return Objects.hash(this.getKey(), this.getDesc(), this.getException(), this.getLevel(), this.getDate());
    }

    public String toString() {
        return "(Event: Level = " + (this.getLevel() == null ? "" : this.getLevel() + " ") + ", Key = " + (this.getKey() == null ? "" : this.getKey() + " ") + ", Desc = " + (this.getDesc() == null ? "" : this.getDesc() + " ") + ", Exception = " + (this.getException() == null ? "" : this.getException() + ")");
    }

    private static String getFormattedDate() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}

