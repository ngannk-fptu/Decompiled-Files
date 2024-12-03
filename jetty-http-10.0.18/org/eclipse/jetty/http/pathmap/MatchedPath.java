/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.http.pathmap;

public interface MatchedPath {
    public static final MatchedPath EMPTY = new MatchedPath(){

        @Override
        public String getPathMatch() {
            return null;
        }

        @Override
        public String getPathInfo() {
            return null;
        }

        public String toString() {
            return MatchedPath.class.getSimpleName() + ".EMPTY";
        }
    };

    public static MatchedPath from(final String pathMatch, final String pathInfo) {
        return new MatchedPath(){

            @Override
            public String getPathMatch() {
                return pathMatch;
            }

            @Override
            public String getPathInfo() {
                return pathInfo;
            }

            public String toString() {
                return MatchedPath.class.getSimpleName() + "[pathMatch=" + pathMatch + ", pathInfo=" + pathInfo + "]";
            }
        };
    }

    public String getPathMatch();

    public String getPathInfo();
}

