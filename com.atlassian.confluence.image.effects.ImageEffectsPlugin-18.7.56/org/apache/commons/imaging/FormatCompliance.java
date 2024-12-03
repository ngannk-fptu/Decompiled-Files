/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.imaging.ImageReadException;

public class FormatCompliance {
    private static final Logger LOGGER = Logger.getLogger(FormatCompliance.class.getName());
    private final boolean failOnError;
    private final String description;
    private final List<String> comments = new ArrayList<String>();

    public FormatCompliance(String description) {
        this.description = description;
        this.failOnError = false;
    }

    public FormatCompliance(String description, boolean failOnError) {
        this.description = description;
        this.failOnError = failOnError;
    }

    public static FormatCompliance getDefault() {
        return new FormatCompliance("ignore", false);
    }

    public void addComment(String comment) throws ImageReadException {
        this.comments.add(comment);
        if (this.failOnError) {
            throw new ImageReadException(comment);
        }
    }

    public void addComment(String comment, int value) throws ImageReadException {
        this.addComment(comment + ": " + this.getValueDescription(value));
    }

    public String toString() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        this.dump(pw);
        return sw.getBuffer().toString();
    }

    public void dump() {
        try (StringWriter sw = new StringWriter();
             PrintWriter pw = new PrintWriter(sw);){
            this.dump(pw);
            pw.flush();
            sw.flush();
            LOGGER.fine(sw.toString());
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void dump(PrintWriter pw) {
        pw.println("Format Compliance: " + this.description);
        if (this.comments.isEmpty()) {
            pw.println("\tNo comments.");
        } else {
            for (int i = 0; i < this.comments.size(); ++i) {
                pw.println("\t" + (i + 1) + ": " + this.comments.get(i));
            }
        }
        pw.println("");
        pw.flush();
    }

    private String getValueDescription(int value) {
        return value + " (" + Integer.toHexString(value) + ")";
    }

    public boolean compareBytes(String name, byte[] expected, byte[] actual) throws ImageReadException {
        if (expected.length != actual.length) {
            this.addComment(name + ": Unexpected length: (expected: " + expected.length + ", actual: " + actual.length + ")");
            return false;
        }
        for (int i = 0; i < expected.length; ++i) {
            if (expected[i] == actual[i]) continue;
            this.addComment(name + ": Unexpected value: (expected: " + this.getValueDescription(expected[i]) + ", actual: " + this.getValueDescription(actual[i]) + ")");
            return false;
        }
        return true;
    }

    public boolean checkBounds(String name, int min, int max, int actual) throws ImageReadException {
        if (actual < min || actual > max) {
            this.addComment(name + ": bounds check: " + min + " <= " + actual + " <= " + max + ": false");
            return false;
        }
        return true;
    }

    public boolean compare(String name, int valid, int actual) throws ImageReadException {
        return this.compare(name, new int[]{valid}, actual);
    }

    public boolean compare(String name, int[] valid, int actual) throws ImageReadException {
        for (int element : valid) {
            if (actual != element) continue;
            return true;
        }
        StringBuilder result = new StringBuilder(43);
        result.append(name);
        result.append(": Unexpected value: (valid: ");
        if (valid.length > 1) {
            result.append('{');
        }
        for (int i = 0; i < valid.length; ++i) {
            if (i > 0) {
                result.append(", ");
            }
            result.append(this.getValueDescription(valid[i]));
        }
        if (valid.length > 1) {
            result.append('}');
        }
        result.append(", actual: ").append(this.getValueDescription(actual)).append(")");
        this.addComment(result.toString());
        return false;
    }
}

