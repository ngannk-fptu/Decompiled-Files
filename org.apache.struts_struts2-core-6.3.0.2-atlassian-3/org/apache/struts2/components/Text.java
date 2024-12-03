/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.text.StringEscapeUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.components.ContextBean;
import org.apache.struts2.components.Param;
import org.apache.struts2.util.TextProviderHelper;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

@StrutsTag(name="text", tldTagClass="org.apache.struts2.views.jsp.TextTag", description="Render a I18n text message")
public class Text
extends ContextBean
implements Param.UnnamedParametric {
    private static final Logger LOG = LogManager.getLogger(Text.class);
    protected List<Object> values = Collections.emptyList();
    protected String actualName;
    protected String name;
    private boolean escapeHtml = false;
    private boolean escapeJavaScript = false;
    private boolean escapeXml = false;
    private boolean escapeCsv = false;

    public Text(ValueStack stack) {
        super(stack);
    }

    @StrutsTagAttribute(description="Name of resource property to fetch", required=true)
    public void setName(String name) {
        this.name = name;
    }

    @StrutsTagAttribute(description="Whether to escape HTML", type="Boolean", defaultValue="false")
    public void setEscapeHtml(boolean escape) {
        this.escapeHtml = escape;
    }

    @StrutsTagAttribute(description="Whether to escape Javascript", type="Boolean", defaultValue="false")
    public void setEscapeJavaScript(boolean escapeJavaScript) {
        this.escapeJavaScript = escapeJavaScript;
    }

    @StrutsTagAttribute(description="Whether to escape XML", type="Boolean", defaultValue="false")
    public void setEscapeXml(boolean escapeXml) {
        this.escapeXml = escapeXml;
    }

    @StrutsTagAttribute(description="Whether to escape CSV (useful to escape a value for a column)", type="Boolean", defaultValue="false")
    public void setEscapeCsv(boolean escapeCsv) {
        this.escapeCsv = escapeCsv;
    }

    @Override
    public boolean usesBody() {
        return true;
    }

    @Override
    public boolean end(Writer writer, String body) {
        this.actualName = this.findString(this.name, "name", "You must specify the i18n key. Example: welcome.header");
        String defaultMessage = StringUtils.isNotEmpty((CharSequence)body) ? body : this.actualName;
        String msg = TextProviderHelper.getText(this.actualName, defaultMessage, this.values, this.getStack());
        if (msg != null) {
            try {
                if (this.getVar() == null) {
                    writer.write(this.prepare(msg));
                } else {
                    this.putInContext(msg);
                }
            }
            catch (IOException e) {
                LOG.error("Could not write out Text tag", (Throwable)e);
            }
        }
        return super.end(writer, "");
    }

    @Override
    public void addParameter(String key, Object value) {
        this.addParameter(value);
    }

    @Override
    public void addParameter(Object value) {
        if (this.values.isEmpty()) {
            this.values = new ArrayList<Object>(4);
        }
        this.values.add(value);
    }

    private String prepare(String value) {
        String result = value;
        if (this.escapeHtml) {
            result = StringEscapeUtils.escapeHtml4((String)result);
        }
        if (this.escapeJavaScript) {
            result = StringEscapeUtils.escapeEcmaScript((String)result);
        }
        if (this.escapeXml) {
            result = StringEscapeUtils.escapeXml10((String)result);
        }
        if (this.escapeCsv) {
            result = StringEscapeUtils.escapeCsv((String)result);
        }
        return result;
    }
}

