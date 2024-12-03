/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.app.event;

import java.util.Iterator;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.app.event.EventHandler;
import org.apache.velocity.app.event.EventHandlerMethodExecutor;
import org.apache.velocity.app.event.IncludeEventHandler;
import org.apache.velocity.app.event.InvalidReferenceEventHandler;
import org.apache.velocity.app.event.MethodExceptionEventHandler;
import org.apache.velocity.app.event.NullSetEventHandler;
import org.apache.velocity.app.event.ReferenceInsertionEventHandler;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.util.ExceptionUtils;
import org.apache.velocity.util.introspection.Info;

public class EventHandlerUtil {
    public static Object referenceInsert(RuntimeServices rsvc, InternalContextAdapter context, String reference, Object value) {
        EventCartridge ev1 = rsvc.getApplicationEventCartridge();
        Iterator applicationEventHandlerIterator = ev1 == null ? null : ev1.getReferenceInsertionEventHandlers();
        EventCartridge ev2 = context.getEventCartridge();
        EventHandlerUtil.initializeEventCartridge(rsvc, ev2);
        Iterator contextEventHandlerIterator = ev2 == null ? null : ev2.getReferenceInsertionEventHandlers();
        try {
            EventHandlerMethodExecutor methodExecutor = null;
            if (applicationEventHandlerIterator != null) {
                methodExecutor = new ReferenceInsertionEventHandler.referenceInsertExecutor(context, reference, value);
                EventHandlerUtil.iterateOverEventHandlers(applicationEventHandlerIterator, methodExecutor);
            }
            if (contextEventHandlerIterator != null) {
                if (methodExecutor == null) {
                    methodExecutor = new ReferenceInsertionEventHandler.referenceInsertExecutor(context, reference, value);
                }
                EventHandlerUtil.iterateOverEventHandlers(contextEventHandlerIterator, methodExecutor);
            }
            return methodExecutor != null ? methodExecutor.getReturnValue() : value;
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw ExceptionUtils.createRuntimeException("Exception in event handler.", e);
        }
    }

    public static boolean shouldLogOnNullSet(RuntimeServices rsvc, InternalContextAdapter context, String lhs, String rhs) {
        EventCartridge ev1 = rsvc.getApplicationEventCartridge();
        Iterator applicationEventHandlerIterator = ev1 == null ? null : ev1.getNullSetEventHandlers();
        EventCartridge ev2 = context.getEventCartridge();
        EventHandlerUtil.initializeEventCartridge(rsvc, ev2);
        Iterator contextEventHandlerIterator = ev2 == null ? null : ev2.getNullSetEventHandlers();
        try {
            NullSetEventHandler.ShouldLogOnNullSetExecutor methodExecutor = new NullSetEventHandler.ShouldLogOnNullSetExecutor(context, lhs, rhs);
            EventHandlerUtil.callEventHandlers(applicationEventHandlerIterator, contextEventHandlerIterator, methodExecutor);
            return (Boolean)methodExecutor.getReturnValue();
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw ExceptionUtils.createRuntimeException("Exception in event handler.", e);
        }
    }

    public static Object methodException(RuntimeServices rsvc, InternalContextAdapter context, Class claz, String method, Exception e) throws Exception {
        EventCartridge ev1 = rsvc.getApplicationEventCartridge();
        Iterator applicationEventHandlerIterator = ev1 == null ? null : ev1.getMethodExceptionEventHandlers();
        EventCartridge ev2 = context.getEventCartridge();
        EventHandlerUtil.initializeEventCartridge(rsvc, ev2);
        Iterator contextEventHandlerIterator = ev2 == null ? null : ev2.getMethodExceptionEventHandlers();
        MethodExceptionEventHandler.MethodExceptionExecutor methodExecutor = new MethodExceptionEventHandler.MethodExceptionExecutor(context, claz, method, e);
        if (!(applicationEventHandlerIterator != null && applicationEventHandlerIterator.hasNext() || contextEventHandlerIterator != null && contextEventHandlerIterator.hasNext())) {
            throw e;
        }
        EventHandlerUtil.callEventHandlers(applicationEventHandlerIterator, contextEventHandlerIterator, methodExecutor);
        return methodExecutor.getReturnValue();
    }

