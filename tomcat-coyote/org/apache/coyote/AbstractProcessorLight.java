/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 */
package org.apache.coyote;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.apache.coyote.Processor;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.DispatchType;
import org.apache.tomcat.util.net.SocketEvent;
import org.apache.tomcat.util.net.SocketWrapperBase;

public abstract class AbstractProcessorLight
implements Processor {
    private Set<DispatchType> dispatches = new CopyOnWriteArraySet<DispatchType>();

    @Override
    public AbstractEndpoint.Handler.SocketState process(SocketWrapperBase<?> socketWrapper, SocketEvent status) throws IOException {
        AbstractEndpoint.Handler.SocketState state = AbstractEndpoint.Handler.SocketState.CLOSED;
        Iterator<DispatchType> dispatches = null;
        do {
            if (dispatches != null) {
                DispatchType nextDispatch = (DispatchType)((Object)dispatches.next());
                if (this.getLog().isDebugEnabled()) {
                    this.getLog().debug((Object)("Processing dispatch type: [" + (Object)((Object)nextDispatch) + "]"));
                }
                state = this.dispatch(nextDispatch.getSocketStatus());
                if (!dispatches.hasNext()) {
                    state = this.checkForPipelinedData(state, socketWrapper);
                }
            } else if (status != SocketEvent.DISCONNECT) {
                if (this.isAsync() || this.isUpgrade() || state == AbstractEndpoint.Handler.SocketState.ASYNC_END) {
                    state = this.dispatch(status);
                    state = this.checkForPipelinedData(state, socketWrapper);
                } else if (status == SocketEvent.OPEN_WRITE) {
                    state = AbstractEndpoint.Handler.SocketState.LONG;
                } else if (status == SocketEvent.OPEN_READ) {
                    state = this.service(socketWrapper);
                } else if (status == SocketEvent.CONNECT_FAIL) {
                    this.logAccess(socketWrapper);
                } else {
                    state = AbstractEndpoint.Handler.SocketState.CLOSED;
                }
            }
            if (this.getLog().isDebugEnabled()) {
                this.getLog().debug((Object)("Socket: [" + socketWrapper + "], Status in: [" + (Object)((Object)status) + "], State out: [" + (Object)((Object)state) + "]"));
            }
            if (this.isAsync()) {
                state = this.asyncPostProcess();
                if (this.getLog().isDebugEnabled()) {
                    this.getLog().debug((Object)("Socket: [" + socketWrapper + "], State after async post processing: [" + (Object)((Object)state) + "]"));
                }
            }
            if (dispatches != null && dispatches.hasNext()) continue;
            dispatches = this.getIteratorAndClearDispatches();
        } while (state == AbstractEndpoint.Handler.SocketState.ASYNC_END || dispatches != null && state != AbstractEndpoint.Handler.SocketState.CLOSED);
        return state;
    }

    private AbstractEndpoint.Handler.SocketState checkForPipelinedData(AbstractEndpoint.Handler.SocketState inState, SocketWrapperBase<?> socketWrapper) throws IOException {
        if (inState == AbstractEndpoint.Handler.SocketState.OPEN) {
            return this.service(socketWrapper);
        }
        return inState;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addDispatch(DispatchType dispatchType) {
        Set<DispatchType> set = this.dispatches;
        synchronized (set) {
            this.dispatches.add(dispatchType);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Iterator<DispatchType> getIteratorAndClearDispatches() {
        Iterator<DispatchType> result;
        Set<DispatchType> set = this.dispatches;
        synchronized (set) {
            result = this.dispatches.iterator();
            if (result.hasNext()) {
                this.dispatches.clear();
            } else {
                result = null;
            }
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void clearDispatches() {
        Set<DispatchType> set = this.dispatches;
        synchronized (set) {
            this.dispatches.clear();
        }
    }

    protected void logAccess(SocketWrapperBase<?> socketWrapper) throws IOException {
    }

    protected abstract AbstractEndpoint.Handler.SocketState service(SocketWrapperBase<?> var1) throws IOException;

    protected abstract AbstractEndpoint.Handler.SocketState dispatch(SocketEvent var1) throws IOException;

    protected abstract AbstractEndpoint.Handler.SocketState asyncPostProcess();

    protected abstract Log getLog();
}

