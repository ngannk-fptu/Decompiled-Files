/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.context.query;

import java.util.Map;
import org.terracotta.context.ContextElement;
import org.terracotta.context.TreeNode;
import org.terracotta.context.query.Matcher;

public final class Matchers {
    private Matchers() {
    }

    public static Matcher<TreeNode> context(final Matcher<ContextElement> matcher) {
        return new Matcher<TreeNode>(){

            @Override
            protected boolean matchesSafely(TreeNode t) {
                return matcher.matches(t.getContext());
            }

            public String toString() {
                return "a context that has " + matcher;
            }
        };
    }

    public static Matcher<ContextElement> attributes(final Matcher<Map<String, Object>> matcher) {
        return new Matcher<ContextElement>(){

            @Override
            protected boolean matchesSafely(ContextElement t) {
                return matcher.matches(t.attributes());
            }

            public String toString() {
                return "an attributes " + matcher;
            }
        };
    }

    public static Matcher<ContextElement> identifier(final Matcher<Class<?>> matcher) {
        return new Matcher<ContextElement>(){

            @Override
            protected boolean matchesSafely(ContextElement t) {
                return matcher.matches(t.identifier());
            }

            public String toString() {
                return "an identifier that is " + matcher;
            }
        };
    }

    public static Matcher<Class<?>> subclassOf(final Class<?> klazz) {
        return new Matcher<Class<?>>(){

            @Override
            protected boolean matchesSafely(Class<?> t) {
                return klazz.isAssignableFrom(t);
            }

            public String toString() {
                return "a subtype of " + klazz;
            }
        };
    }

    public static Matcher<Map<String, Object>> hasAttribute(final String key, final Object value) {
        return new Matcher<Map<String, Object>>(){

            @Override
            protected boolean matchesSafely(Map<String, Object> object) {
                return object.containsKey(key) && value.equals(object.get(key));
            }
        };
    }

    public static Matcher<Map<String, Object>> hasAttribute(final String key, final Matcher<? extends Object> value) {
        return new Matcher<Map<String, Object>>(){

            @Override
            protected boolean matchesSafely(Map<String, Object> object) {
                return object.containsKey(key) && value.matches(object.get(key));
            }
        };
    }

    public static <T> Matcher<T> anyOf(final Matcher<? super T> ... matchers) {
        return new Matcher<T>(){

            @Override
            protected boolean matchesSafely(T object) {
                for (Matcher matcher : matchers) {
                    if (!matcher.matches(object)) continue;
                    return true;
                }
                return false;
            }
        };
    }

    public static <T> Matcher<T> allOf(final Matcher<? super T> ... matchers) {
        return new Matcher<T>(){

            @Override
            protected boolean matchesSafely(T object) {
                for (Matcher matcher : matchers) {
                    if (matcher.matches(object)) continue;
                    return false;
                }
                return true;
            }
        };
    }

    public static <T> Matcher<T> not(final Matcher<T> matcher) {
        return new Matcher<T>(){

            @Override
            protected boolean matchesSafely(T object) {
                return !matcher.matches(object);
            }
        };
    }
}

