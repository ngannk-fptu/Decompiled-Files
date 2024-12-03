/*
 * Decompiled with CFR 0.152.
 */
package groovy.ui.text;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;

public final class StructuredSyntaxResources {
    public static final Clipboard SYSTEM_CLIPBOARD;
    public static final Font LARGE_FONT;
    public static final Font MEDIUM_FONT;
    public static final Font SMALL_FONT;
    public static final Font EDITOR_FONT;
    public static final String UNDO = "Undo";
    public static final String REDO = "Redo";
    public static final String PRINT = "Print";
    public static final String FIND = "Find";
    public static final String FIND_NEXT = "Find Next";
    public static final String REPLACE = "Replace";

    private StructuredSyntaxResources() {
    }

    static {
        Clipboard systemClipboard = null;
        try {
            SecurityManager mgr = System.getSecurityManager();
            if (mgr != null) {
                mgr.checkSystemClipboardAccess();
            }
            systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        }
        catch (SecurityException e) {
            systemClipboard = new Clipboard("UIResourceMgr");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        SYSTEM_CLIPBOARD = systemClipboard;
        LARGE_FONT = Font.decode("Arial-24");
        MEDIUM_FONT = Font.decode("Arial-18");
        SMALL_FONT = Font.decode("Arial-12");
        EDITOR_FONT = Font.decode("Monospaced-12");
    }
}

