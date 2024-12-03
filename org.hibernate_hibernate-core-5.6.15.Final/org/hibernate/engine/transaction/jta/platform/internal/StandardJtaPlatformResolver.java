/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.engine.transaction.jta.platform.internal;

import java.util.Map;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
import org.hibernate.engine.transaction.jta.platform.internal.AtomikosJtaPlatform;
import org.hibernate.engine.transaction.jta.platform.internal.BitronixJtaPlatform;
import org.hibernate.engine.transaction.jta.platform.internal.JBossStandAloneJtaPlatform;
import org.hibernate.engine.transaction.jta.platform.internal.JOTMJtaPlatform;
import org.hibernate.engine.transaction.jta.platform.internal.JOnASJtaPlatform;
import org.hibernate.engine.transaction.jta.platform.internal.NoJtaPlatform;
import org.hibernate.engine.transaction.jta.platform.internal.WebSphereJtaPlatform;
import org.hibernate.engine.transaction.jta.platform.internal.WebSphereLibertyJtaPlatform;
import org.hibernate.engine.transaction.jta.platform.internal.WildFlyStandAloneJtaPlatform;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatformProvider;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatformResolver;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.jboss.logging.Logger;

public class StandardJtaPlatformResolver
implements JtaPlatformResolver {
    public static final StandardJtaPlatformResolver INSTANCE = new StandardJtaPlatformResolver();
    private static final Logger log = Logger.getLogger(StandardJtaPlatformResolver.class);

    @Override
    public JtaPlatform resolveJtaPlatform(Map configurationValues, ServiceRegistryImplementor registry) {
        ClassLoaderService classLoaderService = registry.getService(ClassLoaderService.class);
        for (JtaPlatformProvider provider : classLoaderService.loadJavaServices(JtaPlatformProvider.class)) {
            JtaPlatform providedPlatform = provider.getProvidedJtaPlatform();
            log.tracef("Located JtaPlatformProvider [%s] provided JtaPlaform : %s", (Object)provider, (Object)providedPlatform);
            if (providedPlatform == null) continue;
            return providedPlatform;
        }
        try {
            classLoaderService.classForName("org.wildfly.transaction.client.ContextTransactionManager");
            classLoaderService.classForName("org.wildfly.transaction.client.LocalUserTransaction");
            return new WildFlyStandAloneJtaPlatform();
        }
        catch (ClassLoadingException classLoadingException) {
            try {
                classLoaderService.classForName("com.arjuna.ats.jta.TransactionManager");
                classLoaderService.classForName("com.arjuna.ats.jta.UserTransaction");
                return new JBossStandAloneJtaPlatform();
            }
            catch (ClassLoadingException classLoadingException2) {
                try {
                    classLoaderService.classForName("com.atomikos.icatch.jta.UserTransactionManager");
                    return new AtomikosJtaPlatform();
                }
                catch (ClassLoadingException classLoadingException3) {
                    try {
                        classLoaderService.classForName("bitronix.tm.TransactionManagerServices");
                        return new BitronixJtaPlatform();
                    }
                    catch (ClassLoadingException classLoadingException4) {
                        try {
                            classLoaderService.classForName("org.objectweb.jonas_tm.Current");
                            return new JOnASJtaPlatform();
                        }
                        catch (ClassLoadingException classLoadingException5) {
                            try {
                                classLoaderService.classForName("org.objectweb.jotm.Current");
                                return new JOTMJtaPlatform();
                            }
                            catch (ClassLoadingException classLoadingException6) {
                                try {
                                    classLoaderService.classForName("com.ibm.tx.jta.TransactionManagerFactory");
                                    return new WebSphereLibertyJtaPlatform();
                                }
                                catch (ClassLoadingException classLoadingException7) {
                                    for (WebSphereJtaPlatform.WebSphereEnvironment webSphereEnvironment : WebSphereJtaPlatform.WebSphereEnvironment.values()) {
                                        try {
                                            Class accessClass = classLoaderService.classForName(webSphereEnvironment.getTmAccessClassName());
                                            return new WebSphereJtaPlatform(accessClass, webSphereEnvironment);
                                        }
                                        catch (ClassLoadingException classLoadingException8) {
                                        }
                                    }
                                    log.debugf("Could not resolve JtaPlatform, using default [%s]", (Object)NoJtaPlatform.class.getName());
                                    return NoJtaPlatform.INSTANCE;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

