/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.util.Strftime
 *  org.apache.catalina.util.URLEncoder
 *  org.apache.tomcat.util.res.StringManager
 *  org.apache.tomcat.util.security.Escape
 */
package org.apache.catalina.ssi;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import org.apache.catalina.ssi.SSIConditionalState;
import org.apache.catalina.ssi.SSIExternalResolver;
import org.apache.catalina.util.Strftime;
import org.apache.catalina.util.URLEncoder;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.security.Escape;

public class SSIMediator {
    private static final StringManager sm = StringManager.getManager(SSIMediator.class);
    protected static final String ENCODING_NONE = "none";
    protected static final String ENCODING_ENTITY = "entity";
    protected static final String ENCODING_URL = "url";
    protected static final String DEFAULT_CONFIG_ERR_MSG = "[an error occurred while processing this directive]";
    protected static final String DEFAULT_CONFIG_TIME_FMT = "%A, %d-%b-%Y %T %Z";
    protected static final String DEFAULT_CONFIG_SIZE_FMT = "abbrev";
    protected String configErrMsg = "[an error occurred while processing this directive]";
    protected String configTimeFmt = "%A, %d-%b-%Y %T %Z";
    protected String configSizeFmt = "abbrev";
    protected final String className = this.getClass().getName();
    protected final SSIExternalResolver ssiExternalResolver;
    protected final long lastModifiedDate;
    protected Strftime strftime;
    protected final SSIConditionalState conditionalState = new SSIConditionalState();
    protected int lastMatchCount = 0;

    public SSIMediator(SSIExternalResolver ssiExternalResolver, long lastModifiedDate) {
        this.ssiExternalResolver = ssiExternalResolver;
        this.lastModifiedDate = lastModifiedDate;
        this.setConfigTimeFmt(DEFAULT_CONFIG_TIME_FMT, true);
    }

    public void setConfigErrMsg(String configErrMsg) {
        this.configErrMsg = configErrMsg;
    }

    public void setConfigTimeFmt(String configTimeFmt) {
        this.setConfigTimeFmt(configTimeFmt, false);
    }

    public void setConfigTimeFmt(String configTimeFmt, boolean fromConstructor) {
        this.configTimeFmt = configTimeFmt;
        this.strftime = new Strftime(configTimeFmt, Locale.US);
        this.setDateVariables(fromConstructor);
    }

    public void setConfigSizeFmt(String configSizeFmt) {
        this.configSizeFmt = configSizeFmt;
    }

    public String getConfigErrMsg() {
        return this.configErrMsg;
    }

    public String getConfigTimeFmt() {
        return this.configTimeFmt;
    }

    public String getConfigSizeFmt() {
        return this.configSizeFmt;
    }

    public SSIConditionalState getConditionalState() {
        return this.conditionalState;
    }

    public Collection<String> getVariableNames() {
        HashSet<String> variableNames = new HashSet<String>();
        variableNames.add("DATE_GMT");
        variableNames.add("DATE_LOCAL");
        variableNames.add("LAST_MODIFIED");
        this.ssiExternalResolver.addVariableNames(variableNames);
        variableNames.removeIf(this::isNameReserved);
        return variableNames;
    }

    public long getFileSize(String path, boolean virtual) throws IOException {
        return this.ssiExternalResolver.getFileSize(path, virtual);
    }

    public long getFileLastModified(String path, boolean virtual) throws IOException {
        return this.ssiExternalResolver.getFileLastModified(path, virtual);
    }

    public String getFileText(String path, boolean virtual) throws IOException {
        return this.ssiExternalResolver.getFileText(path, virtual);
    }

    protected boolean isNameReserved(String name) {
        return name.startsWith(this.className + ".");
    }

    public String getVariableValue(String variableName) {
        return this.getVariableValue(variableName, ENCODING_NONE);
    }

    public void setVariableValue(String variableName, String variableValue) {
        if (!this.isNameReserved(variableName)) {
            this.ssiExternalResolver.setVariableValue(variableName, variableValue);
        }
    }

    public String getVariableValue(String variableName, String encoding) {
        String lowerCaseVariableName = variableName.toLowerCase(Locale.ENGLISH);
        String variableValue = null;
        if (!this.isNameReserved(lowerCaseVariableName)) {
            variableValue = this.ssiExternalResolver.getVariableValue(variableName);
            if (variableValue == null) {
                variableName = variableName.toUpperCase(Locale.ENGLISH);
                variableValue = this.ssiExternalResolver.getVariableValue(this.className + "." + variableName);
            }
            if (variableValue != null) {
                variableValue = this.encode(variableValue, encoding);
            }
        }
        return variableValue;
    }

