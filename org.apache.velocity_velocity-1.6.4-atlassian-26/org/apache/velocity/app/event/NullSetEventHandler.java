/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.app.event;

import org.apache.velocity.app.event.EventHandler;
import org.apache.velocity.app.event.EventHandlerMethodExecutor;
import org.apache.velocity.context.Context;
import org.apache.velocity.util.ContextAware;

public interface NullSetEventHandler
extends EventHandler {
    public boolean shouldLogOnNullSet(String var1, String var2);

    public static class ShouldLogOnNullSetExecutor
    implements EventHandlerMethodExecutor {
        private Context context;
        private String lhs;
        private String rhs;
        private boolean result = true;
        private boolean executed = false;

        ShouldLogOnNullSetExecutor(Context context, String lhs, String rhs) {
            this.context = context;
            this.lhs = lhs;
            this.rhs = rhs;
        }

        @Override
        public void execute(EventHandler handler) {
            NullSetEventHandler eh = (NullSetEventHandler)handler;
            if (eh instanceof ContextAware) {
                ((ContextAware)((Object)eh)).setContext(this.context);
            }
            this.executed = true;
            this.result = ((NullSetEventHandler)handler).shouldLogOnNullSet(this.lhs, this.rhs);
        }

        @Override
        public Object getReturnValue() {
            return this.result ? Boolean.TRUE : Boolean.FALSE;
        }

        @Override
        public boolean isDone() {
            return this.executed && !this.result;
        }
    }
}

