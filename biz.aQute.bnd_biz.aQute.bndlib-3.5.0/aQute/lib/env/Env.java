/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.env;

import aQute.lib.converter.Converter;
import aQute.lib.env.Header;
import aQute.lib.io.IO;
import aQute.lib.utf8properties.UTF8Properties;
import aQute.libg.reporter.ReporterAdapter;
import aQute.libg.sed.Domain;
import aQute.libg.sed.Replacer;
import aQute.libg.sed.ReplacerAdapter;
import aQute.service.reporter.Reporter;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

public class Env
extends ReporterAdapter
implements Replacer,
Domain {
    final Properties properties;
    final ReplacerAdapter replacer = new ReplacerAdapter(this);
    final Env parent;
    File base;
    boolean prepared;

    public Env() {
        this(new UTF8Properties(), null, null);
    }

    public Env(Properties properties, Env parent, File base) {
        this.properties = properties;
        this.parent = parent;
        this.base = base;
        if (parent != null) {
            this.setTrace(parent.isTrace());
            this.setExceptions(parent.isExceptions());
            this.setPedantic(parent.isPedantic());
        }
    }

    public Env(Env env) {
        this(new UTF8Properties(env.properties), env, null);
    }

    @Override
    public String process(String line) {
        return this.replacer.process(line);
    }

    @Override
    public Map<String, String> getMap() {
        Properties map = this.properties;
        return map;
    }

    @Override
    public Domain getParent() {
        return this.parent;
    }

    public String getProperty(String key) {
        return this.getProperty(key, null);
    }

    public String getProperty(String key, String deflt) {
        String value = this.properties.getProperty(key);
        if (value == null) {
            value = deflt;
        }
        if (value == null) {
            return null;
        }
        return this.process(value);
    }

    public void setProperty(String key, String value) {
        this.properties.put(key, value);
    }

    public void addProperty(String key, String value) {
        String old = this.properties.getProperty(key);
        old = old == null ? value : old + "," + value;
        this.properties.put(key, value);
    }

    public void removeProperty(String key) {
        this.properties.remove(key);
    }

    public void putAll(Map<String, String> map) {
        this.properties.putAll(map);
    }

    public void putAll(Properties map) {
        this.putAll((Map<String, String>)map);
    }

    public void addAll(Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            this.addProperty(entry.getKey(), entry.getValue());
        }
    }

    public void addAll(Properties map) {
        this.addAll((Map<String, String>)map);
    }

    public void setProperties(File file) throws Exception {
        if (!file.isFile()) {
            this.error("No such file %s", file);
        } else {
            UTF8Properties props = new UTF8Properties();
            props.load(file, this);
            this.putAll(props);
        }
    }

    public void addProperties(File file, Pattern matching) throws Exception {
        if (!file.isDirectory()) {
            this.setProperties(file);
        } else {
            for (File sub : file.listFiles()) {
                if (matching != null && !matching.matcher(sub.getName()).matches()) continue;
                this.addProperties(file, matching);
            }
        }
    }

    public void setProperties(URI uri) throws Exception {
        UTF8Properties props = new UTF8Properties();
        try (InputStream in = uri.toURL().openStream();){
            props.load(in, null, (Reporter)this);
        }
        this.putAll(props);
    }

    public Header getHeader(String header) {
        return new Header(this.getProperty(header));
    }

    public Header getHeader(String header, String deflt) {
        return new Header(this.getProperty(header, deflt));
    }

    public File getBase() {
        if (this.base == null) {
            if (this.parent != null) {
                return this.parent.getBase();
            }
            return IO.work;
        }
        return this.base;
    }

    public void setBase(File file) {
        this.base = file;
    }

    public File getFile(String file) {
        return IO.getFile(this.getBase(), file);
    }

    public void addTarget(Object domain) {
        this.replacer.addTarget(domain);
    }

    public void removeTarget(Object domain) {
        this.replacer.removeTarget(domain);
    }

    protected boolean prepare() throws Exception {
        boolean old = this.prepared;
        this.prepared = true;
        return old;
    }

    protected boolean isPrepared() {
        return this.prepared;
    }

    protected boolean clear() {
        boolean old = this.prepared;
        this.prepared = false;
        return old;
    }

    protected Properties getProperties() {
        return this.properties;
    }

    public File getFile(String file, String notfound) {
        File f = IO.getFile(this.getBase(), file);
        if (!f.isFile() && notfound != null) {
            this.error(notfound, f.getAbsolutePath());
            f = null;
        }
        return f;
    }

    public File getDir(String file, String notfound) {
        File f = IO.getFile(this.base, file);
        if (!f.isDirectory() && notfound != null) {
            this.error(notfound, f.getAbsolutePath());
            f = null;
        }
        return f;
    }

    public <T> T config(Class<?> front, final String prefix) {
        final Env THIS = this;
        return (T)Proxy.newProxyInstance(front.getClassLoader(), new Class[]{front}, new InvocationHandler(){

            @Override
            public Object invoke(Object target, Method method, Object[] parameters) throws Throwable {
                String name = Env.this.mangleMethodName(prefix, method.getName());
                if (parameters == null || parameters.length == 0) {
                    String value = Env.this.getProperty(name);
                    if (value == null) {
                        if (method.getReturnType().isPrimitive()) {
                            return Converter.cnv(method.getReturnType(), null);
                        }
                        return null;
                    }
                    if (method.getReturnType().isInstance(value)) {
                        return value;
                    }
                    return Converter.cnv(method.getGenericReturnType(), (Object)value);
                }
                if (parameters.length == 1) {
                    String arg = parameters[0].toString();
                    if (arg == null) {
                        Env.this.removeProperty(name);
                    } else {
                        Env.this.setProperty(name, arg.toString());
                    }
                    if (method.getReturnType().isInstance(THIS)) {
                        return THIS;
                    }
                    return Converter.cnv(method.getReturnType(), null);
                }
                throw new IllegalArgumentException("Too many arguments: " + Arrays.toString(parameters));
            }
        });
    }

    public <T> T config(Class<?> front) {
        return this.config(front, null);
    }

    String mangleMethodName(String prefix, String string) {
        StringBuilder sb = new StringBuilder();
        if (prefix != null) {
            sb.append(prefix);
        }
        sb.append(string);
        for (int i = 0; i < sb.length(); ++i) {
            boolean twice;
            char c = sb.charAt(i);
            boolean bl = twice = i < sb.length() - 1 && sb.charAt(i + 1) == c;
            if (c != '$' && c != '_') continue;
            if (twice) {
                sb.deleteCharAt(i + 1);
                continue;
            }
            if (c == '$') {
                sb.deleteCharAt(i--);
                continue;
            }
            sb.setCharAt(i, '.');
        }
        return sb.toString();
    }

    public boolean isTrue(String v) {
        return v != null && v.length() > 0 && !v.equalsIgnoreCase("false");
    }
}

