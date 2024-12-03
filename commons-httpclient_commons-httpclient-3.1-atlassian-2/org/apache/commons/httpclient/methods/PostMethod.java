/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.httpclient.methods;

import java.util.Iterator;
import java.util.Vector;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PostMethod
extends EntityEnclosingMethod {
    private static final Log LOG = LogFactory.getLog(PostMethod.class);
    public static final String FORM_URL_ENCODED_CONTENT_TYPE = "application/x-www-form-urlencoded";
    private Vector params = new Vector();

    public PostMethod() {
    }

    public PostMethod(String uri) {
        super(uri);
    }

    @Override
    public String getName() {
        return "POST";
    }

    @Override
    protected boolean hasRequestContent() {
        LOG.trace((Object)"enter PostMethod.hasRequestContent()");
        if (!this.params.isEmpty()) {
            return true;
        }
        return super.hasRequestContent();
    }

    @Override
    protected void clearRequestBody() {
        LOG.trace((Object)"enter PostMethod.clearRequestBody()");
        this.params.clear();
        super.clearRequestBody();
    }

    @Override
    protected RequestEntity generateRequestEntity() {
        if (!this.params.isEmpty()) {
            String content = EncodingUtil.formUrlEncode(this.getParameters(), this.getRequestCharSet());
            ByteArrayRequestEntity entity = new ByteArrayRequestEntity(EncodingUtil.getAsciiBytes(content), FORM_URL_ENCODED_CONTENT_TYPE);
            return entity;
        }
        return super.generateRequestEntity();
    }

    public void setParameter(String parameterName, String parameterValue) {
        LOG.trace((Object)"enter PostMethod.setParameter(String, String)");
        this.removeParameter(parameterName);
        this.addParameter(parameterName, parameterValue);
    }

    public NameValuePair getParameter(String paramName) {
        LOG.trace((Object)"enter PostMethod.getParameter(String)");
        if (paramName == null) {
            return null;
        }
        for (NameValuePair parameter : this.params) {
            if (!paramName.equals(parameter.getName())) continue;
            return parameter;
        }
        return null;
    }

    public NameValuePair[] getParameters() {
        LOG.trace((Object)"enter PostMethod.getParameters()");
        int numPairs = this.params.size();
        Object[] objectArr = this.params.toArray();
        NameValuePair[] nvPairArr = new NameValuePair[numPairs];
        for (int i = 0; i < numPairs; ++i) {
            nvPairArr[i] = (NameValuePair)objectArr[i];
        }
        return nvPairArr;
    }

    public void addParameter(String paramName, String paramValue) throws IllegalArgumentException {
        LOG.trace((Object)"enter PostMethod.addParameter(String, String)");
        if (paramName == null || paramValue == null) {
            throw new IllegalArgumentException("Arguments to addParameter(String, String) cannot be null");
        }
        super.clearRequestBody();
        this.params.add(new NameValuePair(paramName, paramValue));
    }

    public void addParameter(NameValuePair param) throws IllegalArgumentException {
        LOG.trace((Object)"enter PostMethod.addParameter(NameValuePair)");
        if (param == null) {
            throw new IllegalArgumentException("NameValuePair may not be null");
        }
        this.addParameter(param.getName(), param.getValue());
    }

    public void addParameters(NameValuePair[] parameters) {
        LOG.trace((Object)"enter PostMethod.addParameters(NameValuePair[])");
        if (parameters == null) {
            LOG.warn((Object)"Attempt to addParameters(null) ignored");
        } else {
            super.clearRequestBody();
            for (int i = 0; i < parameters.length; ++i) {
                this.params.add(parameters[i]);
            }
        }
    }

    public boolean removeParameter(String paramName) throws IllegalArgumentException {
        LOG.trace((Object)"enter PostMethod.removeParameter(String)");
        if (paramName == null) {
            throw new IllegalArgumentException("Argument passed to removeParameter(String) cannot be null");
        }
        boolean removed = false;
        Iterator iter = this.params.iterator();
        while (iter.hasNext()) {
            NameValuePair pair = (NameValuePair)iter.next();
            if (!paramName.equals(pair.getName())) continue;
            iter.remove();
            removed = true;
        }
        return removed;
    }

    public boolean removeParameter(String paramName, String paramValue) throws IllegalArgumentException {
        LOG.trace((Object)"enter PostMethod.removeParameter(String, String)");
        if (paramName == null) {
            throw new IllegalArgumentException("Parameter name may not be null");
        }
        if (paramValue == null) {
            throw new IllegalArgumentException("Parameter value may not be null");
        }
        Iterator iter = this.params.iterator();
        while (iter.hasNext()) {
            NameValuePair pair = (NameValuePair)iter.next();
            if (!paramName.equals(pair.getName()) || !paramValue.equals(pair.getValue())) continue;
            iter.remove();
            return true;
        }
        return false;
    }

    public void setRequestBody(NameValuePair[] parametersBody) throws IllegalArgumentException {
        LOG.trace((Object)"enter PostMethod.setRequestBody(NameValuePair[])");
        if (parametersBody == null) {
            throw new IllegalArgumentException("Array of parameters may not be null");
        }
        this.clearRequestBody();
        this.addParameters(parametersBody);
    }
}

