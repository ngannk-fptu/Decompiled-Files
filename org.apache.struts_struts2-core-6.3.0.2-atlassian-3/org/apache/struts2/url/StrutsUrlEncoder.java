/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.url;

import com.opensymphony.xwork2.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.url.UrlEncoder;

public class StrutsUrlEncoder
implements UrlEncoder {
    private static final Logger LOG = LogManager.getLogger(StrutsUrlEncoder.class);
    private String encoding = "UTF-8";

    @Inject(value="struts.i18n.encoding", required=false)
    public void setEncoding(String encoding) {
        LOG.debug("Using default encoding: {}", (Object)encoding);
        if (StringUtils.isNotEmpty((CharSequence)encoding)) {
            this.encoding = encoding;
        }
    }

    @Override
    public String encode(String input, String encoding) {
        try {
            return URLEncoder.encode(input, encoding);
        }
        catch (UnsupportedEncodingException e) {
            LOG.warn("Could not encode URL parameter '{}', returning value un-encoded", (Object)input);
            return input;
        }
    }

    @Override
    public String encode(String input) {
        return this.encode(input, this.encoding);
    }
}

