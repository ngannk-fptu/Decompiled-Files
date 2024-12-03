/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.KeyStroke;
import org.jfree.util.Log;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.ResourceBundleWrapper;

public class ResourceBundleSupport {
    private ResourceBundle resources;
    private TreeMap cache;
    private TreeSet lookupPath;
    private String resourceBase;
    private Locale locale;
    static /* synthetic */ Class class$org$jfree$util$ResourceBundleSupport;
    static /* synthetic */ Class class$java$awt$event$KeyEvent;

    public ResourceBundleSupport(Locale locale, String baseName) {
        this(locale, ResourceBundleWrapper.getBundle(baseName, locale), baseName);
    }

    protected ResourceBundleSupport(Locale locale, ResourceBundle resourceBundle, String baseName) {
        if (locale == null) {
            throw new NullPointerException("Locale must not be null");
        }
        if (resourceBundle == null) {
            throw new NullPointerException("Resources must not be null");
        }
        if (baseName == null) {
            throw new NullPointerException("BaseName must not be null");
        }
        this.locale = locale;
        this.resources = resourceBundle;
        this.resourceBase = baseName;
        this.cache = new TreeMap();
        this.lookupPath = new TreeSet();
    }

    public ResourceBundleSupport(Locale locale, ResourceBundle resourceBundle) {
        this(locale, resourceBundle, resourceBundle.toString());
    }

    public ResourceBundleSupport(String baseName) {
        this(Locale.getDefault(), ResourceBundleWrapper.getBundle(baseName), baseName);
    }

    protected ResourceBundleSupport(ResourceBundle resourceBundle, String baseName) {
        this(Locale.getDefault(), resourceBundle, baseName);
    }

    public ResourceBundleSupport(ResourceBundle resourceBundle) {
        this(Locale.getDefault(), resourceBundle, resourceBundle.toString());
    }

    protected final String getResourceBase() {
        return this.resourceBase;
    }

    public synchronized String getString(String key) {
        String retval = (String)this.cache.get(key);
        if (retval != null) {
            return retval;
        }
        this.lookupPath.clear();
        return this.internalGetString(key);
    }

    protected String internalGetString(String key) {
        if (this.lookupPath.contains(key)) {
            throw new MissingResourceException("InfiniteLoop in resource lookup", this.getResourceBase(), this.lookupPath.toString());
        }
        String fromResBundle = this.resources.getString(key);
        if (fromResBundle.startsWith("@@")) {
            int idx = fromResBundle.indexOf(64, 2);
            if (idx == -1) {
                throw new MissingResourceException("Invalid format for global lookup key.", this.getResourceBase(), key);
            }
            try {
                ResourceBundle res = ResourceBundleWrapper.getBundle(fromResBundle.substring(2, idx));
                return res.getString(fromResBundle.substring(idx + 1));
            }
            catch (Exception e) {
                Log.error("Error during global lookup", e);
                throw new MissingResourceException("Error during global lookup", this.getResourceBase(), key);
            }
        }
        if (fromResBundle.startsWith("@")) {
            String newKey = fromResBundle.substring(1);
            this.lookupPath.add(key);
            String retval = this.internalGetString(newKey);
            this.cache.put(key, retval);
            return retval;
        }
        this.cache.put(key, fromResBundle);
        return fromResBundle;
    }

    public Icon getIcon(String key, boolean large) {
        String name = this.getString(key);
        return this.createIcon(name, true, large);
    }

    public Icon getIcon(String key) {
        String name = this.getString(key);
        return this.createIcon(name, false, false);
    }

    public Integer getMnemonic(String key) {
        String name = this.getString(key);
        return this.createMnemonic(name);
    }

    public Integer getOptionalMnemonic(String key) {
        String name = this.getString(key);
        if (name != null && name.length() > 0) {
            return this.createMnemonic(name);
        }
        return null;
    }

    public KeyStroke getKeyStroke(String key) {
        return this.getKeyStroke(key, this.getMenuKeyMask());
    }

    public KeyStroke getOptionalKeyStroke(String key) {
        return this.getOptionalKeyStroke(key, this.getMenuKeyMask());
    }

