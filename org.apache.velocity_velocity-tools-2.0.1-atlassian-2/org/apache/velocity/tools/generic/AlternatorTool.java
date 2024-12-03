/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.tools.generic;

import java.util.Collection;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.generic.Alternator;
import org.apache.velocity.tools.generic.SafeConfig;
import org.apache.velocity.tools.generic.ValueParser;

@DefaultKey(value="alternator")
public class AlternatorTool
extends SafeConfig {
    @Deprecated
    public static final String OLD_AUTO_ALTERNATE_DEFAULT_KEY = "auto-alternate";
    public static final String AUTO_ALTERNATE_DEFAULT_KEY = "autoAlternate";
    private boolean autoAlternateDefault = true;

    @Override
    protected void configure(ValueParser parser) {
        Boolean auto = parser.getBoolean(AUTO_ALTERNATE_DEFAULT_KEY);
        if (auto == null) {
            auto = parser.getBoolean(OLD_AUTO_ALTERNATE_DEFAULT_KEY, Boolean.TRUE);
        }
        this.autoAlternateDefault = auto;
    }

    public boolean getAutoAlternateDefault() {
        return this.autoAlternateDefault;
    }

    protected void setAutoAlternateDefault(boolean bool) {
        this.autoAlternateDefault = bool;
    }

    public Alternator make(Object ... list) {
        return this.make(this.autoAlternateDefault, list);
    }

    @Deprecated
    public Alternator make(Collection list) {
        return this.make(this.autoAlternateDefault, list);
    }

    public Alternator make(boolean auto, Object ... list) {
        if (list == null || list.length == 0) {
            return null;
        }
        if (list.length == 1 && list[0] instanceof Collection && ((Collection)list[0]).isEmpty()) {
            return null;
        }
        return new Alternator(auto, list);
    }

    @Deprecated
    public Alternator make(boolean auto, Collection list) {
        return this.make(auto, new Object[]{list});
    }

    @Deprecated
    public Alternator make(Object o1, Object o2) {
        return this.make(this.autoAlternateDefault, o1, o2);
    }

    @Deprecated
    public Alternator make(boolean auto, Object o1, Object o2) {
        if (o1 == null || o2 == null) {
            return null;
        }
        return new Alternator(auto, o1, o2);
    }

    public Alternator auto(Object ... list) {
        return this.make(true, list);
    }

    @Deprecated
    public Alternator auto(Collection list) {
        return this.make(true, list);
    }

    @Deprecated
    public Alternator auto(Object o1, Object o2) {
        return this.make(true, o1, o2);
    }

    public Alternator manual(Object ... list) {
        return this.make(false, list);
    }

    @Deprecated
    public Alternator manual(Collection list) {
        return this.make(false, list);
    }

    @Deprecated
    public Alternator manual(Object o1, Object o2) {
        return this.make(false, o1, o2);
    }
}

