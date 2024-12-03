/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.webdav;

import java.util.HashMap;
import java.util.Map;

public abstract class EventUtil {
    public static final String EVENT_NODEADDED = "nodeadded";
    public static final String EVENT_NODEREMOVED = "noderemoved";
    public static final String EVENT_PROPERTYADDED = "propertyadded";
    public static final String EVENT_PROPERTYREMOVED = "propertyremoved";
    public static final String EVENT_PROPERTYCHANGED = "propertychanged";
    public static final String EVENT_NODEMOVED = "nodemoved";
    public static final String EVENT_PERSIST = "persist";
    public static final String[] EVENT_ALL = new String[]{"nodeadded", "noderemoved", "propertyadded", "propertyremoved", "propertychanged", "nodemoved", "persist"};
    private static Map<String, Integer> NAME_TO_JCR = new HashMap<String, Integer>();

    public static boolean isValidEventName(String eventName) {
        return NAME_TO_JCR.containsKey(eventName);
    }

    public static int getJcrEventType(String eventName) {
        if (NAME_TO_JCR.containsKey(eventName)) {
            return NAME_TO_JCR.get(eventName);
        }
        throw new IllegalArgumentException("Invalid eventName : " + eventName);
    }

    public static String getEventName(int jcrEventType) {
        String eventName;
        switch (jcrEventType) {
            case 1: {
                eventName = EVENT_NODEADDED;
                break;
            }
            case 2: {
                eventName = EVENT_NODEREMOVED;
                break;
            }
            case 4: {
                eventName = EVENT_PROPERTYADDED;
                break;
            }
            case 16: {
                eventName = EVENT_PROPERTYCHANGED;
                break;
            }
            case 8: {
                eventName = EVENT_PROPERTYREMOVED;
                break;
            }
            case 32: {
                eventName = EVENT_NODEMOVED;
                break;
            }
            case 64: {
                eventName = EVENT_PERSIST;
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid JCR event type: " + jcrEventType);
            }
        }
        return eventName;
    }

    static {
        NAME_TO_JCR.put(EVENT_NODEADDED, 1);
        NAME_TO_JCR.put(EVENT_NODEREMOVED, 2);
        NAME_TO_JCR.put(EVENT_PROPERTYADDED, 4);
        NAME_TO_JCR.put(EVENT_PROPERTYREMOVED, 8);
        NAME_TO_JCR.put(EVENT_PROPERTYCHANGED, 16);
        NAME_TO_JCR.put(EVENT_NODEMOVED, 32);
        NAME_TO_JCR.put(EVENT_PERSIST, 64);
    }
}

