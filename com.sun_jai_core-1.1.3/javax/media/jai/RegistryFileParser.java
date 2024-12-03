/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StreamTokenizer;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import javax.media.jai.JAI;
import javax.media.jai.JaiI18N;
import javax.media.jai.OperationRegistry;
import javax.media.jai.RegistryElementDescriptor;
import javax.media.jai.RegistryMode;
import javax.media.jai.util.CaselessStringKey;

class RegistryFileParser {
    private URL url;
    private InputStream is;
    private ClassLoader classLoader;
    private OperationRegistry or;
    private StreamTokenizer st;
    private int token;
    private int lineno;
    private Hashtable localNamesTable;
    private static String[][] aliases = new String[][]{{"odesc", "descriptor"}, {"rif", "rendered"}, {"crif", "renderable"}, {"cif", "collection"}};
    private boolean headerLinePrinted = false;

    static void loadOperationRegistry(OperationRegistry or, ClassLoader cl, InputStream is) throws IOException {
        new RegistryFileParser(or, cl, is).parseFile();
    }

    static void loadOperationRegistry(OperationRegistry or, ClassLoader cl, URL url) throws IOException {
        new RegistryFileParser(or, cl, url).parseFile();
    }

    private RegistryFileParser(OperationRegistry or, ClassLoader cl, URL url) throws IOException {
        this(or, cl, url.openStream());
        this.url = url;
    }

    private RegistryFileParser(OperationRegistry or, ClassLoader cl, InputStream is) throws IOException {
        if (or == null) {
            or = JAI.getDefaultInstance().getOperationRegistry();
        }
        this.is = is;
        this.url = null;
        this.or = or;
        this.classLoader = cl;
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        this.st = new StreamTokenizer(reader);
        this.st.commentChar(35);
        this.st.eolIsSignificant(true);
        this.st.slashSlashComments(true);
        this.st.slashStarComments(true);
        this.token = this.st.ttype;
        this.lineno = -1;
        this.localNamesTable = new Hashtable();
        String[] modeNames = RegistryMode.getModeNames();
        for (int i = 0; i < modeNames.length; ++i) {
            this.localNamesTable.put(new CaselessStringKey(modeNames[i]), new Hashtable());
        }
    }

    private int skipEmptyTokens() throws IOException {
        while (this.st.sval == null) {
            if (this.token == -1) {
                return this.token;
            }
            this.token = this.st.nextToken();
        }
        return this.token;
    }

    private String[] getNextLine() throws IOException {
        if (this.skipEmptyTokens() == -1) {
            return null;
        }
        Vector<String> v = new Vector<String>();
        this.lineno = this.st.lineno();
        while (this.token != 10 && this.token != -1) {
            if (this.st.sval != null) {
                v.addElement(this.st.sval);
            }
            this.token = this.st.nextToken();
        }
        if (v.size() == 0) {
            return null;
        }
        return v.toArray(new String[0]);
    }

    private String mapName(String key) {
        for (int i = 0; i < aliases.length; ++i) {
            if (!key.equalsIgnoreCase(aliases[i][0])) continue;
            return aliases[i][1];
        }
        return key;
    }

