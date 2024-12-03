/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.engine.spi;

import java.util.HashMap;
import java.util.Map;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.CascadeStyle;
import org.hibernate.engine.spi.CascadingAction;
import org.hibernate.engine.spi.CascadingActions;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.jboss.logging.Logger;

public final class CascadeStyles {
    private static final Logger log = Logger.getLogger(CascadeStyles.class);
    public static final CascadeStyle ALL_DELETE_ORPHAN = new BaseCascadeStyle(){

        @Override
        public boolean doCascade(CascadingAction action) {
            return true;
        }

        @Override
        public boolean hasOrphanDelete() {
            return true;
        }

        public String toString() {
            return "STYLE_ALL_DELETE_ORPHAN";
        }
    };
    public static final CascadeStyle ALL = new BaseCascadeStyle(){

        @Override
        public boolean doCascade(CascadingAction action) {
            return true;
        }

        public String toString() {
            return "STYLE_ALL";
        }
    };
    public static final CascadeStyle UPDATE = new BaseCascadeStyle(){

        @Override
        public boolean doCascade(CascadingAction action) {
            return action == CascadingActions.SAVE_UPDATE;
        }

        public String toString() {
            return "STYLE_SAVE_UPDATE";
        }
    };
    public static final CascadeStyle LOCK = new BaseCascadeStyle(){

        @Override
        public boolean doCascade(CascadingAction action) {
            return action == CascadingActions.LOCK;
        }

        public String toString() {
            return "STYLE_LOCK";
        }
    };
    public static final CascadeStyle REFRESH = new BaseCascadeStyle(){

        @Override
        public boolean doCascade(CascadingAction action) {
            return action == CascadingActions.REFRESH;
        }

        public String toString() {
            return "STYLE_REFRESH";
        }
    };
    public static final CascadeStyle EVICT = new BaseCascadeStyle(){

        @Override
        public boolean doCascade(CascadingAction action) {
            return action == CascadingActions.EVICT;
        }

        public String toString() {
            return "STYLE_EVICT";
        }
    };
    public static final CascadeStyle REPLICATE = new BaseCascadeStyle(){

        @Override
        public boolean doCascade(CascadingAction action) {
            return action == CascadingActions.REPLICATE;
        }

        public String toString() {
            return "STYLE_REPLICATE";
        }
    };
    public static final CascadeStyle MERGE = new BaseCascadeStyle(){

        @Override
        public boolean doCascade(CascadingAction action) {
            return action == CascadingActions.MERGE;
        }

        public String toString() {
            return "STYLE_MERGE";
        }
    };
    public static final CascadeStyle PERSIST = new BaseCascadeStyle(){

        @Override
        public boolean doCascade(CascadingAction action) {
            return action == CascadingActions.PERSIST || action == CascadingActions.PERSIST_ON_FLUSH;
        }

        public String toString() {
            return "STYLE_PERSIST";
        }
    };
    public static final CascadeStyle DELETE = new BaseCascadeStyle(){

        @Override
        public boolean doCascade(CascadingAction action) {
            return action == CascadingActions.DELETE;
        }

        public String toString() {
            return "STYLE_DELETE";
        }
    };
    public static final CascadeStyle DELETE_ORPHAN = new BaseCascadeStyle(){

        @Override
        public boolean doCascade(CascadingAction action) {
            return action == CascadingActions.DELETE || action == CascadingActions.SAVE_UPDATE;
        }

        @Override
        public boolean reallyDoCascade(CascadingAction action) {
            return action == CascadingActions.DELETE;
        }

        @Override
        public boolean hasOrphanDelete() {
            return true;
        }

        public String toString() {
            return "STYLE_DELETE_ORPHAN";
        }
    };
    public static final CascadeStyle NONE = new BaseCascadeStyle(){

        @Override
        public boolean doCascade(CascadingAction action) {
            return false;
        }

        public String toString() {
            return "STYLE_NONE";
        }
    };
    private static final Map<String, CascadeStyle> STYLES = CascadeStyles.buildBaseCascadeStyleMap();

    private CascadeStyles() {
    }

    private static Map<String, CascadeStyle> buildBaseCascadeStyleMap() {
        HashMap<String, CascadeStyle> base = new HashMap<String, CascadeStyle>();
        base.put("all", ALL);
        base.put("all-delete-orphan", ALL_DELETE_ORPHAN);
        base.put("save-update", UPDATE);
        base.put("persist", PERSIST);
        base.put("merge", MERGE);
        base.put("lock", LOCK);
        base.put("refresh", REFRESH);
        base.put("replicate", REPLICATE);
        base.put("evict", EVICT);
        base.put("delete", DELETE);
        base.put("remove", DELETE);
        base.put("delete-orphan", DELETE_ORPHAN);
        base.put("none", NONE);
        return base;
    }

    public static CascadeStyle getCascadeStyle(String cascade) {
        CascadeStyle style = STYLES.get(cascade);
        if (style == null) {
            throw new MappingException("Unsupported cascade style: " + cascade);
        }
        return style;
    }

    public static void registerCascadeStyle(String name, BaseCascadeStyle cascadeStyle) {
        log.tracef("Registering external cascade style [%s : %s]", (Object)name, (Object)cascadeStyle);
        CascadeStyle old = STYLES.put(name, cascadeStyle);
        if (old != null) {
            log.debugf("External cascade style registration [%s : %s] overrode base registration [%s]", (Object)name, (Object)cascadeStyle, (Object)old);
        }
    }

    public static final class MultipleCascadeStyle
    extends BaseCascadeStyle {
        private final CascadeStyle[] styles;

        public MultipleCascadeStyle(CascadeStyle[] styles) {
            this.styles = styles;
        }

        @Override
        public boolean doCascade(CascadingAction action) {
            for (CascadeStyle style : this.styles) {
                if (!style.doCascade(action)) continue;
                return true;
            }
            return false;
        }

        @Override
        public boolean reallyDoCascade(CascadingAction action) {
            for (CascadeStyle style : this.styles) {
                if (!style.reallyDoCascade(action)) continue;
                return true;
            }
            return false;
        }

        @Override
        public boolean hasOrphanDelete() {
            for (CascadeStyle style : this.styles) {
                if (!style.hasOrphanDelete()) continue;
                return true;
            }
            return false;
        }

        public String toString() {
            return ArrayHelper.toString(this.styles);
        }
    }

    public static abstract class BaseCascadeStyle
    implements CascadeStyle {
        @Override
        public boolean reallyDoCascade(CascadingAction action) {
            return this.doCascade(action);
        }

        @Override
        public boolean hasOrphanDelete() {
            return false;
        }
    }
}