    public static String includeEvent(RuntimeServices rsvc, InternalContextAdapter context, String includeResourcePath, String currentResourcePath, String directiveName) {
        EventCartridge ev1 = rsvc.getApplicationEventCartridge();
        Iterator applicationEventHandlerIterator = ev1 == null ? null : ev1.getIncludeEventHandlers();
        EventCartridge ev2 = context.getEventCartridge();
        EventHandlerUtil.initializeEventCartridge(rsvc, ev2);
        Iterator contextEventHandlerIterator = ev2 == null ? null : ev2.getIncludeEventHandlers();
        try {
            IncludeEventHandler.IncludeEventExecutor methodExecutor = new IncludeEventHandler.IncludeEventExecutor(context, includeResourcePath, currentResourcePath, directiveName);
            EventHandlerUtil.callEventHandlers(applicationEventHandlerIterator, contextEventHandlerIterator, methodExecutor);
            return (String)methodExecutor.getReturnValue();
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw ExceptionUtils.createRuntimeException("Exception in event handler.", e);
        }
    }

    public static Object invalidGetMethod(RuntimeServices rsvc, InternalContextAdapter context, String reference, Object object, String property, Info info) {
        return EventHandlerUtil.invalidReferenceHandlerCall(new InvalidReferenceEventHandler.InvalidGetMethodExecutor(context, reference, object, property, info), rsvc, context);
    }

    public static void invalidSetMethod(RuntimeServices rsvc, InternalContextAdapter context, String leftreference, String rightreference, Info info) {
        EventHandlerUtil.invalidReferenceHandlerCall(new InvalidReferenceEventHandler.InvalidSetMethodExecutor(context, leftreference, rightreference, info), rsvc, context);
    }

    public static Object invalidMethod(RuntimeServices rsvc, InternalContextAdapter context, String reference, Object object, String method, Info info) {
        return EventHandlerUtil.invalidReferenceHandlerCall(new InvalidReferenceEventHandler.InvalidMethodExecutor(context, reference, object, method, info), rsvc, context);
    }

    public static Object invalidReferenceHandlerCall(EventHandlerMethodExecutor methodExecutor, RuntimeServices rsvc, InternalContextAdapter context) {
        EventCartridge ev1 = rsvc.getApplicationEventCartridge();
        Iterator applicationEventHandlerIterator = ev1 == null ? null : ev1.getInvalidReferenceEventHandlers();
        EventCartridge ev2 = context.getEventCartridge();
        EventHandlerUtil.initializeEventCartridge(rsvc, ev2);
        Iterator contextEventHandlerIterator = ev2 == null ? null : ev2.getInvalidReferenceEventHandlers();
        try {
            EventHandlerUtil.callEventHandlers(applicationEventHandlerIterator, contextEventHandlerIterator, methodExecutor);
            return methodExecutor.getReturnValue();
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw ExceptionUtils.createRuntimeException("Exception in event handler.", e);
        }
    }

    private static void initializeEventCartridge(RuntimeServices rsvc, EventCartridge eventCartridge) {
        if (eventCartridge != null) {
            try {
                eventCartridge.initialize(rsvc);
            }
            catch (Exception e) {
                throw ExceptionUtils.createRuntimeException("Couldn't initialize event cartridge : ", e);
            }
        }
    }

    private static void callEventHandlers(Iterator applicationEventHandlerIterator, Iterator contextEventHandlerIterator, EventHandlerMethodExecutor eventExecutor) throws Exception {
        EventHandlerUtil.iterateOverEventHandlers(applicationEventHandlerIterator, eventExecutor);
        EventHandlerUtil.iterateOverEventHandlers(contextEventHandlerIterator, eventExecutor);
    }

    private static void iterateOverEventHandlers(Iterator handlerIterator, EventHandlerMethodExecutor eventExecutor) throws Exception {
        if (handlerIterator != null) {
            Iterator i = handlerIterator;
            while (i.hasNext()) {
                EventHandler eventHandler = (EventHandler)i.next();
                if (eventExecutor.isDone()) continue;
                eventExecutor.execute(eventHandler);
            }
        }
    }
}