    public String substituteVariables(String val) {
        int charEnd;
        if (val.indexOf(36) < 0 && val.indexOf(38) < 0) {
            return val;
        }
        val = val.replace("&lt;", "<");
        val = val.replace("&gt;", ">");
        val = val.replace("&quot;", "\"");
        val = val.replace("&amp;", "&");
        StringBuilder sb = new StringBuilder(val);
        int charStart = sb.indexOf("&#");
        while (charStart > -1 && (charEnd = sb.indexOf(";", charStart)) > -1) {
            char c = (char)Integer.parseInt(sb.substring(charStart + 2, charEnd));
            sb.delete(charStart, charEnd + 1);
            sb.insert(charStart, c);
            charStart = sb.indexOf("&#");
        }
        int i = 0;
        while (i < sb.length()) {
            String varName;
            String value;
            while (i < sb.length()) {
                if (sb.charAt(i) == '$') {
                    ++i;
                    break;
                }
                ++i;
            }
            if (i == sb.length()) break;
            if (i > 1 && sb.charAt(i - 2) == '\\') {
                sb.deleteCharAt(i - 2);
                --i;
                continue;
            }
            int nameStart = i;
            int start = i - 1;
            int end = -1;
            int nameEnd = -1;
            char endChar = ' ';
            if (sb.charAt(i) == '{') {
                ++nameStart;
                endChar = '}';
            }
            while (i < sb.length() && sb.charAt(i) != endChar) {
                ++i;
            }
            end = i;
            nameEnd = end++;
            if (endChar == '}') {
                // empty if block
            }
            if ((value = this.getVariableValue(varName = sb.substring(nameStart, nameEnd))) == null) {
                value = "";
            }
            sb.replace(start, end, value);
            i = start + value.length();
        }
        return sb.toString();
    }

    protected String formatDate(Date date, TimeZone timeZone) {
        String retVal;
        if (timeZone != null) {
            TimeZone oldTimeZone = this.strftime.getTimeZone();
            this.strftime.setTimeZone(timeZone);
            retVal = this.strftime.format(date);
            this.strftime.setTimeZone(oldTimeZone);
        } else {
            retVal = this.strftime.format(date);
        }
        return retVal;
    }

    protected String encode(String value, String encoding) {
        String retVal = null;
        if (encoding.equalsIgnoreCase(ENCODING_URL)) {
            retVal = URLEncoder.DEFAULT.encode(value, StandardCharsets.UTF_8);
        } else if (encoding.equalsIgnoreCase(ENCODING_NONE)) {
            retVal = value;
        } else if (encoding.equalsIgnoreCase(ENCODING_ENTITY)) {
            retVal = Escape.htmlElementContent((String)value);
        } else {
            throw new IllegalArgumentException(sm.getString("ssiMediator.unknownEncoding", new Object[]{encoding}));
        }
        return retVal;
    }

    public void log(String message) {
        this.ssiExternalResolver.log(message, null);
    }

    public void log(String message, Throwable throwable) {
        this.ssiExternalResolver.log(message, throwable);
    }

    protected void setDateVariables(boolean fromConstructor) {
        boolean alreadySet;
        boolean bl = alreadySet = this.ssiExternalResolver.getVariableValue(this.className + ".alreadyset") != null;
        if (!fromConstructor || !alreadySet) {
            this.ssiExternalResolver.setVariableValue(this.className + ".alreadyset", "true");
            Date date = new Date();
            TimeZone timeZone = TimeZone.getTimeZone("GMT");
            String retVal = this.formatDate(date, timeZone);
            this.setVariableValue("DATE_GMT", null);
            this.ssiExternalResolver.setVariableValue(this.className + ".DATE_GMT", retVal);
            retVal = this.formatDate(date, null);
            this.setVariableValue("DATE_LOCAL", null);
            this.ssiExternalResolver.setVariableValue(this.className + ".DATE_LOCAL", retVal);
            retVal = this.formatDate(new Date(this.lastModifiedDate), null);
            this.setVariableValue("LAST_MODIFIED", null);
            this.ssiExternalResolver.setVariableValue(this.className + ".LAST_MODIFIED", retVal);
        }
    }

    protected void clearMatchGroups() {
        for (int i = 1; i <= this.lastMatchCount; ++i) {
            this.setVariableValue(Integer.toString(i), "");
        }
        this.lastMatchCount = 0;
    }

    protected void populateMatchGroups(Matcher matcher) {
        this.lastMatchCount = matcher.groupCount();
        if (this.lastMatchCount == 0) {
            return;
        }
        for (int i = 1; i <= this.lastMatchCount; ++i) {
            this.setVariableValue(Integer.toString(i), matcher.group(i));
        }
    }
}

