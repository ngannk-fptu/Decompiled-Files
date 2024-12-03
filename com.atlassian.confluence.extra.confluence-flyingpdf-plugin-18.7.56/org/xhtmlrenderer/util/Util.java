/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.util;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import javax.swing.JDialog;
import javax.swing.JFrame;
import org.xhtmlrenderer.util.Uu;

public class Util {
    private PrintWriter pw = null;
    private boolean on = true;

    public Util(PrintWriter writer) {
        this.pw = writer;
    }

    public Util(OutputStream out) {
        this.pw = new PrintWriter(out);
    }

    public void print(Object o) {
        this.println(o, false);
    }

    public void println(Object o) {
        this.println(o, true);
    }

    public void println(Object o, boolean line) {
        if (o == null) {
            this.ps("null");
            return;
        }
        if (o instanceof Object[]) {
            this.print_array((Object[])o);
            return;
        }
        if (o instanceof int[]) {
            this.print_array((int[])o);
        }
        if (o instanceof String) {
            this.ps((String)o, line);
            return;
        }
        if (o instanceof Exception) {
            this.ps(Util.stack_to_string((Exception)o));
            return;
        }
        if (o instanceof Vector) {
            this.print_vector((Vector)o);
            return;
        }
        if (o instanceof Hashtable) {
            this.print_hashtable((Hashtable)o);
            return;
        }
        if (o instanceof Date) {
            this.print_date((Date)o);
            return;
        }
        if (o instanceof Calendar) {
            this.print_calendar((Calendar)o);
            return;
        }
        this.ps(o.toString(), line);
    }

    public void print_vector(Vector v) {
        this.ps("vector: size=" + v.size());
        for (int i = 0; i < v.size(); ++i) {
            this.ps(v.elementAt(i).toString());
        }
    }

    public void print_array(int[][] array) {
        this.print("array: size=" + array.length + " by " + array[0].length);
        for (int i = 0; i < array.length; ++i) {
            for (int j = 0; j < array[i].length; ++j) {
                this.ps(array[i][j] + " ", false);
            }
            this.print("");
        }
    }

    public void print_array(Object[] array) {
        this.print("array: size=" + array.length);
        for (int i = 0; i < array.length; ++i) {
            this.ps(" " + array[i].toString(), false);
        }
    }

    public void print_array(int[] array) {
        this.print("array: size=" + array.length);
        for (int i = 0; i < array.length; ++i) {
            this.ps(" " + array[i], false);
        }
    }

    public void print_hashtable(Hashtable h) {
        this.print("hashtable size=" + h.size());
        Enumeration keys = h.keys();
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            this.print(key + " = ");
            this.print(h.get(key).toString());
        }
    }

    public void print_array(byte[] array) {
        this.print("byte array: size = " + array.length);
        for (int i = 0; i < array.length; ++i) {
            this.print("" + array[i]);
        }
    }

    public void print_date(Date date) {
        DateFormat date_format = DateFormat.getDateTimeInstance(1, 1);
        this.print(date_format.format(date));
    }

    public void print_calendar(Calendar cal) {
        this.print(cal.getTime());
    }

    public void printUnixtime(long sec) {
        this.print(new Date(sec * 1000L));
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public void setPrintWriter(PrintWriter writer) {
        this.pw = writer;
    }

    private void ps(String s) {
        this.ps(s, true);
    }

    private void ps(String s, boolean line) {
        if (!this.on) {
            return;
        }
        if (line) {
            if (this.pw == null) {
                System.out.println(s);
            } else {
                this.pw.println(s);
            }
        } else if (this.pw == null) {
            System.out.print(s);
        } else {
            this.pw.print(s);
        }
    }

    public static String file_to_string(String filename) throws FileNotFoundException, IOException {
        File file = new File(filename);
        return Util.file_to_string(file);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void string_to_file(String text, File file) throws IOException {
        FileWriter writer = null;
        writer = new FileWriter(file);
        try {
            int n;
            StringReader reader = new StringReader(text);
            char[] buf = new char[1000];
            while ((n = reader.read(buf, 0, 1000)) != -1) {
                writer.write(buf, 0, n);
            }
            writer.flush();
        }
        finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public static int string_to_int(String str) {
        return Integer.parseInt(str);
    }

    public static String stack_to_string(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.close();
        return sw.toString();
    }

    public static String stack_to_string(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.close();
        return sw.toString();
    }

    public static String inputstream_to_string(InputStream in) throws IOException {
        int n;
        InputStreamReader reader = new InputStreamReader(in);
        StringWriter writer = new StringWriter();
        char[] buf = new char[1000];
        while ((n = ((Reader)reader).read(buf, 0, 1000)) != -1) {
            writer.write(buf, 0, n);
        }
        return writer.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String file_to_string(File file) throws IOException {
        String str;
        FileReader reader = null;
        StringWriter writer = null;
        try {
            int n;
            reader = new FileReader(file);
            writer = new StringWriter();
            char[] buf = new char[1000];
            while ((n = reader.read(buf, 0, 1000)) != -1) {
                writer.write(buf, 0, n);
            }
            str = writer.toString();
        }
        finally {
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
        }
        return str;
    }

    public static String replace(String source, String target, String replacement) {
        StringBuffer output = new StringBuffer();
        int n = 0;
        while (true) {
            int off;
            if ((off = source.indexOf(target, n)) == -1) break;
            output.append(source.substring(n, off));
            output.append(replacement);
            n = off + target.length();
        }
        output.append(source.substring(n));
        return output.toString();
    }

    public static String[] vector_to_strings(Vector v) {
        int len = v.size();
        String[] ret = new String[len];
        for (int i = 0; i < len; ++i) {
            ret[i] = v.elementAt(i).toString();
        }
        return ret;
    }

    public static String[] list_to_strings(List l) {
        int len = l.size();
        String[] ret = new String[len];
        for (int i = 0; i < len; ++i) {
            ret[i] = l.get(i).toString();
        }
        return ret;
    }

    public static List toList(Object[] array) {
        return Util.to_list(array);
    }

    public static List to_list(Object[] array) {
        ArrayList<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length; ++i) {
            list.add(array[i]);
        }
        return list;
    }

    public static void sleep(long msec) {
        try {
            Thread.sleep(msec);
        }
        catch (InterruptedException ex) {
            Uu.p(Util.stack_to_string(ex));
        }
    }

    public static void center(JFrame frame) {
        Dimension screen_size = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((int)((screen_size.getWidth() - (double)frame.getWidth()) / 2.0), (int)((screen_size.getHeight() - (double)frame.getHeight()) / 2.0));
    }

    public static void center(JDialog frame) {
        Dimension screen_size = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((int)((screen_size.getWidth() - (double)frame.getWidth()) / 2.0), (int)((screen_size.getHeight() - (double)frame.getHeight()) / 2.0));
    }

    public static boolean isNumber(String str) {
        try {
            Integer.parseInt(str);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isNullOrEmpty(String str, boolean trim) {
        return str == null || str.length() == 0 || trim && str.trim().length() == 0;
    }

    public static boolean isEqual(String str1, String str2) {
        return str1 == str2 || str1 != null && str1.equals(str2);
    }

    public static boolean isEqual(String str1, String str2, boolean ignoreCase) {
        return str1 == str2 || str1 != null && (ignoreCase ? str1.equalsIgnoreCase(str2) : str1.equals(str2));
    }
}

