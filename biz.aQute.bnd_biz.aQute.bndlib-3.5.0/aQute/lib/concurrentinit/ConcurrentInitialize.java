/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.concurrentinit;

public abstract class ConcurrentInitialize<T> {
    private State state = State.INIT;
    private T value;
    private Object lock = new Object();
    private Thread creatingThread;
    private Exception exception;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public T get() throws Exception {
        Object object = this.lock;
        synchronized (object) {
            switch (this.state) {
                case INIT: {
                    this.state = State.CREATING;
                    this.creatingThread = Thread.currentThread();
                    break;
                }
                case CREATING: {
                    if (this.creatingThread == Thread.currentThread()) {
                        throw new IllegalStateException("Cycle:  ConcurrentInitialize's create returns to same instance");
                    }
                    do {
                        this.lock.wait();
                    } while (this.state == State.CREATING);
                    if (this.state == State.ERROR) {
                        throw this.exception;
                    }
                    return this.value;
                }
                case ERROR: {
                    throw this.exception;
                }
                case DONE: {
                    return this.value;
                }
            }
        }
        try {
            this.set(this.create(), null, State.DONE);
            return this.value;
        }
        catch (Exception e) {
            this.set(null, e, State.ERROR);
            throw e;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void set(T value, Exception e, State state) {
        Object object = this.lock;
        synchronized (object) {
            this.value = value;
            this.state = state;
            this.creatingThread = null;
            this.lock.notifyAll();
            this.exception = e;
        }
    }

    public abstract T create() throws Exception;

    static enum State {
        INIT,
        CREATING,
        DONE,
        ERROR;

    }
}

