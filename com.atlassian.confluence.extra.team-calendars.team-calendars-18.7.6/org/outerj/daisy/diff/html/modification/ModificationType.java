/*
 * Decompiled with CFR 0.152.
 */
package org.outerj.daisy.diff.html.modification;

public enum ModificationType {
    CHANGED{

        public String toString() {
            return "changed";
        }
    }
    ,
    REMOVED{

        public String toString() {
            return "removed";
        }
    }
    ,
    ADDED{

        public String toString() {
            return "added";
        }
    }
    ,
    NONE{

        public String toString() {
            return "none";
        }
    };

}

