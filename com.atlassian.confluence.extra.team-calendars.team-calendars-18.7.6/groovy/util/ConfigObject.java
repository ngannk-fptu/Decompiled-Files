/*
 * Decompiled with CFR 0.152.
 */
package groovy.util;

import groovy.lang.GroovyObjectSupport;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.Writable;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.StringGroovyMethods;
import org.codehaus.groovy.syntax.Types;

public class ConfigObject
extends GroovyObjectSupport
implements Writable,
Map,
Cloneable {
    static final Collection<String> KEYWORDS = Types.getKeywords();
    static final String TAB_CHARACTER = "\t";
    private URL configFile;
    private HashMap delegateMap = new LinkedHashMap();

    public ConfigObject(URL file) {
        this.configFile = file;
    }

    public ConfigObject() {
        this(null);
    }

    public URL getConfigFile() {
        return this.configFile;
    }

    public void setConfigFile(URL configFile) {
        this.configFile = configFile;
    }

    @Override
    public Writer writeTo(Writer outArg) throws IOException {
        BufferedWriter out = new BufferedWriter(outArg);
        try {
            this.writeConfig("", this, out, 0, false);
        }
        finally {
            out.flush();
        }
        return outArg;
    }

    @Override
    public Object getProperty(String name) {
        if ("configFile".equals(name)) {
            return this.configFile;
        }
        if (!this.containsKey(name)) {
            ConfigObject prop = new ConfigObject(this.configFile);
            this.put(name, prop);
            return prop;
        }
        return this.get(name);
    }

    public Map flatten() {
        return this.flatten(null);
    }

    public Map flatten(Map target) {
        if (target == null) {
            target = new ConfigObject();
        }
        this.populate("", target, this);
        return target;
    }

    public Map merge(ConfigObject other) {
        return this.doMerge(this, other);
    }

    public Properties toProperties() {
        Properties props = new Properties();
        this.flatten(props);
        props = ConfigObject.convertValuesToString(props);
        return props;
    }

    public Properties toProperties(String prefix) {
        Properties props = new Properties();
        this.populate(prefix + ".", props, this);
        props = ConfigObject.convertValuesToString(props);
        return props;
    }

    private Map doMerge(Map config, Map other) {
        Iterator iterator = other.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry o;
            Map.Entry next = o = iterator.next();
            Object key = next.getKey();
            Object value = next.getValue();
            Object configEntry = config.get(key);
            if (configEntry == null) {
                config.put(key, value);
                continue;
            }
            if (configEntry instanceof Map && !((Map)configEntry).isEmpty() && value instanceof Map) {
                this.doMerge((Map)configEntry, (Map)value);
                continue;
            }
            config.put(key, value);
        }
        return config;
    }

    private void writeConfig(String prefix, ConfigObject map, BufferedWriter out, int tab, boolean apply) throws IOException {
        String space = apply ? StringGroovyMethods.multiply(TAB_CHARACTER, (Number)tab) : "";
        for (Object o1 : map.keySet()) {
            String key = (String)o1;
            Object v = map.get(key);
            if (v instanceof ConfigObject) {
                ConfigObject value = (ConfigObject)v;
                if (value.isEmpty()) continue;
                Map.Entry dotsInKeys = null;
                for (Object o : value.entrySet()) {
                    Map.Entry e = (Map.Entry)o;
                    String k = (String)e.getKey();
                    if (k.indexOf(46) <= -1) continue;
                    dotsInKeys = e;
                    break;
                }
                int configSize = value.size();
                Object firstKey = value.keySet().iterator().next();
                Object firstValue = value.values().iterator().next();
                int firstSize = firstValue instanceof ConfigObject ? ((ConfigObject)firstValue).size() : 1;
                if (configSize == 1 || DefaultGroovyMethods.asBoolean(dotsInKeys)) {
                    if (firstSize == 1 && firstValue instanceof ConfigObject) {
                        key = KEYWORDS.contains(key) ? InvokerHelper.inspect(key) : key;
                        String writePrefix = prefix + key + "." + firstKey + ".";
                        this.writeConfig(writePrefix, (ConfigObject)firstValue, out, tab, true);
                        continue;
                    }
                    if (!DefaultGroovyMethods.asBoolean(dotsInKeys) && firstValue instanceof ConfigObject) {
                        this.writeNode(key, space, tab, value, out);
                        continue;
                    }
                    for (Object j : value.keySet()) {
                        Object k2;
                        Object v2 = value.get(j);
                        Object object = k2 = ((String)j).indexOf(46) > -1 ? InvokerHelper.inspect(j) : j;
                        if (v2 instanceof ConfigObject) {
                            key = KEYWORDS.contains(key) ? InvokerHelper.inspect(key) : key;
                            this.writeConfig(prefix + key, (ConfigObject)v2, out, tab, false);
                            continue;
                        }
                        ConfigObject.writeValue(key + "." + k2, space, prefix, v2, out);
                    }
                    continue;
                }
                this.writeNode(key, space, tab, value, out);
                continue;
            }
            ConfigObject.writeValue(key, space, prefix, v, out);
        }
    }

    private static void writeValue(String key, String space, String prefix, Object value, BufferedWriter out) throws IOException {
        boolean isKeyword = KEYWORDS.contains(key);
        String string = key = isKeyword ? InvokerHelper.inspect(key) : key;
        if (!StringGroovyMethods.asBoolean(prefix) && isKeyword) {
            prefix = "this.";
        }
        out.append(space).append(prefix).append(key).append('=').append(InvokerHelper.inspect(value));
        out.newLine();
    }

    private void writeNode(String key, String space, int tab, ConfigObject value, BufferedWriter out) throws IOException {
        key = KEYWORDS.contains(key) ? InvokerHelper.inspect(key) : key;
        out.append(space).append(key).append(" {");
        out.newLine();
        this.writeConfig("", value, out, tab + 1, true);
        out.append(space).append('}');
        out.newLine();
    }

    private static Properties convertValuesToString(Map props) {
        Properties newProps = new Properties();
        Iterator iterator = props.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry o;
            Map.Entry next = o = iterator.next();
            Object key = next.getKey();
            Object value = next.getValue();
            newProps.put(key, value != null ? value.toString() : null);
        }
        return newProps;
    }

    private void populate(String suffix, Map config, Map map) {
        Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry o;
            Map.Entry next = o = iterator.next();
            Object key = next.getKey();
            Object value = next.getValue();
            if (value instanceof Map) {
                this.populate(suffix + key + ".", config, (Map)value);
                continue;
            }
            try {
                config.put(suffix + key, value);
            }
            catch (NullPointerException nullPointerException) {}
        }
    }

    @Override
    public int size() {
        return this.delegateMap.size();
    }

    @Override
    public boolean isEmpty() {
        return this.delegateMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.delegateMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.delegateMap.containsValue(value);
    }

    public Object get(Object key) {
        return this.delegateMap.get(key);
    }

    public Object put(Object key, Object value) {
        return this.delegateMap.put(key, value);
    }

    public Object remove(Object key) {
        return this.delegateMap.remove(key);
    }

    public void putAll(Map m) {
        this.delegateMap.putAll(m);
    }

    @Override
    public void clear() {
        this.delegateMap.clear();
    }

    public Set keySet() {
        return this.delegateMap.keySet();
    }

    public Collection values() {
        return this.delegateMap.values();
    }

    public Set entrySet() {
        return this.delegateMap.entrySet();
    }

    public ConfigObject clone() {
        try {
            ConfigObject clone = (ConfigObject)super.clone();
            clone.configFile = this.configFile;
            clone.delegateMap = (LinkedHashMap)this.delegateMap.clone();
            return clone;
        }
        catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public Boolean isSet(String option) {
        Object entry;
        if (!(!this.delegateMap.containsKey(option) || (entry = this.delegateMap.get(option)) instanceof ConfigObject && ((ConfigObject)entry).isEmpty())) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public String prettyPrint() {
        StringWriter sw = new StringWriter();
        try {
            this.writeTo(sw);
        }
        catch (IOException e) {
            throw new GroovyRuntimeException(e);
        }
        return sw.toString();
    }

    public String toString() {
        StringWriter sw = new StringWriter();
        try {
            InvokerHelper.write(sw, this);
        }
        catch (IOException e) {
            throw new GroovyRuntimeException(e);
        }
        return sw.toString();
    }
}

