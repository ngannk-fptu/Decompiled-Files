/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.filters;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.tools.ant.filters.BaseParamFilterReader;
import org.apache.tools.ant.filters.ChainableReader;
import org.apache.tools.ant.types.Parameter;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.util.FileUtils;

public final class ReplaceTokens
extends BaseParamFilterReader
implements ChainableReader {
    private static final String DEFAULT_BEGIN_TOKEN = "@";
    private static final String DEFAULT_END_TOKEN = "@";
    private Hashtable<String, String> hash = new Hashtable();
    private final TreeMap<String, String> resolvedTokens = new TreeMap();
    private boolean resolvedTokensBuilt = false;
    private String readBuffer = "";
    private String replaceData = null;
    private int replaceIndex = -1;
    private String beginToken = "@";
    private String endToken = "@";

    public ReplaceTokens() {
    }

    public ReplaceTokens(Reader in) {
        super(in);
    }

    @Override
    public int read() throws IOException {
        if (!this.getInitialized()) {
            this.initialize();
            this.setInitialized(true);
        }
        if (!this.resolvedTokensBuilt) {
            for (Map.Entry<String, String> entry : this.hash.entrySet()) {
                this.resolvedTokens.put(this.beginToken + entry.getKey() + this.endToken, entry.getValue());
            }
            this.resolvedTokensBuilt = true;
        }
        if (this.replaceData != null) {
            if (this.replaceIndex < this.replaceData.length()) {
                return this.replaceData.charAt(this.replaceIndex++);
            }
            this.replaceData = null;
        }
        if (this.readBuffer.isEmpty()) {
            int next = this.in.read();
            if (next == -1) {
                return next;
            }
            this.readBuffer = this.readBuffer + (char)next;
        }
        while (true) {
            SortedMap<String, String> possibleTokens;
            if ((possibleTokens = this.resolvedTokens.tailMap(this.readBuffer)).isEmpty() || !possibleTokens.firstKey().startsWith(this.readBuffer)) {
                return this.getFirstCharacterFromReadBuffer();
            }
            if (this.readBuffer.equals(possibleTokens.firstKey())) {
                this.replaceData = this.resolvedTokens.get(this.readBuffer);
                this.replaceIndex = 0;
                this.readBuffer = "";
                return this.read();
            }
            int next = this.in.read();
            if (next == -1) break;
            this.readBuffer = this.readBuffer + (char)next;
        }
        return this.getFirstCharacterFromReadBuffer();
    }

    private int getFirstCharacterFromReadBuffer() {
        if (this.readBuffer.isEmpty()) {
            return -1;
        }
        char chr = this.readBuffer.charAt(0);
        this.readBuffer = this.readBuffer.substring(1);
        return chr;
    }

    public void setBeginToken(String beginToken) {
        this.beginToken = beginToken;
    }

    private String getBeginToken() {
        return this.beginToken;
    }

    public void setEndToken(String endToken) {
        this.endToken = endToken;
    }

    private String getEndToken() {
        return this.endToken;
    }

    public void setPropertiesResource(Resource r) {
        this.makeTokensFromProperties(r);
    }

    public void addConfiguredToken(Token token) {
        this.hash.put(token.getKey(), token.getValue());
        this.resolvedTokensBuilt = false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Properties getProperties(Resource resource) {
        InputStream in = null;
        Properties props = new Properties();
        try {
            in = resource.getInputStream();
            props.load(in);
        }
        catch (IOException ioe) {
            if (this.getProject() != null) {
                this.getProject().log("getProperties failed, " + ioe.getMessage(), 0);
            } else {
                ioe.printStackTrace();
            }
        }
        finally {
            FileUtils.close(in);
        }
        return props;
    }

    private void setTokens(Hashtable<String, String> hash) {
        this.hash = hash;
    }

    private Hashtable<String, String> getTokens() {
        return this.hash;
    }

    @Override
    public Reader chain(Reader rdr) {
        ReplaceTokens newFilter = new ReplaceTokens(rdr);
        newFilter.setBeginToken(this.getBeginToken());
        newFilter.setEndToken(this.getEndToken());
        newFilter.setTokens(this.getTokens());
        newFilter.setInitialized(true);
        return newFilter;
    }

    private void initialize() {
        Parameter[] params = this.getParameters();
        if (params != null) {
            for (Parameter param : params) {
                String name;
                if (param == null) continue;
                String type = param.getType();
                if ("tokenchar".equals(type)) {
                    name = param.getName();
                    if ("begintoken".equals(name)) {
                        this.beginToken = param.getValue();
                        continue;
                    }
                    if (!"endtoken".equals(name)) continue;
                    this.endToken = param.getValue();
                    continue;
                }
                if ("token".equals(type)) {
                    name = param.getName();
                    String value = param.getValue();
                    this.hash.put(name, value);
                    continue;
                }
                if (!"propertiesfile".equals(type)) continue;
                this.makeTokensFromProperties(new FileResource(new File(param.getValue())));
            }
        }
    }

    private void makeTokensFromProperties(Resource r) {
        Properties props = this.getProperties(r);
        props.stringPropertyNames().forEach(key -> this.hash.put((String)key, props.getProperty((String)key)));
    }

    public static class Token {
        private String key;
        private String value;

        public final void setKey(String key) {
            this.key = key;
        }

        public final void setValue(String value) {
            this.value = value;
        }

        public final String getKey() {
            return this.key;
        }

        public final String getValue() {
            return this.value;
        }
    }
}

