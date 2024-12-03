/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.result;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.util.TextParseUtil;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsStatics;

public abstract class StrutsResultSupport
implements Result,
StrutsStatics {
    private static final Logger LOG = LogManager.getLogger(StrutsResultSupport.class);
    public static final String DEFAULT_PARAM = "location";
    public static final String DEFAULT_URL_ENCODING = "UTF-8";
    protected boolean parseLocation = true;
    private boolean parse;
    private boolean encode;
    private String location;
    private String lastFinalLocation;

    public StrutsResultSupport() {
        this(null, true, false);
    }

    public StrutsResultSupport(String location) {
        this(location, true, false);
    }

    public StrutsResultSupport(String location, boolean parse, boolean encode) {
        this.location = location;
        this.parse = parse;
        this.encode = encode;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return this.location;
    }

    public String getLastFinalLocation() {
        return this.lastFinalLocation;
    }

    public void setParse(boolean parse) {
        this.parse = parse;
    }

    public void setEncode(boolean encode) {
        this.encode = encode;
    }

    @Override
    public void execute(ActionInvocation invocation) throws Exception {
        this.lastFinalLocation = this.parseLocation ? this.conditionalParse(this.location, invocation) : this.location;
        this.doExecute(this.lastFinalLocation, invocation);
    }

    protected String conditionalParse(String param, ActionInvocation invocation) {
        if (this.parse && param != null && invocation != null) {
            return TextParseUtil.translateVariables(param, invocation.getStack(), new EncodingParsedValueEvaluator());
        }
        return param;
    }

    protected Collection<String> conditionalParseCollection(String param, ActionInvocation invocation, boolean excludeEmptyElements) {
        if (this.parse && param != null && invocation != null) {
            return TextParseUtil.translateVariablesCollection(param, invocation.getStack(), excludeEmptyElements, new EncodingParsedValueEvaluator());
        }
        ArrayList<String> collection = new ArrayList<String>(1);
        collection.add(param);
        return collection;
    }

    protected abstract void doExecute(String var1, ActionInvocation var2) throws Exception;

    private final class EncodingParsedValueEvaluator
    implements TextParseUtil.ParsedValueEvaluator {
        private EncodingParsedValueEvaluator() {
        }

        @Override
        public Object evaluate(String parsedValue) {
            if (StrutsResultSupport.this.encode && parsedValue != null) {
                try {
                    return URLEncoder.encode(parsedValue, StrutsResultSupport.DEFAULT_URL_ENCODING);
                }
                catch (UnsupportedEncodingException e) {
                    LOG.warn("error while trying to encode [{}]", (Object)parsedValue, (Object)e);
                }
            }
            return parsedValue;
        }
    }
}

