/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.observation;

import java.util.Map;
import javax.jcr.RepositoryException;

public interface Event {
    public static final int NODE_ADDED = 1;
    public static final int NODE_REMOVED = 2;
    public static final int PROPERTY_ADDED = 4;
    public static final int PROPERTY_REMOVED = 8;
    public static final int PROPERTY_CHANGED = 16;
    public static final int NODE_MOVED = 32;
    public static final int PERSIST = 64;

    public int getType();

    public String getPath() throws RepositoryException;

    public String getUserID();

    public String getIdentifier() throws RepositoryException;

    public Map getInfo() throws RepositoryException;

    public String getUserData() throws RepositoryException;

    public long getDate() throws RepositoryException;
}

