/*
 * Decompiled with CFR 0.152.
 */
package freemarker.debug;

import freemarker.debug.DebugModel;
import java.rmi.RemoteException;

public interface DebuggedEnvironment
extends DebugModel {
    public void resume() throws RemoteException;

    public void stop() throws RemoteException;

    public long getId() throws RemoteException;
}

