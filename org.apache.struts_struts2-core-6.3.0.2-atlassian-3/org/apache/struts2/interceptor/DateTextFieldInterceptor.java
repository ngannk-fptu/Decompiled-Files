/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.dispatcher.Parameter;

public class DateTextFieldInterceptor
extends AbstractInterceptor {
    private static final Logger LOG = LogManager.getLogger(DateTextFieldInterceptor.class);

    @Override
    public String intercept(ActionInvocation ai) throws Exception {
        HttpParameters parameters = ai.getInvocationContext().getParameters();
        HashMap<Object, HashMap<String, String>> dates = new HashMap<Object, HashMap<String, String>>();
        DateWord[] dateWords = DateWord.getAll();
        Set<String> names = parameters.keySet();
        block2: for (String name : names) {
            for (DateWord dateWord : dateWords) {
                String dateKey = "__" + dateWord.getDescription() + "_";
                if (!name.startsWith(dateKey)) continue;
                String key = name.substring(dateKey.length());
                Parameter param = parameters.get(name);
                if (!param.isDefined()) continue block2;
                HashMap<String, String> map = (HashMap<String, String>)dates.get(key);
                if (map == null) {
                    map = new HashMap<String, String>();
                    dates.put(key, map);
                }
                map.put(dateWord.getDateType(), param.getValue());
                parameters = parameters.remove(name);
                continue block2;
            }
        }
        HashMap<String, Parameter> newParams = new HashMap<String, Parameter>();
        Set dateEntries = dates.entrySet();
        for (Map.Entry entry : dateEntries) {
            Set dateFormatEntries = ((Map)entry.getValue()).entrySet();
            String dateFormat = "";
            String dateValue = "";
            for (Map.Entry dateFormatEntry : dateFormatEntries) {
                dateFormat = dateFormat + (String)dateFormatEntry.getKey() + "__";
                dateValue = dateValue + (String)dateFormatEntry.getValue() + "__";
            }
            try {
                SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
                formatter.setLenient(false);
                Date value = formatter.parse(dateValue);
                newParams.put((String)entry.getKey(), new Parameter.Request((String)entry.getKey(), value));
            }
            catch (ParseException e) {
                LOG.warn("Cannot parse the parameter '{}' with format '{}' and with value '{}'", entry.getKey(), (Object)dateFormat, (Object)dateValue);
            }
        }
        ai.getInvocationContext().getParameters().appendAll(newParams);
        return ai.invoke();
    }

    public static enum DateWord {
        S("millisecond", 3, "SSS"),
        s("second", 2, "ss"),
        m("minute", 2, "mm"),
        H("hour", 2, "HH"),
        d("day", 2, "dd"),
        M("month", 2, "MM"),
        y("year", 4, "yyyy");

        private String description;
        private Integer length;
        private String dateType;

        private DateWord(String n2, Integer l, String t) {
            this.description = n2;
            this.length = l;
            this.dateType = t;
        }

        public String getDescription() {
            return this.description;
        }

        public Integer getLength() {
            return this.length;
        }

        public String getDateType() {
            return this.dateType;
        }

        public static DateWord get(Character c) {
            return DateWord.valueOf(DateWord.class, c.toString());
        }

        public static DateWord[] getAll() {
            return DateWord.values();
        }
    }
}

