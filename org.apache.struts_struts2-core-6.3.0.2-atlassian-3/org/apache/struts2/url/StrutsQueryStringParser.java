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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.url.QueryStringParser;
import org.apache.struts2.url.UrlDecoder;

public class StrutsQueryStringParser
implements QueryStringParser {
    private static final Logger LOG = LogManager.getLogger(StrutsQueryStringParser.class);
    private final UrlDecoder decoder;

    @Inject
    public StrutsQueryStringParser(UrlDecoder decoder) {
        this.decoder = decoder;
    }

    @Override
    public Map<String, Object> parse(String queryString, boolean forceValueArray) {
        return this.parse(queryString).getQueryParams();
    }

    @Override
    public QueryStringParser.Result parse(String queryString) {
        String[] params;
        if (StringUtils.isEmpty((CharSequence)queryString)) {
            LOG.debug("Query String is empty, returning an empty map");
            return this.empty();
        }
        QueryStringParser.Result queryParams = StrutsQueryStringParserResult.create();
        for (String param : params = this.extractParams(queryString)) {
            String paramName;
            if (StringUtils.isBlank((CharSequence)param)) {
                LOG.debug("Param [{}] is blank, skipping", (Object)param);
                continue;
            }
            String paramValue = "";
            int index = param.indexOf("=");
            if (index > -1) {
                paramName = param.substring(0, index);
                paramValue = param.substring(index + 1);
            } else {
                paramName = param;
            }
            queryParams = this.extractParam(paramName, paramValue, queryParams);
        }
        return queryParams.withQueryFragment(this.extractFragment(queryString));
    }

    @Override
    public QueryStringParser.Result empty() {
        return new StrutsQueryStringParserResult(Collections.emptyMap(), "");
    }

    private String[] extractParams(String queryString) {
        LOG.trace("Extracting params from query string: {}", (Object)queryString);
        String[] params = queryString.split("&");
        int fragmentIndex = queryString.lastIndexOf("#");
        if (fragmentIndex > -1) {
            LOG.trace("Stripping fragment at index: {}", (Object)fragmentIndex);
            params = queryString.substring(0, fragmentIndex).split("&");
        }
        return params;
    }

    private QueryStringParser.Result extractParam(String paramName, String paramValue, QueryStringParser.Result queryParams) {
        String decodedParamName = this.decoder.decode(paramName, true);
        String decodedParamValue = this.decoder.decode(paramValue, true);
        return queryParams.addParam(decodedParamName, decodedParamValue);
    }

    private String extractFragment(String queryString) {
        int fragmentIndex = queryString.lastIndexOf("#");
        if (fragmentIndex > -1) {
            return queryString.substring(fragmentIndex + 1);
        }
        return "";
    }

    public static class StrutsQueryStringParserResult
    implements QueryStringParser.Result {
        private final Map<String, Object> queryParams;
        private String queryFragment;

        static QueryStringParser.Result create() {
            return new StrutsQueryStringParserResult(new LinkedHashMap<String, Object>(), "");
        }

        private StrutsQueryStringParserResult(Map<String, Object> queryParams, String queryFragment) {
            this.queryParams = queryParams;
            this.queryFragment = queryFragment;
        }

        @Override
        public QueryStringParser.Result addParam(String name, String value) {
            if (this.queryParams.containsKey(name)) {
                Object currentParam = this.queryParams.get(name);
                if (currentParam instanceof String) {
                    this.queryParams.put(name, new String[]{(String)currentParam, value});
                } else {
                    String[] currentParamValues = (String[])currentParam;
                    if (currentParamValues != null) {
                        ArrayList<String> paramList = new ArrayList<String>(Arrays.asList(currentParamValues));
                        paramList.add(value);
                        this.queryParams.put(name, paramList.toArray(new String[0]));
                    } else {
                        this.queryParams.put(name, new String[]{value});
                    }
                }
            } else {
                this.queryParams.put(name, value);
            }
            return this;
        }

        @Override
        public QueryStringParser.Result withQueryFragment(String queryFragment) {
            this.queryFragment = queryFragment;
            return this;
        }

        @Override
        public Map<String, Object> getQueryParams() {
            return Collections.unmodifiableMap(this.queryParams);
        }

        @Override
        public String getQueryFragment() {
            return this.queryFragment;
        }

        @Override
        public boolean contains(String name) {
            return this.queryParams.containsKey(name);
        }

        @Override
        public boolean isEmpty() {
            return this.queryParams.isEmpty();
        }
    }
}

