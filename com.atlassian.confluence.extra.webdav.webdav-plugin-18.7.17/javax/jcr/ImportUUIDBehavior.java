/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr;

public interface ImportUUIDBehavior {
    public static final int IMPORT_UUID_CREATE_NEW = 0;
    public static final int IMPORT_UUID_COLLISION_REMOVE_EXISTING = 1;
    public static final int IMPORT_UUID_COLLISION_REPLACE_EXISTING = 2;
    public static final int IMPORT_UUID_COLLISION_THROW = 3;
}