    private Object getInstance(String className) {
        try {
            Class<?> descriptorClass = null;
            String errorMsg = null;
            if (this.classLoader != null) {
                try {
                    descriptorClass = Class.forName(className, true, this.classLoader);
                }
                catch (Exception e) {
                    errorMsg = e.getMessage();
                }
            }
            if (descriptorClass == null) {
                try {
                    descriptorClass = Class.forName(className);
                }
                catch (Exception e) {
                    errorMsg = e.getMessage();
                }
            }
            if (descriptorClass == null) {
                try {
                    descriptorClass = Class.forName(className, true, ClassLoader.getSystemClassLoader());
                }
                catch (Exception e) {
                    errorMsg = e.getMessage();
                }
            }
            if (descriptorClass == null) {
                this.registryFileError(errorMsg);
                return null;
            }
            return descriptorClass.newInstance();
        }
        catch (Exception e) {
            this.registryFileError(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    boolean parseFile() throws IOException {
        String[] keys;
        if (this.token == -1) {
            return true;
        }
        this.token = this.st.nextToken();
        while (this.token != -1 && (keys = this.getNextLine()) != null) {
            RegistryMode mode;
            String key = this.mapName(keys[0]);
            if (key.equalsIgnoreCase("registryMode")) {
                mode = (RegistryMode)this.getInstance(keys[1]);
                if (mode == null || RegistryMode.addMode(mode)) continue;
                this.registryFileError(JaiI18N.getString("RegistryFileParser10"));
                continue;
            }
            if (key.equalsIgnoreCase("descriptor")) {
                this.registerDescriptor(keys);
                continue;
            }
            mode = RegistryMode.getMode(key);
            if (mode != null) {
                this.registerFactory(mode, keys);
                continue;
            }
            if (key.equalsIgnoreCase("pref")) {
                key = this.mapName(keys[1]);
                if (key.equalsIgnoreCase("product")) {
                    this.setProductPreference(RegistryMode.getMode("rendered"), keys);
                    continue;
                }
                mode = RegistryMode.getMode(key);
                if (mode != null) {
                    this.setFactoryPreference(mode, keys);
                    continue;
                }
                this.registryFileError(JaiI18N.getString("RegistryFileParser4"));
                continue;
            }
            if (key.equalsIgnoreCase("productPref")) {
                key = this.mapName(keys[1]);
                mode = RegistryMode.getMode(key);
                if (mode != null) {
                    this.setProductPreference(mode, keys);
                    continue;
                }
                this.registryFileError(JaiI18N.getString("RegistryFileParser5"));
                continue;
            }
            this.registryFileError(JaiI18N.getString("RegistryFileParser6"));
        }
        if (this.url != null) {
            this.is.close();
        }
        return true;
    }

    private void registerDescriptor(String[] keys) {
        if (keys.length >= 2) {
            RegistryElementDescriptor red = (RegistryElementDescriptor)this.getInstance(keys[1]);
            if (red != null) {
                try {
                    this.or.registerDescriptor(red);
                }
                catch (Exception e) {
                    this.registryFileError(e.getMessage());
                }
            }
        } else {
            this.registryFileError(JaiI18N.getString("RegistryFileParser1"));
        }
    }

    private void registerFactory(RegistryMode mode, String[] keys) {
        if (mode.arePreferencesSupported()) {
            if (keys.length >= 5) {
                Object factory = this.getInstance(keys[1]);
                if (factory != null) {
                    try {
                        this.or.registerFactory(mode.getName(), keys[3], keys[2], factory);
                        this.mapLocalNameToObject(mode.getName(), keys[4], factory);
                    }
                    catch (Exception e) {
                        this.registryFileError(e.getMessage());
                    }
                }
            } else {
                this.registryFileError(JaiI18N.getString("RegistryFileParser2"));
            }
        } else if (keys.length >= 3) {
            Object factory = this.getInstance(keys[1]);
            if (factory != null) {
                try {
                    this.or.registerFactory(mode.getName(), keys[2], null, factory);
                }
                catch (Exception e) {
                    this.registryFileError(e.getMessage());
                }
            }
        } else {
            this.registryFileError(JaiI18N.getString("RegistryFileParser3"));
        }
    }

    private void setProductPreference(RegistryMode mode, String[] keys) {
        String modeName = mode.getName();
        if (mode.arePreferencesSupported()) {
            if (keys.length >= 5) {
                try {
                    this.or.setProductPreference(modeName, keys[2], keys[3], keys[4]);
                }
                catch (Exception e) {
                    this.registryFileError(e.getMessage());
                }
            } else {
                this.registryFileError(JaiI18N.getString("RegistryFileParser5"));
            }
        } else {
            this.registryFileError(JaiI18N.getString("RegistryFileParser9"));
        }
    }

    private void setFactoryPreference(RegistryMode mode, String[] keys) {
        String modeName = mode.getName();
        if (mode.arePreferencesSupported()) {
            if (keys.length >= 6) {
                Object preferred = this.getObjectFromLocalName(modeName, keys[4]);
                Object other = this.getObjectFromLocalName(modeName, keys[5]);
                if (preferred != null && other != null) {
                    try {
                        this.or.setFactoryPreference(modeName, keys[2], keys[3], preferred, other);
                    }
                    catch (Exception e) {
                        this.registryFileError(e.getMessage());
                    }
                }
            } else {
                this.registryFileError(JaiI18N.getString("RegistryFileParser4"));
            }
        } else {
            this.registryFileError(JaiI18N.getString("RegistryFileParser7"));
        }
    }

    private void mapLocalNameToObject(String modeName, String localName, Object factory) {
        Hashtable modeTable = (Hashtable)this.localNamesTable.get(new CaselessStringKey(modeName));
        modeTable.put(new CaselessStringKey(localName), factory);
    }

    private Object getObjectFromLocalName(String modeName, String localName) {
        Hashtable modeTable = (Hashtable)this.localNamesTable.get(new CaselessStringKey(modeName));
        Object obj = modeTable.get(new CaselessStringKey(localName));
        if (obj == null) {
            this.registryFileError(localName + ": " + JaiI18N.getString("RegistryFileParser8"));
        }
        return obj;
    }

    private void registryFileError(String msg) {
        if (!this.headerLinePrinted) {
            if (this.url != null) {
                this.errorMsg(JaiI18N.getString("RegistryFileParser11"), new Object[]{this.url.getPath()});
            }
            this.headerLinePrinted = true;
        }
        this.errorMsg(JaiI18N.getString("RegistryFileParser0"), new Object[]{new Integer(this.lineno)});
        if (msg != null) {
            this.errorMsg(msg, null);
        }
    }

    private void errorMsg(String key, Object[] args) {
        MessageFormat mf = new MessageFormat(key);
        mf.setLocale(Locale.getDefault());
        if (System.err != null) {
            System.err.println(mf.format(args));
        }
    }

    static void writeOperationRegistry(OperationRegistry or, OutputStream os) throws IOException {
        RegistryFileParser.writeOperationRegistry(or, new BufferedWriter(new OutputStreamWriter(os)));
    }

    static void writeOperationRegistry(OperationRegistry or, BufferedWriter bw) throws IOException {
        Iterator dcit = RegistryMode.getDescriptorClasses().iterator();
        String tab = "  ";
        while (dcit.hasNext()) {
            Class descriptorClass = (Class)dcit.next();
            List descriptors = or.getDescriptors(descriptorClass);
            bw.write("#");
            bw.newLine();
            bw.write("# Descriptors corresponding to class : " + descriptorClass.getName());
            bw.newLine();
            bw.write("#");
            bw.newLine();
            if (descriptors == null || descriptors.size() <= 0) {
                bw.write("# <EMPTY>");
                bw.newLine();
            } else {
                Iterator it = descriptors.iterator();
                while (it.hasNext()) {
                    bw.write("descriptor" + tab);
                    bw.write(it.next().getClass().getName());
                    bw.newLine();
                }
            }
            bw.newLine();
            String[] modeNames = RegistryMode.getModeNames(descriptorClass);
            for (int i = 0; i < modeNames.length; ++i) {
                int k;
                String[] productNames;
                Vector productVector;
                int j;
                bw.write("#");
                bw.newLine();
                bw.write("# Factories registered under mode : " + modeNames[i]);
                bw.newLine();
                bw.write("#");
                bw.newLine();
                RegistryMode mode = RegistryMode.getMode(modeNames[i]);
                boolean prefs = mode.arePreferencesSupported();
                String[] descriptorNames = or.getDescriptorNames(modeNames[i]);
                boolean empty = true;
                for (j = 0; j < descriptorNames.length; ++j) {
                    if (prefs) {
                        productVector = or.getOrderedProductList(modeNames[i], descriptorNames[j]);
                        if (productVector == null) continue;
                        productNames = productVector.toArray(new String[0]);
                        for (k = 0; k < productNames.length; ++k) {
                            List factoryList = or.getOrderedFactoryList(modeNames[i], descriptorNames[j], productNames[k]);
                            Iterator fit = factoryList.iterator();
                            while (fit.hasNext()) {
                                Object instance = fit.next();
                                if (instance == null) continue;
                                bw.write(modeNames[i] + tab);
                                bw.write(instance.getClass().getName() + tab);
                                bw.write(productNames[k] + tab);
                                bw.write(descriptorNames[j] + tab);
                                bw.write(or.getLocalName(modeNames[i], instance));
                                bw.newLine();
                                empty = false;
                            }
                        }
                        continue;
                    }
                    Iterator fit = or.getFactoryIterator(modeNames[i], descriptorNames[j]);
                    while (fit.hasNext()) {
                        Object instance = fit.next();
                        if (instance == null) continue;
                        bw.write(modeNames[i] + tab);
                        bw.write(instance.getClass().getName() + tab);
                        bw.write(descriptorNames[j]);
                        bw.newLine();
                        empty = false;
                    }
                }
                if (empty) {
                    bw.write("# <EMPTY>");
                    bw.newLine();
                }
                bw.newLine();
                if (!prefs) {
                    bw.write("#");
                    bw.newLine();
                    bw.write("# Preferences not supported for mode : " + modeNames[i]);
                    bw.newLine();
                    bw.write("#");
                    bw.newLine();
                    bw.newLine();
                    continue;
                }
                bw.write("#");
                bw.newLine();
                bw.write("# Product preferences for mode : " + modeNames[i]);
                bw.newLine();
                bw.write("#");
                bw.newLine();
                empty = true;
                for (j = 0; j < descriptorNames.length; ++j) {
                    String[][] productPrefs = or.getProductPreferences(modeNames[i], descriptorNames[j]);
                    if (productPrefs == null) continue;
                    for (k = 0; k < productPrefs.length; ++k) {
                        bw.write("productPref" + tab);
                        bw.write(modeNames[i] + tab);
                        bw.write(descriptorNames[j] + tab);
                        bw.write(productPrefs[k][0] + tab);
                        bw.write(productPrefs[k][1]);
                        bw.newLine();
                        empty = false;
                    }
                }
                if (empty) {
                    bw.write("# <EMPTY>");
                    bw.newLine();
                }
                bw.newLine();
                bw.write("#");
                bw.newLine();
                bw.write("# Factory preferences for mode : " + modeNames[i]);
                bw.newLine();
                bw.write("#");
                bw.newLine();
                empty = true;
                for (j = 0; j < descriptorNames.length; ++j) {
                    if (!prefs || (productVector = or.getOrderedProductList(modeNames[i], descriptorNames[j])) == null) continue;
                    productNames = productVector.toArray(new String[0]);
                    for (k = 0; k < productNames.length; ++k) {
                        Object[][] fprefs = or.getFactoryPreferences(modeNames[i], descriptorNames[j], productNames[k]);
                        if (fprefs == null) continue;
                        for (int l = 0; l < fprefs.length; ++l) {
                            bw.write("pref" + tab);
                            bw.write(modeNames[i] + tab);
                            bw.write(descriptorNames[j] + tab);
                            bw.write(productNames[k] + tab);
                            bw.write(or.getLocalName(modeNames[i], fprefs[l][0]) + tab);
                            bw.write(or.getLocalName(modeNames[i], fprefs[l][1]));
                            bw.newLine();
                            empty = false;
                        }
                    }
                }
                if (empty) {
                    bw.write("# <EMPTY>");
                    bw.newLine();
                }
                bw.newLine();
            }
        }
        bw.flush();
    }
}

