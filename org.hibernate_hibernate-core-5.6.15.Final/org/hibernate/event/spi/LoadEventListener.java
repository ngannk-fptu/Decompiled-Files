/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.event.spi.LoadEvent;

public interface LoadEventListener
extends Serializable {
    public static final LoadType RELOAD = LoadType.access$400(LoadType.access$300(LoadType.access$200(LoadType.access$100(new LoadType("RELOAD"), false), false), true), false);
    public static final LoadType GET = LoadType.access$400(LoadType.access$300(LoadType.access$200(LoadType.access$100(new LoadType("GET"), true), false), true), false);
    public static final LoadType LOAD = LoadType.access$400(LoadType.access$300(LoadType.access$200(LoadType.access$100(new LoadType("LOAD"), false), true), true), false);
    public static final LoadType IMMEDIATE_LOAD = LoadType.access$400(LoadType.access$300(LoadType.access$200(LoadType.access$100(new LoadType("IMMEDIATE_LOAD"), true), false), false), true);
    public static final LoadType INTERNAL_LOAD_EAGER = LoadType.access$400(LoadType.access$300(LoadType.access$200(LoadType.access$100(new LoadType("INTERNAL_LOAD_EAGER"), false), false), false), false);
    public static final LoadType INTERNAL_LOAD_LAZY = LoadType.access$400(LoadType.access$300(LoadType.access$200(LoadType.access$100(new LoadType("INTERNAL_LOAD_LAZY"), false), true), false), false);
    public static final LoadType INTERNAL_LOAD_NULLABLE = LoadType.access$400(LoadType.access$300(LoadType.access$200(LoadType.access$100(new LoadType("INTERNAL_LOAD_NULLABLE"), true), false), false), false);

    public void onLoad(LoadEvent var1, LoadType var2) throws HibernateException;

    public static final class LoadType {
        private String name;
        private boolean nakedEntityReturned;
        private boolean allowNulls;
        private boolean checkDeleted;
        private boolean allowProxyCreation;

        private LoadType(String name) {
            this.name = name;
        }

        public boolean isAllowNulls() {
            return this.allowNulls;
        }

        private LoadType setAllowNulls(boolean allowNulls) {
            this.allowNulls = allowNulls;
            return this;
        }

        public boolean isNakedEntityReturned() {
            return this.nakedEntityReturned;
        }

        private LoadType setNakedEntityReturned(boolean immediateLoad) {
            this.nakedEntityReturned = immediateLoad;
            return this;
        }

        public boolean isCheckDeleted() {
            return this.checkDeleted;
        }

        private LoadType setCheckDeleted(boolean checkDeleted) {
            this.checkDeleted = checkDeleted;
            return this;
        }

        public boolean isAllowProxyCreation() {
            return this.allowProxyCreation;
        }

        private LoadType setAllowProxyCreation(boolean allowProxyCreation) {
            this.allowProxyCreation = allowProxyCreation;
            return this;
        }

        public String getName() {
            return this.name;
        }

        public String toString() {
            return this.name;
        }

        static /* synthetic */ LoadType access$100(LoadType x0, boolean x1) {
            return x0.setAllowNulls(x1);
        }

        static /* synthetic */ LoadType access$200(LoadType x0, boolean x1) {
            return x0.setAllowProxyCreation(x1);
        }

        static /* synthetic */ LoadType access$300(LoadType x0, boolean x1) {
            return x0.setCheckDeleted(x1);
        }

        static /* synthetic */ LoadType access$400(LoadType x0, boolean x1) {
            return x0.setNakedEntityReturned(x1);
        }
    }
}

