/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.ApplicationContext
 *  org.springframework.lang.Nullable
 *  org.springframework.messaging.MessageChannel
 *  org.springframework.messaging.SubscribableChannel
 *  org.springframework.messaging.handler.MessagingAdviceBean
 *  org.springframework.messaging.handler.annotation.support.AnnotationExceptionHandlerMethodResolver
 *  org.springframework.messaging.handler.invocation.AbstractExceptionHandlerMethodResolver
 *  org.springframework.messaging.simp.SimpMessageSendingOperations
 *  org.springframework.messaging.simp.annotation.support.SimpAnnotationMethodMessageHandler
 *  org.springframework.web.method.ControllerAdviceBean
 */
package org.springframework.web.socket.messaging;

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.handler.MessagingAdviceBean;
import org.springframework.messaging.handler.annotation.support.AnnotationExceptionHandlerMethodResolver;
import org.springframework.messaging.handler.invocation.AbstractExceptionHandlerMethodResolver;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.support.SimpAnnotationMethodMessageHandler;
import org.springframework.web.method.ControllerAdviceBean;

public class WebSocketAnnotationMethodMessageHandler
extends SimpAnnotationMethodMessageHandler {
    public WebSocketAnnotationMethodMessageHandler(SubscribableChannel clientInChannel, MessageChannel clientOutChannel, SimpMessageSendingOperations brokerTemplate) {
        super(clientInChannel, clientOutChannel, brokerTemplate);
    }

    public void afterPropertiesSet() {
        this.initControllerAdviceCache();
        super.afterPropertiesSet();
    }

    private void initControllerAdviceCache() {
        ApplicationContext context = this.getApplicationContext();
        if (context == null) {
            return;
        }
        if (this.logger.isTraceEnabled()) {
            this.logger.trace((Object)("Looking for @MessageExceptionHandler mappings: " + context));
        }
        List beans = ControllerAdviceBean.findAnnotatedBeans((ApplicationContext)context);
        this.initMessagingAdviceCache(MessagingControllerAdviceBean.createFromList(beans));
    }

    private void initMessagingAdviceCache(List<MessagingAdviceBean> beans) {
        for (MessagingAdviceBean bean : beans) {
            AnnotationExceptionHandlerMethodResolver resolver;
            Class type = bean.getBeanType();
            if (type == null || !(resolver = new AnnotationExceptionHandlerMethodResolver(type)).hasExceptionMappings()) continue;
            this.registerExceptionHandlerAdvice(bean, (AbstractExceptionHandlerMethodResolver)resolver);
            if (!this.logger.isTraceEnabled()) continue;
            this.logger.trace((Object)("Detected @MessageExceptionHandler methods in " + bean));
        }
    }

    private static final class MessagingControllerAdviceBean
    implements MessagingAdviceBean {
        private final ControllerAdviceBean adviceBean;

        private MessagingControllerAdviceBean(ControllerAdviceBean adviceBean) {
            this.adviceBean = adviceBean;
        }

        public static List<MessagingAdviceBean> createFromList(List<ControllerAdviceBean> beans) {
            ArrayList<MessagingAdviceBean> result = new ArrayList<MessagingAdviceBean>(beans.size());
            for (ControllerAdviceBean bean : beans) {
                result.add(new MessagingControllerAdviceBean(bean));
            }
            return result;
        }

        @Nullable
        public Class<?> getBeanType() {
            return this.adviceBean.getBeanType();
        }

        public Object resolveBean() {
            return this.adviceBean.resolveBean();
        }

        public boolean isApplicableToBeanType(Class<?> beanType) {
            return this.adviceBean.isApplicableToBeanType(beanType);
        }

        public int getOrder() {
            return this.adviceBean.getOrder();
        }
    }
}

