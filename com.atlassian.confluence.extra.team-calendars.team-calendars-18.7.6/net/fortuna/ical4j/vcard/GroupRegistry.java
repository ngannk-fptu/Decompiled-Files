/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package net.fortuna.ical4j.vcard;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.fortuna.ical4j.vcard.Group;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GroupRegistry {
    private static final Log LOG = LogFactory.getLog(GroupRegistry.class);
    private final Map<Group.Id, Group> defaultGroups = new HashMap<Group.Id, Group>();
    private final Map<String, Group> extendedGroups;

    public GroupRegistry() {
        this.defaultGroups.put(Group.Id.HOME, Group.HOME);
        this.defaultGroups.put(Group.Id.WORK, Group.WORK);
        this.extendedGroups = new ConcurrentHashMap<String, Group>();
    }

    public Group getGroup(String value) {
        Group.Id id = null;
        try {
            id = Group.Id.valueOf(value);
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (id != null) {
            return this.defaultGroups.get((Object)id);
        }
        return this.extendedGroups.get(value);
    }

    public void register(String extendedName, Group group) {
        this.extendedGroups.put(extendedName, group);
    }
}

