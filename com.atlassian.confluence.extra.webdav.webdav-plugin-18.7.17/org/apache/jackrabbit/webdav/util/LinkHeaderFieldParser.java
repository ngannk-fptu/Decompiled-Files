/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.NameValuePair
 *  org.apache.http.message.BasicHeaderValueParser
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeaderValueParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinkHeaderFieldParser {
    private static Logger log = LoggerFactory.getLogger(LinkHeaderFieldParser.class);
    private final List<LinkRelation> relations;

    public LinkHeaderFieldParser(List<String> fieldValues) {
        ArrayList<LinkRelation> tmp = new ArrayList<LinkRelation>();
        if (fieldValues != null) {
            for (String value : fieldValues) {
                this.addFields(tmp, value);
            }
        }
        this.relations = Collections.unmodifiableList(tmp);
    }

    public LinkHeaderFieldParser(Enumeration<?> en) {
        if (en != null && en.hasMoreElements()) {
            ArrayList<LinkRelation> tmp = new ArrayList<LinkRelation>();
            while (en.hasMoreElements()) {
                this.addFields(tmp, en.nextElement().toString());
            }
            this.relations = Collections.unmodifiableList(tmp);
        } else {
            this.relations = Collections.emptyList();
        }
    }

    public String getFirstTargetForRelation(String relationType) {
        for (LinkRelation lr : this.relations) {
            String relationNames = lr.getParameters().get("rel");
            if (relationNames == null) continue;
            for (String rn : relationNames.toLowerCase(Locale.ENGLISH).split("\\s")) {
                if (!relationType.equals(rn)) continue;
                return lr.getTarget();
            }
        }
        return null;
    }

    private void addFields(List<LinkRelation> l, String fieldValue) {
        boolean insideAngleBrackets = false;
        boolean insideDoubleQuotes = false;
        for (int i = 0; i < fieldValue.length(); ++i) {
            char c = fieldValue.charAt(i);
            if (insideAngleBrackets) {
                insideAngleBrackets = c != '>';
                continue;
            }
            if (insideDoubleQuotes) {
                boolean bl = insideDoubleQuotes = c != '\"';
                if (c != '\\' || i >= fieldValue.length() - 1) continue;
                c = fieldValue.charAt(++i);
                continue;
            }
            insideAngleBrackets = c == '<';
            boolean bl = insideDoubleQuotes = c == '\"';
            if (c != ',') continue;
            String v = fieldValue.substring(0, i);
            if (v.length() > 0) {
                try {
                    l.add(new LinkRelation(v));
                }
                catch (Exception ex) {
                    log.warn("parse error in Link Header field value", (Throwable)ex);
                }
            }
            this.addFields(l, fieldValue.substring(i + 1));
            return;
        }
        if (fieldValue.length() > 0) {
            try {
                l.add(new LinkRelation(fieldValue));
            }
            catch (Exception ex) {
                log.warn("parse error in Link Header field value", (Throwable)ex);
            }
        }
    }

    private static class LinkRelation {
        private static Pattern P = Pattern.compile("\\s*<(.*)>\\s*(.*)");
        private String target;
        private Map<String, String> parameters;

        public LinkRelation(String field) throws Exception {
            Matcher m = P.matcher(field);
            if (!m.matches()) {
                throw new Exception("illegal Link header field value:" + field);
            }
            this.target = m.group(1);
            NameValuePair[] params = BasicHeaderValueParser.parseParameters((String)m.group(2), null);
            if (params.length == 0) {
                this.parameters = Collections.emptyMap();
            } else if (params.length == 1) {
                NameValuePair nvp = params[0];
                this.parameters = Collections.singletonMap(nvp.getName().toLowerCase(Locale.ENGLISH), nvp.getValue());
            } else {
                this.parameters = new HashMap<String, String>();
                for (NameValuePair p : params) {
                    if (null == this.parameters.put(p.getName().toLowerCase(Locale.ENGLISH), p.getValue())) continue;
                    throw new Exception("duplicate parameter + " + p.getName() + " field ignored");
                }
            }
        }

        public String getTarget() {
            return this.target;
        }

        public Map<String, String> getParameters() {
            return this.parameters;
        }

        public String toString() {
            return this.target + " " + this.parameters;
        }
    }
}