    public KeyStroke getKeyStroke(String key, int mask) {
        String name = this.getString(key);
        return KeyStroke.getKeyStroke(this.createMnemonic(name), mask);
    }

    public KeyStroke getOptionalKeyStroke(String key, int mask) {
        String name = this.getString(key);
        if (name != null && name.length() > 0) {
            return KeyStroke.getKeyStroke(this.createMnemonic(name), mask);
        }
        return null;
    }

    public JMenu createMenu(String keyPrefix) {
        JMenu retval = new JMenu();
        retval.setText(this.getString(keyPrefix + ".name"));
        retval.setMnemonic(this.getMnemonic(keyPrefix + ".mnemonic"));
        return retval;
    }

    public URL getResourceURL(String key) {
        String name = this.getString(key);
        URL in = ObjectUtilities.getResource(name, class$org$jfree$util$ResourceBundleSupport == null ? (class$org$jfree$util$ResourceBundleSupport = ResourceBundleSupport.class$("org.jfree.util.ResourceBundleSupport")) : class$org$jfree$util$ResourceBundleSupport);
        if (in == null) {
            Log.warn("Unable to find file in the class path: " + name + "; key=" + key);
        }
        return in;
    }

    private ImageIcon createIcon(String resourceName, boolean scale, boolean large) {
        URL in = ObjectUtilities.getResource(resourceName, class$org$jfree$util$ResourceBundleSupport == null ? (class$org$jfree$util$ResourceBundleSupport = ResourceBundleSupport.class$("org.jfree.util.ResourceBundleSupport")) : class$org$jfree$util$ResourceBundleSupport);
        if (in == null) {
            Log.warn("Unable to find file in the class path: " + resourceName);
            return new ImageIcon(this.createTransparentImage(1, 1));
        }
        Image img = Toolkit.getDefaultToolkit().createImage(in);
        if (img == null) {
            Log.warn("Unable to instantiate the image: " + resourceName);
            return new ImageIcon(this.createTransparentImage(1, 1));
        }
        if (scale) {
            if (large) {
                return new ImageIcon(img.getScaledInstance(24, 24, 4));
            }
            return new ImageIcon(img.getScaledInstance(16, 16, 4));
        }
        return new ImageIcon(img);
    }

    private Integer createMnemonic(String keyString) {
        if (keyString == null) {
            throw new NullPointerException("Key is null.");
        }
        if (keyString.length() == 0) {
            throw new IllegalArgumentException("Key is empty.");
        }
        int character = keyString.charAt(0);
        if (keyString.startsWith("VK_")) {
            try {
                Field f = (class$java$awt$event$KeyEvent == null ? (class$java$awt$event$KeyEvent = ResourceBundleSupport.class$("java.awt.event.KeyEvent")) : class$java$awt$event$KeyEvent).getField(keyString);
                Integer keyCode = (Integer)f.get(null);
                character = keyCode;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return new Integer(character);
    }

    private int getMenuKeyMask() {
        try {
            return Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        }
        catch (UnsupportedOperationException he) {
            return 2;
        }
    }

    private BufferedImage createTransparentImage(int width, int height) {
        BufferedImage img = new BufferedImage(width, height, 2);
        int[] data = img.getRGB(0, 0, width, height, null, 0, width);
        Arrays.fill(data, 0);
        img.setRGB(0, 0, width, height, data, 0, width);
        return img;
    }

    public Icon createTransparentIcon(int width, int height) {
        return new ImageIcon(this.createTransparentImage(width, height));
    }

    public String formatMessage(String key, Object parameter) {
        return this.formatMessage(key, new Object[]{parameter});
    }

    public String formatMessage(String key, Object par1, Object par2) {
        return this.formatMessage(key, new Object[]{par1, par2});
    }

    public String formatMessage(String key, Object[] parameters) {
        MessageFormat format = new MessageFormat(this.getString(key));
        format.setLocale(this.getLocale());
        return format.format(parameters);
    }

    public Locale getLocale() {
        return this.locale;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

