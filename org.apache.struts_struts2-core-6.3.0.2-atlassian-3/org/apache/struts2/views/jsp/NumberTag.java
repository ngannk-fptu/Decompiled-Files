/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.struts2.views.jsp;

import com.opensymphony.xwork2.util.ValueStack;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.components.Component;
import org.apache.struts2.components.Number;
import org.apache.struts2.views.jsp.ContextBeanTag;

public class NumberTag
extends ContextBeanTag {
    private static final long serialVersionUID = -6216963123295613440L;
    private String name;
    private String currency;
    private String type;
    private Boolean groupingUsed;
    private Integer maximumFractionDigits;
    private Integer maximumIntegerDigits;
    private Integer minimumFractionDigits;
    private Integer minimumIntegerDigits;
    private Boolean parseIntegerOnly;
    private String roundingMode;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Number(stack);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        Number n = (Number)this.component;
        n.setName(this.name);
        n.setCurrency(this.currency);
        n.setType(this.type);
        n.setGroupingUsed(this.groupingUsed);
        n.setMaximumFractionDigits(this.maximumFractionDigits);
        n.setMaximumIntegerDigits(this.maximumIntegerDigits);
        n.setMinimumFractionDigits(this.minimumFractionDigits);
        n.setMinimumIntegerDigits(this.minimumIntegerDigits);
        n.setParseIntegerOnly(this.parseIntegerOnly);
        n.setRoundingMode(this.roundingMode);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setGroupingUsed(Boolean groupingUsed) {
        this.groupingUsed = groupingUsed;
    }

    public void setMaximumFractionDigits(Integer maximumFractionDigits) {
        this.maximumFractionDigits = maximumFractionDigits;
    }

    public void setMaximumIntegerDigits(Integer maximumIntegerDigits) {
        this.maximumIntegerDigits = maximumIntegerDigits;
    }

    public void setMinimumFractionDigits(Integer minimumFractionDigits) {
        this.minimumFractionDigits = minimumFractionDigits;
    }

    public void setMinimumIntegerDigits(Integer minimumIntegerDigits) {
        this.minimumIntegerDigits = minimumIntegerDigits;
    }

    public void setParseIntegerOnly(Boolean parseIntegerOnly) {
        this.parseIntegerOnly = parseIntegerOnly;
    }

    public void setRoundingMode(String roundingMode) {
        this.roundingMode = roundingMode;
    }

    @Override
    public void setPerformClearTagStateForTagPoolingServers(boolean performClearTagStateForTagPoolingServers) {
        super.setPerformClearTagStateForTagPoolingServers(performClearTagStateForTagPoolingServers);
    }

    @Override
    protected void clearTagStateForTagPoolingServers() {
        if (!this.getPerformClearTagStateForTagPoolingServers()) {
            return;
        }
        super.clearTagStateForTagPoolingServers();
        this.name = null;
        this.currency = null;
        this.type = null;
        this.groupingUsed = null;
        this.maximumFractionDigits = null;
        this.maximumIntegerDigits = null;
        this.minimumFractionDigits = null;
        this.minimumIntegerDigits = null;
        this.parseIntegerOnly = null;
        this.roundingMode = null;
    }
}

