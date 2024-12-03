/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.vmplugin.v7;

import groovy.lang.GroovySystem;
import groovy.lang.MetaClassRegistryChangeEvent;
import groovy.lang.MetaClassRegistryChangeEventListener;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.SwitchPoint;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.vmplugin.v7.Selector;

public class IndyInterface {
    public static final int SAFE_NAVIGATION = 1;
    public static final int THIS_CALL = 2;
    public static final int GROOVY_OBJECT = 4;
    public static final int IMPLICIT_THIS = 8;
    public static final int SPREAD_CALL = 16;
    public static final int UNCACHED_CALL = 32;
    protected static final Logger LOG;
    protected static final boolean LOG_ENABLED;
    public static final MethodHandles.Lookup LOOKUP;
    private static final MethodHandle SELECT_METHOD;
    protected static SwitchPoint switchPoint;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected static void invalidateSwitchPoints() {
        if (LOG_ENABLED) {
            LOG.info("invalidating switch point");
        }
        Class<IndyInterface> clazz = IndyInterface.class;
        synchronized (IndyInterface.class) {
            SwitchPoint old = switchPoint;
            switchPoint = new SwitchPoint();
            SwitchPoint.invalidateAll(new SwitchPoint[]{old});
            // ** MonitorExit[var0] (shouldn't be in output)
            return;
        }
    }

    public static CallSite bootstrap(MethodHandles.Lookup caller, String callType, MethodType type, String name, int flags) {
        int callID;
        boolean spreadCall;
        boolean safe = (flags & 1) != 0;
        boolean thisCall = (flags & 2) != 0;
        boolean bl = spreadCall = (flags & 0x10) != 0;
        if (callType.equals(CALL_TYPES.METHOD.getCallSiteName())) {
            callID = CALL_TYPES.METHOD.ordinal();
        } else if (callType.equals(CALL_TYPES.INIT.getCallSiteName())) {
            callID = CALL_TYPES.INIT.ordinal();
        } else if (callType.equals(CALL_TYPES.GET.getCallSiteName())) {
            callID = CALL_TYPES.GET.ordinal();
        } else if (callType.equals(CALL_TYPES.SET.getCallSiteName())) {
            callID = CALL_TYPES.SET.ordinal();
        } else if (callType.equals(CALL_TYPES.CAST.getCallSiteName())) {
            callID = CALL_TYPES.CAST.ordinal();
        } else {
            throw new GroovyBugError("Unknown call type: " + callType);
        }
        return IndyInterface.realBootstrap(caller, name, callID, type, safe, thisCall, spreadCall);
    }

    @Deprecated
    public static CallSite bootstrapCurrent(MethodHandles.Lookup caller, String name, MethodType type) {
        return IndyInterface.realBootstrap(caller, name, CALL_TYPES.METHOD.ordinal(), type, false, true, false);
    }

    @Deprecated
    public static CallSite bootstrapCurrentSafe(MethodHandles.Lookup caller, String name, MethodType type) {
        return IndyInterface.realBootstrap(caller, name, CALL_TYPES.METHOD.ordinal(), type, true, true, false);
    }

    @Deprecated
    public static CallSite bootstrap(MethodHandles.Lookup caller, String name, MethodType type) {
        return IndyInterface.realBootstrap(caller, name, CALL_TYPES.METHOD.ordinal(), type, false, false, false);
    }

    @Deprecated
    public static CallSite bootstrapSafe(MethodHandles.Lookup caller, String name, MethodType type) {
        return IndyInterface.realBootstrap(caller, name, CALL_TYPES.METHOD.ordinal(), type, true, false, false);
    }

    private static CallSite realBootstrap(MethodHandles.Lookup caller, String name, int callID, MethodType type, boolean safe, boolean thisCall, boolean spreadCall) {
        MutableCallSite mc = new MutableCallSite(type);
        MethodHandle mh = IndyInterface.makeFallBack(mc, caller.lookupClass(), name, callID, type, safe, thisCall, spreadCall);
        mc.setTarget(mh);
        return mc;
    }

    protected static MethodHandle makeFallBack(MutableCallSite mc, Class<?> sender, String name, int callID, MethodType type, boolean safeNavigation, boolean thisCall, boolean spreadCall) {
        MethodHandle mh = MethodHandles.insertArguments(SELECT_METHOD, 0, mc, sender, name, callID, safeNavigation, thisCall, spreadCall, 1);
        mh = mh.asCollector(Object[].class, type.parameterCount()).asType(type);
        return mh;
    }

    public static Object selectMethod(MutableCallSite callSite, Class sender, String methodName, int callID, Boolean safeNavigation, Boolean thisCall, Boolean spreadCall, Object dummyReceiver, Object[] arguments) throws Throwable {
        Selector selector = Selector.getSelector(callSite, sender, methodName, callID, safeNavigation, thisCall, spreadCall, arguments);
        selector.setCallSiteTarget();
        MethodHandle call = selector.handle.asSpreader(Object[].class, arguments.length);
        call = call.asType(MethodType.methodType(Object.class, Object[].class));
        return call.invokeExact(arguments);
    }

    static {
        boolean enableLogger = false;
        LOG = Logger.getLogger(IndyInterface.class.getName());
        try {
            if (System.getProperty("groovy.indy.logging") != null) {
                LOG.setLevel(Level.ALL);
                enableLogger = true;
            }
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        LOG_ENABLED = enableLogger;
        LOOKUP = MethodHandles.lookup();
        MethodType mt = MethodType.methodType(Object.class, MutableCallSite.class, Class.class, String.class, Integer.TYPE, Boolean.class, Boolean.class, Boolean.class, Object.class, Object[].class);
        try {
            SELECT_METHOD = LOOKUP.findStatic(IndyInterface.class, "selectMethod", mt);
        }
        catch (Exception e) {
            throw new GroovyBugError(e);
        }
        switchPoint = new SwitchPoint();
        GroovySystem.getMetaClassRegistry().addMetaClassRegistryChangeEventListener(new MetaClassRegistryChangeEventListener(){

            @Override
            public void updateConstantMetaClass(MetaClassRegistryChangeEvent cmcu) {
                IndyInterface.invalidateSwitchPoints();
            }
        });
    }

    public static enum CALL_TYPES {
        METHOD("invoke"),
        INIT("init"),
        GET("getProperty"),
        SET("setProperty"),
        CAST("cast");

        private final String name;

        private CALL_TYPES(String callSiteName) {
            this.name = callSiteName;
        }

        public String getCallSiteName() {
            return this.name;
        }
    }
}

