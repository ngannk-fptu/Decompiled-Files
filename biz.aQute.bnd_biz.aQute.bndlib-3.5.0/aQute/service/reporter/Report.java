/*
 * Decompiled with CFR 0.152.
 */
package aQute.service.reporter;

import java.util.List;

public interface Report {
    public List<String> getWarnings();

    public List<String> getErrors();

    public Location getLocation(String var1);

    public boolean isOk();

    public static class Location {
        public String message;
        public int line;
        public String file;
        public String header;
        public String context;
        public String reference;
        public String methodName;
        public Object details;
        public int length;
    }
}

