/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.result;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.ValueStack;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpHeaderResult
implements Result {
    private static final long serialVersionUID = 195648957144219214L;
    private static final Logger LOG = LogManager.getLogger(HttpHeaderResult.class);
    public static final String DEFAULT_PARAM = null;
    private boolean parse = true;
    private Map<String, String> headers = new HashMap<String, String>();
    private int status = -1;
    private String error = null;
    private String errorMessage;

    public HttpHeaderResult() {
    }

    public HttpHeaderResult(int status) {
        this();
        this.status = status;
        this.parse = false;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public void setParse(boolean parse) {
        this.parse = parse;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }

    public void addHeader(String name, String value) {
        this.headers.put(name, value);
    }

    @Override
    public void execute(ActionInvocation invocation) throws Exception {
        if (invocation == null) {
            throw new IllegalArgumentException("Invocation cannot be null!");
        }
        HttpServletResponse response = invocation.getInvocationContext().getServletResponse();
        ValueStack stack = invocation.getStack();
        if (this.status != -1) {
            response.setStatus(this.status);
        }
        if (this.headers != null) {
            for (Map.Entry<String, String> entry : this.headers.entrySet()) {
                String value = entry.getValue();
                String finalValue = this.parse ? TextParseUtil.translateVariables(value, stack) : value;
                response.addHeader(entry.getKey(), finalValue);
            }
        }
        if (this.status == -1 && this.error != null) {
            int errorCode = -1;
            try {
                errorCode = Integer.parseInt(this.parse ? TextParseUtil.translateVariables(this.error, stack) : this.error);
            }
            catch (Exception e) {
                LOG.error("Cannot parse errorCode [{}] value as Integer!", (Object)this.error, (Object)e);
            }
            if (errorCode != -1) {
                if (this.errorMessage != null) {
                    String finalMessage = this.parse ? TextParseUtil.translateVariables(this.errorMessage, stack) : this.errorMessage;
                    response.sendError(errorCode, finalMessage);
                } else {
                    response.sendError(errorCode);
                }
            }
        }
    }
}

