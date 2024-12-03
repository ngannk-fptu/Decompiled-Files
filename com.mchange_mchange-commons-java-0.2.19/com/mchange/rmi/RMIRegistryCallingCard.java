/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.rmi;

import com.mchange.io.UnsupportedVersionException;
import com.mchange.rmi.CallingCard;
import com.mchange.rmi.Checkable;
import com.mchange.rmi.ServiceUnavailableException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public class RMIRegistryCallingCard
implements CallingCard,
Serializable {
    transient Remote cached = null;
    transient String url;
    static final long serialVersionUID = 1L;
    private static final short VERSION = 1;

    public RMIRegistryCallingCard(String string, int n, String string2) {
        this.url = "//" + string.toLowerCase() + ':' + n + '/' + string2;
    }

    public RMIRegistryCallingCard(String string, String string2) {
        this(string, 1099, string2);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof RMIRegistryCallingCard && this.url.equals(((RMIRegistryCallingCard)object).url);
    }

    @Override
    public int hashCode() {
        return this.url.hashCode();
    }

    @Override
    public Remote findRemote() throws ServiceUnavailableException, RemoteException {
        if (this.cached instanceof Checkable) {
            try {
                ((Checkable)this.cached).check();
                return this.cached;
            }
            catch (RemoteException remoteException) {
                this.cached = null;
                return this.findRemote();
            }
        }
        try {
            Remote remote = Naming.lookup(this.url);
            if (remote instanceof Checkable) {
                this.cached = remote;
            }
            return remote;
        }
        catch (NotBoundException notBoundException) {
            throw new ServiceUnavailableException("Object Not Bound: " + this.url);
        }
        catch (MalformedURLException malformedURLException) {
            throw new ServiceUnavailableException("Uh oh. Bad url. It never will be available: " + this.url);
        }
    }

    @Override
    public String toString() {
        return super.toString() + " [" + this.url + "];";
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeShort(1);
        objectOutputStream.writeUTF(this.url);
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException {
        short s = objectInputStream.readShort();
        switch (s) {
            case 1: {
                this.url = objectInputStream.readUTF();
                break;
            }
            default: {
                throw new UnsupportedVersionException(this.getClass().getName() + "; Bad version: " + s);
            }
        }
    }
}

