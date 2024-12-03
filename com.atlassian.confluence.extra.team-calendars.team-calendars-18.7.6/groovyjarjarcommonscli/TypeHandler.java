/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarcommonscli;

import groovyjarjarcommonscli.ParseException;
import groovyjarjarcommonscli.PatternOptionBuilder;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

public class TypeHandler {
    public static Object createValue(String str, Object obj) throws ParseException {
        return TypeHandler.createValue(str, (Class)obj);
    }

    public static Object createValue(String str, Class clazz) throws ParseException {
        if (PatternOptionBuilder.STRING_VALUE == clazz) {
            return str;
        }
        if (PatternOptionBuilder.OBJECT_VALUE == clazz) {
            return TypeHandler.createObject(str);
        }
        if (PatternOptionBuilder.NUMBER_VALUE == clazz) {
            return TypeHandler.createNumber(str);
        }
        if (PatternOptionBuilder.DATE_VALUE == clazz) {
            return TypeHandler.createDate(str);
        }
        if (PatternOptionBuilder.CLASS_VALUE == clazz) {
            return TypeHandler.createClass(str);
        }
        if (PatternOptionBuilder.FILE_VALUE == clazz) {
            return TypeHandler.createFile(str);
        }
        if (PatternOptionBuilder.EXISTING_FILE_VALUE == clazz) {
            return TypeHandler.createFile(str);
        }
        if (PatternOptionBuilder.FILES_VALUE == clazz) {
            return TypeHandler.createFiles(str);
        }
        if (PatternOptionBuilder.URL_VALUE == clazz) {
            return TypeHandler.createURL(str);
        }
        return null;
    }

    public static Object createObject(String classname) throws ParseException {
        Class<?> cl = null;
        try {
            cl = Class.forName(classname);
        }
        catch (ClassNotFoundException cnfe) {
            throw new ParseException("Unable to find the class: " + classname);
        }
        Object instance = null;
        try {
            instance = cl.newInstance();
        }
        catch (Exception e) {
            throw new ParseException(e.getClass().getName() + "; Unable to create an instance of: " + classname);
        }
        return instance;
    }

    public static Number createNumber(String str) throws ParseException {
        try {
            if (str.indexOf(46) != -1) {
                return Double.valueOf(str);
            }
            return Long.valueOf(str);
        }
        catch (NumberFormatException e) {
            throw new ParseException(e.getMessage());
        }
    }

    public static Class createClass(String classname) throws ParseException {
        try {
            return Class.forName(classname);
        }
        catch (ClassNotFoundException e) {
            throw new ParseException("Unable to find the class: " + classname);
        }
    }

    public static Date createDate(String str) throws ParseException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static URL createURL(String str) throws ParseException {
        try {
            return new URL(str);
        }
        catch (MalformedURLException e) {
            throw new ParseException("Unable to parse the URL: " + str);
        }
    }

    public static File createFile(String str) throws ParseException {
        return new File(str);
    }

    public static File[] createFiles(String str) throws ParseException {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

