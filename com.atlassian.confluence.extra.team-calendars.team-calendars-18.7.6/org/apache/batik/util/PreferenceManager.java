/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

public class PreferenceManager {
    protected Properties internal = null;
    protected Map defaults = null;
    protected String prefFileName = null;
    protected String fullName = null;
    protected static final String USER_HOME = PreferenceManager.getSystemProperty("user.home");
    protected static final String USER_DIR = PreferenceManager.getSystemProperty("user.dir");
    protected static final String FILE_SEP = PreferenceManager.getSystemProperty("file.separator");
    private static String PREF_DIR = null;

    protected static String getSystemProperty(String prop) {
        try {
            return System.getProperty(prop);
        }
        catch (AccessControlException e) {
            return "";
        }
    }

    public PreferenceManager(String prefFileName) {
        this(prefFileName, null);
    }

    public PreferenceManager(String prefFileName, Map defaults) {
        this.prefFileName = prefFileName;
        this.defaults = defaults;
        this.internal = new Properties();
    }

    public static void setPreferenceDirectory(String dir) {
        PREF_DIR = dir;
    }

    public static String getPreferenceDirectory() {
        return PREF_DIR;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void load() throws IOException {
        FileInputStream fis = null;
        if (this.fullName != null) {
            try {
                fis = new FileInputStream(this.fullName);
            }
            catch (IOException e1) {
                this.fullName = null;
            }
        }
        if (this.fullName == null) {
            if (PREF_DIR != null) {
                try {
                    this.fullName = PREF_DIR + FILE_SEP + this.prefFileName;
                    fis = new FileInputStream(this.fullName);
                }
                catch (IOException e2) {
                    this.fullName = null;
                }
            }
            if (this.fullName == null) {
                try {
                    this.fullName = USER_HOME + FILE_SEP + this.prefFileName;
                    fis = new FileInputStream(this.fullName);
                }
                catch (IOException e3) {
                    try {
                        this.fullName = USER_DIR + FILE_SEP + this.prefFileName;
                        fis = new FileInputStream(this.fullName);
                    }
                    catch (IOException e4) {
                        this.fullName = null;
                    }
                }
            }
        }
        if (this.fullName != null) {
            try {
                this.internal.load(fis);
            }
            finally {
                fis.close();
            }
        }
    }

    public void save() throws IOException {
        FileOutputStream fos = null;
        if (this.fullName != null) {
            try {
                fos = new FileOutputStream(this.fullName);
            }
            catch (IOException e1) {
                this.fullName = null;
            }
        }
        if (this.fullName == null) {
            if (PREF_DIR != null) {
                try {
                    this.fullName = PREF_DIR + FILE_SEP + this.prefFileName;
                    fos = new FileOutputStream(this.fullName);
                }
                catch (IOException e2) {
                    this.fullName = null;
                }
            }
            if (this.fullName == null) {
                try {
                    this.fullName = USER_HOME + FILE_SEP + this.prefFileName;
                    fos = new FileOutputStream(this.fullName);
                }
                catch (IOException e3) {
                    this.fullName = null;
                    throw e3;
                }
            }
        }
        try {
            this.internal.store(fos, this.prefFileName);
        }
        finally {
            fos.close();
        }
    }

    private Object getDefault(String key) {
        if (this.defaults != null) {
            return this.defaults.get(key);
        }
        return null;
    }

    public Rectangle getRectangle(String key) {
        Rectangle defaultValue = (Rectangle)this.getDefault(key);
        String sp = this.internal.getProperty(key);
        if (sp == null) {
            return defaultValue;
        }
        Rectangle result = new Rectangle();
        try {
            StringTokenizer st = new StringTokenizer(sp, " ", false);
            if (!st.hasMoreTokens()) {
                this.internal.remove(key);
                return defaultValue;
            }
            String token = st.nextToken();
            int x = Integer.parseInt(token);
            if (!st.hasMoreTokens()) {
                this.internal.remove(key);
                return defaultValue;
            }
            token = st.nextToken();
            int y = Integer.parseInt(token);
            if (!st.hasMoreTokens()) {
                this.internal.remove(key);
                return defaultValue;
            }
            token = st.nextToken();
            int w = Integer.parseInt(token);
            if (!st.hasMoreTokens()) {
                this.internal.remove(key);
                return defaultValue;
            }
            token = st.nextToken();
            int h = Integer.parseInt(token);
            result.setBounds(x, y, w, h);
            return result;
        }
        catch (NumberFormatException e) {
            this.internal.remove(key);
            return defaultValue;
        }
    }

    public Dimension getDimension(String key) {
        Dimension defaultValue = (Dimension)this.getDefault(key);
        String sp = this.internal.getProperty(key);
        if (sp == null) {
            return defaultValue;
        }
        Dimension result = new Dimension();
        try {
            StringTokenizer st = new StringTokenizer(sp, " ", false);
            if (!st.hasMoreTokens()) {
                this.internal.remove(key);
                return defaultValue;
            }
            String token = st.nextToken();
            int w = Integer.parseInt(token);
            if (!st.hasMoreTokens()) {
                this.internal.remove(key);
                return defaultValue;
            }
            token = st.nextToken();
            int h = Integer.parseInt(token);
            result.setSize(w, h);
            return result;
        }
        catch (NumberFormatException e) {
            this.internal.remove(key);
            return defaultValue;
        }
    }

    public Point getPoint(String key) {
        Point defaultValue = (Point)this.getDefault(key);
        String sp = this.internal.getProperty(key);
        if (sp == null) {
            return defaultValue;
        }
        Point result = new Point();
        try {
            StringTokenizer st = new StringTokenizer(sp, " ", false);
            if (!st.hasMoreTokens()) {
                this.internal.remove(key);
                return defaultValue;
            }
            String token = st.nextToken();
            int x = Integer.parseInt(token);
            if (!st.hasMoreTokens()) {
                this.internal.remove(key);
                return defaultValue;
            }
            token = st.nextToken();
            int y = Integer.parseInt(token);
            if (!st.hasMoreTokens()) {
                this.internal.remove(key);
                return defaultValue;
            }
            result.setLocation(x, y);
            return result;
        }
        catch (NumberFormatException e) {
            this.internal.remove(key);
            return defaultValue;
        }
    }

    public Color getColor(String key) {
        Color defaultValue = (Color)this.getDefault(key);
        String sp = this.internal.getProperty(key);
        if (sp == null) {
            return defaultValue;
        }
        try {
            StringTokenizer st = new StringTokenizer(sp, " ", false);
            if (!st.hasMoreTokens()) {
                this.internal.remove(key);
                return defaultValue;
            }
            String token = st.nextToken();
            int r = Integer.parseInt(token);
            if (!st.hasMoreTokens()) {
                this.internal.remove(key);
                return defaultValue;
            }
            token = st.nextToken();
            int g = Integer.parseInt(token);
            if (!st.hasMoreTokens()) {
                this.internal.remove(key);
                return defaultValue;
            }
            token = st.nextToken();
            int b = Integer.parseInt(token);
            if (!st.hasMoreTokens()) {
                this.internal.remove(key);
                return defaultValue;
            }
            token = st.nextToken();
            int a = Integer.parseInt(token);
            return new Color(r, g, b, a);
        }
        catch (NumberFormatException e) {
            this.internal.remove(key);
            return defaultValue;
        }
    }

    public Font getFont(String key) {
        Font defaultValue = (Font)this.getDefault(key);
        String sp = this.internal.getProperty(key);
        if (sp == null) {
            return defaultValue;
        }
        try {
            StringTokenizer st = new StringTokenizer(sp, " ", false);
            if (!st.hasMoreTokens()) {
                this.internal.remove(key);
                return defaultValue;
            }
            String name = st.nextToken();
            if (!st.hasMoreTokens()) {
                this.internal.remove(key);
                return defaultValue;
            }
            String token = st.nextToken();
            int size = Integer.parseInt(token);
            if (!st.hasMoreTokens()) {
                this.internal.remove(key);
                return defaultValue;
            }
            token = st.nextToken();
            int type = Integer.parseInt(token);
            return new Font(name, type, size);
        }
        catch (NumberFormatException e) {
            this.internal.remove(key);
            return defaultValue;
        }
    }

    public String getString(String key) {
        String sp = this.internal.getProperty(key);
        if (sp == null) {
            sp = (String)this.getDefault(key);
        }
        return sp;
    }

    public String[] getStrings(String mkey) {
        int i = 0;
        ArrayList<String> v = new ArrayList<String>();
        while (true) {
            String last = this.getString(mkey + i);
            ++i;
            if (last == null) break;
            v.add(last);
        }
        if (v.size() != 0) {
            String[] str = new String[v.size()];
            return v.toArray(str);
        }
        return (String[])this.getDefault(mkey);
    }

    public URL getURL(String key) {
        URL defaultValue = (URL)this.getDefault(key);
        String sp = this.internal.getProperty(key);
        if (sp == null) {
            return defaultValue;
        }
        URL url = null;
        try {
            url = new URL(sp);
        }
        catch (MalformedURLException ex) {
            this.internal.remove(key);
            return defaultValue;
        }
        return url;
    }

    public URL[] getURLs(String mkey) {
        int i = 0;
        ArrayList<URL> v = new ArrayList<URL>();
        while (true) {
            URL last = this.getURL(mkey + i);
            ++i;
            if (last == null) break;
            v.add(last);
        }
        if (v.size() != 0) {
            URL[] path = new URL[v.size()];
            return v.toArray(path);
        }
        return (URL[])this.getDefault(mkey);
    }

    public File getFile(String key) {
        File defaultValue = (File)this.getDefault(key);
        String sp = this.internal.getProperty(key);
        if (sp == null) {
            return defaultValue;
        }
        File file = new File(sp);
        if (file.exists()) {
            return file;
        }
        this.internal.remove(key);
        return defaultValue;
    }

    public File[] getFiles(String mkey) {
        int i = 0;
        ArrayList<File> v = new ArrayList<File>();
        while (true) {
            File last = this.getFile(mkey + i);
            ++i;
            if (last == null) break;
            v.add(last);
        }
        if (v.size() != 0) {
            File[] path = new File[v.size()];
            return v.toArray(path);
        }
        return (File[])this.getDefault(mkey);
    }

    public int getInteger(String key) {
        int value;
        String sp;
        int defaultValue = 0;
        if (this.getDefault(key) != null) {
            defaultValue = (Integer)this.getDefault(key);
        }
        if ((sp = this.internal.getProperty(key)) == null) {
            return defaultValue;
        }
        try {
            value = Integer.parseInt(sp);
        }
        catch (NumberFormatException ex) {
            this.internal.remove(key);
            return defaultValue;
        }
        return value;
    }

    public float getFloat(String key) {
        float value;
        String sp;
        float defaultValue = 0.0f;
        if (this.getDefault(key) != null) {
            defaultValue = ((Float)this.getDefault(key)).floatValue();
        }
        if ((sp = this.internal.getProperty(key)) == null) {
            return defaultValue;
        }
        try {
            value = Float.parseFloat(sp);
        }
        catch (NumberFormatException ex) {
            this.setFloat(key, defaultValue);
            return defaultValue;
        }
        return value;
    }

    public boolean getBoolean(String key) {
        if (this.internal.getProperty(key) != null) {
            return this.internal.getProperty(key).equals("true");
        }
        if (this.getDefault(key) != null) {
            return (Boolean)this.getDefault(key);
        }
        return false;
    }

    public void setRectangle(String key, Rectangle value) {
        if (value != null && !value.equals(this.getDefault(key))) {
            this.internal.setProperty(key, value.x + " " + value.y + " " + value.width + ' ' + value.height);
        } else {
            this.internal.remove(key);
        }
    }

    public void setDimension(String key, Dimension value) {
        if (value != null && !value.equals(this.getDefault(key))) {
            this.internal.setProperty(key, value.width + " " + value.height);
        } else {
            this.internal.remove(key);
        }
    }

    public void setPoint(String key, Point value) {
        if (value != null && !value.equals(this.getDefault(key))) {
            this.internal.setProperty(key, value.x + " " + value.y);
        } else {
            this.internal.remove(key);
        }
    }

    public void setColor(String key, Color value) {
        if (value != null && !value.equals(this.getDefault(key))) {
            this.internal.setProperty(key, value.getRed() + " " + value.getGreen() + " " + value.getBlue() + " " + value.getAlpha());
        } else {
            this.internal.remove(key);
        }
    }

    public void setFont(String key, Font value) {
        if (value != null && !value.equals(this.getDefault(key))) {
            this.internal.setProperty(key, value.getName() + " " + value.getSize() + " " + value.getStyle());
        } else {
            this.internal.remove(key);
        }
    }

    public void setString(String key, String value) {
        if (value != null && !value.equals(this.getDefault(key))) {
            this.internal.setProperty(key, value);
        } else {
            this.internal.remove(key);
        }
    }

    public void setStrings(String mkey, String[] values) {
        String last;
        int j = 0;
        if (values != null) {
            for (String value : values) {
                if (value == null) continue;
                this.setString(mkey + j, value);
                ++j;
            }
        }
        while ((last = this.getString(mkey + j)) != null) {
            this.setString(mkey + j, null);
            ++j;
        }
    }

    public void setURL(String key, URL value) {
        if (value != null && !value.equals(this.getDefault(key))) {
            this.internal.setProperty(key, value.toString());
        } else {
            this.internal.remove(key);
        }
    }

    public void setURLs(String mkey, URL[] values) {
        String last;
        int j = 0;
        if (values != null) {
            for (URL value : values) {
                if (value == null) continue;
                this.setURL(mkey + j, value);
                ++j;
            }
        }
        while ((last = this.getString(mkey + j)) != null) {
            this.setString(mkey + j, null);
            ++j;
        }
    }

    public void setFile(String key, File value) {
        if (value != null && !value.equals(this.getDefault(key))) {
            this.internal.setProperty(key, value.getAbsolutePath());
        } else {
            this.internal.remove(key);
        }
    }

    public void setFiles(String mkey, File[] values) {
        String last;
        int j = 0;
        if (values != null) {
            for (File value : values) {
                if (value == null) continue;
                this.setFile(mkey + j, value);
                ++j;
            }
        }
        while ((last = this.getString(mkey + j)) != null) {
            this.setString(mkey + j, null);
            ++j;
        }
    }

    public void setInteger(String key, int value) {
        if (this.getDefault(key) != null && (Integer)this.getDefault(key) != value) {
            this.internal.setProperty(key, Integer.toString(value));
        } else {
            this.internal.remove(key);
        }
    }

    public void setFloat(String key, float value) {
        if (this.getDefault(key) != null && ((Float)this.getDefault(key)).floatValue() != value) {
            this.internal.setProperty(key, Float.toString(value));
        } else {
            this.internal.remove(key);
        }
    }

    public void setBoolean(String key, boolean value) {
        if (this.getDefault(key) != null && (Boolean)this.getDefault(key) != value) {
            this.internal.setProperty(key, value ? "true" : "false");
        } else {
            this.internal.remove(key);
        }
    }
}

