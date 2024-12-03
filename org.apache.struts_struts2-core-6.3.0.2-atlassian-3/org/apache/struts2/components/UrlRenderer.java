/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.components;

import java.io.Writer;
import org.apache.struts2.components.Form;
import org.apache.struts2.components.UrlProvider;
import org.apache.struts2.dispatcher.mapper.ActionMapper;

public interface UrlRenderer {
    public void beforeRenderUrl(UrlProvider var1);

    public void renderUrl(Writer var1, UrlProvider var2);

    public void renderFormUrl(Form var1);

    public void setActionMapper(ActionMapper var1);
}

