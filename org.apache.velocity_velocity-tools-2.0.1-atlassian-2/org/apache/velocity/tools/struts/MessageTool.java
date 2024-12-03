/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.struts.util.MessageResources
 */
package org.apache.velocity.tools.struts;

import java.util.List;
import java.util.Locale;
import org.apache.struts.util.MessageResources;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.config.ValidScope;
import org.apache.velocity.tools.struts.MessageResourcesTool;

@DefaultKey(value="text")
@ValidScope(value={"request"})
public class MessageTool
extends MessageResourcesTool {
    public TextKey get(String key) {
        return new TextKey(key, null, null, this.getLocale());
    }

    public String get(String key, String bundle) {
        return this.get(key, bundle, (Object[])null);
    }

    public String get(String key, Object[] args) {
        return this.get(key, null, args);
    }

    public String get(String key, String bundle, Object[] args) {
        return this.get(key, bundle, args, this.getLocale());
    }

    public String get(String key, String bundle, Object[] args, Locale locale) {
        MessageResources res = this.getResources(bundle);
        if (res == null) {
            return null;
        }
        if (args == null) {
            return res.getMessage(locale, key);
        }
        return res.getMessage(locale, key, args);
    }

    public String get(String key, List args) {
        return this.get(key, null, args);
    }

    public String get(String key, String bundle, List args) {
        return this.get(key, bundle, args.toArray());
    }

    public String get(String key, String bundle, List args, Locale locale) {
        return this.get(key, bundle, args.toArray(), locale);
    }

    public boolean exists(String key) {
        return this.exists(key, null);
    }

    public boolean exists(String key, String bundle) {
        MessageResources res = this.getResources(bundle);
        if (res == null) {
            return false;
        }
        return res.isPresent(this.getLocale(), key);
    }

    public class TextKey {
        private final String key;
        private final String bundle;
        private final Object[] args;
        private final Locale locale;

        public TextKey(String key, String bundle, Object[] args, Locale locale) {
            this.key = key;
            this.bundle = bundle;
            this.args = args;
            this.locale = locale;
        }

        public TextKey get(String appendme) {
            StringBuilder sb = new StringBuilder(this.key);
            sb.append('.');
            sb.append(appendme);
            return new TextKey(sb.toString(), this.bundle, this.args, this.locale);
        }

        public TextKey bundle(String setme) {
            return new TextKey(this.key, setme, this.args, this.locale);
        }

        public TextKey locale(Locale setme) {
            return new TextKey(this.key, this.bundle, this.args, setme);
        }

        public TextKey insert(Object addme) {
            return this.insert(new Object[]{addme});
        }

        public TextKey insert(Object addme, Object metoo) {
            return this.insert(new Object[]{addme, metoo});
        }

        public TextKey insert(Object addme, Object metoo, Object methree) {
            return this.insert(new Object[]{addme, metoo, methree});
        }

        public TextKey insert(List addme) {
            Object[] newargs = addme.toArray();
            return this.insert(newargs);
        }

        public TextKey insert(Object[] addme) {
            Object[] newargs;
            if (this.args == null) {
                newargs = addme;
            } else {
                newargs = new Object[this.args.length + addme.length];
                System.arraycopy(this.args, 0, newargs, 0, this.args.length);
                System.arraycopy(addme, 0, newargs, this.args.length, addme.length);
            }
            return new TextKey(this.key, this.bundle, newargs, this.locale);
        }

        public TextKey clearArgs() {
            return new TextKey(this.key, this.bundle, null, this.locale);
        }

        public boolean getExists() {
            return this.exists();
        }

        public boolean exists() {
            return MessageTool.this.exists(this.key, this.bundle);
        }

        public String toString() {
            return MessageTool.this.get(this.key, this.bundle, this.args, this.locale);
        }
    }
}

