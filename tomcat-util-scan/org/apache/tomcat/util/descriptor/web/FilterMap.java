/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.DispatcherType
 *  org.apache.tomcat.util.buf.UDecoder
 */
package org.apache.tomcat.util.descriptor.web;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Locale;
import javax.servlet.DispatcherType;
import org.apache.tomcat.util.buf.UDecoder;
import org.apache.tomcat.util.descriptor.web.XmlEncodingBase;

public class FilterMap
extends XmlEncodingBase
implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final int ERROR = 1;
    public static final int FORWARD = 2;
    public static final int INCLUDE = 4;
    public static final int REQUEST = 8;
    public static final int ASYNC = 16;
    private static final int NOT_SET = 0;
    private int dispatcherMapping = 0;
    private String filterName = null;
    private String[] servletNames = new String[0];
    private boolean matchAllUrlPatterns = false;
    private boolean matchAllServletNames = false;
    private String[] urlPatterns = new String[0];

    public String getFilterName() {
        return this.filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public String[] getServletNames() {
        if (this.matchAllServletNames) {
            return new String[0];
        }
        return this.servletNames;
    }

    public void addServletName(String servletName) {
        if ("*".equals(servletName)) {
            this.matchAllServletNames = true;
        } else {
            String[] results = new String[this.servletNames.length + 1];
            System.arraycopy(this.servletNames, 0, results, 0, this.servletNames.length);
            results[this.servletNames.length] = servletName;
            this.servletNames = results;
        }
    }

    public boolean getMatchAllUrlPatterns() {
        return this.matchAllUrlPatterns;
    }

    public boolean getMatchAllServletNames() {
        return this.matchAllServletNames;
    }

    public String[] getURLPatterns() {
        if (this.matchAllUrlPatterns) {
            return new String[0];
        }
        return this.urlPatterns;
    }

    public void addURLPattern(String urlPattern) {
        this.addURLPatternDecoded(UDecoder.URLDecode((String)urlPattern, (Charset)this.getCharset()));
    }

    public void addURLPatternDecoded(String urlPattern) {
        if ("*".equals(urlPattern)) {
            this.matchAllUrlPatterns = true;
        } else {
            String[] results = new String[this.urlPatterns.length + 1];
            System.arraycopy(this.urlPatterns, 0, results, 0, this.urlPatterns.length);
            results[this.urlPatterns.length] = UDecoder.URLDecode((String)urlPattern, (Charset)this.getCharset());
            this.urlPatterns = results;
        }
    }

    public void setDispatcher(String dispatcherString) {
        String dispatcher = dispatcherString.toUpperCase(Locale.ENGLISH);
        if (dispatcher.equals(DispatcherType.FORWARD.name())) {
            this.dispatcherMapping |= 2;
        } else if (dispatcher.equals(DispatcherType.INCLUDE.name())) {
            this.dispatcherMapping |= 4;
        } else if (dispatcher.equals(DispatcherType.REQUEST.name())) {
            this.dispatcherMapping |= 8;
        } else if (dispatcher.equals(DispatcherType.ERROR.name())) {
            this.dispatcherMapping |= 1;
        } else if (dispatcher.equals(DispatcherType.ASYNC.name())) {
            this.dispatcherMapping |= 0x10;
        }
    }

    public int getDispatcherMapping() {
        if (this.dispatcherMapping == 0) {
            return 8;
        }
        return this.dispatcherMapping;
    }

    public String[] getDispatcherNames() {
        ArrayList<String> result = new ArrayList<String>();
        if ((this.dispatcherMapping & 2) != 0) {
            result.add(DispatcherType.FORWARD.name());
        }
        if ((this.dispatcherMapping & 4) != 0) {
            result.add(DispatcherType.INCLUDE.name());
        }
        if ((this.dispatcherMapping & 8) != 0) {
            result.add(DispatcherType.REQUEST.name());
        }
        if ((this.dispatcherMapping & 1) != 0) {
            result.add(DispatcherType.ERROR.name());
        }
        if ((this.dispatcherMapping & 0x10) != 0) {
            result.add(DispatcherType.ASYNC.name());
        }
        return result.toArray(new String[0]);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("FilterMap[");
        sb.append("filterName=");
        sb.append(this.filterName);
        for (String servletName : this.servletNames) {
            sb.append(", servletName=");
            sb.append(servletName);
        }
        for (String urlPattern : this.urlPatterns) {
            sb.append(", urlPattern=");
            sb.append(urlPattern);
        }
        sb.append(']');
        return sb.toString();
    }
}

