/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.email;

public class EmailAddress {
    private String name;
    private String address;

    public EmailAddress() {
    }

    public EmailAddress(String email) {
        int minLen = 9;
        int len = email.length();
        if (!(len <= 9 || email.charAt(0) != '<' && email.charAt(1) != '<' || email.charAt(len - 1) != '>' && email.charAt(len - 2) != '>')) {
            this.address = this.trim(email, true);
            return;
        }
        int paramDepth = 0;
        int start = 0;
        int end = 0;
        int nStart = 0;
        int nEnd = 0;
        for (int i = 0; i < len; ++i) {
            char c = email.charAt(i);
            if (c == '(') {
                ++paramDepth;
                if (start != 0) continue;
                end = i;
                nStart = i + 1;
                continue;
            }
            if (c == ')') {
                --paramDepth;
                if (end != 0) continue;
                start = i + 1;
                nEnd = i;
                continue;
            }
            if (paramDepth == 0 && c == '<') {
                if (start == 0) {
                    nEnd = i;
                }
                start = i + 1;
                continue;
            }
            if (paramDepth != 0 || c != '>' || (end = i) == len - 1) continue;
            nStart = i + 1;
        }
        if (end == 0) {
            end = len;
        }
        if (nEnd == 0) {
            nEnd = len;
        }
        this.address = this.trim(email.substring(start, end), true);
        this.name = this.trim(email.substring(nStart, nEnd), false);
        if (this.name.length() + this.address.length() > len) {
            this.name = null;
        }
    }

    private String trim(String t, boolean trimAngleBrackets) {
        boolean trim;
        int start = 0;
        int end = t.length();
        do {
            trim = false;
            if (t.charAt(end - 1) == ')' || t.charAt(end - 1) == '>' && trimAngleBrackets || t.charAt(end - 1) == '\"' && t.charAt(end - 2) != '\\' || t.charAt(end - 1) <= ' ') {
                trim = true;
                --end;
            }
            if (t.charAt(start) != '(' && (t.charAt(start) != '<' || !trimAngleBrackets) && t.charAt(start) != '\"' && t.charAt(start) > ' ') continue;
            trim = true;
            ++start;
        } while (trim);
        return t.substring(start, end);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String toString() {
        if (this.name == null) {
            return this.address;
        }
        return this.name + " <" + this.address + ">";
    }

    public String getAddress() {
        return this.address;
    }

    public String getName() {
        return this.name;
    }
}

