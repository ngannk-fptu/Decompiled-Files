/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.event;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.configuration2.event.Event;

public class EventType<T extends Event>
implements Serializable {
    private static final long serialVersionUID = 20150416L;
    private static final String FMT_TO_STRING = "%s [ %s ]";
    private final EventType<? super T> superType;
    private final String name;

    public EventType(EventType<? super T> superEventType, String typeName) {
        this.superType = superEventType;
        this.name = typeName;
    }

    public EventType<? super T> getSuperType() {
        return this.superType;
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        return String.format(FMT_TO_STRING, this.getClass().getSimpleName(), this.getName());
    }

    public static Set<EventType<?>> fetchSuperEventTypes(EventType<?> eventType) {
        HashSet types = new HashSet();
        for (EventType<?> currentType = eventType; currentType != null; currentType = currentType.getSuperType()) {
            types.add(currentType);
        }
        return types;
    }

    public static boolean isInstanceOf(EventType<?> derivedType, EventType<?> baseType) {
        for (EventType<?> currentType = derivedType; currentType != null; currentType = currentType.getSuperType()) {
            if (currentType != baseType) continue;
            return true;
        }
        return false;
    }
}

