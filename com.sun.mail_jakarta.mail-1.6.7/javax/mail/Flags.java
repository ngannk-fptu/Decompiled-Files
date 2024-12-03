/*
 * Decompiled with CFR 0.152.
 */
package javax.mail;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;

public class Flags
implements Cloneable,
Serializable {
    private int system_flags = 0;
    private Hashtable<String, String> user_flags = null;
    private static final int ANSWERED_BIT = 1;
    private static final int DELETED_BIT = 2;
    private static final int DRAFT_BIT = 4;
    private static final int FLAGGED_BIT = 8;
    private static final int RECENT_BIT = 16;
    private static final int SEEN_BIT = 32;
    private static final int USER_BIT = Integer.MIN_VALUE;
    private static final long serialVersionUID = 6243590407214169028L;

    public Flags() {
    }

    public Flags(Flags flags) {
        this.system_flags = flags.system_flags;
        if (flags.user_flags != null) {
            this.user_flags = (Hashtable)flags.user_flags.clone();
        }
    }

    public Flags(Flag flag) {
        this.system_flags |= flag.bit;
    }

    public Flags(String flag) {
        this.user_flags = new Hashtable(1);
        this.user_flags.put(flag.toLowerCase(Locale.ENGLISH), flag);
    }

    public void add(Flag flag) {
        this.system_flags |= flag.bit;
    }

    public void add(String flag) {
        if (this.user_flags == null) {
            this.user_flags = new Hashtable(1);
        }
        this.user_flags.put(flag.toLowerCase(Locale.ENGLISH), flag);
    }

    public void add(Flags f) {
        this.system_flags |= f.system_flags;
        if (f.user_flags != null) {
            if (this.user_flags == null) {
                this.user_flags = new Hashtable(1);
            }
            Enumeration<String> e = f.user_flags.keys();
            while (e.hasMoreElements()) {
                String s = e.nextElement();
                this.user_flags.put(s, f.user_flags.get(s));
            }
        }
    }

    public void remove(Flag flag) {
        this.system_flags &= ~flag.bit;
    }

    public void remove(String flag) {
        if (this.user_flags != null) {
            this.user_flags.remove(flag.toLowerCase(Locale.ENGLISH));
        }
    }

    public void remove(Flags f) {
        this.system_flags &= ~f.system_flags;
        if (f.user_flags != null) {
            if (this.user_flags == null) {
                return;
            }
            Enumeration<String> e = f.user_flags.keys();
            while (e.hasMoreElements()) {
                this.user_flags.remove(e.nextElement());
            }
        }
    }

    public boolean retainAll(Flags f) {
        boolean changed = false;
        int sf = this.system_flags & f.system_flags;
        if (this.system_flags != sf) {
            this.system_flags = sf;
            changed = true;
        }
        if (this.user_flags != null && (f.system_flags & Integer.MIN_VALUE) == 0) {
            if (f.user_flags != null) {
                Enumeration<String> e = this.user_flags.keys();
                while (e.hasMoreElements()) {
                    String key = e.nextElement();
                    if (f.user_flags.containsKey(key)) continue;
                    this.user_flags.remove(key);
                    changed = true;
                }
            } else {
                changed = this.user_flags.size() > 0;
                this.user_flags = null;
            }
        }
        return changed;
    }

    public boolean contains(Flag flag) {
        return (this.system_flags & flag.bit) != 0;
    }

    public boolean contains(String flag) {
        if (this.user_flags == null) {
            return false;
        }
        return this.user_flags.containsKey(flag.toLowerCase(Locale.ENGLISH));
    }

    public boolean contains(Flags f) {
        if ((f.system_flags & this.system_flags) != f.system_flags) {
            return false;
        }
        if (f.user_flags != null) {
            if (this.user_flags == null) {
                return false;
            }
            Enumeration<String> e = f.user_flags.keys();
            while (e.hasMoreElements()) {
                if (this.user_flags.containsKey(e.nextElement())) continue;
                return false;
            }
        }
        return true;
    }

    public boolean equals(Object obj) {
        int fsize;
        if (!(obj instanceof Flags)) {
            return false;
        }
        Flags f = (Flags)obj;
        if (f.system_flags != this.system_flags) {
            return false;
        }
        int size = this.user_flags == null ? 0 : this.user_flags.size();
        int n = fsize = f.user_flags == null ? 0 : f.user_flags.size();
        if (size == 0 && fsize == 0) {
            return true;
        }
        if (f.user_flags != null && this.user_flags != null && fsize == size) {
            return this.user_flags.keySet().equals(f.user_flags.keySet());
        }
        return false;
    }

    public int hashCode() {
        int hash = this.system_flags;
        if (this.user_flags != null) {
            Enumeration<String> e = this.user_flags.keys();
            while (e.hasMoreElements()) {
                hash += e.nextElement().hashCode();
            }
        }
        return hash;
    }

    public Flag[] getSystemFlags() {
        Vector<Flag> v = new Vector<Flag>();
        if ((this.system_flags & 1) != 0) {
            v.addElement(Flag.ANSWERED);
        }
        if ((this.system_flags & 2) != 0) {
            v.addElement(Flag.DELETED);
        }
        if ((this.system_flags & 4) != 0) {
            v.addElement(Flag.DRAFT);
        }
        if ((this.system_flags & 8) != 0) {
            v.addElement(Flag.FLAGGED);
        }
        if ((this.system_flags & 0x10) != 0) {
            v.addElement(Flag.RECENT);
        }
        if ((this.system_flags & 0x20) != 0) {
            v.addElement(Flag.SEEN);
        }
        if ((this.system_flags & Integer.MIN_VALUE) != 0) {
            v.addElement(Flag.USER);
        }
        Object[] f = new Flag[v.size()];
        v.copyInto(f);
        return f;
    }

    public String[] getUserFlags() {
        Vector<String> v = new Vector<String>();
        if (this.user_flags != null) {
            Enumeration<String> e = this.user_flags.elements();
            while (e.hasMoreElements()) {
                v.addElement(e.nextElement());
            }
        }
        Object[] f = new String[v.size()];
        v.copyInto(f);
        return f;
    }

    public void clearSystemFlags() {
        this.system_flags = 0;
    }

    public void clearUserFlags() {
        this.user_flags = null;
    }

    public Object clone() {
        Flags f = null;
        try {
            f = (Flags)super.clone();
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            // empty catch block
        }
        if (this.user_flags != null) {
            f.user_flags = (Hashtable)this.user_flags.clone();
        }
        return f;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if ((this.system_flags & 1) != 0) {
            sb.append("\\Answered ");
        }
        if ((this.system_flags & 2) != 0) {
            sb.append("\\Deleted ");
        }
        if ((this.system_flags & 4) != 0) {
            sb.append("\\Draft ");
        }
        if ((this.system_flags & 8) != 0) {
            sb.append("\\Flagged ");
        }
        if ((this.system_flags & 0x10) != 0) {
            sb.append("\\Recent ");
        }
        if ((this.system_flags & 0x20) != 0) {
            sb.append("\\Seen ");
        }
        if ((this.system_flags & Integer.MIN_VALUE) != 0) {
            sb.append("\\* ");
        }
        boolean first = true;
        if (this.user_flags != null) {
            Enumeration<String> e = this.user_flags.elements();
            while (e.hasMoreElements()) {
                if (first) {
                    first = false;
                } else {
                    sb.append(' ');
                }
                sb.append(e.nextElement());
            }
        }
        if (first && sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    public static final class Flag {
        public static final Flag ANSWERED = new Flag(1);
        public static final Flag DELETED = new Flag(2);
        public static final Flag DRAFT = new Flag(4);
        public static final Flag FLAGGED = new Flag(8);
        public static final Flag RECENT = new Flag(16);
        public static final Flag SEEN = new Flag(32);
        public static final Flag USER = new Flag(Integer.MIN_VALUE);
        private int bit;

        private Flag(int bit) {
            this.bit = bit;
        }
    }
}

