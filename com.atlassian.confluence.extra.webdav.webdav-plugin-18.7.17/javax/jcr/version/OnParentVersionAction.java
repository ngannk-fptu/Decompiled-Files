/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.version;

public final class OnParentVersionAction {
    public static final int COPY = 1;
    public static final int VERSION = 2;
    public static final int INITIALIZE = 3;
    public static final int COMPUTE = 4;
    public static final int IGNORE = 5;
    public static final int ABORT = 6;
    public static final String ACTIONNAME_COPY = "COPY";
    public static final String ACTIONNAME_VERSION = "VERSION";
    public static final String ACTIONNAME_INITIALIZE = "INITIALIZE";
    public static final String ACTIONNAME_COMPUTE = "COMPUTE";
    public static final String ACTIONNAME_IGNORE = "IGNORE";
    public static final String ACTIONNAME_ABORT = "ABORT";

    public static String nameFromValue(int action) {
        switch (action) {
            case 1: {
                return ACTIONNAME_COPY;
            }
            case 2: {
                return ACTIONNAME_VERSION;
            }
            case 3: {
                return ACTIONNAME_INITIALIZE;
            }
            case 4: {
                return ACTIONNAME_COMPUTE;
            }
            case 5: {
                return ACTIONNAME_IGNORE;
            }
            case 6: {
                return ACTIONNAME_ABORT;
            }
        }
        throw new IllegalArgumentException("unknown on-version action: " + action);
    }

    public static int valueFromName(String name) {
        if (name.equals(ACTIONNAME_COPY)) {
            return 1;
        }
        if (name.equals(ACTIONNAME_VERSION)) {
            return 2;
        }
        if (name.equals(ACTIONNAME_INITIALIZE)) {
            return 3;
        }
        if (name.equals(ACTIONNAME_COMPUTE)) {
            return 4;
        }
        if (name.equals(ACTIONNAME_IGNORE)) {
            return 5;
        }
        if (name.equals(ACTIONNAME_ABORT)) {
            return 6;
        }
        throw new IllegalArgumentException("unknown on-version action: " + name);
    }

    private OnParentVersionAction() {
    }
}

