/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.interceptor.debugging;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.Parameter;
import org.apache.struts2.dispatcher.PrepareOperations;
import org.apache.struts2.dispatcher.RequestMap;
import org.apache.struts2.interceptor.debugging.ObjectToHTMLWriter;
import org.apache.struts2.interceptor.debugging.PrettyPrintWriter;
import org.apache.struts2.views.freemarker.FreemarkerManager;
import org.apache.struts2.views.freemarker.FreemarkerResult;

public class DebuggingInterceptor
extends AbstractInterceptor {
    private static final long serialVersionUID = -3097324155953078783L;
    private static final Logger LOG = LogManager.getLogger(DebuggingInterceptor.class);
    private final String[] ignorePrefixes = new String[]{"org.apache.struts.", "com.opensymphony.xwork2.", "xwork."};
    private final HashSet<String> ignoreKeys = new HashSet<String>(Arrays.asList("application", "session", "parameters", "request"));
    private static final String XML_MODE = "xml";
    private static final String CONSOLE_MODE = "console";
    private static final String COMMAND_MODE = "command";
    private static final String BROWSER_MODE = "browser";
    private static final String SESSION_KEY = "org.apache.struts2.interceptor.debugging.VALUE_STACK";
    private static final String DEBUG_PARAM = "debug";
    private static final String OBJECT_PARAM = "object";
    private static final String EXPRESSION_PARAM = "expression";
    private static final String DECORATE_PARAM = "decorate";
    private boolean enableXmlWithConsole = false;
    private boolean devMode;
    private FreemarkerManager freemarkerManager;
    private boolean consoleEnabled = false;
    private ReflectionProvider reflectionProvider;

    @Inject(value="struts.devMode")
    public void setDevMode(String mode) {
        this.devMode = "true".equals(mode);
    }

    @Inject
    public void setFreemarkerManager(FreemarkerManager mgr) {
        this.freemarkerManager = mgr;
    }

    @Inject
    public void setReflectionProvider(ReflectionProvider reflectionProvider) {
        this.reflectionProvider = reflectionProvider;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String intercept(ActionInvocation inv) throws Exception {
        boolean devMode;
        boolean actionOnly = false;
        boolean cont = true;
        Boolean devModeOverride = PrepareOperations.getDevModeOverride();
        boolean bl = devMode = devModeOverride != null ? devModeOverride : this.devMode;
        if (devMode) {
            final ActionContext ctx = ActionContext.getContext();
            String type = this.getParameter(DEBUG_PARAM);
            ctx.getParameters().remove(DEBUG_PARAM);
            if (XML_MODE.equals(type)) {
                inv.addPreResultListener(new PreResultListener(){

                    @Override
                    public void beforeResult(ActionInvocation inv, String result) {
                        DebuggingInterceptor.this.printContext();
                    }
                });
            } else if (CONSOLE_MODE.equals(type)) {
                this.consoleEnabled = true;
                inv.addPreResultListener(new PreResultListener(){

                    @Override
                    public void beforeResult(ActionInvocation inv, String actionResult) {
                        String xml = "";
                        if (DebuggingInterceptor.this.enableXmlWithConsole) {
                            StringWriter writer = new StringWriter();
                            DebuggingInterceptor.this.printContext(new PrettyPrintWriter(writer));
                            xml = writer.toString();
                            xml = xml.replaceAll("&", "&amp;");
                            xml = xml.replaceAll(">", "&gt;");
                            xml = xml.replaceAll("<", "&lt;");
                        }
                        ActionContext.getContext().put("debugXML", xml);
                        FreemarkerResult result = new FreemarkerResult();
                        result.setFreemarkerManager(DebuggingInterceptor.this.freemarkerManager);
                        result.setContentType("text/html");
                        result.setLocation("/org/apache/struts2/interceptor/debugging/console.ftl");
                        result.setParse(false);
                        try {
                            result.execute(inv);
                        }
                        catch (Exception ex) {
                            LOG.error("Unable to create debugging console", (Throwable)ex);
                        }
                    }
                });
            } else if (COMMAND_MODE.equals(type)) {
                ValueStack stack = (ValueStack)ctx.getSession().get(SESSION_KEY);
                if (stack == null) {
                    stack = ctx.getValueStack();
                    ctx.getSession().put(SESSION_KEY, stack);
                }
                String cmd = this.getParameter(EXPRESSION_PARAM);
                ServletActionContext.getRequest().setAttribute("decorator", (Object)"none");
                HttpServletResponse res = ServletActionContext.getResponse();
                res.setContentType("text/plain");
                try (PrintWriter writer = ServletActionContext.getResponse().getWriter();){
                    writer.print(stack.findValue(cmd));
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
                cont = false;
            } else if (BROWSER_MODE.equals(type)) {
                actionOnly = true;
                inv.addPreResultListener(new PreResultListener(){

                    @Override
                    public void beforeResult(ActionInvocation inv, String actionResult) {
                        String rootObjectExpression = DebuggingInterceptor.this.getParameter(DebuggingInterceptor.OBJECT_PARAM);
                        if (rootObjectExpression == null) {
                            rootObjectExpression = "action";
                        }
                        String decorate = DebuggingInterceptor.this.getParameter(DebuggingInterceptor.DECORATE_PARAM);
                        ValueStack stack = ctx.getValueStack();
                        Object rootObject = stack.findValue(rootObjectExpression);
                        try (StringWriter writer = new StringWriter();){
                            ObjectToHTMLWriter htmlWriter = new ObjectToHTMLWriter(writer);
                            htmlWriter.write(DebuggingInterceptor.this.reflectionProvider, rootObject, rootObjectExpression);
                            String html = writer.toString();
                            writer.close();
                            stack.set("debugHtml", html);
                            if ("false".equals(decorate)) {
                                ServletActionContext.getRequest().setAttribute("decorator", (Object)"none");
                            }
                            FreemarkerResult result = new FreemarkerResult();
                            result.setFreemarkerManager(DebuggingInterceptor.this.freemarkerManager);
                            result.setContentType("text/html");
                            result.setLocation("/org/apache/struts2/interceptor/debugging/browser.ftl");
                            result.execute(inv);
                        }
                        catch (Exception ex) {
                            LOG.error("Unable to create debugging console", (Throwable)ex);
                        }
                    }
                });
            }
        }
        if (cont) {
            try {
                String string;
                if (actionOnly) {
                    inv.invokeActionOnly();
                    string = null;
                    return string;
                }
                string = inv.invoke();
                return string;
            }
            finally {
                if (devMode && this.consoleEnabled) {
                    ActionContext ctx = ActionContext.getContext();
                    ctx.getSession().put(SESSION_KEY, ctx.getValueStack());
                }
            }
        }
        return null;
    }

    private String getParameter(String key) {
        Parameter parameter = ActionContext.getContext().getParameters().get(key);
        return parameter.getValue();
    }

    protected void printContext() {
        HttpServletResponse res = ServletActionContext.getResponse();
        res.setContentType("text/xml");
        try {
            PrettyPrintWriter writer = new PrettyPrintWriter(ServletActionContext.getResponse().getWriter());
            this.printContext(writer);
            writer.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    protected void printContext(PrettyPrintWriter writer) {
        ActionContext ctx = ActionContext.getContext();
        writer.startNode(DEBUG_PARAM);
        this.serializeIt(ctx.getParameters(), "parameters", writer, new ArrayList<Object>());
        writer.startNode("context");
        Map<String, Object> ctxMap = ctx.getContextMap();
        for (String key : ctxMap.keySet()) {
            boolean print = !this.ignoreKeys.contains(key);
            for (String ignorePrefix : this.ignorePrefixes) {
                if (!key.startsWith(ignorePrefix)) continue;
                print = false;
                break;
            }
            if (!print) continue;
            this.serializeIt(ctxMap.get(key), key, writer, new ArrayList<Object>());
        }
        writer.endNode();
        RequestMap requestMap = (RequestMap)ctx.get("request");
        this.serializeIt(requestMap, "request", writer, this.filterValueStack(requestMap));
        this.serializeIt(ctx.getSession(), "session", writer, new ArrayList<Object>());
        ValueStack stack = ctx.getValueStack();
        this.serializeIt(stack.getRoot(), "valueStack", writer, new ArrayList<Object>());
        writer.endNode();
    }

    protected void serializeIt(Object bean, String name, PrettyPrintWriter writer, List<Object> stack) {
        writer.flush();
        if (bean != null && stack.contains(bean)) {
            LOG.info("Circular reference detected, not serializing object: {}", (Object)name);
            return;
        }
        if (bean != null) {
            stack.add(bean);
        }
        if (bean == null) {
            return;
        }
        String clsName = bean.getClass().getName();
        writer.startNode(name);
        if (bean instanceof Collection) {
            Collection col = (Collection)bean;
            for (Object aCol : col) {
                this.serializeIt(aCol, "value", writer, stack);
            }
        } else if (bean instanceof Map) {
            Map map = (Map)bean;
            for (Map.Entry entry : map.entrySet()) {
                Object objValue = entry.getValue();
                this.serializeIt(objValue, entry.getKey().toString(), writer, stack);
            }
        } else if (bean.getClass().isArray()) {
            for (int i = 0; i < Array.getLength(bean); ++i) {
                this.serializeIt(Array.get(bean, i), "arrayitem", writer, stack);
            }
        } else if (clsName.startsWith("java.lang")) {
            writer.setValue(bean.toString());
        } else {
            try {
                PropertyDescriptor[] props;
                BeanInfo info = Introspector.getBeanInfo(bean.getClass());
                for (PropertyDescriptor prop : props = info.getPropertyDescriptors()) {
                    String n = prop.getName();
                    Method m = prop.getReadMethod();
                    if (m == null) continue;
                    this.serializeIt(m.invoke(bean, new Object[0]), n, writer, stack);
                }
            }
            catch (Exception e) {
                LOG.error(e.toString(), (Throwable)e);
            }
        }
        writer.endNode();
        stack.remove(bean);
    }

    public void setEnableXmlWithConsole(boolean enableXmlWithConsole) {
        this.enableXmlWithConsole = enableXmlWithConsole;
    }

    private List<Object> filterValueStack(Map requestMap) {
        ArrayList<Object> filter = new ArrayList<Object>();
        Object valueStack = requestMap.get("struts.valueStack");
        if (valueStack != null) {
            filter.add(valueStack);
        }
        return filter;
    }
}

