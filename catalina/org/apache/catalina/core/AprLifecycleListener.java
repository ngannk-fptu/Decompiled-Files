/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.jni.Library
 *  org.apache.tomcat.jni.LibraryNotFoundError
 *  org.apache.tomcat.jni.SSL
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Server;
import org.apache.catalina.core.AprStatus;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.jni.Library;
import org.apache.tomcat.jni.LibraryNotFoundError;
import org.apache.tomcat.jni.SSL;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.res.StringManager;

public class AprLifecycleListener
implements LifecycleListener {
    private static final Log log = LogFactory.getLog(AprLifecycleListener.class);
    private static final List<String> initInfoLogMessages = new ArrayList<String>(3);
    protected static final StringManager sm = StringManager.getManager(AprLifecycleListener.class);
    protected static final int TCN_REQUIRED_MAJOR = 1;
    protected static final int TCN_REQUIRED_MINOR = 2;
    protected static final int TCN_REQUIRED_PATCH = 14;
    protected static final int TCN_RECOMMENDED_MAJOR = 1;
    protected static final int TCN_RECOMMENDED_MINOR = 2;
    protected static final int TCN_RECOMMENDED_PV = 38;
    private static int tcnMajor = 0;
    private static int tcnMinor = 0;
    private static int tcnPatch = 0;
    private static int tcnVersion = 0;
    protected static String SSLEngine = "on";
    protected static String FIPSMode = "off";
    protected static String SSLRandomSeed = "builtin";
    protected static boolean sslInitialized = false;
    protected static boolean fipsModeActive = false;
    private static final int FIPS_ON = 1;
    private static final int FIPS_OFF = 0;
    protected static final Object lock = new Object();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean isAprAvailable() {
        if (AprStatus.isInstanceCreated()) {
            Object object = lock;
            synchronized (object) {
                AprLifecycleListener.init();
            }
        }
        return AprStatus.isAprAvailable();
    }

    public AprLifecycleListener() {
        AprStatus.setInstanceCreated(true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void lifecycleEvent(LifecycleEvent event) {
        if ("before_init".equals(event.getType())) {
            Object object = lock;
            synchronized (object) {
                if (!(event.getLifecycle() instanceof Server)) {
                    log.warn((Object)sm.getString("listener.notServer", new Object[]{event.getLifecycle().getClass().getSimpleName()}));
                }
                AprLifecycleListener.init();
                for (String msg : initInfoLogMessages) {
                    log.info((Object)msg);
                }
                initInfoLogMessages.clear();
                if (AprStatus.isAprAvailable()) {
                    try {
                        AprLifecycleListener.initializeSSL();
                    }
                    catch (Throwable t) {
                        t = ExceptionUtils.unwrapInvocationTargetException((Throwable)t);
                        ExceptionUtils.handleThrowable((Throwable)t);
                        log.error((Object)sm.getString("aprListener.sslInit"), t);
                    }
                }
                if (null != FIPSMode && !"off".equalsIgnoreCase(FIPSMode) && !this.isFIPSModeActive()) {
                    String errorMessage = sm.getString("aprListener.initializeFIPSFailed");
                    Error e = new Error(errorMessage);
                    log.fatal((Object)errorMessage, (Throwable)e);
                    throw e;
                }
            }
        }
        if ("after_destroy".equals(event.getType())) {
            Object object = lock;
            synchronized (object) {
                if (!AprStatus.isAprAvailable()) {
                    return;
                }
                try {
                    AprLifecycleListener.terminateAPR();
                }
                catch (Throwable t) {
                    t = ExceptionUtils.unwrapInvocationTargetException((Throwable)t);
                    ExceptionUtils.handleThrowable((Throwable)t);
                    log.info((Object)sm.getString("aprListener.aprDestroy"));
                }
            }
        }
    }

    private static void terminateAPR() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String methodName = "terminate";
        Method method = Class.forName("org.apache.tomcat.jni.Library").getMethod(methodName, null);
        method.invoke(null, (Object[])null);
        AprStatus.setAprAvailable(false);
        AprStatus.setAprInitialized(false);
        sslInitialized = false;
        fipsModeActive = false;
    }

    private static void init() {
        int rqver = 1214;
        int rcver = 1238;
        if (AprStatus.isAprInitialized()) {
            return;
        }
        AprStatus.setAprInitialized(true);
        try {
            Library.initialize(null);
            tcnMajor = Library.TCN_MAJOR_VERSION;
            tcnMinor = Library.TCN_MINOR_VERSION;
            tcnPatch = Library.TCN_PATCH_VERSION;
            tcnVersion = tcnMajor * 1000 + tcnMinor * 100 + tcnPatch;
        }
        catch (LibraryNotFoundError lnfe) {
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("aprListener.aprInitDebug", new Object[]{lnfe.getLibraryNames(), System.getProperty("java.library.path"), lnfe.getMessage()}), (Throwable)lnfe);
            }
            initInfoLogMessages.add(sm.getString("aprListener.aprInit", new Object[]{System.getProperty("java.library.path")}));
            return;
        }
        catch (Throwable t) {
            t = ExceptionUtils.unwrapInvocationTargetException((Throwable)t);
            ExceptionUtils.handleThrowable((Throwable)t);
            log.warn((Object)sm.getString("aprListener.aprInitError", new Object[]{t.getMessage()}), t);
            return;
        }
        if (tcnMajor > 1 && "off".equalsIgnoreCase(SSLEngine)) {
            log.error((Object)sm.getString("aprListener.sslRequired", new Object[]{SSLEngine, Library.versionString()}));
            try {
                AprLifecycleListener.terminateAPR();
            }
            catch (Throwable t) {
                t = ExceptionUtils.unwrapInvocationTargetException((Throwable)t);
                ExceptionUtils.handleThrowable((Throwable)t);
            }
            return;
        }
        if (tcnVersion < rqver) {
            log.error((Object)sm.getString("aprListener.tcnInvalid", new Object[]{Library.versionString(), "1.2.14"}));
            try {
                AprLifecycleListener.terminateAPR();
            }
            catch (Throwable t) {
                t = ExceptionUtils.unwrapInvocationTargetException((Throwable)t);
                ExceptionUtils.handleThrowable((Throwable)t);
            }
            return;
        }
        if (tcnVersion < rcver) {
            initInfoLogMessages.add(sm.getString("aprListener.tcnVersion", new Object[]{Library.versionString(), "1.2.38"}));
        }
        initInfoLogMessages.add(sm.getString("aprListener.tcnValid", new Object[]{Library.versionString(), Library.aprVersionString()}));
        initInfoLogMessages.add(sm.getString("aprListener.flags", new Object[]{Library.APR_HAVE_IPV6, Library.APR_HAS_SENDFILE, Library.APR_HAS_SO_ACCEPTFILTER, Library.APR_HAS_RANDOM, Library.APR_HAVE_UNIX}));
        initInfoLogMessages.add(sm.getString("aprListener.config", new Object[]{AprStatus.getUseAprConnector(), AprStatus.getUseOpenSSL()}));
        AprStatus.setAprAvailable(true);
    }

    /*
     * Enabled aggressive block sorting
     */
    private static void initializeSSL() throws Exception {
        boolean usingProviders;
        if ("off".equalsIgnoreCase(SSLEngine)) {
            return;
        }
        if (sslInitialized) {
            return;
        }
        sslInitialized = true;
        String methodName = "randSet";
        Class[] paramTypes = new Class[]{String.class};
        Object[] paramValues = new Object[]{SSLRandomSeed};
        Class<?> clazz = Class.forName("org.apache.tomcat.jni.SSL");
        Method method = clazz.getMethod(methodName, paramTypes);
        method.invoke(null, paramValues);
        methodName = "initialize";
        paramValues[0] = "on".equalsIgnoreCase(SSLEngine) ? null : SSLEngine;
        method = clazz.getMethod(methodName, paramTypes);
        method.invoke(null, paramValues);
        boolean bl = usingProviders = tcnMajor > 1 || tcnVersion > 1233 && ((long)SSL.version() & 0xF0000000L) > 0x20000000L;
        if (usingProviders || null != FIPSMode && !"off".equalsIgnoreCase(FIPSMode)) {
            boolean enterFipsMode;
            int fipsModeState;
            block25: {
                fipsModeActive = false;
                fipsModeState = SSL.fipsModeGet();
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("aprListener.currentFIPSMode", new Object[]{fipsModeState}));
                }
                if (null == FIPSMode || "off".equalsIgnoreCase(FIPSMode)) {
                    if (fipsModeState == 1) {
                        fipsModeActive = true;
                    }
                    enterFipsMode = false;
                } else if ("on".equalsIgnoreCase(FIPSMode)) {
                    if (fipsModeState == 1) {
                        if (!usingProviders) {
                            log.info((Object)sm.getString("aprListener.skipFIPSInitialization"));
                        }
                        fipsModeActive = true;
                        enterFipsMode = false;
                    } else {
                        if (usingProviders) {
                            throw new IllegalStateException(sm.getString("aprListener.FIPSProviderNotDefault", new Object[]{FIPSMode}));
                        }
                        enterFipsMode = true;
                    }
                } else {
                    if ("require".equalsIgnoreCase(FIPSMode)) {
                        if (fipsModeState == 1) {
                            fipsModeActive = true;
                            enterFipsMode = false;
                            break block25;
                        } else {
                            if (usingProviders) {
                                throw new IllegalStateException(sm.getString("aprListener.FIPSProviderNotDefault", new Object[]{FIPSMode}));
                            }
                            throw new IllegalStateException(sm.getString("aprListener.requireNotInFIPSMode"));
                        }
                    }
                    if (!"enter".equalsIgnoreCase(FIPSMode)) {
                        throw new IllegalArgumentException(sm.getString("aprListener.wrongFIPSMode", new Object[]{FIPSMode}));
                    }
                    if (fipsModeState == 0) {
                        if (usingProviders) {
                            throw new IllegalStateException(sm.getString("aprListener.FIPSProviderNotDefault", new Object[]{FIPSMode}));
                        }
                        enterFipsMode = true;
                    } else {
                        if (!usingProviders) {
                            throw new IllegalStateException(sm.getString("aprListener.enterAlreadyInFIPSMode", new Object[]{fipsModeState}));
                        }
                        fipsModeActive = true;
                        enterFipsMode = false;
                    }
                }
            }
            if (enterFipsMode) {
                log.info((Object)sm.getString("aprListener.initializingFIPS"));
                fipsModeState = SSL.fipsModeSet((int)1);
                if (fipsModeState != 1) {
                    String message = sm.getString("aprListener.initializeFIPSFailed");
                    log.error((Object)message);
                    throw new IllegalStateException(message);
                }
                fipsModeActive = true;
                log.info((Object)sm.getString("aprListener.initializeFIPSSuccess"));
            }
            if (usingProviders && fipsModeActive) {
                log.info((Object)sm.getString("aprListener.usingFIPSProvider"));
            }
        }
        log.info((Object)sm.getString("aprListener.initializedOpenSSL", new Object[]{SSL.versionString()}));
    }

    public String getSSLEngine() {
        return SSLEngine;
    }

    public void setSSLEngine(String SSLEngine) {
        if (!SSLEngine.equals(AprLifecycleListener.SSLEngine)) {
            if (sslInitialized) {
                throw new IllegalStateException(sm.getString("aprListener.tooLateForSSLEngine"));
            }
            AprLifecycleListener.SSLEngine = SSLEngine;
        }
    }

    public String getSSLRandomSeed() {
        return SSLRandomSeed;
    }

    public void setSSLRandomSeed(String SSLRandomSeed) {
        if (!SSLRandomSeed.equals(AprLifecycleListener.SSLRandomSeed)) {
            if (sslInitialized) {
                throw new IllegalStateException(sm.getString("aprListener.tooLateForSSLRandomSeed"));
            }
            AprLifecycleListener.SSLRandomSeed = SSLRandomSeed;
        }
    }

    public String getFIPSMode() {
        return FIPSMode;
    }

    public void setFIPSMode(String FIPSMode) {
        if (!FIPSMode.equals(AprLifecycleListener.FIPSMode)) {
            if (sslInitialized) {
                throw new IllegalStateException(sm.getString("aprListener.tooLateForFIPSMode"));
            }
            AprLifecycleListener.FIPSMode = FIPSMode;
        }
    }

    public boolean isFIPSModeActive() {
        return fipsModeActive;
    }

    public void setUseAprConnector(boolean useAprConnector) {
        if (useAprConnector != AprStatus.getUseAprConnector()) {
            AprStatus.setUseAprConnector(useAprConnector);
        }
    }

    public static boolean getUseAprConnector() {
        return AprStatus.getUseAprConnector();
    }

    public void setUseOpenSSL(boolean useOpenSSL) {
        if (useOpenSSL != AprStatus.getUseOpenSSL()) {
            AprStatus.setUseOpenSSL(useOpenSSL);
        }
    }

    public static boolean getUseOpenSSL() {
        return AprStatus.getUseOpenSSL();
    }

    public static boolean isInstanceCreated() {
        return AprStatus.isInstanceCreated();
    }
}

