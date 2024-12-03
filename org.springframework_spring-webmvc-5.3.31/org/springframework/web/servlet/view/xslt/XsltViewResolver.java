/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.servlet.view.xslt;

import java.util.Properties;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.URIResolver;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import org.springframework.web.servlet.view.xslt.XsltView;

public class XsltViewResolver
extends UrlBasedViewResolver {
    @Nullable
    private String sourceKey;
    @Nullable
    private URIResolver uriResolver;
    @Nullable
    private ErrorListener errorListener;
    private boolean indent = true;
    @Nullable
    private Properties outputProperties;
    private boolean cacheTemplates = true;

    public XsltViewResolver() {
        this.setViewClass(this.requiredViewClass());
    }

    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
    }

    public void setUriResolver(URIResolver uriResolver) {
        this.uriResolver = uriResolver;
    }

    public void setErrorListener(ErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    public void setIndent(boolean indent) {
        this.indent = indent;
    }

    public void setOutputProperties(Properties outputProperties) {
        this.outputProperties = outputProperties;
    }

    public void setCacheTemplates(boolean cacheTemplates) {
        this.cacheTemplates = cacheTemplates;
    }

    @Override
    protected Class<?> requiredViewClass() {
        return XsltView.class;
    }

    @Override
    protected AbstractUrlBasedView instantiateView() {
        return this.getViewClass() == XsltView.class ? new XsltView() : super.instantiateView();
    }

    @Override
    protected AbstractUrlBasedView buildView(String viewName) throws Exception {
        XsltView view = (XsltView)super.buildView(viewName);
        if (this.sourceKey != null) {
            view.setSourceKey(this.sourceKey);
        }
        if (this.uriResolver != null) {
            view.setUriResolver(this.uriResolver);
        }
        if (this.errorListener != null) {
            view.setErrorListener(this.errorListener);
        }
        view.setIndent(this.indent);
        if (this.outputProperties != null) {
            view.setOutputProperties(this.outputProperties);
        }
        view.setCacheTemplates(this.cacheTemplates);
        return view;
    }
}

