/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletRequestWrapper
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpServletResponseWrapper
 */
package freemarker.ext.servlet;

import freemarker.core.Environment;
import freemarker.core._DelayedFTLTypeDescription;
import freemarker.core._MiscTemplateException;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.utility.DeepUnwrap;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class IncludePage
implements TemplateDirectiveModel {
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    public IncludePage(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
        Object wrappedRequest;
        boolean inheritParams;
        Object wrappedResponse;
        TemplateModel path = (TemplateModel)params.get("path");
        if (path == null) {
            throw new _MiscTemplateException(env, "Missing required parameter \"path\"");
        }
        if (!(path instanceof TemplateScalarModel)) {
            throw new _MiscTemplateException(env, "Expected a scalar model. \"path\" is instead ", new _DelayedFTLTypeDescription(path));
        }
        String strPath = ((TemplateScalarModel)path).getAsString();
        if (strPath == null) {
            throw new _MiscTemplateException(env, "String value of \"path\" parameter is null");
        }
        Writer envOut = env.getOut();
        if (envOut == this.response.getWriter()) {
            wrappedResponse = this.response;
        } else {
            final PrintWriter printWriter = envOut instanceof PrintWriter ? (PrintWriter)envOut : new PrintWriter(envOut);
            wrappedResponse = new HttpServletResponseWrapper(this.response){

                public PrintWriter getWriter() {
                    return printWriter;
                }
            };
        }
        TemplateModel inheritParamsModel = (TemplateModel)params.get("inherit_params");
        if (inheritParamsModel == null) {
            inheritParams = true;
        } else {
            if (!(inheritParamsModel instanceof TemplateBooleanModel)) {
                throw new _MiscTemplateException(env, "\"inherit_params\" should be a boolean but it's a(n) ", inheritParamsModel.getClass().getName(), " instead");
            }
            inheritParams = ((TemplateBooleanModel)inheritParamsModel).getAsBoolean();
        }
        TemplateModel paramsModel = (TemplateModel)params.get("params");
        if (paramsModel == null && inheritParams) {
            wrappedRequest = this.request;
        } else {
            Map paramsMap;
            if (paramsModel != null) {
                Object unwrapped = DeepUnwrap.unwrap(paramsModel);
                if (!(unwrapped instanceof Map)) {
                    throw new _MiscTemplateException(env, "Expected \"params\" to unwrap into a java.util.Map. It unwrapped into ", unwrapped.getClass().getName(), " instead.");
                }
                paramsMap = (Map)unwrapped;
            } else {
                paramsMap = Collections.EMPTY_MAP;
            }
            wrappedRequest = new CustomParamsRequest(this.request, paramsMap, inheritParams);
        }
        try {
            this.request.getRequestDispatcher(strPath).include((ServletRequest)wrappedRequest, (ServletResponse)wrappedResponse);
        }
        catch (ServletException e) {
            throw new _MiscTemplateException((Throwable)e, env);
        }
    }

    private static final class CustomParamsRequest
    extends HttpServletRequestWrapper {
        private final HashMap paramsMap;

        private CustomParamsRequest(HttpServletRequest request, Map paramMap, boolean inheritParams) {
            super(request);
            this.paramsMap = inheritParams ? new HashMap(request.getParameterMap()) : new HashMap();
            for (Map.Entry entry : paramMap.entrySet()) {
                int el;
                int i;
                String[] valueArray;
                String name = String.valueOf(entry.getKey());
                Object value = entry.getValue();
                if (value == null) {
                    valueArray = new String[]{null};
                } else if (value instanceof String[]) {
                    valueArray = (String[])value;
                } else if (value instanceof Collection) {
                    Collection col = (Collection)value;
                    valueArray = new String[col.size()];
                    i = 0;
                    Iterator it2 = col.iterator();
                    while (it2.hasNext()) {
                        valueArray[i++] = String.valueOf(it2.next());
                    }
                } else if (value.getClass().isArray()) {
                    int len = Array.getLength(value);
                    valueArray = new String[len];
                    for (i = 0; i < len; ++i) {
                        valueArray[i] = String.valueOf(Array.get(value, i));
                    }
                } else {
                    valueArray = new String[]{String.valueOf(value)};
                }
                String[] existingParams = (String[])this.paramsMap.get(name);
                int n = el = existingParams == null ? 0 : existingParams.length;
                if (el == 0) {
                    this.paramsMap.put(name, valueArray);
                    continue;
                }
                int vl = valueArray.length;
                if (vl <= 0) continue;
                String[] newValueArray = new String[el + vl];
                System.arraycopy(valueArray, 0, newValueArray, 0, vl);
                System.arraycopy(existingParams, 0, newValueArray, vl, el);
                this.paramsMap.put(name, newValueArray);
            }
        }

        public String[] getParameterValues(String name) {
            String[] value = (String[])this.paramsMap.get(name);
            return value != null ? (String[])value.clone() : null;
        }

        public String getParameter(String name) {
            String[] values = (String[])this.paramsMap.get(name);
            return values != null && values.length > 0 ? values[0] : null;
        }

        public Enumeration getParameterNames() {
            return Collections.enumeration(this.paramsMap.keySet());
        }

        public Map getParameterMap() {
            HashMap clone = (HashMap)this.paramsMap.clone();
            for (Map.Entry entry : clone.entrySet()) {
                entry.setValue(((String[])entry.getValue()).clone());
            }
            return Collections.unmodifiableMap(clone);
        }
    }
}

