/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.vavr.control.Try
 *  kotlin.coroutines.Continuation
 *  kotlinx.coroutines.reactive.AwaitKt
 *  kotlinx.coroutines.reactive.ReactiveFlowKt
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.reactivestreams.Publisher
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils
 *  org.springframework.core.CoroutinesUtils
 *  org.springframework.core.KotlinDetector
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.NamedThreadLocal
 *  org.springframework.core.ReactiveAdapter
 *  org.springframework.core.ReactiveAdapterRegistry
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ConcurrentReferenceHashMap
 *  org.springframework.util.StringUtils
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.transaction.interceptor;

import io.vavr.control.Try;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.concurrent.ConcurrentMap;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.reactive.AwaitKt;
import kotlinx.coroutines.reactive.ReactiveFlowKt;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.core.CoroutinesUtils;
import org.springframework.core.KotlinDetector;
import org.springframework.core.MethodParameter;
import org.springframework.core.NamedThreadLocal;
import org.springframework.core.ReactiveAdapter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.lang.Nullable;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.ReactiveTransaction;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.interceptor.CompositeTransactionAttributeSource;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.DelegatingTransactionAttribute;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.reactive.TransactionContextManager;
import org.springframework.transaction.support.CallbackPreferringPlatformTransactionManager;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class TransactionAspectSupport
implements BeanFactoryAware,
InitializingBean {
    private static final Object DEFAULT_TRANSACTION_MANAGER_KEY = new Object();
    private static final String COROUTINES_FLOW_CLASS_NAME = "kotlinx.coroutines.flow.Flow";
    private static final boolean vavrPresent = ClassUtils.isPresent((String)"io.vavr.control.Try", (ClassLoader)TransactionAspectSupport.class.getClassLoader());
    private static final boolean reactiveStreamsPresent = ClassUtils.isPresent((String)"org.reactivestreams.Publisher", (ClassLoader)TransactionAspectSupport.class.getClassLoader());
    private static final ThreadLocal<TransactionInfo> transactionInfoHolder = new NamedThreadLocal("Current aspect-driven transaction");
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private final ReactiveAdapterRegistry reactiveAdapterRegistry;
    @Nullable
    private String transactionManagerBeanName;
    @Nullable
    private TransactionManager transactionManager;
    @Nullable
    private TransactionAttributeSource transactionAttributeSource;
    @Nullable
    private BeanFactory beanFactory;
    private final ConcurrentMap<Object, TransactionManager> transactionManagerCache = new ConcurrentReferenceHashMap(4);
    private final ConcurrentMap<Method, ReactiveTransactionSupport> transactionSupportCache = new ConcurrentReferenceHashMap(1024);

    @Nullable
    protected static TransactionInfo currentTransactionInfo() throws NoTransactionException {
        return transactionInfoHolder.get();
    }

    public static TransactionStatus currentTransactionStatus() throws NoTransactionException {
        TransactionInfo info = TransactionAspectSupport.currentTransactionInfo();
        if (info == null || info.transactionStatus == null) {
            throw new NoTransactionException("No transaction aspect-managed TransactionStatus in scope");
        }
        return info.transactionStatus;
    }

    protected TransactionAspectSupport() {
        this.reactiveAdapterRegistry = reactiveStreamsPresent ? ReactiveAdapterRegistry.getSharedInstance() : null;
    }

    public void setTransactionManagerBeanName(@Nullable String transactionManagerBeanName) {
        this.transactionManagerBeanName = transactionManagerBeanName;
    }

    @Nullable
    protected final String getTransactionManagerBeanName() {
        return this.transactionManagerBeanName;
    }

    public void setTransactionManager(@Nullable TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Nullable
    public TransactionManager getTransactionManager() {
        return this.transactionManager;
    }

    public void setTransactionAttributes(Properties transactionAttributes) {
        NameMatchTransactionAttributeSource tas = new NameMatchTransactionAttributeSource();
        tas.setProperties(transactionAttributes);
        this.transactionAttributeSource = tas;
    }

    public void setTransactionAttributeSources(TransactionAttributeSource ... transactionAttributeSources) {
        this.transactionAttributeSource = new CompositeTransactionAttributeSource(transactionAttributeSources);
    }

    public void setTransactionAttributeSource(@Nullable TransactionAttributeSource transactionAttributeSource) {
        this.transactionAttributeSource = transactionAttributeSource;
    }

    @Nullable
    public TransactionAttributeSource getTransactionAttributeSource() {
        return this.transactionAttributeSource;
    }

    public void setBeanFactory(@Nullable BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Nullable
    protected final BeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    public void afterPropertiesSet() {
        if (this.getTransactionManager() == null && this.beanFactory == null) {
            throw new IllegalStateException("Set the 'transactionManager' property or make sure to run within a BeanFactory containing a TransactionManager bean!");
        }
        if (this.getTransactionAttributeSource() == null) {
            throw new IllegalStateException("Either 'transactionAttributeSource' or 'transactionAttributes' is required: If there are no transactional methods, then don't use a transaction aspect.");
        }
    }

    @Nullable
    protected Object invokeWithinTransaction(Method method, @Nullable Class<?> targetClass, InvocationCallback invocation) throws Throwable {
        Object result;
        TransactionAttributeSource tas = this.getTransactionAttributeSource();
        TransactionAttribute txAttr = tas != null ? tas.getTransactionAttribute(method, targetClass) : null;
        TransactionManager tm = this.determineTransactionManager(txAttr);
        if (this.reactiveAdapterRegistry != null && tm instanceof ReactiveTransactionManager) {
            boolean hasSuspendingFlowReturnType;
            boolean isSuspendingFunction = KotlinDetector.isSuspendingFunction((Method)method);
            boolean bl = hasSuspendingFlowReturnType = isSuspendingFunction && COROUTINES_FLOW_CLASS_NAME.equals(new MethodParameter(method, -1).getParameterType().getName());
            if (isSuspendingFunction && !(invocation instanceof CoroutinesInvocationCallback)) {
                throw new IllegalStateException("Coroutines invocation not supported: " + method);
            }
            CoroutinesInvocationCallback corInv = isSuspendingFunction ? (CoroutinesInvocationCallback)invocation : null;
            ReactiveTransactionSupport txSupport = this.transactionSupportCache.computeIfAbsent(method, key -> {
                Class<Object> reactiveType = isSuspendingFunction ? (hasSuspendingFlowReturnType ? Flux.class : Mono.class) : method.getReturnType();
                ReactiveAdapter adapter = this.reactiveAdapterRegistry.getAdapter(reactiveType);
                if (adapter == null) {
                    throw new IllegalStateException("Cannot apply reactive transaction to non-reactive return type: " + method.getReturnType());
                }
                return new ReactiveTransactionSupport(adapter);
            });
            InvocationCallback callback = invocation;
            if (corInv != null) {
                callback = () -> CoroutinesUtils.invokeSuspendingFunction((Method)method, (Object)corInv.getTarget(), (Object[])corInv.getArguments());
            }
            Object result2 = txSupport.invokeWithinTransaction(method, targetClass, callback, txAttr, (ReactiveTransactionManager)tm);
            if (corInv != null) {
                Publisher pr = (Publisher)result2;
                return hasSuspendingFlowReturnType ? KotlinDelegate.asFlow(pr) : KotlinDelegate.awaitSingleOrNull(pr, corInv.getContinuation());
            }
            return result2;
        }
        PlatformTransactionManager ptm = this.asPlatformTransactionManager(tm);
        String joinpointIdentification = this.methodIdentification(method, targetClass, txAttr);
        if (txAttr == null || !(ptm instanceof CallbackPreferringPlatformTransactionManager)) {
            TransactionStatus status2;
            Object retVal;
            TransactionInfo txInfo = this.createTransactionIfNecessary(ptm, txAttr, joinpointIdentification);
            try {
                retVal = invocation.proceedWithInvocation();
            }
            catch (Throwable ex) {
                this.completeTransactionAfterThrowing(txInfo, ex);
                throw ex;
            }
            finally {
                this.cleanupTransactionInfo(txInfo);
            }
            if (retVal != null && vavrPresent && VavrDelegate.isVavrTry(retVal) && (status2 = txInfo.getTransactionStatus()) != null && txAttr != null) {
                retVal = VavrDelegate.evaluateTryFailure(retVal, txAttr, status2);
            }
            this.commitTransactionAfterReturning(txInfo);
            return retVal;
        }
        ThrowableHolder throwableHolder = new ThrowableHolder();
        try {
            result = ((CallbackPreferringPlatformTransactionManager)ptm).execute(txAttr, status -> {
                TransactionInfo txInfo = this.prepareTransactionInfo(ptm, txAttr, joinpointIdentification, status);
                try {
                    Object retVal = invocation.proceedWithInvocation();
                    if (retVal != null && vavrPresent && VavrDelegate.isVavrTry(retVal)) {
                        retVal = VavrDelegate.evaluateTryFailure(retVal, txAttr, status);
                    }
                    Object object = retVal;
                    return object;
                }
                catch (Throwable ex) {
                    if (txAttr.rollbackOn(ex)) {
                        if (ex instanceof RuntimeException) {
                            throw (RuntimeException)ex;
                        }
                        throw new ThrowableHolderException(ex);
                    }
                    throwableHolder.throwable = ex;
                    Object var9_11 = null;
                    return var9_11;
                }
                finally {
                    this.cleanupTransactionInfo(txInfo);
                }
            });
        }
        catch (ThrowableHolderException ex) {
            throw ex.getCause();
        }
        catch (TransactionSystemException ex2) {
            if (throwableHolder.throwable != null) {
                this.logger.error((Object)"Application exception overridden by commit exception", throwableHolder.throwable);
                ex2.initApplicationException(throwableHolder.throwable);
            }
            throw ex2;
        }
        catch (Throwable ex2) {
            if (throwableHolder.throwable != null) {
                this.logger.error((Object)"Application exception overridden by commit exception", throwableHolder.throwable);
            }
            throw ex2;
        }
        if (throwableHolder.throwable != null) {
            throw throwableHolder.throwable;
        }
        return result;
    }

    protected void clearTransactionManagerCache() {
        this.transactionManagerCache.clear();
        this.beanFactory = null;
    }

    @Nullable
    protected TransactionManager determineTransactionManager(@Nullable TransactionAttribute txAttr) {
        if (txAttr == null || this.beanFactory == null) {
            return this.getTransactionManager();
        }
        String qualifier = txAttr.getQualifier();
        if (StringUtils.hasText((String)qualifier)) {
            return this.determineQualifiedTransactionManager(this.beanFactory, qualifier);
        }
        if (StringUtils.hasText((String)this.transactionManagerBeanName)) {
            return this.determineQualifiedTransactionManager(this.beanFactory, this.transactionManagerBeanName);
        }
        TransactionManager defaultTransactionManager = this.getTransactionManager();
        if (defaultTransactionManager == null && (defaultTransactionManager = (TransactionManager)this.transactionManagerCache.get(DEFAULT_TRANSACTION_MANAGER_KEY)) == null) {
            defaultTransactionManager = (TransactionManager)this.beanFactory.getBean(TransactionManager.class);
            this.transactionManagerCache.putIfAbsent(DEFAULT_TRANSACTION_MANAGER_KEY, defaultTransactionManager);
        }
        return defaultTransactionManager;
    }

    private TransactionManager determineQualifiedTransactionManager(BeanFactory beanFactory, String qualifier) {
        TransactionManager txManager = (TransactionManager)this.transactionManagerCache.get(qualifier);
        if (txManager == null) {
            txManager = (TransactionManager)BeanFactoryAnnotationUtils.qualifiedBeanOfType((BeanFactory)beanFactory, TransactionManager.class, (String)qualifier);
            this.transactionManagerCache.putIfAbsent(qualifier, txManager);
        }
        return txManager;
    }

    @Nullable
    private PlatformTransactionManager asPlatformTransactionManager(@Nullable Object transactionManager) {
        if (transactionManager == null || transactionManager instanceof PlatformTransactionManager) {
            return (PlatformTransactionManager)transactionManager;
        }
        throw new IllegalStateException("Specified transaction manager is not a PlatformTransactionManager: " + transactionManager);
    }

    private String methodIdentification(Method method, @Nullable Class<?> targetClass, @Nullable TransactionAttribute txAttr) {
        String methodIdentification = this.methodIdentification(method, targetClass);
        if (methodIdentification == null) {
            if (txAttr instanceof DefaultTransactionAttribute) {
                methodIdentification = ((DefaultTransactionAttribute)txAttr).getDescriptor();
            }
            if (methodIdentification == null) {
                methodIdentification = ClassUtils.getQualifiedMethodName((Method)method, targetClass);
            }
        }
        return methodIdentification;
    }

    @Nullable
    protected String methodIdentification(Method method, @Nullable Class<?> targetClass) {
        return null;
    }

    protected TransactionInfo createTransactionIfNecessary(@Nullable PlatformTransactionManager tm, @Nullable TransactionAttribute txAttr, final String joinpointIdentification) {
        if (txAttr != null && txAttr.getName() == null) {
            txAttr = new DelegatingTransactionAttribute(txAttr){

                @Override
                public String getName() {
                    return joinpointIdentification;
                }
            };
        }
        TransactionStatus status = null;
        if (txAttr != null) {
            if (tm != null) {
                status = tm.getTransaction(txAttr);
            } else if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Skipping transactional joinpoint [" + joinpointIdentification + "] because no transaction manager has been configured"));
            }
        }
        return this.prepareTransactionInfo(tm, txAttr, joinpointIdentification, status);
    }

    protected TransactionInfo prepareTransactionInfo(@Nullable PlatformTransactionManager tm, @Nullable TransactionAttribute txAttr, String joinpointIdentification, @Nullable TransactionStatus status) {
        TransactionInfo txInfo = new TransactionInfo(tm, txAttr, joinpointIdentification);
        if (txAttr != null) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)("Getting transaction for [" + txInfo.getJoinpointIdentification() + "]"));
            }
            txInfo.newTransactionStatus(status);
        } else if (this.logger.isTraceEnabled()) {
            this.logger.trace((Object)("No need to create transaction for [" + joinpointIdentification + "]: This method is not transactional."));
        }
        txInfo.bindToThread();
        return txInfo;
    }

    protected void commitTransactionAfterReturning(@Nullable TransactionInfo txInfo) {
        if (txInfo != null && txInfo.getTransactionStatus() != null) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)("Completing transaction for [" + txInfo.getJoinpointIdentification() + "]"));
            }
            txInfo.getTransactionManager().commit(txInfo.getTransactionStatus());
        }
    }

    protected void completeTransactionAfterThrowing(@Nullable TransactionInfo txInfo, Throwable ex) {
        if (txInfo != null && txInfo.getTransactionStatus() != null) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)("Completing transaction for [" + txInfo.getJoinpointIdentification() + "] after exception: " + ex));
            }
            if (txInfo.transactionAttribute != null && txInfo.transactionAttribute.rollbackOn(ex)) {
                try {
                    txInfo.getTransactionManager().rollback(txInfo.getTransactionStatus());
                }
                catch (TransactionSystemException ex2) {
                    this.logger.error((Object)"Application exception overridden by rollback exception", ex);
                    ex2.initApplicationException(ex);
                    throw ex2;
                }
                catch (Error | RuntimeException ex2) {
                    this.logger.error((Object)"Application exception overridden by rollback exception", ex);
                    throw ex2;
                }
            }
            try {
                txInfo.getTransactionManager().commit(txInfo.getTransactionStatus());
            }
            catch (TransactionSystemException ex2) {
                this.logger.error((Object)"Application exception overridden by commit exception", ex);
                ex2.initApplicationException(ex);
                throw ex2;
            }
            catch (Error | RuntimeException ex2) {
                this.logger.error((Object)"Application exception overridden by commit exception", ex);
                throw ex2;
            }
        }
    }

    protected void cleanupTransactionInfo(@Nullable TransactionInfo txInfo) {
        if (txInfo != null) {
            txInfo.restoreThreadLocalStatus();
        }
    }

    private static final class ReactiveTransactionInfo {
        @Nullable
        private final ReactiveTransactionManager transactionManager;
        @Nullable
        private final TransactionAttribute transactionAttribute;
        private final String joinpointIdentification;
        @Nullable
        private ReactiveTransaction reactiveTransaction;

        public ReactiveTransactionInfo(@Nullable ReactiveTransactionManager transactionManager, @Nullable TransactionAttribute transactionAttribute, String joinpointIdentification) {
            this.transactionManager = transactionManager;
            this.transactionAttribute = transactionAttribute;
            this.joinpointIdentification = joinpointIdentification;
        }

        public ReactiveTransactionManager getTransactionManager() {
            Assert.state((this.transactionManager != null ? 1 : 0) != 0, (String)"No ReactiveTransactionManager set");
            return this.transactionManager;
        }

        @Nullable
        public TransactionAttribute getTransactionAttribute() {
            return this.transactionAttribute;
        }

        public String getJoinpointIdentification() {
            return this.joinpointIdentification;
        }

        public void newReactiveTransaction(@Nullable ReactiveTransaction transaction) {
            this.reactiveTransaction = transaction;
        }

        @Nullable
        public ReactiveTransaction getReactiveTransaction() {
            return this.reactiveTransaction;
        }

        public String toString() {
            return this.transactionAttribute != null ? this.transactionAttribute.toString() : "No transaction";
        }
    }

    private class ReactiveTransactionSupport {
        private final ReactiveAdapter adapter;

        public ReactiveTransactionSupport(ReactiveAdapter adapter) {
            this.adapter = adapter;
        }

        public Object invokeWithinTransaction(Method method, @Nullable Class<?> targetClass, InvocationCallback invocation, @Nullable TransactionAttribute txAttr, ReactiveTransactionManager rtm) {
            String joinpointIdentification = TransactionAspectSupport.this.methodIdentification(method, targetClass, txAttr);
            if (Mono.class.isAssignableFrom(method.getReturnType()) || KotlinDetector.isSuspendingFunction((Method)method) && !TransactionAspectSupport.COROUTINES_FLOW_CLASS_NAME.equals(new MethodParameter(method, -1).getParameterType().getName())) {
                return TransactionContextManager.currentContext().flatMap(context -> this.createTransactionIfNecessary(rtm, txAttr, joinpointIdentification).flatMap(it -> {
                    try {
                        return Mono.usingWhen((Publisher)Mono.just((Object)it), txInfo -> {
                            try {
                                return (Mono)invocation.proceedWithInvocation();
                            }
                            catch (Throwable ex) {
                                return Mono.error((Throwable)ex);
                            }
                        }, this::commitTransactionAfterReturning, (txInfo, err) -> Mono.empty(), this::rollbackTransactionOnCancel).onErrorResume(ex -> this.completeTransactionAfterThrowing((ReactiveTransactionInfo)it, (Throwable)ex).then(Mono.error((Throwable)ex)));
                    }
                    catch (Throwable ex2) {
                        return this.completeTransactionAfterThrowing((ReactiveTransactionInfo)it, ex2).then(Mono.error((Throwable)ex2));
                    }
                })).contextWrite(TransactionContextManager.getOrCreateContext()).contextWrite(TransactionContextManager.getOrCreateContextHolder());
            }
            return this.adapter.fromPublisher((Publisher)TransactionContextManager.currentContext().flatMapMany(context -> this.createTransactionIfNecessary(rtm, txAttr, joinpointIdentification).flatMapMany(it -> {
                try {
                    return Flux.usingWhen((Publisher)Mono.just((Object)it), txInfo -> {
                        try {
                            return this.adapter.toPublisher(invocation.proceedWithInvocation());
                        }
                        catch (Throwable ex) {
                            return Mono.error((Throwable)ex);
                        }
                    }, this::commitTransactionAfterReturning, (txInfo, ex) -> Mono.empty(), this::rollbackTransactionOnCancel).onErrorResume(ex -> this.completeTransactionAfterThrowing((ReactiveTransactionInfo)it, (Throwable)ex).then(Mono.error((Throwable)ex)));
                }
                catch (Throwable ex2) {
                    return this.completeTransactionAfterThrowing((ReactiveTransactionInfo)it, ex2).then(Mono.error((Throwable)ex2));
                }
            })).contextWrite(TransactionContextManager.getOrCreateContext()).contextWrite(TransactionContextManager.getOrCreateContextHolder()));
        }

        private Mono<ReactiveTransactionInfo> createTransactionIfNecessary(ReactiveTransactionManager tm, @Nullable TransactionAttribute txAttr, final String joinpointIdentification) {
            TransactionAttribute attrToUse;
            if (txAttr != null && txAttr.getName() == null) {
                txAttr = new DelegatingTransactionAttribute(txAttr){

                    @Override
                    public String getName() {
                        return joinpointIdentification;
                    }
                };
            }
            Mono<ReactiveTransaction> tx = (attrToUse = txAttr) != null ? tm.getReactiveTransaction(attrToUse) : Mono.empty();
            return tx.map(it -> this.prepareTransactionInfo(tm, attrToUse, joinpointIdentification, (ReactiveTransaction)it)).switchIfEmpty(Mono.defer(() -> Mono.just((Object)this.prepareTransactionInfo(tm, attrToUse, joinpointIdentification, null))));
        }

        private ReactiveTransactionInfo prepareTransactionInfo(@Nullable ReactiveTransactionManager tm, @Nullable TransactionAttribute txAttr, String joinpointIdentification, @Nullable ReactiveTransaction transaction) {
            ReactiveTransactionInfo txInfo = new ReactiveTransactionInfo(tm, txAttr, joinpointIdentification);
            if (txAttr != null) {
                if (TransactionAspectSupport.this.logger.isTraceEnabled()) {
                    TransactionAspectSupport.this.logger.trace((Object)("Getting transaction for [" + txInfo.getJoinpointIdentification() + "]"));
                }
                txInfo.newReactiveTransaction(transaction);
            } else if (TransactionAspectSupport.this.logger.isTraceEnabled()) {
                TransactionAspectSupport.this.logger.trace((Object)("Don't need to create transaction for [" + joinpointIdentification + "]: This method isn't transactional."));
            }
            return txInfo;
        }

        private Mono<Void> commitTransactionAfterReturning(@Nullable ReactiveTransactionInfo txInfo) {
            if (txInfo != null && txInfo.getReactiveTransaction() != null) {
                if (TransactionAspectSupport.this.logger.isTraceEnabled()) {
                    TransactionAspectSupport.this.logger.trace((Object)("Completing transaction for [" + txInfo.getJoinpointIdentification() + "]"));
                }
                return txInfo.getTransactionManager().commit(txInfo.getReactiveTransaction());
            }
            return Mono.empty();
        }

        private Mono<Void> rollbackTransactionOnCancel(@Nullable ReactiveTransactionInfo txInfo) {
            if (txInfo != null && txInfo.getReactiveTransaction() != null) {
                if (TransactionAspectSupport.this.logger.isTraceEnabled()) {
                    TransactionAspectSupport.this.logger.trace((Object)("Rolling back transaction for [" + txInfo.getJoinpointIdentification() + "] after cancellation"));
                }
                return txInfo.getTransactionManager().rollback(txInfo.getReactiveTransaction());
            }
            return Mono.empty();
        }

        private Mono<Void> completeTransactionAfterThrowing(@Nullable ReactiveTransactionInfo txInfo, Throwable ex) {
            if (txInfo != null && txInfo.getReactiveTransaction() != null) {
                if (TransactionAspectSupport.this.logger.isTraceEnabled()) {
                    TransactionAspectSupport.this.logger.trace((Object)("Completing transaction for [" + txInfo.getJoinpointIdentification() + "] after exception: " + ex));
                }
                if (txInfo.transactionAttribute != null && txInfo.transactionAttribute.rollbackOn(ex)) {
                    return txInfo.getTransactionManager().rollback(txInfo.getReactiveTransaction()).onErrorMap(ex2 -> {
                        TransactionAspectSupport.this.logger.error((Object)"Application exception overridden by rollback exception", ex);
                        if (ex2 instanceof TransactionSystemException) {
                            ((TransactionSystemException)((Object)((Object)ex2))).initApplicationException(ex);
                        }
                        return ex2;
                    });
                }
                return txInfo.getTransactionManager().commit(txInfo.getReactiveTransaction()).onErrorMap(ex2 -> {
                    TransactionAspectSupport.this.logger.error((Object)"Application exception overridden by commit exception", ex);
                    if (ex2 instanceof TransactionSystemException) {
                        ((TransactionSystemException)((Object)((Object)ex2))).initApplicationException(ex);
                    }
                    return ex2;
                });
            }
            return Mono.empty();
        }
    }

    private static class KotlinDelegate {
        private KotlinDelegate() {
        }

        private static Object asFlow(Publisher<?> publisher) {
            return ReactiveFlowKt.asFlow(publisher);
        }

        @Nullable
        private static Object awaitSingleOrNull(Publisher<?> publisher, Object continuation) {
            return AwaitKt.awaitSingleOrNull(publisher, (Continuation)((Continuation)continuation));
        }
    }

    private static class VavrDelegate {
        private VavrDelegate() {
        }

        public static boolean isVavrTry(Object retVal) {
            return retVal instanceof Try;
        }

        public static Object evaluateTryFailure(Object retVal, TransactionAttribute txAttr, TransactionStatus status) {
            return ((Try)retVal).onFailure(ex -> {
                if (txAttr.rollbackOn((Throwable)ex)) {
                    status.setRollbackOnly();
                }
            });
        }
    }

    private static class ThrowableHolderException
    extends RuntimeException {
        public ThrowableHolderException(Throwable throwable) {
            super(throwable);
        }

        @Override
        public String toString() {
            return this.getCause().toString();
        }
    }

    private static class ThrowableHolder {
        @Nullable
        public Throwable throwable;

        private ThrowableHolder() {
        }
    }

    protected static interface CoroutinesInvocationCallback
    extends InvocationCallback {
        public Object getTarget();

        public Object[] getArguments();

        default public Object getContinuation() {
            Object[] args = this.getArguments();
            return args[args.length - 1];
        }
    }

    @FunctionalInterface
    protected static interface InvocationCallback {
        @Nullable
        public Object proceedWithInvocation() throws Throwable;
    }

    protected static final class TransactionInfo {
        @Nullable
        private final PlatformTransactionManager transactionManager;
        @Nullable
        private final TransactionAttribute transactionAttribute;
        private final String joinpointIdentification;
        @Nullable
        private TransactionStatus transactionStatus;
        @Nullable
        private TransactionInfo oldTransactionInfo;

        public TransactionInfo(@Nullable PlatformTransactionManager transactionManager, @Nullable TransactionAttribute transactionAttribute, String joinpointIdentification) {
            this.transactionManager = transactionManager;
            this.transactionAttribute = transactionAttribute;
            this.joinpointIdentification = joinpointIdentification;
        }

        public PlatformTransactionManager getTransactionManager() {
            Assert.state((this.transactionManager != null ? 1 : 0) != 0, (String)"No PlatformTransactionManager set");
            return this.transactionManager;
        }

        @Nullable
        public TransactionAttribute getTransactionAttribute() {
            return this.transactionAttribute;
        }

        public String getJoinpointIdentification() {
            return this.joinpointIdentification;
        }

        public void newTransactionStatus(@Nullable TransactionStatus status) {
            this.transactionStatus = status;
        }

        @Nullable
        public TransactionStatus getTransactionStatus() {
            return this.transactionStatus;
        }

        public boolean hasTransaction() {
            return this.transactionStatus != null;
        }

        private void bindToThread() {
            this.oldTransactionInfo = (TransactionInfo)transactionInfoHolder.get();
            transactionInfoHolder.set(this);
        }

        private void restoreThreadLocalStatus() {
            transactionInfoHolder.set(this.oldTransactionInfo);
        }

        public String toString() {
            return this.transactionAttribute != null ? this.transactionAttribute.toString() : "No transaction";
        }
    }
}

