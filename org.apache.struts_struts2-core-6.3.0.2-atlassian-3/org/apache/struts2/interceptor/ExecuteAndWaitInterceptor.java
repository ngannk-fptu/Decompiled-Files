/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpSession
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.exec.BackgroundProcess;
import org.apache.struts2.interceptor.exec.ExecutorProvider;
import org.apache.struts2.interceptor.exec.StrutsBackgroundProcess;
import org.apache.struts2.interceptor.exec.StrutsExecutorProvider;
import org.apache.struts2.util.TokenHelper;
import org.apache.struts2.views.freemarker.FreemarkerResult;

public class ExecuteAndWaitInterceptor
extends MethodFilterInterceptor {
    private static final long serialVersionUID = -2754639196749652512L;
    private static final Logger LOG = LogManager.getLogger(ExecuteAndWaitInterceptor.class);
    public static final String KEY = "__execWait";
    public static final String WAIT = "wait";
    protected int delay;
    protected int delaySleepInterval = 100;
    protected boolean executeAfterValidationPass = false;
    private int threadPriority = 5;
    private Container container;
    private ExecutorProvider executor;

    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

    @Inject
    public void setExecutorProvider(ExecutorProvider executorProvider) {
        this.executor = executorProvider;
    }

    protected BackgroundProcess getNewBackgroundProcess(String name, ActionInvocation actionInvocation, int threadPriority) {
        return new StrutsBackgroundProcess(actionInvocation, name + "_background-process", threadPriority);
    }

    protected String getBackgroundProcessName(ActionProxy proxy) {
        return proxy.getActionName();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected String doIntercept(ActionInvocation actionInvocation) throws Exception {
        HttpSession httpSession;
        ActionProxy proxy = actionInvocation.getProxy();
        String name = this.getBackgroundProcessName(proxy);
        ActionContext context = actionInvocation.getInvocationContext();
        Map<String, Object> session = context.getSession();
        HttpSession httpSession2 = httpSession = ServletActionContext.getRequest().getSession(true);
        synchronized (httpSession2) {
            Boolean secondTime = true;
            if (this.executeAfterValidationPass) {
                secondTime = (Boolean)context.get(KEY);
                if (secondTime == null) {
                    context.put(KEY, true);
                    secondTime = false;
                } else {
                    secondTime = true;
                    context.put(KEY, null);
                }
            }
            String bp_SessionKey = KEY + name;
            BackgroundProcess bp = (BackgroundProcess)session.get(bp_SessionKey);
            LOG.debug("Intercepting invocation for BackgroundProcess - session key: {}, value: {}", (Object)bp_SessionKey, (Object)bp);
            if (bp != null && bp.getInvocation() == null) {
                LOG.trace("BackgroundProcess invocation is null (remove key, clear instance)");
                session.remove(bp_SessionKey);
                bp = null;
            }
            if ((!this.executeAfterValidationPass || secondTime.booleanValue()) && bp == null) {
                LOG.trace("BackgroundProcess instance is null (create new instance) - executeAfterValidationPass: {}, secondTime: {}.", (Object)this.executeAfterValidationPass, (Object)secondTime);
                bp = this.getNewBackgroundProcess(name, actionInvocation, this.threadPriority).prepare();
                session.put(bp_SessionKey, bp);
                if (this.executor == null || this.executor.isShutdown()) {
                    LOG.warn("Executor is shutting down (or null), cannot execute a new process, invoke next ActionInvocation step and return.");
                    return actionInvocation.invoke();
                }
                this.executor.execute(bp);
                this.performInitialDelay(bp);
                secondTime = false;
            }
            if (!(this.executeAfterValidationPass && secondTime.booleanValue() || bp == null || bp.isDone())) {
                Map<String, ResultConfig> results;
                LOG.trace("BackgroundProcess instance is not done (wait processing) - executeAfterValidationPass: {}, secondTime: {}.", (Object)this.executeAfterValidationPass, (Object)secondTime);
                actionInvocation.getStack().push(bp.getAction());
                String token = TokenHelper.getToken();
                if (token != null) {
                    TokenHelper.setSessionToken(TokenHelper.getTokenName(), token);
                }
                if (!(results = proxy.getConfig().getResults()).containsKey(WAIT)) {
                    LOG.warn("ExecuteAndWait interceptor has detected that no result named 'wait' is available. Defaulting to a plain built-in wait page. It is highly recommend you provide an action-specific or global result named '{}'.", (Object)WAIT);
                    FreemarkerResult waitResult = new FreemarkerResult();
                    this.container.inject(waitResult);
                    waitResult.setLocation("/org/apache/struts2/interceptor/wait.ftl");
                    waitResult.execute(actionInvocation);
                    return "none";
                }
                return WAIT;
            }
            if (!(this.executeAfterValidationPass && secondTime.booleanValue() || bp == null || !bp.isDone())) {
                LOG.trace("BackgroundProcess instance is done (remove key, return result) - executeAfterValidationPass: {}, secondTime: {}.", (Object)this.executeAfterValidationPass, (Object)secondTime);
                session.remove(bp_SessionKey);
                actionInvocation.getStack().push(bp.getAction());
                if (bp.getException() != null) {
                    throw bp.getException();
                }
                return bp.getResult();
            }
            LOG.trace("BackgroundProcess state fall-through (first instance, pass through), invoke next ActionInvocation step and return - executeAfterValidationPass: {}, secondTime: {}.", (Object)this.executeAfterValidationPass, (Object)secondTime);
            return actionInvocation.invoke();
        }
    }

    protected void performInitialDelay(BackgroundProcess bp) throws InterruptedException {
        int step;
        if (this.delay <= 0 || this.delaySleepInterval <= 0) {
            return;
        }
        int steps = this.delay / this.delaySleepInterval;
        LOG.debug("Delaying for {} millis. (using {} steps)", (Object)this.delay, (Object)steps);
        for (step = 0; step < steps && !bp.isDone(); ++step) {
            Thread.sleep(this.delaySleepInterval);
        }
        LOG.debug("Sleeping ended after {} steps and the background process is {}", (Object)step, (Object)(bp.isDone() ? " done" : " not done"));
    }

    public void setThreadPriority(int threadPriority) {
        this.threadPriority = threadPriority;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void setDelaySleepInterval(int delaySleepInterval) {
        this.delaySleepInterval = delaySleepInterval;
    }

    public void setExecuteAfterValidationPass(boolean executeAfterValidationPass) {
        this.executeAfterValidationPass = executeAfterValidationPass;
    }

    @Override
    public void init() {
        super.init();
        if (this.executor == null) {
            LOG.debug("Using: {} as ExecutorProvider", (Object)StrutsExecutorProvider.class.getSimpleName());
            this.executor = this.container.getInstance(StrutsExecutorProvider.class);
        }
    }

    @Override
    public void destroy() {
        try {
            this.executor.shutdown();
        }
        finally {
            super.destroy();
        }
    }
}

