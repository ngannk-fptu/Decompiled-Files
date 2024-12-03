/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.LayoutPreservingProperties;

public class PropertyFile
extends Task {
    private String comment;
    private Properties properties;
    private File propertyfile;
    private boolean useJDKProperties;
    private Vector<Entry> entries = new Vector();

    @Override
    public void execute() throws BuildException {
        this.checkParameters();
        this.readFile();
        this.executeOperation();
        this.writeFile();
    }

    public Entry createEntry() {
        Entry e = new Entry();
        this.entries.addElement(e);
        return e;
    }

    private void executeOperation() throws BuildException {
        this.entries.forEach(e -> e.executeOn(this.properties));
    }

    private void readFile() throws BuildException {
        block20: {
            this.properties = this.useJDKProperties ? new Properties() : new LayoutPreservingProperties();
            try {
                if (this.propertyfile.exists()) {
                    this.log("Updating property file: " + this.propertyfile.getAbsolutePath());
                    try (InputStream fis = Files.newInputStream(this.propertyfile.toPath(), new OpenOption[0]);
                         BufferedInputStream bis = new BufferedInputStream(fis);){
                        this.properties.load(bis);
                        break block20;
                    }
                }
                this.log("Creating new property file: " + this.propertyfile.getAbsolutePath());
                try (OutputStream out = Files.newOutputStream(this.propertyfile.toPath(), new OpenOption[0]);){
                    out.flush();
                }
            }
            catch (IOException ioe) {
                throw new BuildException(ioe.toString());
            }
        }
    }

    private void checkParameters() throws BuildException {
        if (!this.checkParam(this.propertyfile)) {
            throw new BuildException("file token must not be null.", this.getLocation());
        }
    }

    public void setFile(File file) {
        this.propertyfile = file;
    }

    public void setComment(String hdr) {
        this.comment = hdr;
    }

    public void setJDKProperties(boolean val) {
        this.useJDKProperties = val;
    }

    private void writeFile() throws BuildException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            this.properties.store(baos, this.comment);
        }
        catch (IOException x) {
            throw new BuildException(x, this.getLocation());
        }
        try {
            try (OutputStream os = Files.newOutputStream(this.propertyfile.toPath(), new OpenOption[0]);){
                os.write(baos.toByteArray());
            }
            catch (IOException x) {
                FileUtils.getFileUtils().tryHardToDelete(this.propertyfile);
                throw x;
            }
        }
        catch (IOException x) {
            throw new BuildException(x, this.getLocation());
        }
    }

    private boolean checkParam(File param) {
        return param != null;
    }

    public static class Entry {
        private static final int DEFAULT_INT_VALUE = 0;
        private static final String DEFAULT_DATE_VALUE = "now";
        private static final String DEFAULT_STRING_VALUE = "";
        private String key = null;
        private int type = 2;
        private int operation = 2;
        private String value = null;
        private String defaultValue = null;
        private String newValue = null;
        private String pattern = null;
        private int field = 5;

        public void setKey(String value) {
            this.key = value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public void setOperation(Operation value) {
            this.operation = Operation.toOperation(value.getValue());
        }

        public void setType(Type value) {
            this.type = Type.toType(value.getValue());
        }

        public void setDefault(String value) {
            this.defaultValue = value;
        }

        public void setPattern(String value) {
            this.pattern = value;
        }

        public void setUnit(Unit unit) {
            this.field = unit.getCalendarField();
        }

        protected void executeOn(Properties props) throws BuildException {
            block7: {
                this.checkParameters();
                if (this.operation == 3) {
                    props.remove(this.key);
                    return;
                }
                String oldValue = (String)props.get(this.key);
                try {
                    if (this.type == 0) {
                        this.executeInteger(oldValue);
                        break block7;
                    }
                    if (this.type == 1) {
                        this.executeDate(oldValue);
                        break block7;
                    }
                    if (this.type == 2) {
                        this.executeString(oldValue);
                        break block7;
                    }
                    throw new BuildException("Unknown operation type: %d", this.type);
                }
                catch (NullPointerException npe) {
                    npe.printStackTrace();
                }
            }
            if (this.newValue == null) {
                this.newValue = DEFAULT_STRING_VALUE;
            }
            props.put(this.key, this.newValue);
        }

        private void executeDate(String oldValue) throws BuildException {
            Calendar currentValue = Calendar.getInstance();
            if (this.pattern == null) {
                this.pattern = "yyyy/MM/dd HH:mm";
            }
            SimpleDateFormat fmt = new SimpleDateFormat(this.pattern);
            String currentStringValue = this.getCurrentValue(oldValue);
            if (currentStringValue == null) {
                currentStringValue = DEFAULT_DATE_VALUE;
            }
            if (DEFAULT_DATE_VALUE.equals(currentStringValue)) {
                currentValue.setTime(new Date());
            } else {
                try {
                    currentValue.setTime(fmt.parse(currentStringValue));
                }
                catch (ParseException parseException) {
                    // empty catch block
                }
            }
            if (this.operation != 2) {
                int offset = 0;
                try {
                    offset = Integer.parseInt(this.value);
                    if (this.operation == 1) {
                        offset = -1 * offset;
                    }
                }
                catch (NumberFormatException e) {
                    throw new BuildException("Value not an integer on " + this.key);
                }
                currentValue.add(this.field, offset);
            }
            this.newValue = fmt.format(currentValue.getTime());
        }

        private void executeInteger(String oldValue) throws BuildException {
            int currentValue = 0;
            int newV = 0;
            DecimalFormat fmt = this.pattern != null ? new DecimalFormat(this.pattern) : new DecimalFormat();
            try {
                String curval = this.getCurrentValue(oldValue);
                currentValue = curval != null ? fmt.parse(curval).intValue() : 0;
            }
            catch (NumberFormatException | ParseException curval) {
                // empty catch block
            }
            if (this.operation == 2) {
                newV = currentValue;
            } else {
                int operationValue = 1;
                if (this.value != null) {
                    try {
                        operationValue = fmt.parse(this.value).intValue();
                    }
                    catch (NumberFormatException | ParseException exception) {
                        // empty catch block
                    }
                }
                if (this.operation == 0) {
                    newV = currentValue + operationValue;
                } else if (this.operation == 1) {
                    newV = currentValue - operationValue;
                }
            }
            this.newValue = fmt.format(newV);
        }

        private void executeString(String oldValue) throws BuildException {
            String newV = DEFAULT_STRING_VALUE;
            String currentValue = this.getCurrentValue(oldValue);
            if (currentValue == null) {
                currentValue = DEFAULT_STRING_VALUE;
            }
            if (this.operation == 2) {
                newV = currentValue;
            } else if (this.operation == 0) {
                newV = currentValue + this.value;
            }
            this.newValue = newV;
        }

        private void checkParameters() throws BuildException {
            if (this.type == 2 && this.operation == 1) {
                throw new BuildException("- is not supported for string properties (key:" + this.key + ")");
            }
            if (this.value == null && this.defaultValue == null && this.operation != 3) {
                throw new BuildException("\"value\" and/or \"default\" attribute must be specified (key: %s)", this.key);
            }
            if (this.key == null) {
                throw new BuildException("key is mandatory");
            }
            if (this.type == 2 && this.pattern != null) {
                throw new BuildException("pattern is not supported for string properties (key: %s)", this.key);
            }
        }

        private String getCurrentValue(String oldValue) {
            String ret = null;
            if (this.operation == 2) {
                if (this.value != null && this.defaultValue == null) {
                    ret = this.value;
                }
                if (this.value == null && this.defaultValue != null && oldValue != null) {
                    ret = oldValue;
                }
                if (this.value == null && this.defaultValue != null && oldValue == null) {
                    ret = this.defaultValue;
                }
                if (this.value != null && this.defaultValue != null && oldValue != null) {
                    ret = this.value;
                }
                if (this.value != null && this.defaultValue != null && oldValue == null) {
                    ret = this.defaultValue;
                }
            } else {
                ret = oldValue == null ? this.defaultValue : oldValue;
            }
            return ret;
        }

        public static class Type
        extends EnumeratedAttribute {
            public static final int INTEGER_TYPE = 0;
            public static final int DATE_TYPE = 1;
            public static final int STRING_TYPE = 2;

            @Override
            public String[] getValues() {
                return new String[]{"int", "date", "string"};
            }

            public static int toType(String type) {
                if ("int".equals(type)) {
                    return 0;
                }
                if ("date".equals(type)) {
                    return 1;
                }
                return 2;
            }
        }

        public static class Operation
        extends EnumeratedAttribute {
            public static final int INCREMENT_OPER = 0;
            public static final int DECREMENT_OPER = 1;
            public static final int EQUALS_OPER = 2;
            public static final int DELETE_OPER = 3;

            @Override
            public String[] getValues() {
                return new String[]{"+", "-", "=", "del"};
            }

            public static int toOperation(String oper) {
                if ("+".equals(oper)) {
                    return 0;
                }
                if ("-".equals(oper)) {
                    return 1;
                }
                if ("del".equals(oper)) {
                    return 3;
                }
                return 2;
            }
        }
    }

    public static class Unit
    extends EnumeratedAttribute {
        private static final String MILLISECOND = "millisecond";
        private static final String SECOND = "second";
        private static final String MINUTE = "minute";
        private static final String HOUR = "hour";
        private static final String DAY = "day";
        private static final String WEEK = "week";
        private static final String MONTH = "month";
        private static final String YEAR = "year";
        private static final String[] UNITS = new String[]{"millisecond", "second", "minute", "hour", "day", "week", "month", "year"};
        private Map<String, Integer> calendarFields = new HashMap<String, Integer>();

        public Unit() {
            this.calendarFields.put(MILLISECOND, 14);
            this.calendarFields.put(SECOND, 13);
            this.calendarFields.put(MINUTE, 12);
            this.calendarFields.put(HOUR, 11);
            this.calendarFields.put(DAY, 5);
            this.calendarFields.put(WEEK, 3);
            this.calendarFields.put(MONTH, 2);
            this.calendarFields.put(YEAR, 1);
        }

        public int getCalendarField() {
            return this.calendarFields.get(this.getValue().toLowerCase());
        }

        @Override
        public String[] getValues() {
            return UNITS;
        }
    }
}

