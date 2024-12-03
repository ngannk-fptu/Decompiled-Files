/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.ObjectProvider
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.core.env.Environment
 *  org.springframework.http.client.ClientHttpRequestFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.scheduling.TaskScheduler
 *  org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
 *  org.springframework.util.Assert
 *  org.springframework.web.client.RestOperations
 */
package org.springframework.vault.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.Assert;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.LifecycleAwareSessionManager;
import org.springframework.vault.authentication.SessionManager;
import org.springframework.vault.client.ClientHttpRequestFactoryFactory;
import org.springframework.vault.client.RestTemplateBuilder;
import org.springframework.vault.client.RestTemplateCustomizer;
import org.springframework.vault.client.RestTemplateFactory;
import org.springframework.vault.client.SimpleVaultEndpointProvider;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.client.VaultEndpointProvider;
import org.springframework.vault.config.DefaultRestTemplateFactory;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.core.lease.SecretLeaseContainer;
import org.springframework.vault.support.ClientOptions;
import org.springframework.vault.support.SslConfiguration;
import org.springframework.web.client.RestOperations;

@Configuration(proxyBeanMethods=false)
public abstract class AbstractVaultConfiguration
implements ApplicationContextAware {
    @Nullable
    private ApplicationContext applicationContext;

    public abstract VaultEndpoint vaultEndpoint();

    public VaultEndpointProvider vaultEndpointProvider() {
        return SimpleVaultEndpointProvider.of(this.vaultEndpoint());
    }

    public abstract ClientAuthentication clientAuthentication();

    protected RestTemplateBuilder restTemplateBuilder(VaultEndpointProvider endpointProvider, ClientHttpRequestFactory requestFactory) {
        ObjectProvider customizers = this.getBeanFactory().getBeanProvider(RestTemplateCustomizer.class);
        RestTemplateBuilder builder = RestTemplateBuilder.builder().endpointProvider(endpointProvider).requestFactory(requestFactory);
        builder.customizers((RestTemplateCustomizer[])customizers.stream().toArray(RestTemplateCustomizer[]::new));
        return builder;
    }

    @Bean
    public RestTemplateFactory restTemplateFactory(ClientFactoryWrapper requestFactoryWrapper) {
        return new DefaultRestTemplateFactory(requestFactoryWrapper.getClientHttpRequestFactory(), it -> this.restTemplateBuilder(this.vaultEndpointProvider(), (ClientHttpRequestFactory)it));
    }

    @Bean
    public VaultTemplate vaultTemplate() {
        return new VaultTemplate(this.restTemplateBuilder(this.vaultEndpointProvider(), this.getClientFactoryWrapper().getClientHttpRequestFactory()), (SessionManager)this.getBeanFactory().getBean("sessionManager", SessionManager.class));
    }

    @Bean
    public SessionManager sessionManager() {
        ClientAuthentication clientAuthentication = this.clientAuthentication();
        Assert.notNull((Object)clientAuthentication, (String)"ClientAuthentication must not be null");
        return new LifecycleAwareSessionManager(clientAuthentication, (TaskScheduler)this.getVaultThreadPoolTaskScheduler(), this.restOperations());
    }

    @Bean
    public SecretLeaseContainer secretLeaseContainer() throws Exception {
        SecretLeaseContainer secretLeaseContainer = new SecretLeaseContainer((VaultOperations)this.getBeanFactory().getBean("vaultTemplate", VaultTemplate.class), (TaskScheduler)this.getVaultThreadPoolTaskScheduler());
        secretLeaseContainer.afterPropertiesSet();
        secretLeaseContainer.start();
        return secretLeaseContainer;
    }

    @Bean(value={"vaultThreadPoolTaskScheduler"})
    public TaskSchedulerWrapper threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setThreadNamePrefix("spring-vault-ThreadPoolTaskScheduler-");
        threadPoolTaskScheduler.setDaemon(true);
        return new TaskSchedulerWrapper(threadPoolTaskScheduler);
    }

    public RestOperations restOperations() {
        return this.getRestTemplateFactory().create();
    }

    @Bean
    public ClientFactoryWrapper clientHttpRequestFactoryWrapper() {
        return new ClientFactoryWrapper(ClientHttpRequestFactoryFactory.create(this.clientOptions(), this.sslConfiguration()));
    }

    public ClientOptions clientOptions() {
        return new ClientOptions();
    }

    public SslConfiguration sslConfiguration() {
        return SslConfiguration.unconfigured();
    }

    protected Environment getEnvironment() {
        Assert.state((this.applicationContext != null ? 1 : 0) != 0, (String)"ApplicationContext must be set before accessing getEnvironment()");
        return this.applicationContext.getEnvironment();
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    protected RestTemplateFactory getRestTemplateFactory() {
        return (RestTemplateFactory)this.getBeanFactory().getBean(RestTemplateFactory.class);
    }

    protected ThreadPoolTaskScheduler getVaultThreadPoolTaskScheduler() {
        return ((TaskSchedulerWrapper)this.getBeanFactory().getBean("vaultThreadPoolTaskScheduler", TaskSchedulerWrapper.class)).getTaskScheduler();
    }

    protected BeanFactory getBeanFactory() {
        Assert.state((this.applicationContext != null ? 1 : 0) != 0, (String)"ApplicationContext must be set before accessing getBeanFactory()");
        return this.applicationContext;
    }

    private ClientFactoryWrapper getClientFactoryWrapper() {
        return (ClientFactoryWrapper)this.getBeanFactory().getBean("clientHttpRequestFactoryWrapper", ClientFactoryWrapper.class);
    }

    public static class TaskSchedulerWrapper
    implements InitializingBean,
    DisposableBean {
        private final ThreadPoolTaskScheduler taskScheduler;
        private final boolean acceptAfterPropertiesSet;
        private final boolean acceptDestroy;

        public TaskSchedulerWrapper(ThreadPoolTaskScheduler taskScheduler) {
            this(taskScheduler, true, true);
        }

        protected TaskSchedulerWrapper(ThreadPoolTaskScheduler taskScheduler, boolean acceptAfterPropertiesSet, boolean acceptDestroy) {
            Assert.notNull((Object)taskScheduler, (String)"ThreadPoolTaskScheduler must not be null");
            this.taskScheduler = taskScheduler;
            this.acceptAfterPropertiesSet = acceptAfterPropertiesSet;
            this.acceptDestroy = acceptDestroy;
        }

        public static TaskSchedulerWrapper fromInstance(ThreadPoolTaskScheduler scheduler) {
            return new TaskSchedulerWrapper(scheduler, false, false);
        }

        ThreadPoolTaskScheduler getTaskScheduler() {
            return this.taskScheduler;
        }

        public void destroy() {
            if (this.acceptDestroy) {
                this.taskScheduler.destroy();
            }
        }

        public void afterPropertiesSet() {
            if (this.acceptAfterPropertiesSet) {
                this.taskScheduler.afterPropertiesSet();
            }
        }
    }

    public static class ClientFactoryWrapper
    implements InitializingBean,
    DisposableBean {
        private final ClientHttpRequestFactory clientHttpRequestFactory;

        public ClientFactoryWrapper(ClientHttpRequestFactory clientHttpRequestFactory) {
            this.clientHttpRequestFactory = clientHttpRequestFactory;
        }

        public void destroy() throws Exception {
            if (this.clientHttpRequestFactory instanceof DisposableBean) {
                ((DisposableBean)this.clientHttpRequestFactory).destroy();
            }
        }

        public void afterPropertiesSet() throws Exception {
            if (this.clientHttpRequestFactory instanceof InitializingBean) {
                ((InitializingBean)this.clientHttpRequestFactory).afterPropertiesSet();
            }
        }

        public ClientHttpRequestFactory getClientHttpRequestFactory() {
            return this.clientHttpRequestFactory;
        }
    }
}

