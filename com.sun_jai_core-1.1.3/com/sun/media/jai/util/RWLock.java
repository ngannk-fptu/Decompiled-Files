/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.util;

import java.util.LinkedList;
import java.util.ListIterator;

public final class RWLock {
    private boolean allowUpgrades;
    private static int READ = 1;
    private static int WRITE = 2;
    private static int NOT_FOUND = -1;
    private WaitingList waitingList = new WaitingList();

    public RWLock(boolean allowUpgrades) {
        this.allowUpgrades = allowUpgrades;
    }

    public RWLock() {
        this(true);
    }

    public synchronized boolean forReading(int waitTime) {
        ReaderWriter element = null;
        int index = this.waitingList.findMe();
        if (index != NOT_FOUND) {
            element = (ReaderWriter)this.waitingList.get(index);
        } else {
            element = new ReaderWriter(READ);
            this.waitingList.add(element);
        }
        if (element.lockCount > 0) {
            ++element.lockCount;
            return true;
        }
        long startTime = System.currentTimeMillis();
        long endTime = (long)waitTime + startTime;
        do {
            int nextWriter = this.waitingList.indexOfFirstWriter();
            index = this.waitingList.findMe();
            if (nextWriter == NOT_FOUND || nextWriter > index) {
                ++element.lockCount;
                element.granted = true;
                return true;
            }
            if (waitTime == 0) {
                this.waitingList.remove(element);
                return false;
            }
            try {
                if (waitTime < 0) {
                    this.wait();
                    continue;
                }
                long delta = endTime - System.currentTimeMillis();
                if (delta <= 0L) continue;
                this.wait(delta);
            }
            catch (InterruptedException e) {
                System.err.println(element.key.getName() + " : interrupted while waiting for a READ lock!");
            }
        } while (waitTime < 0 || endTime > System.currentTimeMillis());
        this.waitingList.remove(element);
        this.notifyAll();
        return false;
    }

    public synchronized boolean forReading() {
        return this.forReading(-1);
    }

    public synchronized boolean forWriting(int waitTime) throws UpgradeNotAllowed {
        ReaderWriter element = null;
        int index = this.waitingList.findMe();
        if (index != NOT_FOUND) {
            element = (ReaderWriter)this.waitingList.get(index);
        } else {
            element = new ReaderWriter(WRITE);
            this.waitingList.add(element);
        }
        if (element.granted && element.lockType == READ) {
            try {
                if (!this.upgrade(waitTime)) {
                    return false;
                }
            }
            catch (LockNotHeld e) {
                return false;
            }
        }
        if (element.lockCount > 0) {
            ++element.lockCount;
            return true;
        }
        long startTime = System.currentTimeMillis();
        long endTime = (long)waitTime + startTime;
        do {
            if ((index = this.waitingList.findMe()) == 0) {
                ++element.lockCount;
                element.granted = true;
                return true;
            }
            if (waitTime == 0) {
                this.waitingList.remove(element);
                return false;
            }
            try {
                if (waitTime < 0) {
                    this.wait();
                    continue;
                }
                long delta = endTime - System.currentTimeMillis();
                if (delta <= 0L) continue;
                this.wait(delta);
            }
            catch (InterruptedException e) {
                System.err.println(element.key.getName() + " : interrupted while waiting for a WRITE lock!");
            }
        } while (waitTime < 0 || endTime > System.currentTimeMillis());
        this.waitingList.remove(element);
        this.notifyAll();
        return false;
    }

    public synchronized boolean forWriting() throws UpgradeNotAllowed {
        return this.forWriting(-1);
    }

    public synchronized boolean upgrade(int waitTime) throws UpgradeNotAllowed, LockNotHeld {
        if (!this.allowUpgrades) {
            throw new UpgradeNotAllowed();
        }
        int index = this.waitingList.findMe();
        if (index == NOT_FOUND) {
            throw new LockNotHeld();
        }
        ReaderWriter element = (ReaderWriter)this.waitingList.get(index);
        if (element.lockType == WRITE) {
            return true;
        }
        int lastGranted = this.waitingList.indexOfLastGranted();
        if (lastGranted == NOT_FOUND) {
            throw new LockNotHeld();
        }
        if (index != lastGranted) {
            this.waitingList.remove(index);
            ListIterator<ReaderWriter> iter = this.waitingList.listIterator(lastGranted);
            iter.add(element);
        }
        element.lockType = WRITE;
        long startTime = System.currentTimeMillis();
        long endTime = (long)waitTime + startTime;
        do {
            if ((index = this.waitingList.findMe()) == 0) {
                return true;
            }
            if (waitTime == 0) {
                element.lockType = READ;
                return false;
            }
            try {
                if (waitTime < 0) {
                    this.wait();
                    continue;
                }
                long delta = endTime - System.currentTimeMillis();
                if (delta <= 0L) continue;
                this.wait(delta);
            }
            catch (InterruptedException e) {
                System.err.println(element.key.getName() + " : interrupted while waiting to UPGRADE lock!");
            }
        } while (waitTime < 0 || endTime > System.currentTimeMillis());
        element.lockType = READ;
        this.notifyAll();
        return false;
    }

    public synchronized boolean upgrade() throws UpgradeNotAllowed, LockNotHeld {
        return this.upgrade(-1);
    }

    public synchronized boolean downgrade() throws LockNotHeld {
        int index = this.waitingList.findMe();
        if (index == NOT_FOUND) {
            throw new LockNotHeld();
        }
        ReaderWriter e = (ReaderWriter)this.waitingList.get(index);
        if (e.lockType == WRITE) {
            e.lockType = READ;
            this.notifyAll();
        }
        return true;
    }

    public synchronized void release() throws LockNotHeld {
        int index = this.waitingList.findMe();
        if (index == NOT_FOUND) {
            throw new LockNotHeld();
        }
        ReaderWriter e = (ReaderWriter)this.waitingList.get(index);
        if (--e.lockCount == 0) {
            this.waitingList.remove(index);
            this.notifyAll();
        }
    }

    public class LockNotHeld
    extends RuntimeException {
    }

    public class UpgradeNotAllowed
    extends RuntimeException {
    }

    private class ReaderWriter {
        Thread key = Thread.currentThread();
        int lockType;
        int lockCount;
        boolean granted;

        ReaderWriter() {
            this(0);
        }

        ReaderWriter(int type) {
            this.lockType = type;
            this.lockCount = 0;
            this.granted = false;
        }

        public boolean equals(Object o) {
            return this.key == ((ReaderWriter)o).key;
        }
    }

    private class WaitingList
    extends LinkedList {
        private WaitingList() {
        }

        int indexOfFirstWriter() {
            ListIterator iter = this.listIterator(0);
            int index = 0;
            while (iter.hasNext()) {
                if (((ReaderWriter)iter.next()).lockType == WRITE) {
                    return index;
                }
                ++index;
            }
            return NOT_FOUND;
        }

        int indexOfLastGranted() {
            ListIterator iter = this.listIterator(this.size());
            int index = this.size() - 1;
            while (iter.hasPrevious()) {
                if (((ReaderWriter)iter.previous()).granted) {
                    return index;
                }
                --index;
            }
            return NOT_FOUND;
        }

        int findMe() {
            return this.indexOf(new ReaderWriter());
        }
    }
}

