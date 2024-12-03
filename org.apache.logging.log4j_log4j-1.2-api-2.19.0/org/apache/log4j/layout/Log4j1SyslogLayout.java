/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.core.Layout
 *  org.apache.logging.log4j.core.LogEvent
 *  org.apache.logging.log4j.core.StringLayout
 *  org.apache.logging.log4j.core.config.plugins.Plugin
 *  org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute
 *  org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory
 *  org.apache.logging.log4j.core.config.plugins.PluginElement
 *  org.apache.logging.log4j.core.layout.AbstractStringLayout
 *  org.apache.logging.log4j.core.layout.AbstractStringLayout$Builder
 *  org.apache.logging.log4j.core.net.Facility
 *  org.apache.logging.log4j.core.net.Priority
 *  org.apache.logging.log4j.core.pattern.DatePatternConverter
 *  org.apache.logging.log4j.core.pattern.LogEventPatternConverter
 *  org.apache.logging.log4j.core.util.Builder
 *  org.apache.logging.log4j.core.util.NetUtils
 */
package org.apache.log4j.layout;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.StringLayout;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;
import org.apache.logging.log4j.core.net.Facility;
import org.apache.logging.log4j.core.net.Priority;
import org.apache.logging.log4j.core.pattern.DatePatternConverter;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.util.NetUtils;

@Plugin(name="Log4j1SyslogLayout", category="Core", elementType="layout", printObject=true)
public final class Log4j1SyslogLayout
extends AbstractStringLayout {
    private static final String localHostname = NetUtils.getLocalHostname();
    private final Facility facility;
    private final boolean facilityPrinting;
    private final boolean header;
    private final StringLayout messageLayout;
    private static final String[] dateFormatOptions = new String[]{"MMM dd HH:mm:ss", null, "en"};
    private final LogEventPatternConverter dateConverter = DatePatternConverter.newInstance((String[])dateFormatOptions);

    @PluginBuilderFactory
    public static <B extends Builder<B>> B newBuilder() {
        return (B)((Object)((Builder)new Builder().asBuilder()));
    }

    private Log4j1SyslogLayout(Facility facility, boolean facilityPrinting, boolean header, StringLayout messageLayout, Charset charset) {
        super(charset);
        this.facility = facility;
        this.facilityPrinting = facilityPrinting;
        this.header = header;
        this.messageLayout = messageLayout;
    }

    public String toSerializable(LogEvent event) {
        String message = this.messageLayout != null ? (String)((Object)this.messageLayout.toSerializable(event)) : event.getMessage().getFormattedMessage();
        StringBuilder buf = Log4j1SyslogLayout.getStringBuilder();
        buf.append('<');
        buf.append(Priority.getPriority((Facility)this.facility, (Level)event.getLevel()));
        buf.append('>');
        if (this.header) {
            int index = buf.length() + 4;
            this.dateConverter.format(event, buf);
            if (buf.charAt(index) == '0') {
                buf.setCharAt(index, ' ');
            }
            buf.append(' ');
            buf.append(localHostname);
            buf.append(' ');
        }
        if (this.facilityPrinting) {
            buf.append(this.facility != null ? this.facility.name().toLowerCase() : "user").append(':');
        }
        buf.append(message);
        return buf.toString();
    }

    public Map<String, String> getContentFormat() {
        HashMap<String, String> result = new HashMap<String, String>();
        result.put("structured", "false");
        result.put("formatType", "logfilepatternreceiver");
        result.put("dateFormat", dateFormatOptions[0]);
        if (this.header) {
            result.put("format", "<LEVEL>TIMESTAMP PROP(HOSTNAME) MESSAGE");
        } else {
            result.put("format", "<LEVEL>MESSAGE");
        }
        return result;
    }

    public static class Builder<B extends Builder<B>>
    extends AbstractStringLayout.Builder<B>
    implements org.apache.logging.log4j.core.util.Builder<Log4j1SyslogLayout> {
        @PluginBuilderAttribute
        private Facility facility = Facility.USER;
        @PluginBuilderAttribute
        private boolean facilityPrinting;
        @PluginBuilderAttribute
        private boolean header;
        @PluginElement(value="Layout")
        private Layout<? extends Serializable> messageLayout;

        public Builder() {
            this.setCharset(StandardCharsets.UTF_8);
        }

        public Log4j1SyslogLayout build() {
            if (!this.isValid()) {
                return null;
            }
            if (this.messageLayout != null && !(this.messageLayout instanceof StringLayout)) {
                LOGGER.error("Log4j1SyslogLayout: the message layout must be a StringLayout.");
                return null;
            }
            return new Log4j1SyslogLayout(this.facility, this.facilityPrinting, this.header, (StringLayout)this.messageLayout, this.getCharset());
        }

        public Facility getFacility() {
            return this.facility;
        }

        public boolean isFacilityPrinting() {
            return this.facilityPrinting;
        }

        public boolean isHeader() {
            return this.header;
        }

        public Layout<? extends Serializable> getMessageLayout() {
            return this.messageLayout;
        }

        public B setFacility(Facility facility) {
            this.facility = facility;
            return (B)((Object)((Builder)this.asBuilder()));
        }

        public B setFacilityPrinting(boolean facilityPrinting) {
            this.facilityPrinting = facilityPrinting;
            return (B)((Object)((Builder)this.asBuilder()));
        }

        public B setHeader(boolean header) {
            this.header = header;
            return (B)((Object)((Builder)this.asBuilder()));
        }

        public B setMessageLayout(Layout<? extends Serializable> messageLayout) {
            this.messageLayout = messageLayout;
            return (B)((Object)((Builder)this.asBuilder()));
        }
    }
}

