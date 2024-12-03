/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  zipkin2.Span
 */
package zipkin2.reporter;

import zipkin2.Span;

public interface Reporter<S> {
    public static final Reporter<Span> NOOP = new Reporter<Span>(){

        @Override
        public void report(Span span) {
        }

        public String toString() {
            return "NoopReporter{}";
        }
    };
    public static final Reporter<Span> CONSOLE = new Reporter<Span>(){

        @Override
        public void report(Span span) {
            System.out.println(span.toString());
        }

        public String toString() {
            return "ConsoleReporter{}";
        }
    };

    public void report(S var1);
}

