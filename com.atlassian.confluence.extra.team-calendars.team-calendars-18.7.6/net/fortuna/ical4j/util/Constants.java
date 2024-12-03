/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.util;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.property.Action;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Clazz;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Priority;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Transp;
import net.fortuna.ical4j.model.property.Version;

public final class Constants {
    private Constants() {
    }

    public static Property forProperty(Property property) {
        Property retVal = property;
        if (Action.AUDIO.equals(property)) {
            retVal = Action.AUDIO;
        } else if (Action.DISPLAY.equals(property)) {
            retVal = Action.DISPLAY;
        } else if (Action.EMAIL.equals(property)) {
            retVal = Action.EMAIL;
        } else if (Action.PROCEDURE.equals(property)) {
            retVal = Action.PROCEDURE;
        } else if (CalScale.GREGORIAN.equals(property)) {
            retVal = CalScale.GREGORIAN;
        } else if (Clazz.CONFIDENTIAL.equals(property)) {
            retVal = Clazz.CONFIDENTIAL;
        } else if (Clazz.PRIVATE.equals(property)) {
            retVal = Clazz.PRIVATE;
        } else if (Clazz.PUBLIC.equals(property)) {
            retVal = Clazz.PUBLIC;
        } else if (Method.ADD.equals(property)) {
            retVal = Method.ADD;
        } else if (Method.CANCEL.equals(property)) {
            retVal = Method.CANCEL;
        } else if (Method.COUNTER.equals(property)) {
            retVal = Method.COUNTER;
        } else if (Method.DECLINE_COUNTER.equals(property)) {
            retVal = Method.DECLINE_COUNTER;
        } else if (Method.PUBLISH.equals(property)) {
            retVal = Method.PUBLISH;
        } else if (Method.REFRESH.equals(property)) {
            retVal = Method.REFRESH;
        } else if (Method.REPLY.equals(property)) {
            retVal = Method.REPLY;
        } else if (Method.REQUEST.equals(property)) {
            retVal = Method.REQUEST;
        } else if (Priority.HIGH.equals(property)) {
            retVal = Priority.HIGH;
        } else if (Priority.LOW.equals(property)) {
            retVal = Priority.LOW;
        } else if (Priority.MEDIUM.equals(property)) {
            retVal = Priority.MEDIUM;
        } else if (Priority.UNDEFINED.equals(property)) {
            retVal = Priority.UNDEFINED;
        } else if (Status.VEVENT_CANCELLED.equals(property)) {
            retVal = Status.VEVENT_CANCELLED;
        } else if (Status.VEVENT_CONFIRMED.equals(property)) {
            retVal = Status.VEVENT_CONFIRMED;
        } else if (Status.VEVENT_TENTATIVE.equals(property)) {
            retVal = Status.VEVENT_TENTATIVE;
        } else if (Status.VJOURNAL_CANCELLED.equals(property)) {
            retVal = Status.VJOURNAL_CANCELLED;
        } else if (Status.VJOURNAL_DRAFT.equals(property)) {
            retVal = Status.VJOURNAL_DRAFT;
        } else if (Status.VJOURNAL_FINAL.equals(property)) {
            retVal = Status.VJOURNAL_FINAL;
        } else if (Status.VTODO_CANCELLED.equals(property)) {
            retVal = Status.VTODO_CANCELLED;
        } else if (Status.VTODO_COMPLETED.equals(property)) {
            retVal = Status.VTODO_COMPLETED;
        } else if (Status.VTODO_IN_PROCESS.equals(property)) {
            retVal = Status.VTODO_IN_PROCESS;
        } else if (Status.VTODO_NEEDS_ACTION.equals(property)) {
            retVal = Status.VTODO_NEEDS_ACTION;
        } else if (Transp.OPAQUE.equals(property)) {
            retVal = Transp.OPAQUE;
        } else if (Transp.TRANSPARENT.equals(property)) {
            retVal = Transp.TRANSPARENT;
        } else if (Version.VERSION_2_0.equals(property)) {
            retVal = Version.VERSION_2_0;
        }
        return retVal;
    }
}

