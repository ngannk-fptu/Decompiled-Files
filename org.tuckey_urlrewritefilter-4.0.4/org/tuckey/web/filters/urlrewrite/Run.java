/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.tuckey.web.filters.urlrewrite;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.tuckey.web.filters.urlrewrite.ConditionMatch;
import org.tuckey.web.filters.urlrewrite.RunConfig;
import org.tuckey.web.filters.urlrewrite.extend.RewriteMatch;
import org.tuckey.web.filters.urlrewrite.json.JsonRewriteMatch;
import org.tuckey.web.filters.urlrewrite.utils.Log;
import org.tuckey.web.filters.urlrewrite.utils.StringMatchingMatcher;
import org.tuckey.web.filters.urlrewrite.utils.StringUtils;
import org.tuckey.web.filters.urlrewrite.utils.TypeUtils;

public class Run {
    private static Log log = Log.getLog(Run.class);
    private boolean newEachTime = false;
    private boolean jsonHandler = false;
    private String classStr;
    private static final String DEAULT_METHOD_STR = "run";
    private String methodStr = "run";
    private int id = 0;
    private String error = null;
    private boolean valid = false;
    private boolean initialised = false;
    private Object runClassInstance;
    private Constructor runConstructor;
    private Method initMethod;
    private Method filterInitMethod;
    private Method runMethod;
    private Class[] runMethodParams;
    private String[] runMethodParamNames;
    private boolean runMethodUseDefaultParams = true;
    private Method destroyMethod;
    private RunConfig runServletConfig;
    private Hashtable initParams = new Hashtable();
    private static boolean loadClass = true;
    private static Class[][] runMethodPossibleSignatures = new Class[][]{{ServletRequest.class, ServletResponse.class}, {ServletRequest.class}, {ServletResponse.class}, {HttpServletRequest.class, HttpServletResponse.class}, {HttpServletRequest.class}, {HttpServletResponse.class}};
    private boolean filter = false;

    public boolean initialise(ServletContext context) {
        return this.initialise(context, null);
    }

    public boolean initialise(ServletContext context, Class extraParam) {
        log.debug("initialising run");
        this.runServletConfig = new RunConfig(context, this.initParams);
        this.initialised = true;
        this.valid = false;
        if (StringUtils.isBlank(this.classStr)) {
            this.setError("cannot initialise run " + this.id + " value is empty");
            return this.valid;
        }
        if (this.methodStr == null) {
            this.methodStr = DEAULT_METHOD_STR;
        }
        log.debug("methodStr: " + this.methodStr);
        String rawMethodStr = this.methodStr;
        int bkStart = rawMethodStr.indexOf(40);
        int bkEnd = rawMethodStr.indexOf(41);
        if (bkStart != -1 && bkEnd != -1 && bkEnd - bkStart > 0) {
            this.runMethodUseDefaultParams = false;
            this.methodStr = rawMethodStr.substring(0, bkStart);
            String paramsList = rawMethodStr.substring(bkStart + 1, bkEnd);
            if ((paramsList = StringUtils.trimToNull(paramsList)) != null) {
                String[] params = paramsList.split(",");
                Class[] paramClasses = new Class[params.length];
                String[] paramNames = new String[params.length];
                for (int i = 0; i < params.length; ++i) {
                    Class clazz;
                    String param = StringUtils.trimToNull(params[i]);
                    if (param == null) continue;
                    if (param.contains(" ")) {
                        String paramName = StringUtils.trimToNull(param.substring(param.indexOf(" ")));
                        if (paramName != null) {
                            log.debug("param name: " + paramName);
                            paramNames[i] = paramName;
                        }
                        param = param.substring(0, param.indexOf(32));
                    }
                    if ((clazz = this.parseClass(param)) == null) {
                        return this.valid;
                    }
                    paramClasses[i] = clazz;
                }
                this.runMethodParams = paramClasses;
                this.runMethodParamNames = paramNames;
            }
        }
        if (loadClass) {
            this.prepareRunObject(extraParam);
        } else {
            this.valid = true;
        }
        return this.valid;
    }

