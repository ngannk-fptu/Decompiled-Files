/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jline.console.ConsoleReader
 *  jline.internal.Log
 */
package org.codehaus.groovy.tools.shell;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.ResourceBundle;
import jline.console.ConsoleReader;
import jline.internal.Log;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.tools.shell.util.JAnsiHelper;

public class PatchedConsoleReader
extends ConsoleReader
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;

    public PatchedConsoleReader(InputStream inStream, OutputStream out) throws IOException {
        super(inStream, out);
        MetaClass metaClass;
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    public void printColumns(Collection<? extends CharSequence> items) throws IOException {
        int n;
        int n2;
        int n3;
        if (items == null || items.isEmpty()) {
            return;
        }
        int width = this.getTerminal().getWidth();
        int height = this.getTerminal().getHeight();
        int maxWidth = 0;
        CharSequence item = null;
        Iterator<? extends CharSequence> iterator = items.iterator();
        while (iterator.hasNext()) {
            int n4;
            item = (CharSequence)ScriptBytecodeAdapter.castToType(iterator.next(), CharSequence.class);
            maxWidth = n4 = Math.max(maxWidth, JAnsiHelper.stripAnsi(item).length());
        }
        maxWidth = n3 = maxWidth + 3;
        Log.debug((Object[])new Object[]{"Max width: ", maxWidth});
        int showLines = 0;
        showLines = this.isPaginationEnabled() ? (n2 = height - 1) : (n = Integer.MAX_VALUE);
        StringBuilder buff = new StringBuilder();
        int realLength = 0;
        CharSequence item2 = null;
        Iterator<? extends CharSequence> iterator2 = items.iterator();
        while (iterator2.hasNext()) {
            item2 = (CharSequence)ScriptBytecodeAdapter.castToType(iterator2.next(), CharSequence.class);
            if (realLength + maxWidth > width) {
                int n5;
                this.println(buff);
                buff.setLength(0);
                realLength = n5 = 0;
                if (--showLines == 0) {
                    this.print(((ResourceBundle)this.getProperty("resources")).getString("DISPLAY_MORE"));
                    this.flush();
                    int c = this.readCharacter();
                    if (c == 13 || c == 10) {
                        int n6;
                        showLines = n6 = 1;
                    } else if (c != 113) {
                        int n7;
                        showLines = n7 = height - 1;
                    }
                    this.back(((ResourceBundle)this.getProperty("resources")).getString("DISPLAY_MORE").length());
                    if (c == 113) break;
                }
            }
            buff.append(item2.toString());
            int strippedItemLength = JAnsiHelper.stripAnsi(item2).length();
            realLength = realLength + strippedItemLength;
            int i = 0;
            while (i < maxWidth - strippedItemLength) {
                buff.append(" ");
                int n8 = i;
                int cfr_ignored_0 = n8 + 1;
            }
            realLength = realLength + (maxWidth - strippedItemLength);
        }
        if (buff.length() > 0) {
            this.println(buff);
        }
    }

    public static /* synthetic */ void access$0(PatchedConsoleReader $that, int param0) throws IOException {
        $that.back(param0);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != PatchedConsoleReader.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    @Override
    public /* synthetic */ MetaClass getMetaClass() {
        MetaClass metaClass = this.metaClass;
        if (metaClass != null) {
            return metaClass;
        }
        this.metaClass = this.$getStaticMetaClass();
        return this.metaClass;
    }

    @Override
    public /* synthetic */ void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass;
    }

    @Override
    public /* synthetic */ Object invokeMethod(String string, Object object) {
        return this.getMetaClass().invokeMethod((Object)this, string, object);
    }

    @Override
    public /* synthetic */ Object getProperty(String string) {
        return this.getMetaClass().getProperty(this, string);
    }

    @Override
    public /* synthetic */ void setProperty(String string, Object object) {
        this.getMetaClass().setProperty(this, string, object);
    }
}

