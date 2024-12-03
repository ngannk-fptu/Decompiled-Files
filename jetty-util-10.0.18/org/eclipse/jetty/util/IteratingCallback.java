/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.util;

import java.io.IOException;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.thread.AutoLock;

public abstract class IteratingCallback
implements Callback {
    private final AutoLock _lock = new AutoLock();
    private State _state;
    private Throwable _failure;
    private boolean _iterate;

    protected IteratingCallback() {
        this._state = State.IDLE;
    }

    protected IteratingCallback(boolean needReset) {
        this._state = needReset ? State.SUCCEEDED : State.IDLE;
    }

    protected abstract Action process() throws Throwable;

    protected void onCompleteSuccess() {
    }

    protected void onCompleteFailure(Throwable cause) {
    }

    /*
     * Unable to fully structure code
     */
    public void iterate() {
        process = false;
        ignored = this._lock.lock();
        try {
            switch (1.$SwitchMap$org$eclipse$jetty$util$IteratingCallback$State[this._state.ordinal()]) {
                case 1: 
                case 2: {
                    ** break;
lbl7:
                    // 1 sources

                    break;
                }
                case 3: {
                    this._state = State.PROCESSING;
                    process = true;
                    ** break;
lbl12:
                    // 1 sources

                    break;
                }
                case 4: {
                    this._iterate = true;
                    ** break;
lbl16:
                    // 1 sources

                    break;
                }
                case 5: 
                case 6: {
                    ** break;
lbl19:
                    // 1 sources

                    break;
                }
                default: {
                    throw new IllegalStateException(this.toString());
                }
            }
        }
        finally {
            if (ignored != null) {
                ignored.close();
            }
        }
        if (process) {
            this.processing();
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    private void processing() {
        boolean notifyCompleteSuccess = false;
        Throwable notifyCompleteFailure = null;
        block24: while (true) {
            Action action = null;
            try {
                action = this.process();
            }
            catch (Throwable x) {
                this.failed(x);
            }
            AutoLock ignored = this._lock.lock();
            try {
                switch (this._state) {
                    case PROCESSING: {
                        if (action == null) throw new IllegalStateException(String.format("%s[action=%s]", new Object[]{this, action}));
                        switch (action) {
                            case IDLE: {
                                if (this._iterate) {
                                    this._iterate = false;
                                    continue block24;
                                }
                                this._state = State.IDLE;
                                break block24;
                            }
                            case SCHEDULED: {
                                this._state = State.PENDING;
                                break block24;
                            }
                            case SUCCEEDED: {
                                this._iterate = false;
                                this._state = State.SUCCEEDED;
                                notifyCompleteSuccess = true;
                                break block24;
                            }
                            default: {
                                throw new IllegalStateException(String.format("%s[action=%s]", new Object[]{this, action}));
                            }
                        }
                    }
                    case CALLED: {
                        if (action != Action.SCHEDULED) {
                            throw new IllegalStateException(String.format("%s[action=%s]", new Object[]{this, action}));
                        }
                        this._state = State.PROCESSING;
                        continue block24;
                    }
                    case FAILED: 
                    case CLOSED: {
                        notifyCompleteFailure = this._failure;
                        this._failure = null;
                        break block24;
                    }
                    case SUCCEEDED: {
                        break block24;
                    }
                    default: {
                        throw new IllegalStateException(String.format("%s[action=%s]", new Object[]{this, action}));
                    }
                }
            }
            finally {
                if (ignored == null) continue;
                ignored.close();
                continue;
            }
            break;
        }
        if (notifyCompleteSuccess) {
            this.onCompleteSuccess();
            return;
        }
        if (notifyCompleteFailure == null) return;
        this.onCompleteFailure(notifyCompleteFailure);
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public void succeeded() {
        process = false;
        ignored = this._lock.lock();
        try {
            switch (1.$SwitchMap$org$eclipse$jetty$util$IteratingCallback$State[this._state.ordinal()]) {
                case 4: {
                    this._state = State.CALLED;
                    ** break;
lbl8:
                    // 1 sources

                    break;
                }
                case 1: {
                    this._state = State.PROCESSING;
                    process = true;
                    ** break;
lbl13:
                    // 1 sources

                    break;
                }
                case 5: 
                case 7: {
                    ** break;
lbl16:
                    // 1 sources

                    break;
                }
                default: {
                    throw new IllegalStateException(this.toString());
                }
            }
        }
        finally {
            if (ignored != null) {
                ignored.close();
            }
        }
        if (process) {
            this.processing();
        }
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public void failed(Throwable x) {
        failure = false;
        ignored = this._lock.lock();
        try {
            switch (1.$SwitchMap$org$eclipse$jetty$util$IteratingCallback$State[this._state.ordinal()]) {
                case 2: 
                case 3: 
                case 5: 
                case 6: 
                case 7: {
                    ** break;
lbl7:
                    // 1 sources

                    break;
                }
                case 1: {
                    this._state = State.FAILED;
                    failure = true;
                    ** break;
lbl12:
                    // 1 sources

                    break;
                }
                case 4: {
                    this._state = State.FAILED;
                    this._failure = x;
                    ** break;
lbl17:
                    // 1 sources

                    break;
                }
                default: {
                    throw new IllegalStateException(this.toString());
                }
            }
        }
        finally {
            if (ignored != null) {
                ignored.close();
            }
        }
        if (failure) {
            this.onCompleteFailure(x);
        }
    }

    /*
     * Unable to fully structure code
     */
    public void close() {
        failure = null;
        ignored = this._lock.lock();
        try {
            switch (1.$SwitchMap$org$eclipse$jetty$util$IteratingCallback$State[this._state.ordinal()]) {
                case 3: 
                case 5: 
                case 6: {
                    this._state = State.CLOSED;
                    ** break;
lbl8:
                    // 1 sources

                    break;
                }
                case 4: {
                    this._failure = new IOException(String.format("Close %s in state %s", new Object[]{this, this._state}));
                    this._state = State.CLOSED;
                    ** break;
lbl13:
                    // 1 sources

                    break;
                }
                case 7: {
                    ** break;
lbl16:
                    // 1 sources

                    break;
                }
                default: {
                    failure = String.format("Close %s in state %s", new Object[]{this, this._state});
                    this._state = State.CLOSED;
                    break;
                }
            }
        }
        finally {
            if (ignored != null) {
                ignored.close();
            }
        }
        if (failure != null) {
            this.onCompleteFailure(new IOException(failure));
        }
    }

    boolean isIdle() {
        try (AutoLock ignored = this._lock.lock();){
            boolean bl = this._state == State.IDLE;
            return bl;
        }
    }

    public boolean isClosed() {
        try (AutoLock ignored = this._lock.lock();){
            boolean bl = this._state == State.CLOSED;
            return bl;
        }
    }

    public boolean isFailed() {
        try (AutoLock ignored = this._lock.lock();){
            boolean bl = this._state == State.FAILED;
            return bl;
        }
    }

    public boolean isSucceeded() {
        try (AutoLock ignored = this._lock.lock();){
            boolean bl = this._state == State.SUCCEEDED;
            return bl;
        }
    }

    public boolean reset() {
        try (AutoLock ignored = this._lock.lock();){
            switch (this._state) {
                case IDLE: {
                    boolean bl = true;
                    return bl;
                }
                case FAILED: 
                case SUCCEEDED: {
                    this._state = State.IDLE;
                    this._failure = null;
                    this._iterate = false;
                    boolean bl = true;
                    return bl;
                }
            }
            boolean bl = false;
            return bl;
        }
    }

    public String toString() {
        return String.format("%s@%x[%s]", new Object[]{this.getClass().getSimpleName(), this.hashCode(), this._state});
    }

    private static enum State {
        IDLE,
        PROCESSING,
        PENDING,
        CALLED,
        SUCCEEDED,
        FAILED,
        CLOSED;

    }

    protected static enum Action {
        IDLE,
        SCHEDULED,
        SUCCEEDED;

    }
}

