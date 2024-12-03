/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.app.event;

import org.apache.velocity.app.event.EventHandler;
import org.apache.velocity.app.event.EventHandlerMethodExecutor;
import org.apache.velocity.context.Context;
import org.apache.velocity.util.introspection.Info;

public interface InvalidReferenceEventHandler
extends EventHandler {
    public Object invalidGetMethod(Context var1, String var2, Object var3, String var4, Info var5);

    public boolean invalidSetMethod(Context var1, String var2, String var3, Info var4);

    public Object invalidMethod(Context var1, String var2, Object var3, String var4, Info var5);

    public static class InvalidMethodExecutor
    implements EventHandlerMethodExecutor {
        private Context context;
        private String reference;
        private Object object;
        private String method;
        private Info info;
        private Object result;
        private boolean executed = false;

        InvalidMethodExecutor(Context context, String reference, Object object, String method, Info info) {
            this.context = context;
            this.reference = reference;
            this.object = object;
            this.method = method;
            this.info = info;
        }

        @Override
        public void execute(EventHandler handler) {
            this.executed = true;
            this.result = ((InvalidReferenceEventHandler)handler).invalidMethod(this.context, this.reference, this.object, this.method, this.info);
        }

        @Override
        public Object getReturnValue() {
            return this.result;
        }

        @Override
        public boolean isDone() {
            return this.executed && this.result != null;
        }
    }

    public static class InvalidSetMethodExecutor
    implements EventHandlerMethodExecutor {
        private Context context;
        private String leftreference;
        private String rightreference;
        private Info info;
        private boolean result;

        InvalidSetMethodExecutor(Context context, String leftreference, String rightreference, Info info) {
            this.context = context;
            this.leftreference = leftreference;
            this.rightreference = rightreference;
            this.info = info;
        }

        @Override
        public void execute(EventHandler handler) {
            this.result = ((InvalidReferenceEventHandler)handler).invalidSetMethod(this.context, this.leftreference, this.rightreference, this.info);
        }

        @Override
        public Object getReturnValue() {
            return null;
        }

        @Override
        public boolean isDone() {
            return this.result;
        }
    }

    public static class InvalidGetMethodExecutor
    implements EventHandlerMethodExecutor {
        private Context context;
        private String reference;
        private Object object;
        private String property;
        private Info info;
        private Object result;

        InvalidGetMethodExecutor(Context context, String reference, Object object, String property, Info info) {
            this.context = context;
            this.reference = reference;
            this.object = object;
            this.property = property;
            this.info = info;
        }

        @Override
        public void execute(EventHandler handler) {
            this.result = ((InvalidReferenceEventHandler)handler).invalidGetMethod(this.context, this.reference, this.object, this.property, this.info);
        }

        @Override
        public Object getReturnValue() {
            return this.result;
        }

        @Override
        public boolean isDone() {
            return this.result != null;
        }
    }
}

