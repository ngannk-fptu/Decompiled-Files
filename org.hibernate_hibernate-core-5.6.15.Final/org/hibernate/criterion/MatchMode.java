/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.criterion;

public enum MatchMode {
    EXACT{

        @Override
        public String toMatchString(String pattern) {
            return pattern;
        }
    }
    ,
    START{

        @Override
        public String toMatchString(String pattern) {
            return pattern + '%';
        }
    }
    ,
    END{

        @Override
        public String toMatchString(String pattern) {
            return '%' + pattern;
        }
    }
    ,
    ANYWHERE{

        @Override
        public String toMatchString(String pattern) {
            return '%' + pattern + '%';
        }
    };


    public abstract String toMatchString(String var1);
}