    private Class parseClass(String param) {
        Class paramClass = TypeUtils.findClass(param);
        if ("javax.servlet.FilterChain".equalsIgnoreCase(param) || "FilterChain".equalsIgnoreCase(param) || "chain".equalsIgnoreCase(param)) {
            this.filter = true;
            paramClass = FilterChain.class;
        }
        if (loadClass) {
            if (paramClass == null) {
                try {
                    paramClass = Class.forName(param);
                }
                catch (ClassNotFoundException e) {
                    this.setError("could not find " + param + " got a " + e.toString(), e);
                    return null;
                }
                catch (NoClassDefFoundError e) {
                    this.setError("could not find " + param + " got a " + e.toString(), e);
                    return null;
                }
            }
            if (paramClass == null) {
                this.setError("could not find class of type " + param);
                return null;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("parseClass found class " + paramClass + " for " + param);
        }
        return paramClass;
    }

    private void prepareRunObject(Class extraParam) {
        Class<?> runClass;
        if (log.isDebugEnabled()) {
            log.debug("looking for class " + this.classStr);
        }
        try {
            runClass = Class.forName(this.classStr);
        }
        catch (ClassNotFoundException e) {
            this.setError("could not find " + this.classStr + " got a " + e.toString(), e);
            return;
        }
        catch (NoClassDefFoundError e) {
            this.setError("could not find " + this.classStr + " got a " + e.toString(), e);
            return;
        }
        try {
            this.runConstructor = runClass.getConstructor(null);
        }
        catch (NoSuchMethodException e) {
            this.setError("could not get constructor for " + this.classStr, e);
            return;
        }
        if (!this.runMethodUseDefaultParams) {
            if (log.isDebugEnabled()) {
                log.debug("looking for " + this.methodStr + " with specific params");
            }
            try {
                this.runMethod = runClass.getMethod(this.methodStr, this.runMethodParams);
            }
            catch (NoSuchMethodException e) {
                if (log.isDebugEnabled()) {
                    log.debug(this.methodStr + " not found");
                }
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("looking for " + this.methodStr + "(ServletRequest, ServletResponse)");
            }
            for (int i = 0; i < runMethodPossibleSignatures.length; ++i) {
                Class[] runMethodPossibleSignature = runMethodPossibleSignatures[i];
                if (extraParam != null) {
                    if (runMethodPossibleSignature.length == 2) {
                        runMethodPossibleSignature = new Class[]{runMethodPossibleSignature[0], runMethodPossibleSignature[1], extraParam};
                    }
                    if (runMethodPossibleSignature.length == 1) {
                        runMethodPossibleSignature = new Class[]{runMethodPossibleSignature[0], extraParam};
                    }
                }
                if (log.isDebugEnabled()) {
                    StringBuffer possible = new StringBuffer();
                    for (int j = 0; j < runMethodPossibleSignature.length; ++j) {
                        if (j > 0) {
                            possible.append(",");
                        }
                        possible.append(runMethodPossibleSignature[j].getName());
                    }
                    log.debug("looking for " + this.methodStr + "(" + possible + ")");
                }
                try {
                    this.runMethod = runClass.getMethod(this.methodStr, runMethodPossibleSignature);
                    this.runMethodParams = runMethodPossibleSignature;
                    break;
                }
                catch (NoSuchMethodException e) {
                    this.runMethodParams = null;
                    continue;
                }
            }
            if (this.runMethod == null) {
                this.setError("could not find method with the name " + this.methodStr + " on " + this.classStr);
                return;
            }
        }
        Method[] methods = runClass.getMethods();
        for (int i = 0; i < methods.length; ++i) {
            Method method = methods[i];
            if ("destroy".equals(method.getName()) && method.getParameterTypes().length == 0) {
                log.debug("found destroy methodStr");
                this.destroyMethod = method;
            }
            if ("init".equals(method.getName()) && method.getParameterTypes().length == 1 && ServletConfig.class.getName().equals(method.getParameterTypes()[0].getName())) {
                log.debug("found init methodStr");
                this.initMethod = method;
            }
            if ("init".equals(method.getName()) && method.getParameterTypes().length == 1 && FilterConfig.class.getName().equals(method.getParameterTypes()[0].getName())) {
                log.debug("found filter init methodStr");
                this.filterInitMethod = method;
            }
            if (this.initMethod != null && this.destroyMethod != null) break;
        }
        if (!this.newEachTime) {
            this.runClassInstance = this.fetchNewInstance();
        }
        this.valid = true;
    }

    private void invokeDestroy(Object runClassInstanceToDestroy) {
        if (runClassInstanceToDestroy != null && this.destroyMethod != null) {
            if (log.isDebugEnabled()) {
                log.debug("running " + this.classStr + ".destroy()");
            }
            try {
                this.destroyMethod.invoke(runClassInstanceToDestroy, (Object[])null);
            }
            catch (IllegalAccessException e) {
                this.logInvokeException("destroy()", e);
            }
            catch (InvocationTargetException e) {
                this.logInvokeException("destroy()", e);
            }
        }
    }

    private RewriteMatch invokeRunMethod(Object classInstanceToRun, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain chain, Object[] matchObjs) throws ServletException, InvocationTargetException {
        if (log.isDebugEnabled()) {
            log.debug("running " + this.classStr + "." + this.getMethodSignature() + " ");
        }
        if (classInstanceToRun == null || this.runMethod == null) {
            return null;
        }
        RewriteMatch returned = null;
        Object[] params = null;
        if (this.runMethodParams != null && this.runMethodParams.length > 0) {
            params = new Object[this.runMethodParams.length];
            int paramMatchCounter = 0;
            for (int i = 0; i < this.runMethodParams.length; ++i) {
                Object param;
                Object matchObj;
                Class runMethodParam = this.runMethodParams[i];
                String runMethodParamName = null;
                if (this.runMethodParamNames != null && this.runMethodParamNames.length > i) {
                    runMethodParamName = this.runMethodParamNames[i];
                }
                if (runMethodParamName != null) {
                    log.debug("need parameter from request called " + runMethodParamName);
                    matchObj = httpServletRequest.getParameter(runMethodParamName);
                    param = TypeUtils.getConvertedParam(runMethodParam, matchObj);
                } else if (runMethodParam.isAssignableFrom(HttpServletRequest.class)) {
                    param = httpServletRequest;
                } else if (runMethodParam.isAssignableFrom(HttpServletResponse.class)) {
                    param = httpServletResponse;
                } else if (runMethodParam.isAssignableFrom(FilterChain.class)) {
                    param = chain;
                } else {
                    matchObj = null;
                    if (matchObjs != null && matchObjs.length > paramMatchCounter) {
                        matchObj = matchObjs[paramMatchCounter];
                    }
                    param = TypeUtils.getConvertedParam(runMethodParam, matchObj);
                    ++paramMatchCounter;
                }
                params[i] = param;
                if (!log.isDebugEnabled()) continue;
                log.debug("argument " + i + " (" + runMethodParam.getName() + "): " + param);
            }
        }
        try {
            Object objReturned = this.runMethod.invoke(classInstanceToRun, params);
            if (this.jsonHandler) {
                returned = new JsonRewriteMatch(objReturned);
            } else if (objReturned != null && objReturned instanceof RewriteMatch) {
                returned = (RewriteMatch)objReturned;
            }
        }
        catch (IllegalAccessException e) {
            if (log.isDebugEnabled()) {
                log.debug(e);
            }
            throw new ServletException((Throwable)e);
        }
        return returned;
    }

    public void destroy() {
        this.initialised = false;
        this.valid = false;
        this.invokeDestroy(this.runClassInstance);
        this.destroyMethod = null;
        this.runMethod = null;
        this.initMethod = null;
        this.filterInitMethod = null;
        this.runServletConfig = null;
        this.runConstructor = null;
        this.runClassInstance = null;
        this.methodStr = null;
        this.classStr = null;
        this.error = null;
    }

    public RewriteMatch execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException, InvocationTargetException {
        return this.execute(httpServletRequest, httpServletResponse, null, null);
    }

    public RewriteMatch execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, StringMatchingMatcher matcher, ConditionMatch conditionMatch, FilterChain chain) throws IOException, ServletException, InvocationTargetException {
        StringMatchingMatcher condMatcher;
        int matches = 0;
        int condMatches = 0;
        if (matcher != null && matcher.isFound()) {
            matches = matcher.groupCount();
        }
        if (conditionMatch != null && (condMatcher = conditionMatch.getMatcher()) != null && condMatcher.isFound()) {
            condMatches = condMatcher.groupCount();
        }
        Object[] allMatches = null;
        if (matches + condMatches > 0) {
            int i;
            allMatches = new String[matches + condMatches];
            if (matcher != null && matches > 0) {
                for (i = 0; i < matches; ++i) {
                    allMatches[i] = matcher.group(i + 1);
                }
            }
            if (conditionMatch != null && condMatches > 0) {
                for (i = 0; i < condMatches; ++i) {
                    allMatches[i] = conditionMatch.getMatcher().group(i);
                }
            }
        }
        return this.execute(httpServletRequest, httpServletResponse, allMatches, chain);
    }

