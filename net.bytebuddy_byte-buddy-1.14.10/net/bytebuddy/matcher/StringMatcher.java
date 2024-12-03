/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.matcher;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class StringMatcher
extends ElementMatcher.Junction.ForNonNullValues<String> {
    private final String value;
    private final Mode mode;

    public StringMatcher(String value, Mode mode) {
        this.value = value;
        this.mode = mode;
    }

    @Override
    protected boolean doMatch(String target) {
        return this.mode.matches(this.value, target);
    }

    public String toString() {
        return this.mode.getDescription() + '(' + this.value + ')';
    }

    @Override
    public boolean equals(@MaybeNull Object object) {
        if (!super.equals(object)) {
            return false;
        }
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (this.getClass() != object.getClass()) {
            return false;
        }
        if (!this.mode.equals((Object)((StringMatcher)object).mode)) {
            return false;
        }
        return this.value.equals(((StringMatcher)object).value);
    }

    @Override
    public int hashCode() {
        return (super.hashCode() * 31 + this.value.hashCode()) * 31 + this.mode.hashCode();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Mode {
        EQUALS_FULLY("equals"){

            protected boolean matches(String expected, String actual) {
                return actual.equals(expected);
            }
        }
        ,
        EQUALS_FULLY_IGNORE_CASE("equalsIgnoreCase"){

            protected boolean matches(String expected, String actual) {
                return actual.equalsIgnoreCase(expected);
            }
        }
        ,
        STARTS_WITH("startsWith"){

            protected boolean matches(String expected, String actual) {
                return actual.startsWith(expected);
            }
        }
        ,
        STARTS_WITH_IGNORE_CASE("startsWithIgnoreCase"){

            @SuppressFBWarnings(value={"DM_CONVERT_CASE"}, justification="Both strings are transformed by the default locale.")
            protected boolean matches(String expected, String actual) {
                return actual.toLowerCase().startsWith(expected.toLowerCase());
            }
        }
        ,
        ENDS_WITH("endsWith"){

            protected boolean matches(String expected, String actual) {
                return actual.endsWith(expected);
            }
        }
        ,
        ENDS_WITH_IGNORE_CASE("endsWithIgnoreCase"){

            @SuppressFBWarnings(value={"DM_CONVERT_CASE"}, justification="Both strings are transformed by the default locale.")
            protected boolean matches(String expected, String actual) {
                return actual.toLowerCase().endsWith(expected.toLowerCase());
            }
        }
        ,
        CONTAINS("contains"){

            protected boolean matches(String expected, String actual) {
                return actual.contains(expected);
            }
        }
        ,
        CONTAINS_IGNORE_CASE("containsIgnoreCase"){

            @SuppressFBWarnings(value={"DM_CONVERT_CASE"}, justification="Both strings are transformed by the default locale.")
            protected boolean matches(String expected, String actual) {
                return actual.toLowerCase().contains(expected.toLowerCase());
            }
        }
        ,
        MATCHES("matches"){

            protected boolean matches(String expected, String actual) {
                return actual.matches(expected);
            }
        };

        private final String description;

        private Mode(String description) {
            this.description = description;
        }

        protected String getDescription() {
            return this.description;
        }

        protected abstract boolean matches(String var1, String var2);
    }
}

