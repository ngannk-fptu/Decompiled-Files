/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.app.event;

import org.apache.velocity.app.event.EventHandler;
import org.apache.velocity.app.event.EventHandlerMethodExecutor;
import org.apache.velocity.context.Context;
import org.apache.velocity.util.ContextAware;

public interface IncludeEventHandler
extends EventHandler {
    public String includeEvent(String var1, String var2, String var3);

    public static class IncludeEventExecutor
    implements EventHandlerMethodExecutor {
        private Context context;
        private String includeResourcePath;
        private String currentResourcePath;
        private String directiveName;
        private boolean executed = false;

        IncludeEventExecutor(Context context, String includeResourcePath, String currentResourcePath, String directiveName) {
            this.context = context;
            this.includeResourcePath = includeResourcePath;
            this.currentResourcePath = currentResourcePath;
            this.directiveName = directiveName;
        }

        public void execute(EventHandler handler) {
            IncludeEventHandler eh = (IncludeEventHandler)handler;
            if (eh instanceof ContextAware) {
                ((ContextAware)((Object)eh)).setContext(this.context);
            }
            this.executed = true;
            this.includeResourcePath = ((IncludeEventHandler)handler).includeEvent(this.includeResourcePath, this.currentResourcePath, this.directiveName);
        }

        public Object getReturnValue() {
            return this.includeResourcePath;
        }

        public boolean isDone() {
            return this.executed && this.includeResourcePath == null;
        }
    }
}

