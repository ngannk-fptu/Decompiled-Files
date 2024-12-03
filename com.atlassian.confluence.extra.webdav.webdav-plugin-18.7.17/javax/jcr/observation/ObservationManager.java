/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.observation;

import javax.jcr.RepositoryException;
import javax.jcr.observation.EventJournal;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.EventListenerIterator;

public interface ObservationManager {
    public void addEventListener(EventListener var1, int var2, String var3, boolean var4, String[] var5, String[] var6, boolean var7) throws RepositoryException;

    public void removeEventListener(EventListener var1) throws RepositoryException;

    public EventListenerIterator getRegisteredEventListeners() throws RepositoryException;

    public void setUserData(String var1) throws RepositoryException;

    public EventJournal getEventJournal() throws RepositoryException;

    public EventJournal getEventJournal(int var1, String var2, boolean var3, String[] var4, String[] var5) throws RepositoryException;
}

