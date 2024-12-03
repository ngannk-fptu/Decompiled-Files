/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.spaces;

import com.atlassian.confluence.spaces.Space;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SpaceType
implements Serializable,
Comparable<SpaceType> {
    private static final String GLOBAL_TYPESTR = "global";
    private static final String PERSONAL_TYPESTR = "personal";
    public static final SpaceType GLOBAL = new SpaceType("global");
    public static final SpaceType PERSONAL = new SpaceType("personal");
    private static final Map<String, SpaceType> officialSpaceTypes = new HashMap<String, SpaceType>();
    private static Map<String, SpaceType> customSpaceTypes = new HashMap<String, SpaceType>();
    private final String spaceTypeString;

    public static boolean isPersonal(Space space) {
        return PERSONAL.equals(space.getSpaceType());
    }

    public static boolean isGlobal(Space space) {
        return GLOBAL.equals(space.getSpaceType());
    }

    public static SpaceType getSpaceType(String spaceTypeAsString) {
        if (spaceTypeAsString == null) {
            return null;
        }
        SpaceType foundType = officialSpaceTypes.get(spaceTypeAsString);
        if (foundType == null) {
            foundType = customSpaceTypes.get(spaceTypeAsString);
        }
        return foundType == null ? GLOBAL : foundType;
    }

    public static void addCustomSpaceType(String spaceTypeKey) {
        if (!customSpaceTypes.containsKey(spaceTypeKey)) {
            customSpaceTypes.put(spaceTypeKey, new SpaceType(spaceTypeKey));
        }
    }

    public static boolean isKnownSpaceType(String spaceType) {
        return officialSpaceTypes.containsKey(spaceType);
    }

    private SpaceType(String SpaceTypePrefix) {
        this.spaceTypeString = SpaceTypePrefix;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SpaceType SpaceType2 = (SpaceType)o;
        return this.spaceTypeString.equals(SpaceType2.spaceTypeString);
    }

    @Override
    public int compareTo(SpaceType o) {
        return this.spaceTypeString.compareTo(o.spaceTypeString);
    }

    public String toI18NKey() {
        return "space.type." + this.toString();
    }

    public String toString() {
        return this.spaceTypeString;
    }

    public int hashCode() {
        return this.spaceTypeString.hashCode();
    }

    private Object readResolve() throws ObjectStreamException {
        return SpaceType.getSpaceType(this.spaceTypeString);
    }

    static {
        officialSpaceTypes.put(PERSONAL_TYPESTR, PERSONAL);
        officialSpaceTypes.put(GLOBAL_TYPESTR, GLOBAL);
    }
}