    public RewriteMatch execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Throwable throwable) throws IOException, ServletException, InvocationTargetException {
        Object[] params = new Object[]{throwable};
        return this.execute(httpServletRequest, httpServletResponse, params, null);
    }

    public RewriteMatch execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object[] params) throws IOException, ServletException, InvocationTargetException {
        return this.execute(httpServletRequest, httpServletResponse, params, null);
    }

    public RewriteMatch execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object[] params, FilterChain chain) throws IOException, ServletException, InvocationTargetException {
        RewriteMatch returned;
        if (!this.initialised) {
            log.debug("not initialised skipping");
            return null;
        }
        if (!this.valid) {
            log.debug("not valid skipping");
            return null;
        }
        try {
            if (this.newEachTime) {
                Object newRunClassInstance = this.fetchNewInstance();
                returned = this.invokeRunMethod(newRunClassInstance, httpServletRequest, httpServletResponse, chain, params);
                this.invokeDestroy(newRunClassInstance);
            } else {
                returned = this.invokeRunMethod(this.runClassInstance, httpServletRequest, httpServletResponse, chain, params);
            }
        }
        catch (ServletException e) {
            httpServletRequest.setAttribute("javax.servlet.error.exception", (Object)e);
            throw e;
        }
        return returned;
    }

    private void logInvokeException(String methodStr, Exception e) {
        Throwable cause = e.getCause();
        if (cause == null) {
            this.setError("when invoking " + methodStr + " on " + this.classStr + " got an " + e.toString(), e);
        } else {
            this.setError("when invoking " + methodStr + " on " + this.classStr + " got an " + e.toString() + " caused by " + cause.toString(), cause);
        }
    }

    private Object fetchNewInstance() {
        Object[] args;
        Object obj;
        log.debug("getting new instance of " + this.classStr);
        try {
            obj = this.runConstructor.newInstance(null);
        }
        catch (InstantiationException e) {
            this.logInvokeException("constructor", e);
            return null;
        }
        catch (IllegalAccessException e) {
            this.logInvokeException("constructor", e);
            return null;
        }
        catch (InvocationTargetException e) {
            this.logInvokeException("constructor", e);
            return null;
        }
        if (this.initMethod != null) {
            log.debug("about to run init(ServletConfig) on " + this.classStr);
            args = new Object[]{this.runServletConfig};
            try {
                this.initMethod.invoke(obj, args);
            }
            catch (IllegalAccessException e) {
                this.logInvokeException("init(ServletConfig)", e);
                return null;
            }
            catch (InvocationTargetException e) {
                this.logInvokeException("init(ServletConfig)", e);
                return null;
            }
        }
        if (this.filterInitMethod != null) {
            log.debug("about to run init(FilterConfig) on " + this.classStr);
            args = new Object[]{this.runServletConfig};
            try {
                this.filterInitMethod.invoke(obj, args);
            }
            catch (IllegalAccessException e) {
                this.logInvokeException("init(FilterConfig)", e);
                return null;
            }
            catch (InvocationTargetException e) {
                this.logInvokeException("init(FilterConfig)", e);
                return null;
            }
        }
        return obj;
    }

    public String getError() {
        return this.error;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public boolean isValid() {
        return this.valid;
    }

    public boolean isInitialised() {
        return this.initialised;
    }

    public String getClassStr() {
        return this.classStr;
    }

    public String getMethodStr() {
        return this.methodStr;
    }

    public String getMethodSignature() {
        return TypeUtils.getMethodSignature(this.methodStr, this.runMethodParams);
    }

    public boolean isNewEachTime() {
        return this.newEachTime;
    }

    public void setNewEachTime(boolean newEachTime) {
        this.newEachTime = newEachTime;
    }

    public Object getRunClassInstance() {
        return this.runClassInstance;
    }

    public void addInitParam(String name, String value) {
        if (name != null) {
            this.initParams.put(name, value);
        }
    }

    public String getInitParam(String paramName) {
        return (String)this.initParams.get(paramName);
    }

    public void setClassStr(String classStr) {
        this.classStr = classStr;
    }

    public void setMethodStr(String methodStr) {
        this.methodStr = methodStr;
    }

    public static void setLoadClass(boolean loadClass) {
        Run.loadClass = loadClass;
    }

    public void setError(String error, Throwable t) {
        this.error = error;
        log.error(error, t);
    }

    public void setError(String error) {
        this.error = error;
        log.error(error);
    }

    public String getDisplayName() {
        return "Run " + this.id;
    }

    public boolean isFilter() {
        return this.filter;
    }

    public void setJsonHandler(boolean jsonHandler) {
        this.jsonHandler = jsonHandler;
    }
}

