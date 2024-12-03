/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.service.event.Event
 *  org.osgi.service.event.EventAdmin
 */
package org.eclipse.gemini.blueprint.extender.internal.blueprint.event;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

enum PublishType {
    SEND{

        @Override
        void publish(EventAdmin admin, Event event) {
            admin.sendEvent(event);
        }
    }
    ,
    POST{

        @Override
        void publish(EventAdmin admin, Event event) {
            admin.postEvent(event);
        }
    };


    abstract void publish(EventAdmin var1, Event var2);
}

