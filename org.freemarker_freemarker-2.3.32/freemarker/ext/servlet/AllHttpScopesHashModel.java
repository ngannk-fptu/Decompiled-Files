/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 */
package freemarker.ext.servlet;

import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleHash;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.NullArgumentException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class AllHttpScopesHashModel
extends SimpleHash {
    private final ServletContext context;
    private final HttpServletRequest request;
    private final Map unlistedModels = new HashMap();

    public AllHttpScopesHashModel(ObjectWrapper objectWrapper, ServletContext context, HttpServletRequest request) {
        super(objectWrapper);
        NullArgumentException.check("wrapper", objectWrapper);
        this.context = context;
        this.request = request;
    }

    public void putUnlistedModel(String key, TemplateModel model) {
        this.unlistedModels.put(key, model);
    }

    @Override
    public TemplateModel get(String key) throws TemplateModelException {
        TemplateModel model = super.get(key);
        if (model != null) {
            return model;
        }
        model = (TemplateModel)this.unlistedModels.get(key);
        if (model != null) {
            return model;
        }
        Object obj = this.request.getAttribute(key);
        if (obj != null) {
            return this.wrap(obj);
        }
        HttpSession session = this.request.getSession(false);
        if (session != null && (obj = session.getAttribute(key)) != null) {
            return this.wrap(obj);
        }
        obj = this.context.getAttribute(key);
        if (obj != null) {
            return this.wrap(obj);
        }
        return this.wrap(null);
    }
}

