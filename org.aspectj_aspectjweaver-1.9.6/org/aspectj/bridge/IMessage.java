/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.bridge;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.aspectj.bridge.ISourceLocation;

public interface IMessage {
    public static final IMessage[] RA_IMessage = new IMessage[0];
    public static final Kind WEAVEINFO = new Kind("weaveinfo", 5);
    public static final Kind INFO = new Kind("info", 10);
    public static final Kind DEBUG = new Kind("debug", 20);
    public static final Kind TASKTAG = new Kind("task", 25);
    public static final Kind WARNING = new Kind("warning", 30);
    public static final Kind ERROR = new Kind("error", 40);
    public static final Kind FAIL = new Kind("fail", 50);
    public static final Kind ABORT = new Kind("abort", 60);
    public static final List<Kind> KINDS = Collections.unmodifiableList(Arrays.asList(WEAVEINFO, INFO, DEBUG, TASKTAG, WARNING, ERROR, FAIL, ABORT));

    public String getMessage();

    public Kind getKind();

    public boolean isError();

    public boolean isWarning();

    public boolean isDebug();

    public boolean isInfo();

    public boolean isAbort();

    public boolean isTaskTag();

    public boolean isFailed();

    public boolean getDeclared();

    public int getID();

    public int getSourceStart();

    public int getSourceEnd();

    public Throwable getThrown();

    public ISourceLocation getSourceLocation();

    public String getDetails();

    public List<ISourceLocation> getExtraSourceLocations();

    public static final class Kind
    implements Comparable<Kind> {
        public static final Comparator<Kind> COMPARATOR = new Comparator<Kind>(){

            @Override
            public int compare(Kind one, Kind two) {
                if (null == one) {
                    return null == two ? 0 : -1;
                }
                if (null == two) {
                    return 1;
                }
                if (one == two) {
                    return 0;
                }
                return one.precedence - two.precedence;
            }
        };
        private final int precedence;
        private final String name;

        public boolean isSameOrLessThan(Kind kind) {
            return 0 >= COMPARATOR.compare(this, kind);
        }

        @Override
        public int compareTo(Kind other) {
            return COMPARATOR.compare(this, other);
        }

        private Kind(String name, int precedence) {
            this.name = name;
            this.precedence = precedence;
        }

        public String toString() {
            return this.name;
        }
    }
}

