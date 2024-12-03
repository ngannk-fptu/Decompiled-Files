/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config;

import com.typesafe.config.ConfigOrigin;
import com.typesafe.config.impl.ConfigImplUtil;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;

public abstract class ConfigException
extends RuntimeException
implements Serializable {
    private static final long serialVersionUID = 1L;
    private final transient ConfigOrigin origin;

    protected ConfigException(ConfigOrigin origin, String message, Throwable cause) {
        super(origin.description() + ": " + message, cause);
        this.origin = origin;
    }

    protected ConfigException(ConfigOrigin origin, String message) {
        this(origin.description() + ": " + message, null);
    }

    protected ConfigException(String message, Throwable cause) {
        super(message, cause);
        this.origin = null;
    }

    protected ConfigException(String message) {
        this(message, null);
    }

    public ConfigOrigin origin() {
        return this.origin;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        ConfigImplUtil.writeOrigin(out, this.origin);
    }

    private static <T> void setOriginField(T hasOriginField, Class<T> clazz, ConfigOrigin origin) throws IOException {
        Field f;
        try {
            f = clazz.getDeclaredField("origin");
        }
        catch (NoSuchFieldException e) {
            throw new IOException(clazz.getSimpleName() + " has no origin field?", e);
        }
        catch (SecurityException e) {
            throw new IOException("unable to fill out origin field in " + clazz.getSimpleName(), e);
        }
        f.setAccessible(true);
        try {
            f.set(hasOriginField, origin);
        }
        catch (IllegalArgumentException e) {
            throw new IOException("unable to set origin field", e);
        }
        catch (IllegalAccessException e) {
            throw new IOException("unable to set origin field", e);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        ConfigOrigin origin = ConfigImplUtil.readOrigin(in);
        ConfigException.setOriginField(this, ConfigException.class, origin);
    }

    public static class Generic
    extends ConfigException {
        private static final long serialVersionUID = 1L;

        public Generic(String message, Throwable cause) {
            super(message, cause);
        }

        public Generic(String message) {
            this(message, null);
        }
    }

    public static class BadBean
    extends BugOrBroken {
        private static final long serialVersionUID = 1L;

        public BadBean(String message, Throwable cause) {
            super(message, cause);
        }

        public BadBean(String message) {
            this(message, null);
        }
    }

    public static class ValidationFailed
    extends ConfigException {
        private static final long serialVersionUID = 1L;
        private final Iterable<ValidationProblem> problems;

        public ValidationFailed(Iterable<ValidationProblem> problems) {
            super(ValidationFailed.makeMessage(problems), null);
            this.problems = problems;
        }

        public Iterable<ValidationProblem> problems() {
            return this.problems;
        }

        private static String makeMessage(Iterable<ValidationProblem> problems) {
            StringBuilder sb = new StringBuilder();
            for (ValidationProblem p : problems) {
                sb.append(p.origin().description());
                sb.append(": ");
                sb.append(p.path());
                sb.append(": ");
                sb.append(p.problem());
                sb.append(", ");
            }
            if (sb.length() == 0) {
                throw new BugOrBroken("ValidationFailed must have a non-empty list of problems");
            }
            sb.setLength(sb.length() - 2);
            return sb.toString();
        }
    }

    public static class ValidationProblem
    implements Serializable {
        private final String path;
        private final transient ConfigOrigin origin;
        private final String problem;

        public ValidationProblem(String path, ConfigOrigin origin, String problem) {
            this.path = path;
            this.origin = origin;
            this.problem = problem;
        }

        public String path() {
            return this.path;
        }

        public ConfigOrigin origin() {
            return this.origin;
        }

        public String problem() {
            return this.problem;
        }

        private void writeObject(ObjectOutputStream out) throws IOException {
            out.defaultWriteObject();
            ConfigImplUtil.writeOrigin(out, this.origin);
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            ConfigOrigin origin = ConfigImplUtil.readOrigin(in);
            ConfigException.setOriginField(this, ValidationProblem.class, origin);
        }

        public String toString() {
            return "ValidationProblem(" + this.path + "," + this.origin + "," + this.problem + ")";
        }
    }

    public static class NotResolved
    extends BugOrBroken {
        private static final long serialVersionUID = 1L;

        public NotResolved(String message, Throwable cause) {
            super(message, cause);
        }

        public NotResolved(String message) {
            this(message, null);
        }
    }

    public static class UnresolvedSubstitution
    extends Parse {
        private static final long serialVersionUID = 1L;
        private final String detail;

        public UnresolvedSubstitution(ConfigOrigin origin, String detail, Throwable cause) {
            super(origin, "Could not resolve substitution to a value: " + detail, cause);
            this.detail = detail;
        }

        public UnresolvedSubstitution(ConfigOrigin origin, String detail) {
            this(origin, detail, null);
        }

        private UnresolvedSubstitution(UnresolvedSubstitution wrapped, ConfigOrigin origin, String message) {
            super(origin, message, wrapped);
            this.detail = wrapped.detail;
        }

        public UnresolvedSubstitution addExtraDetail(String extra) {
            return new UnresolvedSubstitution(this, this.origin(), String.format(extra, this.detail));
        }
    }

    public static class Parse
    extends ConfigException {
        private static final long serialVersionUID = 1L;

        public Parse(ConfigOrigin origin, String message, Throwable cause) {
            super(origin, message, cause);
        }

        public Parse(ConfigOrigin origin, String message) {
            this(origin, message, null);
        }
    }

    public static class IO
    extends ConfigException {
        private static final long serialVersionUID = 1L;

        public IO(ConfigOrigin origin, String message, Throwable cause) {
            super(origin, message, cause);
        }

        public IO(ConfigOrigin origin, String message) {
            this(origin, message, null);
        }
    }

    public static class BugOrBroken
    extends ConfigException {
        private static final long serialVersionUID = 1L;

        public BugOrBroken(String message, Throwable cause) {
            super(message, cause);
        }

        public BugOrBroken(String message) {
            this(message, null);
        }
    }

    public static class BadPath
    extends ConfigException {
        private static final long serialVersionUID = 1L;

        public BadPath(ConfigOrigin origin, String path, String message, Throwable cause) {
            super(origin, path != null ? "Invalid path '" + path + "': " + message : message, cause);
        }

        public BadPath(ConfigOrigin origin, String path, String message) {
            this(origin, path, message, null);
        }

        public BadPath(String path, String message, Throwable cause) {
            super(path != null ? "Invalid path '" + path + "': " + message : message, cause);
        }

        public BadPath(String path, String message) {
            this(path, message, null);
        }

        public BadPath(ConfigOrigin origin, String message) {
            this(origin, null, message);
        }
    }

    public static class BadValue
    extends ConfigException {
        private static final long serialVersionUID = 1L;

        public BadValue(ConfigOrigin origin, String path, String message, Throwable cause) {
            super(origin, "Invalid value at '" + path + "': " + message, cause);
        }

        public BadValue(ConfigOrigin origin, String path, String message) {
            this(origin, path, message, null);
        }

        public BadValue(String path, String message, Throwable cause) {
            super("Invalid value at '" + path + "': " + message, cause);
        }

        public BadValue(String path, String message) {
            this(path, message, null);
        }
    }

    public static class Null
    extends Missing {
        private static final long serialVersionUID = 1L;

        private static String makeMessage(String path, String expected) {
            if (expected != null) {
                return "Configuration key '" + path + "' is set to null but expected " + expected;
            }
            return "Configuration key '" + path + "' is null";
        }

        public Null(ConfigOrigin origin, String path, String expected, Throwable cause) {
            super(origin, Null.makeMessage(path, expected), cause);
        }

        public Null(ConfigOrigin origin, String path, String expected) {
            this(origin, path, expected, null);
        }
    }

    public static class Missing
    extends ConfigException {
        private static final long serialVersionUID = 1L;

        public Missing(String path, Throwable cause) {
            super("No configuration setting found for key '" + path + "'", cause);
        }

        public Missing(ConfigOrigin origin, String path) {
            this(origin, "No configuration setting found for key '" + path + "'", null);
        }

        public Missing(String path) {
            this(path, null);
        }

        protected Missing(ConfigOrigin origin, String message, Throwable cause) {
            super(origin, message, cause);
        }
    }

    public static class WrongType
    extends ConfigException {
        private static final long serialVersionUID = 1L;

        public WrongType(ConfigOrigin origin, String path, String expected, String actual, Throwable cause) {
            super(origin, path + " has type " + actual + " rather than " + expected, cause);
        }

        public WrongType(ConfigOrigin origin, String path, String expected, String actual) {
            this(origin, path, expected, actual, null);
        }

        public WrongType(ConfigOrigin origin, String message, Throwable cause) {
            super(origin, message, cause);
        }

        public WrongType(ConfigOrigin origin, String message) {
            super(origin, message, null);
        }
    }
}

