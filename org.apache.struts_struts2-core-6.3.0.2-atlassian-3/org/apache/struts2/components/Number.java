/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.components;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;
import java.io.IOException;
import java.io.Writer;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Currency;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.components.ContextBean;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

@StrutsTag(name="number", tldBodyContent="empty", tldTagClass="org.apache.struts2.views.jsp.NumberTag", description="Render a formatted number.")
public class Number
extends ContextBean {
    private static final Logger LOG = LogManager.getLogger(Number.class);
    public static final String NUMBERTAG_PROPERTY = "struts.number.format";
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

    public Number(ValueStack stack) {
        super(stack);
    }

    @Override
    public boolean end(Writer writer, String body) {
        java.lang.Number number = this.findNumberName();
        if (number != null) {
            NumberFormat format = this.getNumberFormat();
            this.findCurrency(format);
            this.setNumberFormatParameters(format);
            this.setRoundingMode(format);
            String msg = format.format(number);
            try {
                if (this.getVar() == null) {
                    writer.write(msg);
                } else {
                    this.putInContext(msg);
                }
            }
            catch (IOException e) {
                LOG.error("Could not write out Number tag", (Throwable)e);
            }
        }
        return super.end(writer, "");
    }

    private void findCurrency(NumberFormat format) {
        if (this.currency != null) {
            Object currencyValue = this.findValue(this.currency);
            if (currencyValue != null) {
                this.currency = currencyValue.toString();
            }
            try {
                format.setCurrency(Currency.getInstance(this.currency));
            }
            catch (IllegalArgumentException iae) {
                LOG.error("Could not recognise a currency of [" + this.currency + "]");
            }
        }
    }

    private void setNumberFormatParameters(NumberFormat format) {
        if (this.groupingUsed != null) {
            format.setGroupingUsed(this.groupingUsed);
        }
        if (this.maximumFractionDigits != null) {
            format.setMaximumFractionDigits(this.maximumFractionDigits);
        }
        if (this.maximumIntegerDigits != null) {
            format.setMaximumIntegerDigits(this.maximumIntegerDigits);
        }
        if (this.minimumFractionDigits != null) {
            format.setMinimumFractionDigits(this.minimumFractionDigits);
        }
        if (this.minimumIntegerDigits != null) {
            format.setMinimumIntegerDigits(this.minimumIntegerDigits);
        }
        if (this.parseIntegerOnly != null) {
            format.setParseIntegerOnly(this.parseIntegerOnly);
        }
    }

    private java.lang.Number findNumberName() {
        java.lang.Number number = null;
        try {
            Object numberObject = this.findValue(this.name);
            if (numberObject instanceof java.lang.Number) {
                number = (java.lang.Number)numberObject;
            }
        }
        catch (Exception e) {
            LOG.error("Could not convert object with key [" + this.name + "] to a java.lang.Number instance");
        }
        return number;
    }

    private void setRoundingMode(NumberFormat format) {
        if (this.roundingMode != null) {
            this.roundingMode = this.findString(this.roundingMode);
            if ("ceiling".equals(this.roundingMode)) {
                format.setRoundingMode(RoundingMode.CEILING);
            } else if ("down".equals(this.roundingMode)) {
                format.setRoundingMode(RoundingMode.DOWN);
            } else if ("floor".equals(this.roundingMode)) {
                format.setRoundingMode(RoundingMode.FLOOR);
            } else if ("half-down".equals(this.roundingMode)) {
                format.setRoundingMode(RoundingMode.HALF_DOWN);
            } else if ("half-even".equals(this.roundingMode)) {
                format.setRoundingMode(RoundingMode.HALF_EVEN);
            } else if ("half-up".equals(this.roundingMode)) {
                format.setRoundingMode(RoundingMode.HALF_UP);
            } else if ("unnecessary".equals(this.roundingMode)) {
                format.setRoundingMode(RoundingMode.UNNECESSARY);
            } else if ("up".equals(this.roundingMode)) {
                format.setRoundingMode(RoundingMode.UP);
            } else {
                LOG.error("Could not recognise a roundingMode of [" + this.roundingMode + "]");
            }
        }
    }

    private NumberFormat getNumberFormat() {
        NumberFormat format = null;
        if (this.type == null) {
            try {
                this.type = this.findString(NUMBERTAG_PROPERTY);
            }
            catch (Exception e) {
                LOG.error("Could not find [struts.number.format] on the stack!", (Throwable)e);
            }
        }
        if (this.type != null) {
            this.type = this.findString(this.type);
            if ("currency".equals(this.type)) {
                format = NumberFormat.getCurrencyInstance(ActionContext.getContext().getLocale());
            } else if ("integer".equals(this.type)) {
                format = NumberFormat.getIntegerInstance(ActionContext.getContext().getLocale());
            } else if ("number".equals(this.type)) {
                format = NumberFormat.getNumberInstance(ActionContext.getContext().getLocale());
            } else if ("percent".equals(this.type)) {
                format = NumberFormat.getPercentInstance(ActionContext.getContext().getLocale());
            }
        }
        if (format == null) {
            format = NumberFormat.getInstance(ActionContext.getContext().getLocale());
        }
        return format;
    }

    @StrutsTagAttribute(description="Type of number formatter (currency, integer, number or percent, default is number)")
    public void setType(String type) {
        this.type = type;
    }

    @StrutsTagAttribute(description="The currency to use for a currency format")
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getName() {
        return this.name;
    }

    @StrutsTagAttribute(description="The number value to format", required=true)
    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public String getCurrency() {
        return this.currency;
    }

    @StrutsTagAttribute(description="Whether grouping is used", type="Boolean")
    public void setGroupingUsed(Boolean groupingUsed) {
        this.groupingUsed = groupingUsed;
    }

    public Boolean isGroupingUsed() {
        return this.groupingUsed;
    }

    public Integer getMaximumFractionDigits() {
        return this.maximumFractionDigits;
    }

    @StrutsTagAttribute(description="Maximum fraction digits", type="Integer")
    public void setMaximumFractionDigits(Integer maximumFractionDigits) {
        this.maximumFractionDigits = maximumFractionDigits;
    }

    public Integer getMaximumIntegerDigits() {
        return this.maximumIntegerDigits;
    }

    @StrutsTagAttribute(description="Maximum integer digits", type="Integer")
    public void setMaximumIntegerDigits(Integer maximumIntegerDigits) {
        this.maximumIntegerDigits = maximumIntegerDigits;
    }

    public Integer getMinimumFractionDigits() {
        return this.minimumFractionDigits;
    }

    @StrutsTagAttribute(description="Minimum fraction digits", type="Integer")
    public void setMinimumFractionDigits(Integer minimumFractionDigits) {
        this.minimumFractionDigits = minimumFractionDigits;
    }

    public Integer getMinimumIntegerDigits() {
        return this.minimumIntegerDigits;
    }

    @StrutsTagAttribute(description="Maximum integer digits", type="Integer")
    public void setMinimumIntegerDigits(Integer minimumIntegerDigits) {
        this.minimumIntegerDigits = minimumIntegerDigits;
    }

    public Boolean isParseIntegerOnly() {
        return this.parseIntegerOnly;
    }

    @StrutsTagAttribute(description="Parse integer only", type="Boolean")
    public void setParseIntegerOnly(Boolean parseIntegerOnly) {
        this.parseIntegerOnly = parseIntegerOnly;
    }

    public String getRoundingMode() {
        return this.roundingMode;
    }

    @StrutsTagAttribute(description="The rounding mode to use, possible values: ceiling, down, floor, half-down, half-even, half-up, unnecessary, up")
    public void setRoundingMode(String roundingMode) {
        this.roundingMode = roundingMode;
    }
}

