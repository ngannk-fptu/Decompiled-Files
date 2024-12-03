/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.model;

import com.atlassian.confluence.extra.calendar3.model.JsonSerializable;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement
public class JqlAutoCompleteResult
implements JsonSerializable {
    private static final Logger LOG = LoggerFactory.getLogger(JqlAutoCompleteResult.class);
    @XmlElement
    private List<Result> results;

    public JqlAutoCompleteResult(List<Result> results) {
        this.setResults(results);
    }

    public JqlAutoCompleteResult() {
        this(null);
    }

    public List<Result> getResults() {
        return this.results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    @Override
    public JSONObject toJson() {
        JSONObject thisObject = new JSONObject();
        try {
            List<Result> results = this.getResults();
            JSONArray resultsArray = new JSONArray();
            if (null != results) {
                for (Result result : results) {
                    JSONObject resultObj = new JSONObject();
                    resultObj.put("displayName", (Object)result.getDisplayName());
                    resultObj.put("value", (Object)result.getValue());
                    resultsArray.put((Object)resultObj);
                }
            }
            thisObject.put("results", (Object)resultsArray);
        }
        catch (JSONException json) {
            LOG.error("Unable to create a JSON object based on this object", (Throwable)json);
        }
        return thisObject;
    }

    @XmlRootElement
    public static class Result
    implements JsonSerializable {
        private static final Logger LOG = LoggerFactory.getLogger(Result.class);
        @XmlElement
        private String displayName;
        @XmlElement
        private String value;

        public Result(String displayName, String value) {
            this.setDisplayName(displayName);
            this.setValue(value);
        }

        public Result() {
            this(null, null);
        }

        public String getDisplayName() {
            return this.displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getValue() {
            return this.value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public JSONObject toJson() {
            JSONObject thisObject = new JSONObject();
            try {
                thisObject.put("displayName", (Object)this.getDisplayName());
                thisObject.put("value", (Object)this.getValue());
            }
            catch (JSONException json) {
                LOG.error("Unable to create a JSON object based on this object", (Throwable)json);
            }
            return thisObject;
        }
    }
}

