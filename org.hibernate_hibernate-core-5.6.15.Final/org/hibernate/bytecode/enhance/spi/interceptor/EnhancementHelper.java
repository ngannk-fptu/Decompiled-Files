/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.bytecode.enhance.spi.interceptor;

import java.util.Locale;
import java.util.function.BiFunction;
import org.hibernate.FlushMode;
import org.hibernate.LazyInitializationException;
import org.hibernate.bytecode.enhance.spi.interceptor.BytecodeInterceptorLogging;
import org.hibernate.bytecode.enhance.spi.interceptor.BytecodeLazyAttributeInterceptor;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.SessionFactoryRegistry;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.ToOne;
import org.hibernate.mapping.Value;

public class EnhancementHelper {
    public static boolean includeInBaseFetchGroup(Property bootMapping, boolean isEnhanced, InheritanceChecker inheritanceChecker, boolean collectionsInDefaultFetchGroupEnabled) {
        Value value = bootMapping.getValue();
        if (!isEnhanced) {
            if (value instanceof ToOne && ((ToOne)value).isUnwrapProxy()) {
                BytecodeInterceptorLogging.MESSAGE_LOGGER.debugf("To-one property `%s#%s` was mapped with LAZY + NO_PROXY but the class was not enhanced", bootMapping.getPersistentClass().getEntityName(), bootMapping.getName());
            }
            return true;
        }
        if (value instanceof ToOne) {
            boolean unwrapExplicitlyRequested;
            ToOne toOne = (ToOne)value;
            if (!toOne.isLazy()) {
                return true;
            }
            if (bootMapping.getLazyGroup() != null) {
                BytecodeInterceptorLogging.MESSAGE_LOGGER.lazyGroupIgnoredForToOne(bootMapping.getPersistentClass().getEntityName(), bootMapping.getName(), bootMapping.getLazyGroup());
            }
            if (!toOne.isReferenceToPrimaryKey()) {
                return false;
            }
            if (toOne.getColumnSpan() == 0) {
                return false;
            }
            boolean bl = unwrapExplicitlyRequested = toOne.isUnwrapProxy() && !toOne.isUnwrapProxyImplicit();
            if (inheritanceChecker.hasSubclasses(toOne.getReferencedEntityName())) {
                if (unwrapExplicitlyRequested) {
                    BytecodeInterceptorLogging.LOGGER.debugf("`%s#%s` was mapped with LAZY and explicit NO_PROXY but the associated entity (`%s`) has subclasses", (Object)bootMapping.getPersistentClass().getEntityName(), (Object)bootMapping.getName(), (Object)toOne.getReferencedEntityName());
                }
                return true;
            }
            if (toOne instanceof ManyToOne && ((ManyToOne)toOne).isIgnoreNotFound() && unwrapExplicitlyRequested) {
                BytecodeInterceptorLogging.LOGGER.debugf("%s#%s specified NotFoundAction.IGNORE & LazyToOneOption.NO_PROXY; skipping FK selection to more efficiently handle NotFoundAction.IGNORE", (Object)bootMapping.getPersistentClass().getEntityName(), (Object)bootMapping.getName());
                return false;
            }
            if (unwrapExplicitlyRequested) {
                return true;
            }
            return true;
        }
        return collectionsInDefaultFetchGroupEnabled && value instanceof Collection || !bootMapping.isLazy();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <T> T performWork(BytecodeLazyAttributeInterceptor interceptor, BiFunction<SharedSessionContractImplementor, Boolean, T> work, String entityName, String attributeName) {
        SharedSessionContractImplementor session = interceptor.getLinkedSession();
        boolean isTempSession = false;
        boolean isJta = false;
        if (session == null) {
            if (interceptor.allowLoadOutsideTransaction()) {
                session = EnhancementHelper.openTemporarySessionForLoading(interceptor, entityName, attributeName);
                isTempSession = true;
            } else {
                EnhancementHelper.throwLazyInitializationException(Cause.NO_SESSION, entityName, attributeName);
            }
        } else if (!session.isOpen()) {
            if (interceptor.allowLoadOutsideTransaction()) {
                session = EnhancementHelper.openTemporarySessionForLoading(interceptor, entityName, attributeName);
                isTempSession = true;
            } else {
                EnhancementHelper.throwLazyInitializationException(Cause.CLOSED_SESSION, entityName, attributeName);
            }
        } else if (!session.isConnected()) {
            if (interceptor.allowLoadOutsideTransaction()) {
                session = EnhancementHelper.openTemporarySessionForLoading(interceptor, entityName, attributeName);
                isTempSession = true;
            } else {
                EnhancementHelper.throwLazyInitializationException(Cause.DISCONNECTED_SESSION, entityName, attributeName);
            }
        }
        if (isTempSession) {
            BytecodeInterceptorLogging.LOGGER.debug((Object)"Enhancement interception Helper#performWork started temporary Session");
            isJta = session.getTransactionCoordinator().getTransactionCoordinatorBuilder().isJta();
            if (!isJta) {
                BytecodeInterceptorLogging.LOGGER.debug((Object)"Enhancement interception Helper#performWork starting transaction on temporary Session");
                session.beginTransaction();
            }
        }
        try {
            T t = work.apply(session, isTempSession);
            return t;
        }
        finally {
            if (isTempSession) {
                try {
                    if (!isJta) {
                        BytecodeInterceptorLogging.LOGGER.debug((Object)"Enhancement interception Helper#performWork committing transaction on temporary Session");
                        session.getTransaction().commit();
                    }
                }
                catch (Exception e) {
                    BytecodeInterceptorLogging.LOGGER.warn((Object)"Unable to commit JDBC transaction on temporary session used to load lazy collection associated to no session");
                }
                try {
                    BytecodeInterceptorLogging.LOGGER.debug((Object)"Enhancement interception Helper#performWork closing temporary Session");
                    session.close();
                }
                catch (Exception e) {
                    BytecodeInterceptorLogging.LOGGER.warn((Object)"Unable to close temporary session used to load lazy collection associated to no session");
                }
            }
        }
    }

    private static void throwLazyInitializationException(Cause cause, String entityName, String attributeName) {
        String reason;
        switch (cause) {
            case NO_SESSION: {
                reason = "no session and settings disallow loading outside the Session";
                break;
            }
            case CLOSED_SESSION: {
                reason = "session is closed and settings disallow loading outside the Session";
                break;
            }
            case DISCONNECTED_SESSION: {
                reason = "session is disconnected and settings disallow loading outside the Session";
                break;
            }
            case NO_SF_UUID: {
                reason = "could not determine SessionFactory UUId to create temporary Session for loading";
                break;
            }
            default: {
                reason = "<should never get here>";
            }
        }
        String message = String.format(Locale.ROOT, "Unable to perform requested lazy initialization [%s.%s] - %s", entityName, attributeName, reason);
        throw new LazyInitializationException(message);
    }

    private static SharedSessionContractImplementor openTemporarySessionForLoading(BytecodeLazyAttributeInterceptor interceptor, String entityName, String attributeName) {
        if (interceptor.getSessionFactoryUuid() == null) {
            EnhancementHelper.throwLazyInitializationException(Cause.NO_SF_UUID, entityName, attributeName);
        }
        SessionFactoryImplementor sf = (SessionFactoryImplementor)SessionFactoryRegistry.INSTANCE.getSessionFactory(interceptor.getSessionFactoryUuid());
        SharedSessionContractImplementor session = (SharedSessionContractImplementor)((Object)sf.openSession());
        session.getPersistenceContextInternal().setDefaultReadOnly(true);
        session.setHibernateFlushMode(FlushMode.MANUAL);
        return session;
    }

    static enum Cause {
        NO_SESSION,
        CLOSED_SESSION,
        DISCONNECTED_SESSION,
        NO_SF_UUID;

    }

    @FunctionalInterface
    public static interface InheritanceChecker {
        public boolean hasSubclasses(String var1);
    }
}

