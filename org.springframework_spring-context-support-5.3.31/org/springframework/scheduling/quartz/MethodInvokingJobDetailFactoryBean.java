/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.quartz.DisallowConcurrentExecution
 *  org.quartz.JobDetail
 *  org.quartz.JobExecutionContext
 *  org.quartz.JobExecutionException
 *  org.quartz.PersistJobDataAfterExecution
 *  org.quartz.impl.JobDetailImpl
 *  org.springframework.beans.factory.BeanClassLoaderAware
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.BeanNameAware
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.support.ArgumentConvertingMethodInvoker
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.MethodInvoker
 */
package org.springframework.scheduling.quartz;

import java.lang.reflect.InvocationTargetException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.impl.JobDetailImpl;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.support.ArgumentConvertingMethodInvoker;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.quartz.JobMethodInvocationFailedException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MethodInvoker;

public class MethodInvokingJobDetailFactoryBean
extends ArgumentConvertingMethodInvoker
implements FactoryBean<JobDetail>,
BeanNameAware,
BeanClassLoaderAware,
BeanFactoryAware,
InitializingBean {
    @Nullable
    private String name;
    private String group = "DEFAULT";
    private boolean concurrent = true;
    @Nullable
    private String targetBeanName;
    @Nullable
    private String beanName;
    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
    @Nullable
    private BeanFactory beanFactory;
    @Nullable
    private JobDetail jobDetail;

    public void setName(String name) {
        this.name = name;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setConcurrent(boolean concurrent) {
        this.concurrent = concurrent;
    }

    public void setTargetBeanName(String targetBeanName) {
        this.targetBeanName = targetBeanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    protected Class<?> resolveClassName(String className) throws ClassNotFoundException {
        return ClassUtils.forName((String)className, (ClassLoader)this.beanClassLoader);
    }

    public void afterPropertiesSet() throws ClassNotFoundException, NoSuchMethodException {
        this.prepare();
        String name = this.name != null ? this.name : this.beanName;
        Class jobClass = this.concurrent ? MethodInvokingJob.class : StatefulMethodInvokingJob.class;
        JobDetailImpl jdi = new JobDetailImpl();
        jdi.setName(name != null ? name : ((Object)((Object)this)).toString());
        jdi.setGroup(this.group);
        jdi.setJobClass(jobClass);
        jdi.setDurability(true);
        jdi.getJobDataMap().put("methodInvoker", (Object)this);
        this.jobDetail = jdi;
        this.postProcessJobDetail(this.jobDetail);
    }

    protected void postProcessJobDetail(JobDetail jobDetail) {
    }

    public Class<?> getTargetClass() {
        Class targetClass = super.getTargetClass();
        if (targetClass == null && this.targetBeanName != null) {
            Assert.state((this.beanFactory != null ? 1 : 0) != 0, (String)"BeanFactory must be set when using 'targetBeanName'");
            targetClass = this.beanFactory.getType(this.targetBeanName);
        }
        return targetClass;
    }

    public Object getTargetObject() {
        Object targetObject = super.getTargetObject();
        if (targetObject == null && this.targetBeanName != null) {
            Assert.state((this.beanFactory != null ? 1 : 0) != 0, (String)"BeanFactory must be set when using 'targetBeanName'");
            targetObject = this.beanFactory.getBean(this.targetBeanName);
        }
        return targetObject;
    }

    @Nullable
    public JobDetail getObject() {
        return this.jobDetail;
    }

    public Class<? extends JobDetail> getObjectType() {
        return this.jobDetail != null ? this.jobDetail.getClass() : JobDetail.class;
    }

    public boolean isSingleton() {
        return true;
    }

    @PersistJobDataAfterExecution
    @DisallowConcurrentExecution
    public static class StatefulMethodInvokingJob
    extends MethodInvokingJob {
    }

    public static class MethodInvokingJob
    extends QuartzJobBean {
        protected static final Log logger = LogFactory.getLog(MethodInvokingJob.class);
        @Nullable
        private MethodInvoker methodInvoker;

        public void setMethodInvoker(MethodInvoker methodInvoker) {
            this.methodInvoker = methodInvoker;
        }

        @Override
        protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
            Assert.state((this.methodInvoker != null ? 1 : 0) != 0, (String)"No MethodInvoker set");
            try {
                context.setResult(this.methodInvoker.invoke());
            }
            catch (InvocationTargetException ex) {
                if (ex.getTargetException() instanceof JobExecutionException) {
                    throw (JobExecutionException)ex.getTargetException();
                }
                throw new JobMethodInvocationFailedException(this.methodInvoker, ex.getTargetException());
            }
            catch (Exception ex) {
                throw new JobMethodInvocationFailedException(this.methodInvoker, ex);
            }
        }
    }
}

