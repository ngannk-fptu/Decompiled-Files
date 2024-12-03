/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.tools.generic;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.velocity.tools.ConversionUtils;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.generic.LocaleConfig;
import org.apache.velocity.tools.generic.ValueParser;

@DefaultKey(value="text")
public class ResourceTool
extends LocaleConfig {
    public static final String BUNDLES_KEY = "bundles";
    private String[] bundles = new String[]{"resources"};
    private boolean deprecationSupportMode = false;

    protected final void setDefaultBundle(String bundle) {
        if (bundle == null) {
            throw new NullPointerException("Default bundle cannot be null");
        }
        this.bundles = new String[]{bundle};
    }

    protected final String getDefaultBundle() {
        return this.bundles[0];
    }

    @Deprecated
    protected final void setDefaultLocale(Locale locale) {
        if (locale == null) {
            throw new NullPointerException("Default locale cannot be null");
        }
        super.setLocale(locale);
    }

    @Deprecated
    protected final Locale getDefaultLocale() {
        return super.getLocale();
    }

    @Deprecated
    public void setDeprecationSupportMode(boolean depMode) {
        this.deprecationSupportMode = depMode;
    }

    @Override
    protected void configure(ValueParser parser) {
        String[] bundles = parser.getStrings(BUNDLES_KEY);
        if (bundles != null) {
            this.bundles = bundles;
        }
        super.configure(parser);
    }

    public Key get(Object k) {
        String key = k == null ? null : String.valueOf(k);
        return this.get(key);
    }

    public Key get(String key) {
        return new Key(key, this.bundles, this.getLocale(), null);
    }

    public List<String> getKeys() {
        return this.getKeys(null, this.bundles, (Object)this.getLocale());
    }

    public Key bundle(String bundle) {
        return new Key(null, new String[]{bundle}, this.getLocale(), null);
    }

    public Key locale(Object locale) {
        return new Key(null, this.bundles, locale, null);
    }

    public Key insert(Object[] args) {
        return new Key(null, this.bundles, this.getLocale(), args);
    }

    public Key insert(List args) {
        return this.insert(args.toArray());
    }

    public Key insert(Object arg) {
        return this.insert(new Object[]{arg});
    }

    public Key insert(Object arg0, Object arg1) {
        return this.insert(new Object[]{arg0, arg1});
    }

    protected ResourceBundle getBundle(String baseName, Object loc) {
        Locale locale;
        Locale locale2 = locale = loc == null ? this.getLocale() : this.toLocale(loc);
        if (baseName == null || locale == null) {
            return null;
        }
        return ResourceBundle.getBundle(baseName, locale);
    }

    public Object get(Object key, String baseName, Object loc) {
        ResourceBundle bundle = this.getBundle(baseName, loc);
        if (key == null || bundle == null) {
            return null;
        }
        try {
            return bundle.getObject(String.valueOf(key));
        }
        catch (Exception e) {
            return null;
        }
    }

    public Object get(Object k, String[] bundles, Object l) {
        String key = k == null ? null : String.valueOf(k);
        for (int i = 0; i < bundles.length; ++i) {
            Object resource = this.get((Object)key, bundles[i], l);
            if (resource == null) continue;
            return resource;
        }
        return null;
    }

    public List<String> getKeys(String prefix, String baseName, Object loc) {
        ResourceBundle bundle = this.getBundle(baseName, loc);
        if (bundle == null) {
            return null;
        }
        Enumeration<String> keys = bundle.getKeys();
        if (keys == null) {
            return null;
        }
        ArrayList<String> list = new ArrayList<String>();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            if (prefix == null) {
                list.add(key);
                continue;
            }
            if (!key.startsWith(prefix)) continue;
            if ((key = key.substring(prefix.length(), key.length())).charAt(0) == '.') {
                key = key.substring(1, key.length());
            }
            list.add(key);
        }
        return list;
    }

    public List<String> getKeys(String prefix, String[] bundles, Object loc) {
        Locale locale;
        Locale locale2 = locale = loc == null ? this.getLocale() : this.toLocale(loc);
        if (locale == null || bundles == null || bundles.length == 0) {
            return null;
        }
        ArrayList<String> master = new ArrayList<String>();
        for (String bundle : bundles) {
            List<String> sub = this.getKeys(prefix, bundle, (Object)locale);
            if (sub == null) continue;
            master.addAll(sub);
        }
        return master;
    }

    private Locale toLocale(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Locale) {
            return (Locale)obj;
        }
        String s = String.valueOf(obj);
        return ConversionUtils.toLocale(s);
    }

    public String render(Object resource, Object[] args) {
        String value = String.valueOf(resource);
        if (this.deprecationSupportMode && args == null) {
            return value;
        }
        return MessageFormat.format(value, args);
    }

    public final class Key {
        private final String[] bundles;
        private final String key;
        private final Object locale;
        private final Object[] args;
        private boolean cached = false;
        private Object rawValue;

        public Key(String key, String[] bundles, Object locale, Object[] args) {
            this.key = key;
            this.bundles = bundles;
            this.locale = locale;
            this.args = args;
        }

        public Key get(Object k) {
            return this.get(String.valueOf(k));
        }

        public Key get(String key) {
            String newKey = this.key == null ? key : this.key + '.' + key;
            return new Key(newKey, this.bundles, this.locale, this.args);
        }

        public Key bundle(String bundle) {
            String[] newBundles = new String[]{bundle};
            return new Key(this.key, newBundles, this.locale, this.args);
        }

        public Key locale(Object locale) {
            return new Key(this.key, this.bundles, locale, this.args);
        }

        public Key insert(Object[] args) {
            Object[] newargs;
            if (this.args == null) {
                newargs = args;
            } else {
                newargs = new Object[this.args.length + args.length];
                System.arraycopy(this.args, 0, newargs, 0, this.args.length);
                System.arraycopy(args, 0, newargs, this.args.length, args.length);
            }
            return new Key(this.key, this.bundles, this.locale, newargs);
        }

        public Key insert(List args) {
            return this.insert(args.toArray());
        }

        public Key insert(Object arg) {
            return this.insert(new Object[]{arg});
        }

        public Key insert(Object arg0, Object arg1) {
            return this.insert(new Object[]{arg0, arg1});
        }

        public boolean getExists() {
            return this.getRaw() != null;
        }

        public Object getRaw() {
            if (!this.cached) {
                this.rawValue = ResourceTool.this.get((Object)this.key, this.bundles, this.locale);
                this.cached = true;
            }
            return this.rawValue;
        }

        public List<String> getKeys() {
            return ResourceTool.this.getKeys(this.key, this.bundles, this.locale);
        }

        public String toString() {
            if (this.key == null) {
                return "";
            }
            if (!this.getExists()) {
                return "???" + this.key + "???";
            }
            return ResourceTool.this.render(this.rawValue, this.args);
        }
    }
}

