/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.util;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.SessionIdGenerator;
import org.apache.catalina.util.LifecycleBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

public abstract class SessionIdGeneratorBase
extends LifecycleBase
implements SessionIdGenerator {
    private final Log log = LogFactory.getLog(SessionIdGeneratorBase.class);
    private static final StringManager sm = StringManager.getManager((String)"org.apache.catalina.util");
    public static final String DEFAULT_SECURE_RANDOM_ALGORITHM;
    private final Queue<SecureRandom> randoms = new ConcurrentLinkedQueue<SecureRandom>();
    private String secureRandomClass = null;
    private String secureRandomAlgorithm = DEFAULT_SECURE_RANDOM_ALGORITHM;
    private String secureRandomProvider = null;
    private String jvmRoute = "";
    private int sessionIdLength = 16;

    public String getSecureRandomClass() {
        return this.secureRandomClass;
    }

    public void setSecureRandomClass(String secureRandomClass) {
        this.secureRandomClass = secureRandomClass;
    }

    public String getSecureRandomAlgorithm() {
        return this.secureRandomAlgorithm;
    }

    public void setSecureRandomAlgorithm(String secureRandomAlgorithm) {
        this.secureRandomAlgorithm = secureRandomAlgorithm;
    }

    public String getSecureRandomProvider() {
        return this.secureRandomProvider;
    }

    public void setSecureRandomProvider(String secureRandomProvider) {
        this.secureRandomProvider = secureRandomProvider;
    }

    @Override
    public String getJvmRoute() {
        return this.jvmRoute;
    }

    @Override
    public void setJvmRoute(String jvmRoute) {
        this.jvmRoute = jvmRoute;
    }

    @Override
    public int getSessionIdLength() {
        return this.sessionIdLength;
    }

    @Override
    public void setSessionIdLength(int sessionIdLength) {
        this.sessionIdLength = sessionIdLength;
    }

    @Override
    public String generateSessionId() {
        return this.generateSessionId(this.jvmRoute);
    }

    protected void getRandomBytes(byte[] bytes) {
        SecureRandom random = this.randoms.poll();
        if (random == null) {
            random = this.createSecureRandom();
        }
        random.nextBytes(bytes);
        this.randoms.add(random);
    }

    private SecureRandom createSecureRandom() {
        Random result = null;
        long t1 = System.currentTimeMillis();
        if (this.secureRandomClass != null) {
            try {
                Class<?> clazz = Class.forName(this.secureRandomClass);
                result = (SecureRandom)clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
            }
            catch (Exception e) {
                this.log.error((Object)sm.getString("sessionIdGeneratorBase.random", new Object[]{this.secureRandomClass}), (Throwable)e);
            }
        }
        boolean error = false;
        if (result == null) {
            try {
                if (this.secureRandomProvider != null && this.secureRandomProvider.length() > 0) {
                    result = SecureRandom.getInstance(this.secureRandomAlgorithm, this.secureRandomProvider);
                } else if (this.secureRandomAlgorithm != null && this.secureRandomAlgorithm.length() > 0) {
                    result = SecureRandom.getInstance(this.secureRandomAlgorithm);
                }
            }
            catch (NoSuchAlgorithmException e) {
                error = true;
                this.log.error((Object)sm.getString("sessionIdGeneratorBase.randomAlgorithm", new Object[]{this.secureRandomAlgorithm}), (Throwable)e);
            }
            catch (NoSuchProviderException e) {
                error = true;
                this.log.error((Object)sm.getString("sessionIdGeneratorBase.randomProvider", new Object[]{this.secureRandomProvider}), (Throwable)e);
            }
        }
        if (result == null && error && !DEFAULT_SECURE_RANDOM_ALGORITHM.equals(this.secureRandomAlgorithm)) {
            try {
                result = SecureRandom.getInstance(DEFAULT_SECURE_RANDOM_ALGORITHM);
            }
            catch (NoSuchAlgorithmException e) {
                this.log.error((Object)sm.getString("sessionIdGeneratorBase.randomAlgorithm", new Object[]{this.secureRandomAlgorithm}), (Throwable)e);
            }
        }
        if (result == null) {
            result = new SecureRandom();
        }
        result.nextInt();
        long t2 = System.currentTimeMillis();
        if (t2 - t1 > 100L) {
            this.log.warn((Object)sm.getString("sessionIdGeneratorBase.createRandom", new Object[]{((SecureRandom)result).getAlgorithm(), t2 - t1}));
        }
        return result;
    }

    @Override
    protected void initInternal() throws LifecycleException {
    }

    @Override
    protected void startInternal() throws LifecycleException {
        this.generateSessionId();
        this.setState(LifecycleState.STARTING);
    }

    @Override
    protected void stopInternal() throws LifecycleException {
        this.setState(LifecycleState.STOPPING);
        this.randoms.clear();
    }

    @Override
    protected void destroyInternal() throws LifecycleException {
    }

    static {
        Set<String> algorithmNames = Security.getAlgorithms("SecureRandom");
        if (algorithmNames.contains("SHA1PRNG")) {
            DEFAULT_SECURE_RANDOM_ALGORITHM = "SHA1PRNG";
        } else {
            DEFAULT_SECURE_RANDOM_ALGORITHM = "";
            Log log = LogFactory.getLog(SessionIdGeneratorBase.class);
            log.warn((Object)sm.getString("sessionIdGeneratorBase.noSHA1PRNG"));
        }
    }
}

