/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.app.event;

import org.apache.velocity.app.event.EventHandler;
import org.apache.velocity.app.event.EventHandlerMethodExecutor;
import org.apache.velocity.context.Context;
import org.apache.velocity.util.ContextAware;

public interface MethodExceptionEventHandler
extends EventHandler {
    public Object methodException(Class var1, String var2, Exception var3) throws Exception;

    public static class MethodExceptionExecutor
    implements EventHandlerMethodExecutor {
        private Context context;
        private Class claz;
        private String method;
        private Exception e;
        private Object result;
        private boolean executed = false;

        MethodExceptionExecutor(Context context, Class claz, String method, Exception e) {
            this.context = context;
            this.claz = claz;
            this.method = method;
            this.e = e;
        }

        @Override
        public void execute(EventHandler handler) throws Exception {
            MethodExceptionEventHandler eh = (MethodExceptionEventHandler)handler;
            if (eh instanceof ContextAware) {
                ((ContextAware)((Object)eh)).setContext(this.context);
            }
            this.executed = true;
            this.result = ((MethodExceptionEventHandler)handler).methodException(this.claz, this.method, this.e);
        }

        @Override
        public Object getReturnValue() {
            return this.result;
        }

        @Override
        public boolean isDone() {
            return this.executed;
        }
    }
}

