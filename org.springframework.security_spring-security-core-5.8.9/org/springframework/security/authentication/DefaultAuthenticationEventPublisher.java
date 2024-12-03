/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.context.ApplicationEvent
 *  org.springframework.context.ApplicationEventPublisher
 *  org.springframework.context.ApplicationEventPublisherAware
 *  org.springframework.util.Assert
 */
package org.springframework.security.authentication;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationFailureCredentialsExpiredEvent;
import org.springframework.security.authentication.event.AuthenticationFailureDisabledEvent;
import org.springframework.security.authentication.event.AuthenticationFailureExpiredEvent;
import org.springframework.security.authentication.event.AuthenticationFailureLockedEvent;
import org.springframework.security.authentication.event.AuthenticationFailureProviderNotFoundEvent;
import org.springframework.security.authentication.event.AuthenticationFailureProxyUntrustedEvent;
import org.springframework.security.authentication.event.AuthenticationFailureServiceExceptionEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

public class DefaultAuthenticationEventPublisher
implements AuthenticationEventPublisher,
ApplicationEventPublisherAware {
    private final Log logger = LogFactory.getLog(this.getClass());
    private ApplicationEventPublisher applicationEventPublisher;
    private final HashMap<String, Constructor<? extends AbstractAuthenticationEvent>> exceptionMappings = new HashMap();
    private Constructor<? extends AbstractAuthenticationFailureEvent> defaultAuthenticationFailureEventConstructor;

    public DefaultAuthenticationEventPublisher() {
        this(null);
    }

    public DefaultAuthenticationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.addMapping(BadCredentialsException.class.getName(), AuthenticationFailureBadCredentialsEvent.class);
        this.addMapping(UsernameNotFoundException.class.getName(), AuthenticationFailureBadCredentialsEvent.class);
        this.addMapping(AccountExpiredException.class.getName(), AuthenticationFailureExpiredEvent.class);
        this.addMapping(ProviderNotFoundException.class.getName(), AuthenticationFailureProviderNotFoundEvent.class);
        this.addMapping(DisabledException.class.getName(), AuthenticationFailureDisabledEvent.class);
        this.addMapping(LockedException.class.getName(), AuthenticationFailureLockedEvent.class);
        this.addMapping(AuthenticationServiceException.class.getName(), AuthenticationFailureServiceExceptionEvent.class);
        this.addMapping(CredentialsExpiredException.class.getName(), AuthenticationFailureCredentialsExpiredEvent.class);
        this.addMapping("org.springframework.security.authentication.cas.ProxyUntrustedException", AuthenticationFailureProxyUntrustedEvent.class);
        this.addMapping("org.springframework.security.oauth2.server.resource.InvalidBearerTokenException", AuthenticationFailureBadCredentialsEvent.class);
    }

    @Override
    public void publishAuthenticationSuccess(Authentication authentication) {
        if (this.applicationEventPublisher != null) {
            this.applicationEventPublisher.publishEvent((ApplicationEvent)new AuthenticationSuccessEvent(authentication));
        }
    }

    @Override
    public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
        Constructor<? extends AbstractAuthenticationEvent> constructor = this.getEventConstructor(exception);
        AbstractAuthenticationEvent event = null;
        if (constructor != null) {
            try {
                event = constructor.newInstance(authentication, exception);
            }
            catch (IllegalAccessException | InstantiationException | InvocationTargetException reflectiveOperationException) {
                // empty catch block
            }
        }
        if (event != null) {
            if (this.applicationEventPublisher != null) {
                this.applicationEventPublisher.publishEvent(event);
            }
        } else if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("No event was found for the exception " + exception.getClass().getName()));
        }
    }

    private Constructor<? extends AbstractAuthenticationEvent> getEventConstructor(AuthenticationException exception) {
        Constructor<? extends AbstractAuthenticationEvent> eventConstructor = this.exceptionMappings.get(exception.getClass().getName());
        return eventConstructor != null ? eventConstructor : this.defaultAuthenticationFailureEventConstructor;
    }

    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Deprecated
    public void setAdditionalExceptionMappings(Properties additionalExceptionMappings) {
        Assert.notNull((Object)additionalExceptionMappings, (String)"The exceptionMappings object must not be null");
        for (Object exceptionClass : additionalExceptionMappings.keySet()) {
            String eventClass = (String)additionalExceptionMappings.get(exceptionClass);
            try {
                Class<?> clazz = this.getClass().getClassLoader().loadClass(eventClass);
                Assert.isAssignable(AbstractAuthenticationFailureEvent.class, clazz);
                this.addMapping((String)exceptionClass, clazz);
            }
            catch (ClassNotFoundException ex) {
                throw new RuntimeException("Failed to load authentication event class " + eventClass);
            }
        }
    }

    public void setAdditionalExceptionMappings(Map<Class<? extends AuthenticationException>, Class<? extends AbstractAuthenticationFailureEvent>> mappings) {
        Assert.notEmpty(mappings, (String)"The mappings Map must not be empty nor null");
        for (Map.Entry<Class<? extends AuthenticationException>, Class<? extends AbstractAuthenticationFailureEvent>> entry : mappings.entrySet()) {
            Class<? extends AuthenticationException> exceptionClass = entry.getKey();
            Class<? extends AbstractAuthenticationFailureEvent> eventClass = entry.getValue();
            Assert.notNull(exceptionClass, (String)"exceptionClass cannot be null");
            Assert.notNull(eventClass, (String)"eventClass cannot be null");
            this.addMapping(exceptionClass.getName(), eventClass);
        }
    }

    public void setDefaultAuthenticationFailureEvent(Class<? extends AbstractAuthenticationFailureEvent> defaultAuthenticationFailureEventClass) {
        Assert.notNull(defaultAuthenticationFailureEventClass, (String)"defaultAuthenticationFailureEventClass must not be null");
        try {
            this.defaultAuthenticationFailureEventConstructor = defaultAuthenticationFailureEventClass.getConstructor(Authentication.class, AuthenticationException.class);
        }
        catch (NoSuchMethodException ex) {
            throw new RuntimeException("Default Authentication Failure event class " + defaultAuthenticationFailureEventClass.getName() + " has no suitable constructor");
        }
    }

    private void addMapping(String exceptionClass, Class<? extends AbstractAuthenticationFailureEvent> eventClass) {
        try {
            Constructor<? extends AbstractAuthenticationFailureEvent> constructor = eventClass.getConstructor(Authentication.class, AuthenticationException.class);
            this.exceptionMappings.put(exceptionClass, constructor);
        }
        catch (NoSuchMethodException ex) {
            throw new RuntimeException("Authentication event class " + eventClass.getName() + " has no suitable constructor");
        }
    }
}

