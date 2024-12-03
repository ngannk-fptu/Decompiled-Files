/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.store;

import com.atlassian.lucene36.store.FSLockFactory;
import com.atlassian.lucene36.store.Lock;
import com.atlassian.lucene36.store.LockFactory;
import com.atlassian.lucene36.store.LockObtainFailedException;
import com.atlassian.lucene36.store.VerifyingLockFactory;
import java.io.File;
import java.io.IOException;

public class LockStressTest {
    public static void main(String[] args) throws Exception {
        LockFactory lockFactory;
        int myID;
        if (args.length != 6) {
            System.out.println("\nUsage: java org.apache.lucene.store.LockStressTest myID verifierHostOrIP verifierPort lockFactoryClassName lockDirName sleepTime\n\n  myID = int from 0 .. 255 (should be unique for test process)\n  verifierHostOrIP = host name or IP address where LockVerifyServer is running\n  verifierPort = port that LockVerifyServer is listening on\n  lockFactoryClassName = primary LockFactory class that we will use\n  lockDirName = path to the lock directory (only set for Simple/NativeFSLockFactory\n  sleepTimeMS = milliseconds to pause betweeen each lock obtain/release\n\nYou should run multiple instances of this process, each with its own\nunique ID, and each pointing to the same lock directory, to verify\nthat locking is working correctly.\n\nMake sure you are first running LockVerifyServer.\n\n");
            System.exit(1);
        }
        if ((myID = Integer.parseInt(args[0])) < 0 || myID > 255) {
            System.out.println("myID must be a unique int 0..255");
            System.exit(1);
        }
        String verifierHost = args[1];
        int verifierPort = Integer.parseInt(args[2]);
        String lockFactoryClassName = args[3];
        String lockDirName = args[4];
        int sleepTimeMS = Integer.parseInt(args[5]);
        try {
            lockFactory = Class.forName(lockFactoryClassName).asSubclass(LockFactory.class).newInstance();
        }
        catch (IllegalAccessException e) {
            throw new IOException("IllegalAccessException when instantiating LockClass " + lockFactoryClassName);
        }
        catch (InstantiationException e) {
            throw new IOException("InstantiationException when instantiating LockClass " + lockFactoryClassName);
        }
        catch (ClassCastException e) {
            throw new IOException("unable to cast LockClass " + lockFactoryClassName + " instance to a LockFactory");
        }
        catch (ClassNotFoundException e) {
            throw new IOException("unable to find LockClass " + lockFactoryClassName);
        }
        File lockDir = new File(lockDirName);
        if (lockFactory instanceof FSLockFactory) {
            ((FSLockFactory)lockFactory).setLockDir(lockDir);
        }
        lockFactory.setLockPrefix("test");
        VerifyingLockFactory verifyLF = new VerifyingLockFactory((byte)myID, lockFactory, verifierHost, verifierPort);
        Lock l = ((LockFactory)verifyLF).makeLock("test.lock");
        while (true) {
            boolean obtained = false;
            try {
                obtained = l.obtain(10L);
            }
            catch (LockObtainFailedException e) {
                System.out.print("x");
            }
            if (obtained) {
                System.out.print("l");
                l.release();
            }
            Thread.sleep(sleepTimeMS);
        }
    }
}

