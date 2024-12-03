/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.tools.generic;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.generic.ConversionTool;

@DefaultKey(value="parser")
public class ValueParser
extends ConversionTool
implements Map<String, Object> {
    private Map<String, Object> source = null;
    private boolean allowSubkeys = true;
    private Boolean hasSubkeys = null;
    private boolean readOnly = true;
    public static final String ALLOWSUBKEYS_KEY = "allowSubkeys";
    public static final String READONLY_KEY = "readOnly";

    public ValueParser() {
    }

    public ValueParser(Map<String, Object> source) {
        this.setSource(source);
    }

    protected void setSource(Map<String, Object> source) {
        this.source = source;
    }

    protected Map<String, Object> getSource() {
        return this.source;
    }

    protected boolean getAllowSubkeys() {
        return this.allowSubkeys;
    }

    protected void setAllowSubkeys(boolean allow) {
        this.allowSubkeys = allow;
    }

    protected boolean getReadOnly() {
        return this.readOnly;
    }

    protected void setReadOnly(boolean ro) {
        this.readOnly = ro;
    }

    @Override
    protected void configure(ValueParser values) {
        Boolean ro;
        Boolean allow;
        super.configure(values);
        Boolean depMode = values.getBoolean("deprecationSupportMode");
        if (depMode != null && depMode.booleanValue()) {
            this.setAllowSubkeys(false);
        }
        if ((allow = values.getBoolean(ALLOWSUBKEYS_KEY)) != null) {
            this.setAllowSubkeys(allow);
        }
        if ((ro = values.getBoolean(READONLY_KEY)) != null) {
            this.setReadOnly(ro);
        }
    }

    public boolean exists(String key) {
        return this.getValue(key) != null;
    }

    public Object get(String key) {
        Object value = this.getValue(key);
        if (value == null && this.getSource() != null && this.getAllowSubkeys()) {
            value = this.getSubkey(key);
        }
        return value;
    }

    public Object getValue(String key) {
        if (this.getSource() == null) {
            return null;
        }
        return this.getSource().get(key);
    }

    public Object getValue(String key, Object alternate) {
        Object value = this.getValue(key);
        if (value == null) {
            return alternate;
        }
        return value;
    }

    public Object[] getValues(String key) {
        Object value = this.getValue(key);
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return this.parseStringList((String)value);
        }
        if (value instanceof Object[]) {
            return (Object[])value;
        }
        return new Object[]{value};
    }

    public String getString(String key) {
        return this.toString(this.getValue(key));
    }

    public String getString(String key, String alternate) {
        String s = this.getString(key);
        return s != null ? s : alternate;
    }

    public Boolean getBoolean(String key) {
        return this.toBoolean(this.getValue(key));
    }

    public boolean getBoolean(String key, boolean alternate) {
        Boolean bool = this.getBoolean(key);
        return bool != null ? bool : alternate;
    }

    public Boolean getBoolean(String key, Boolean alternate) {
        Boolean bool = this.getBoolean(key);
        return bool != null ? bool : alternate;
    }

    public Integer getInteger(String key) {
        return this.toInteger(this.getValue(key));
    }

    public Integer getInteger(String key, Integer alternate) {
        Integer num = this.getInteger(key);
        if (num == null) {
            return alternate;
        }
        return num;
    }

    public Double getDouble(String key) {
        return this.toDouble(this.getValue(key));
    }

    public Double getDouble(String key, Double alternate) {
        Double num = this.getDouble(key);
        if (num == null) {
            return alternate;
        }
        return num;
    }

    public Number getNumber(String key) {
        return this.toNumber(this.getValue(key));
    }

    public Locale getLocale(String key) {
        return this.toLocale(this.getValue(key));
    }

    public Number getNumber(String key, Number alternate) {
        Number n = this.getNumber(key);
        return n != null ? (Number)n : (Number)alternate;
    }

    public int getInt(String key, int alternate) {
        Number n = this.getNumber(key);
        return n != null ? n.intValue() : alternate;
    }

    public double getDouble(String key, double alternate) {
        Number n = this.getNumber(key);
        return n != null ? n.doubleValue() : alternate;
    }

    public Locale getLocale(String key, Locale alternate) {
        Locale l = this.getLocale(key);
        return l != null ? l : alternate;
    }

    public String[] getStrings(String key) {
        return this.toStrings(this.getValues(key));
    }

    public Boolean[] getBooleans(String key) {
        return this.toBooleans(this.getValues(key));
    }

    public Number[] getNumbers(String key) {
        return this.toNumbers(this.getValues(key));
    }

    public int[] getInts(String key) {
        return this.toInts(this.getValues(key));
    }

    public double[] getDoubles(String key) {
        return this.toDoubles(this.getValues(key));
    }

    public Locale[] getLocales(String key) {
        return this.toLocales(this.getValues(key));
    }

    public boolean hasSubkeys() {
        if (this.getSource() == null) {
            return false;
        }
        if (this.hasSubkeys == null) {
            for (String key : this.getSource().keySet()) {
                int dot = key.indexOf(46);
                if (dot <= 0 || dot >= key.length()) continue;
                this.hasSubkeys = Boolean.TRUE;
                break;
            }
            if (this.hasSubkeys == null) {
                this.hasSubkeys = Boolean.FALSE;
            }
        }
        return this.hasSubkeys;
    }

    protected ValueParser getSubkey(String subkey) {
        if (!this.hasSubkeys() || subkey == null || subkey.length() == 0) {
            return null;
        }
        HashMap<String, Object> values = null;
        subkey = subkey.concat(".");
        for (Map.Entry<String, Object> entry : this.getSource().entrySet()) {
            if (!entry.getKey().startsWith(subkey) || entry.getKey().length() <= subkey.length()) continue;
            if (values == null) {
                values = new HashMap<String, Object>();
            }
            values.put(entry.getKey().substring(subkey.length()), entry.getValue());
        }
        if (values == null) {
            return null;
        }
        return new ValueParser(values);
    }

    @Override
    public int size() {
        return this.getSource().size();
    }

    @Override
    public boolean isEmpty() {
        return this.getSource().isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.getSource().containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.getSource().containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return this.get(String.valueOf(key));
    }

    @Override
    public Object put(String key, Object value) {
        if (this.readOnly) {
            throw new UnsupportedOperationException("Cannot put(" + key + "," + value + "); " + this.getClass().getName() + " is read-only");
        }
        if (this.hasSubkeys != null && this.hasSubkeys.equals(Boolean.FALSE) && key.indexOf(46) != -1) {
            this.hasSubkeys = Boolean.TRUE;
        }
        return this.source.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        if (this.readOnly) {
            throw new UnsupportedOperationException("Cannot remove(" + key + "); " + this.getClass().getName() + " is read-only");
        }
        if (this.hasSubkeys != null && this.hasSubkeys.equals(Boolean.TRUE) && ((String)key).indexOf(46) != -1) {
            this.hasSubkeys = null;
        }
        return this.source.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
        if (this.readOnly) {
            throw new UnsupportedOperationException("Cannot putAll(" + m + "); " + this.getClass().getName() + " is read-only");
        }
        this.hasSubkeys = null;
        this.source.putAll(m);
    }

    @Override
    public void clear() {
        if (this.readOnly) {
            throw new UnsupportedOperationException("Cannot clear(); " + this.getClass().getName() + " is read-only");
        }
        this.hasSubkeys = Boolean.FALSE;
        this.source.clear();
    }

    @Override
    public Set<String> keySet() {
        return this.getSource().keySet();
    }

    @Override
    public Collection values() {
        return this.getSource().values();
    }

    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        return this.getSource().entrySet();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append('{');
        boolean empty = true;
        for (Map.Entry<String, Object> entry : this.entrySet()) {
            if (!empty) {
                builder.append(", ");
            }
            empty = false;
            builder.append(entry.getKey());
            builder.append('=');
            builder.append(String.valueOf(entry.getValue()));
        }
        builder.append('}');
        return builder.toString();
    }
}

