/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.validator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.Field;
import org.apache.commons.validator.Validator;
import org.apache.commons.validator.ValidatorException;
import org.apache.commons.validator.ValidatorResults;
import org.apache.commons.validator.util.ValidatorUtils;

public class ValidatorAction
implements Serializable {
    private static final long serialVersionUID = 1339713700053204597L;
    private transient Log log = LogFactory.getLog(ValidatorAction.class);
    private String name = null;
    private String classname = null;
    private Class<?> validationClass = null;
    private String method = null;
    private Method validationMethod = null;
    private String methodParams = "java.lang.Object,org.apache.commons.validator.ValidatorAction,org.apache.commons.validator.Field";
    private Class<?>[] parameterClasses = null;
    private String depends = null;
    private String msg = null;
    private String jsFunctionName = null;
    private String jsFunction = null;
    private String javascript = null;
    private Object instance = null;
    private final List<String> dependencyList = Collections.synchronizedList(new ArrayList());
    private final List<String> methodParameterList = new ArrayList<String>();

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassname() {
        return this.classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getMethodParams() {
        return this.methodParams;
    }

    public void setMethodParams(String methodParams) {
        this.methodParams = methodParams;
        this.methodParameterList.clear();
        StringTokenizer st = new StringTokenizer(methodParams, ",");
        while (st.hasMoreTokens()) {
            String value = st.nextToken().trim();
            if (value == null || value.length() <= 0) continue;
            this.methodParameterList.add(value);
        }
    }

    public String getDepends() {
        return this.depends;
    }

    public void setDepends(String depends) {
        this.depends = depends;
        this.dependencyList.clear();
        StringTokenizer st = new StringTokenizer(depends, ",");
        while (st.hasMoreTokens()) {
            String depend = st.nextToken().trim();
            if (depend == null || depend.length() <= 0) continue;
            this.dependencyList.add(depend);
        }
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getJsFunctionName() {
        return this.jsFunctionName;
    }

    public void setJsFunctionName(String jsFunctionName) {
        this.jsFunctionName = jsFunctionName;
    }

    public void setJsFunction(String jsFunction) {
        if (this.javascript != null) {
            throw new IllegalStateException("Cannot call setJsFunction() after calling setJavascript()");
        }
        this.jsFunction = jsFunction;
    }

    public String getJavascript() {
        return this.javascript;
    }

    public void setJavascript(String javascript) {
        if (this.jsFunction != null) {
            throw new IllegalStateException("Cannot call setJavascript() after calling setJsFunction()");
        }
        this.javascript = javascript;
    }

    protected void init() {
        this.loadJavascriptFunction();
    }

    protected synchronized void loadJavascriptFunction() {
        if (this.javascriptAlreadyLoaded()) {
            return;
        }
        if (this.getLog().isTraceEnabled()) {
            this.getLog().trace((Object)"  Loading function begun");
        }
        if (this.jsFunction == null) {
            this.jsFunction = this.generateJsFunction();
        }
        String javascriptFileName = this.formatJavascriptFileName();
        if (this.getLog().isTraceEnabled()) {
            this.getLog().trace((Object)("  Loading js function '" + javascriptFileName + "'"));
        }
        this.javascript = this.readJavascriptFile(javascriptFileName);
        if (this.getLog().isTraceEnabled()) {
            this.getLog().trace((Object)"  Loading javascript function completed");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String readJavascriptFile(String javascriptFileName) {
        InputStream is;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = this.getClass().getClassLoader();
        }
        if ((is = classLoader.getResourceAsStream(javascriptFileName)) == null) {
            is = this.getClass().getResourceAsStream(javascriptFileName);
        }
        if (is == null) {
            this.getLog().debug((Object)("  Unable to read javascript name " + javascriptFileName));
            return null;
        }
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        try {
            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }
        }
        catch (IOException e) {
            this.getLog().error((Object)"Error reading javascript file.", (Throwable)e);
        }
        finally {
            try {
                reader.close();
            }
            catch (IOException e) {
                this.getLog().error((Object)"Error closing stream to javascript file.", (Throwable)e);
            }
        }
        String function = buffer.toString();
        return function.equals("") ? null : function;
    }

    private String formatJavascriptFileName() {
        String name = this.jsFunction.substring(1);
        if (!this.jsFunction.startsWith("/")) {
            name = this.jsFunction.replace('.', '/') + ".js";
        }
        return name;
    }

    private boolean javascriptAlreadyLoaded() {
        return this.javascript != null;
    }

    private String generateJsFunction() {
        StringBuilder jsName = new StringBuilder("org.apache.commons.validator.javascript");
        jsName.append(".validate");
        jsName.append(this.name.substring(0, 1).toUpperCase());
        jsName.append(this.name.substring(1, this.name.length()));
        return jsName.toString();
    }

    public boolean isDependency(String validatorName) {
        return this.dependencyList.contains(validatorName);
    }

    public List<String> getDependencyList() {
        return Collections.unmodifiableList(this.dependencyList);
    }

    public String toString() {
        StringBuilder results = new StringBuilder("ValidatorAction: ");
        results.append(this.name);
        results.append("\n");
        return results.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    boolean executeValidationMethod(Field field, Map<String, Object> params, ValidatorResults results, int pos) throws ValidatorException {
        params.put("org.apache.commons.validator.ValidatorAction", this);
        try {
            Object result;
            block15: {
                if (this.validationMethod == null) {
                    ValidatorAction validatorAction = this;
                    synchronized (validatorAction) {
                        ClassLoader loader = this.getClassLoader(params);
                        this.loadValidationClass(loader);
                        this.loadParameterClasses(loader);
                        this.loadValidationMethod();
                    }
                }
                Object[] paramValues = this.getParameterValues(params);
                if (field.isIndexed()) {
                    this.handleIndexedField(field, pos, paramValues);
                }
                result = null;
                try {
                    result = this.validationMethod.invoke(this.getValidationClassInstance(), paramValues);
                }
                catch (IllegalArgumentException e) {
                    throw new ValidatorException(e.getMessage());
                }
                catch (IllegalAccessException e) {
                    throw new ValidatorException(e.getMessage());
                }
                catch (InvocationTargetException e) {
                    if (e.getTargetException() instanceof Exception) {
                        throw (Exception)e.getTargetException();
                    }
                    if (!(e.getTargetException() instanceof Error)) break block15;
                    throw (Error)e.getTargetException();
                }
            }
            boolean valid = this.isValid(result);
            if (!valid || valid && !this.onlyReturnErrors(params)) {
                results.add(field, this.name, valid, result);
            }
            if (!valid) {
                return false;
            }
        }
        catch (Exception e) {
            if (e instanceof ValidatorException) {
                throw (ValidatorException)e;
            }
            this.getLog().error((Object)("Unhandled exception thrown during validation: " + e.getMessage()), (Throwable)e);
            results.add(field, this.name, false);
            return false;
        }
        return true;
    }

    private void loadValidationMethod() throws ValidatorException {
        if (this.validationMethod != null) {
            return;
        }
        try {
            this.validationMethod = this.validationClass.getMethod(this.method, this.parameterClasses);
        }
        catch (NoSuchMethodException e) {
            throw new ValidatorException("No such validation method: " + e.getMessage());
        }
    }

    private void loadValidationClass(ClassLoader loader) throws ValidatorException {
        if (this.validationClass != null) {
            return;
        }
        try {
            this.validationClass = loader.loadClass(this.classname);
        }
        catch (ClassNotFoundException e) {
            throw new ValidatorException(e.toString());
        }
    }

    private void loadParameterClasses(ClassLoader loader) throws ValidatorException {
        if (this.parameterClasses != null) {
            return;
        }
        Class[] parameterClasses = new Class[this.methodParameterList.size()];
        for (int i = 0; i < this.methodParameterList.size(); ++i) {
            String paramClassName = this.methodParameterList.get(i);
            try {
                parameterClasses[i] = loader.loadClass(paramClassName);
                continue;
            }
            catch (ClassNotFoundException e) {
                throw new ValidatorException(e.getMessage());
            }
        }
        this.parameterClasses = parameterClasses;
    }

    private Object[] getParameterValues(Map<String, ? super Object> params) {
        Object[] paramValue = new Object[this.methodParameterList.size()];
        for (int i = 0; i < this.methodParameterList.size(); ++i) {
            String paramClassName = this.methodParameterList.get(i);
            paramValue[i] = params.get(paramClassName);
        }
        return paramValue;
    }

    private Object getValidationClassInstance() throws ValidatorException {
        if (Modifier.isStatic(this.validationMethod.getModifiers())) {
            this.instance = null;
        } else if (this.instance == null) {
            try {
                this.instance = this.validationClass.newInstance();
            }
            catch (InstantiationException e) {
                String msg = "Couldn't create instance of " + this.classname + ".  " + e.getMessage();
                throw new ValidatorException(msg);
            }
            catch (IllegalAccessException e) {
                String msg = "Couldn't create instance of " + this.classname + ".  " + e.getMessage();
                throw new ValidatorException(msg);
            }
        }
        return this.instance;
    }

    private void handleIndexedField(Field field, int pos, Object[] paramValues) throws ValidatorException {
        int beanIndex = this.methodParameterList.indexOf("java.lang.Object");
        int fieldIndex = this.methodParameterList.indexOf("org.apache.commons.validator.Field");
        Object[] indexedList = field.getIndexedProperty(paramValues[beanIndex]);
        paramValues[beanIndex] = indexedList[pos];
        Field indexedField = (Field)field.clone();
        indexedField.setKey(ValidatorUtils.replace(indexedField.getKey(), "[]", "[" + pos + "]"));
        paramValues[fieldIndex] = indexedField;
    }

    private boolean isValid(Object result) {
        if (result instanceof Boolean) {
            Boolean valid = (Boolean)result;
            return valid;
        }
        return result != null;
    }

    private ClassLoader getClassLoader(Map<String, Object> params) {
        Validator v = (Validator)params.get("org.apache.commons.validator.Validator");
        return v.getClassLoader();
    }

    private boolean onlyReturnErrors(Map<String, Object> params) {
        Validator v = (Validator)params.get("org.apache.commons.validator.Validator");
        return v.getOnlyReturnErrors();
    }

    private Log getLog() {
        if (this.log == null) {
            this.log = LogFactory.getLog(ValidatorAction.class);
        }
        return this.log;
    }
}

