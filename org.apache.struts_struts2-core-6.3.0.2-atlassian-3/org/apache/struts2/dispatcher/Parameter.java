/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.text.StringEscapeUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.dispatcher;

import java.util.Objects;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface Parameter {
    public String getName();

    public String getValue();

    public boolean isDefined();

    public boolean isMultiple();

    public String[] getMultipleValues();

    public Object getObject();

    public static class Empty
    implements Parameter {
        private String name;

        public Empty(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getValue() {
            return null;
        }

        @Override
        public boolean isDefined() {
            return false;
        }

        @Override
        public boolean isMultiple() {
            return false;
        }

        @Override
        public String[] getMultipleValues() {
            return new String[0];
        }

        @Override
        public Object getObject() {
            return null;
        }

        public String toString() {
            return "Empty{name='" + this.name + '\'' + '}';
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Empty)) {
                return false;
            }
            Empty empty = (Empty)o;
            return Objects.equals(this.name, empty.name);
        }

        public int hashCode() {
            return Objects.hash(this.name);
        }
    }

    public static class File
    extends Request {
        public File(String name, Object value) {
            super(name, value);
        }

        @Override
        public String toString() {
            return "File{name='" + this.getName() + '\'' + '}';
        }
    }

    public static class Request
    implements Parameter {
        private static final Logger LOG = LogManager.getLogger(Request.class);
        private final String name;
        private final Object value;

        public Request(String name, Object value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getValue() {
            String[] values = this.toStringArray();
            return values != null && values.length > 0 ? values[0] : null;
        }

        private String[] toStringArray() {
            if (this.value == null) {
                LOG.trace("The value is null, empty array of string will be returned!");
                return new String[0];
            }
            if (this.value.getClass().isArray()) {
                LOG.trace("Converting value {} to array of strings", this.value);
                Object[] values = (Object[])this.value;
                String[] strValues = new String[values.length];
                int i = 0;
                for (Object v : values) {
                    strValues[i] = Objects.toString(v, null);
                    ++i;
                }
                return strValues;
            }
            LOG.trace("Converting value {} to simple string", this.value);
            return new String[]{this.value.toString()};
        }

        @Override
        public boolean isDefined() {
            return this.value != null && this.toStringArray().length > 0;
        }

        @Override
        public boolean isMultiple() {
            return this.isDefined() && this.toStringArray().length > 1;
        }

        @Override
        public String[] getMultipleValues() {
            return this.toStringArray();
        }

        @Override
        public Object getObject() {
            return this.value;
        }

        public String toString() {
            return StringEscapeUtils.escapeHtml4((String)this.getValue());
        }
    }
}

