/*
 * Decompiled with CFR 0.152.
 */
package java_cup.runtime;

import java.util.Stack;
import java_cup.runtime.Scanner;
import java_cup.runtime.Symbol;
import java_cup.runtime.virtual_parse_stack;

public abstract class lr_parser {
    protected static final int _error_sync_size = 3;
    protected boolean _done_parsing = false;
    protected int tos;
    protected Symbol cur_token;
    protected Stack stack = new Stack();
    protected short[][] production_tab;
    protected short[][] action_tab;
    protected short[][] reduce_tab;
    private Scanner _scanner;
    protected Symbol[] lookahead;
    protected int lookahead_pos;

    public lr_parser() {
    }

    public lr_parser(Scanner scanner) {
        this();
        this.setScanner(scanner);
    }

    public abstract int EOF_sym();

    public abstract short[][] action_table();

    protected boolean advance_lookahead() {
        ++this.lookahead_pos;
        return this.lookahead_pos < this.error_sync_size();
    }

    protected Symbol cur_err_token() {
        return this.lookahead[this.lookahead_pos];
    }

    public void debug_message(String string) {
        System.err.println(string);
    }

    public Symbol debug_parse() throws Exception {
        Symbol symbol = null;
        this.production_tab = this.production_table();
        this.action_tab = this.action_table();
        this.reduce_tab = this.reduce_table();
        this.debug_message("# Initializing parser");
        this.init_actions();
        this.user_init();
        this.cur_token = this.scan();
        this.debug_message("# Current Symbol is #" + this.cur_token.sym);
        this.stack.removeAllElements();
        this.stack.push(new Symbol(0, this.start_state()));
        this.tos = 0;
        this._done_parsing = false;
        while (!this._done_parsing) {
            if (this.cur_token.used_by_parser) {
                throw new Error("Symbol recycling detected (fix your scanner).");
            }
            short s = this.get_action(((Symbol)this.stack.peek()).parse_state, this.cur_token.sym);
            if (s > 0) {
                this.cur_token.parse_state = s - 1;
                this.cur_token.used_by_parser = true;
                this.debug_shift(this.cur_token);
                this.stack.push(this.cur_token);
                ++this.tos;
                this.cur_token = this.scan();
                this.debug_message("# Current token is " + this.cur_token);
                continue;
            }
            if (s < 0) {
                symbol = this.do_action(-s - 1, this, this.stack, this.tos);
                short s2 = this.production_tab[-s - 1][0];
                int n = this.production_tab[-s - 1][1];
                this.debug_reduce(-s - 1, s2, n);
                int n2 = 0;
                while (n2 < n) {
                    this.stack.pop();
                    --this.tos;
                    ++n2;
                }
                s = this.get_reduce(((Symbol)this.stack.peek()).parse_state, s2);
                this.debug_message("# Reduce rule: top state " + ((Symbol)this.stack.peek()).parse_state + ", lhs sym " + s2 + " -> state " + s);
                symbol.parse_state = s;
                symbol.used_by_parser = true;
                this.stack.push(symbol);
                ++this.tos;
                this.debug_message("# Goto state #" + s);
                continue;
            }
            if (s != 0) continue;
            this.syntax_error(this.cur_token);
            if (!this.error_recovery(true)) {
                this.unrecovered_syntax_error(this.cur_token);
                this.done_parsing();
                continue;
            }
            symbol = (Symbol)this.stack.peek();
        }
        return symbol;
    }

    public void debug_reduce(int n, int n2, int n3) {
        this.debug_message("# Reduce with prod #" + n + " [NT=" + n2 + ", " + "SZ=" + n3 + "]");
    }

    public void debug_shift(Symbol symbol) {
        this.debug_message("# Shift under term #" + symbol.sym + " to state #" + symbol.parse_state);
    }

    public void debug_stack() {
        StringBuffer stringBuffer = new StringBuffer("## STACK:");
        int n = 0;
        while (n < this.stack.size()) {
            Symbol symbol = (Symbol)this.stack.elementAt(n);
            stringBuffer.append(" <state " + symbol.parse_state + ", sym " + symbol.sym + ">");
            if (n % 3 == 2 || n == this.stack.size() - 1) {
                this.debug_message(stringBuffer.toString());
                stringBuffer = new StringBuffer("         ");
            }
            ++n;
        }
    }

    public abstract Symbol do_action(int var1, lr_parser var2, Stack var3, int var4) throws Exception;

    public void done_parsing() {
        this._done_parsing = true;
    }

    public void dump_stack() {
        if (this.stack == null) {
            this.debug_message("# Stack dump requested, but stack is null");
            return;
        }
        this.debug_message("============ Parse Stack Dump ============");
        int n = 0;
        while (n < this.stack.size()) {
            this.debug_message("Symbol: " + ((Symbol)this.stack.elementAt((int)n)).sym + " State: " + ((Symbol)this.stack.elementAt((int)n)).parse_state);
            ++n;
        }
        this.debug_message("==========================================");
    }

    protected boolean error_recovery(boolean bl) throws Exception {
        if (bl) {
            this.debug_message("# Attempting error recovery");
        }
        if (!this.find_recovery_config(bl)) {
            if (bl) {
                this.debug_message("# Error recovery fails");
            }
            return false;
        }
        this.read_lookahead();
        while (true) {
            if (bl) {
                this.debug_message("# Trying to parse ahead");
            }
            if (this.try_parse_ahead(bl)) break;
            if (this.lookahead[0].sym == this.EOF_sym()) {
                if (bl) {
                    this.debug_message("# Error recovery fails at EOF");
                }
                return false;
            }
            if (bl) {
                this.debug_message("# Consuming Symbol #" + this.lookahead[0].sym);
            }
            this.restart_lookahead();
        }
        if (bl) {
            this.debug_message("# Parse-ahead ok, going back to normal parse");
        }
        this.parse_lookahead(bl);
        return true;
    }

    public abstract int error_sym();

    protected int error_sync_size() {
        return 3;
    }

    protected boolean find_recovery_config(boolean bl) {
        if (bl) {
            this.debug_message("# Finding recovery state on stack");
        }
        int n = ((Symbol)this.stack.peek()).right;
        int n2 = ((Symbol)this.stack.peek()).left;
        while (!this.shift_under_error()) {
            if (bl) {
                this.debug_message("# Pop stack by one, state was # " + ((Symbol)this.stack.peek()).parse_state);
            }
            n2 = ((Symbol)this.stack.pop()).left;
            --this.tos;
            if (!this.stack.empty()) continue;
            if (bl) {
                this.debug_message("# No recovery state found on stack");
            }
            return false;
        }
        short s = this.get_action(((Symbol)this.stack.peek()).parse_state, this.error_sym());
        if (bl) {
            this.debug_message("# Recover state found (#" + ((Symbol)this.stack.peek()).parse_state + ")");
            this.debug_message("# Shifting on error to state #" + (s - 1));
        }
        Symbol symbol = new Symbol(this.error_sym(), n2, n);
        symbol.parse_state = s - 1;
        symbol.used_by_parser = true;
        this.stack.push(symbol);
        ++this.tos;
        return true;
    }

    public Scanner getScanner() {
        return this._scanner;
    }

    protected final short get_action(int n, int n2) {
        short[] sArray = this.action_tab[n];
        if (sArray.length < 20) {
            int n3 = 0;
            while (n3 < sArray.length) {
                short s;
                if ((s = sArray[n3++]) == n2 || s == -1) {
                    return sArray[n3];
                }
                ++n3;
            }
        } else {
            int n4 = 0;
            int n5 = (sArray.length - 1) / 2 - 1;
            while (n4 <= n5) {
                int n6 = (n4 + n5) / 2;
                if (n2 == sArray[n6 * 2]) {
                    return sArray[n6 * 2 + 1];
                }
                if (n2 > sArray[n6 * 2]) {
                    n4 = n6 + 1;
                    continue;
                }
                n5 = n6 - 1;
            }
            return sArray[sArray.length - 1];
        }
        return 0;
    }

    protected final short get_reduce(int n, int n2) {
        short[] sArray = this.reduce_tab[n];
        if (sArray == null) {
            return -1;
        }
        int n3 = 0;
        while (n3 < sArray.length) {
            short s;
            if ((s = sArray[n3++]) == n2 || s == -1) {
                return sArray[n3];
            }
            ++n3;
        }
        return -1;
    }

    protected abstract void init_actions() throws Exception;

    public Symbol parse() throws Exception {
        Symbol symbol = null;
        this.production_tab = this.production_table();
        this.action_tab = this.action_table();
        this.reduce_tab = this.reduce_table();
        this.init_actions();
        this.user_init();
        this.cur_token = this.scan();
        this.stack.removeAllElements();
        this.stack.push(new Symbol(0, this.start_state()));
        this.tos = 0;
        this._done_parsing = false;
        while (!this._done_parsing) {
            if (this.cur_token.used_by_parser) {
                throw new Error("Symbol recycling detected (fix your scanner).");
            }
            short s = this.get_action(((Symbol)this.stack.peek()).parse_state, this.cur_token.sym);
            if (s > 0) {
                this.cur_token.parse_state = s - 1;
                this.cur_token.used_by_parser = true;
                this.stack.push(this.cur_token);
                ++this.tos;
                this.cur_token = this.scan();
                continue;
            }
            if (s < 0) {
                symbol = this.do_action(-s - 1, this, this.stack, this.tos);
                short s2 = this.production_tab[-s - 1][0];
                int n = this.production_tab[-s - 1][1];
                int n2 = 0;
                while (n2 < n) {
                    this.stack.pop();
                    --this.tos;
                    ++n2;
                }
                s = this.get_reduce(((Symbol)this.stack.peek()).parse_state, s2);
                symbol.parse_state = s;
                symbol.used_by_parser = true;
                this.stack.push(symbol);
                ++this.tos;
                continue;
            }
            if (s != 0) continue;
            this.syntax_error(this.cur_token);
            if (!this.error_recovery(false)) {
                this.unrecovered_syntax_error(this.cur_token);
                this.done_parsing();
                continue;
            }
            symbol = (Symbol)this.stack.peek();
        }
        return symbol;
    }

    protected void parse_lookahead(boolean bl) throws Exception {
        Symbol symbol = null;
        this.lookahead_pos = 0;
        if (bl) {
            this.debug_message("# Reparsing saved input with actions");
            this.debug_message("# Current Symbol is #" + this.cur_err_token().sym);
            this.debug_message("# Current state is #" + ((Symbol)this.stack.peek()).parse_state);
        }
        while (!this._done_parsing) {
            short s = this.get_action(((Symbol)this.stack.peek()).parse_state, this.cur_err_token().sym);
            if (s > 0) {
                this.cur_err_token().parse_state = s - 1;
                this.cur_err_token().used_by_parser = true;
                if (bl) {
                    this.debug_shift(this.cur_err_token());
                }
                this.stack.push(this.cur_err_token());
                ++this.tos;
                if (!this.advance_lookahead()) {
                    if (bl) {
                        this.debug_message("# Completed reparse");
                    }
                    return;
                }
                if (!bl) continue;
                this.debug_message("# Current Symbol is #" + this.cur_err_token().sym);
                continue;
            }
            if (s < 0) {
                symbol = this.do_action(-s - 1, this, this.stack, this.tos);
                short s2 = this.production_tab[-s - 1][0];
                int n = this.production_tab[-s - 1][1];
                if (bl) {
                    this.debug_reduce(-s - 1, s2, n);
                }
                int n2 = 0;
                while (n2 < n) {
                    this.stack.pop();
                    --this.tos;
                    ++n2;
                }
                s = this.get_reduce(((Symbol)this.stack.peek()).parse_state, s2);
                symbol.parse_state = s;
                symbol.used_by_parser = true;
                this.stack.push(symbol);
                ++this.tos;
                if (!bl) continue;
                this.debug_message("# Goto state #" + s);
                continue;
            }
            if (s != 0) continue;
            this.report_fatal_error("Syntax error", symbol);
            return;
        }
    }

    public abstract short[][] production_table();

    protected void read_lookahead() throws Exception {
        this.lookahead = new Symbol[this.error_sync_size()];
        int n = 0;
        while (n < this.error_sync_size()) {
            this.lookahead[n] = this.cur_token;
            this.cur_token = this.scan();
            ++n;
        }
        this.lookahead_pos = 0;
    }

    public abstract short[][] reduce_table();

    public void report_error(String string, Object object) {
        System.err.print(string);
        if (object instanceof Symbol) {
            if (((Symbol)object).left != -1) {
                System.err.println(" at character " + ((Symbol)object).left + " of input");
            } else {
                System.err.println("");
            }
        } else {
            System.err.println("");
        }
    }

    public void report_fatal_error(String string, Object object) throws Exception {
        this.done_parsing();
        this.report_error(string, object);
        throw new Exception("Can't recover from previous error(s)");
    }

    protected void restart_lookahead() throws Exception {
        int n = 1;
        while (n < this.error_sync_size()) {
            this.lookahead[n - 1] = this.lookahead[n];
            ++n;
        }
        this.lookahead[this.error_sync_size() - 1] = this.cur_token;
        this.cur_token = this.scan();
        this.lookahead_pos = 0;
    }

    public Symbol scan() throws Exception {
        Symbol symbol = this.getScanner().next_token();
        return symbol != null ? symbol : new Symbol(this.EOF_sym());
    }

    public void setScanner(Scanner scanner) {
        this._scanner = scanner;
    }

    protected boolean shift_under_error() {
        return this.get_action(((Symbol)this.stack.peek()).parse_state, this.error_sym()) > 0;
    }

    public abstract int start_production();

    public abstract int start_state();

    public void syntax_error(Symbol symbol) {
        this.report_error("Syntax error", symbol);
    }

    protected boolean try_parse_ahead(boolean bl) throws Exception {
        virtual_parse_stack virtual_parse_stack2 = new virtual_parse_stack(this.stack);
        short s;
        while ((s = this.get_action(virtual_parse_stack2.top(), this.cur_err_token().sym)) != 0) {
            if (s > 0) {
                virtual_parse_stack2.push(s - 1);
                if (bl) {
                    this.debug_message("# Parse-ahead shifts Symbol #" + this.cur_err_token().sym + " into state #" + (s - 1));
                }
                if (this.advance_lookahead()) continue;
                return true;
            }
            if (-s - 1 == this.start_production()) {
                if (bl) {
                    this.debug_message("# Parse-ahead accepts");
                }
                return true;
            }
            short s2 = this.production_tab[-s - 1][0];
            int n = this.production_tab[-s - 1][1];
            int n2 = 0;
            while (n2 < n) {
                virtual_parse_stack2.pop();
                ++n2;
            }
            if (bl) {
                this.debug_message("# Parse-ahead reduces: handle size = " + n + " lhs = #" + s2 + " from state #" + virtual_parse_stack2.top());
            }
            virtual_parse_stack2.push(this.get_reduce(virtual_parse_stack2.top(), s2));
            if (!bl) continue;
            this.debug_message("# Goto state #" + virtual_parse_stack2.top());
        }
        return false;
    }

    protected static short[][] unpackFromStrings(String[] stringArray) {
        StringBuffer stringBuffer = new StringBuffer(stringArray[0]);
        int n = 1;
        while (n < stringArray.length) {
            stringBuffer.append(stringArray[n]);
            ++n;
        }
        int n2 = 0;
        int n3 = stringBuffer.charAt(n2) << 16 | stringBuffer.charAt(n2 + 1);
        n2 += 2;
        short[][] sArray = new short[n3][];
        int n4 = 0;
        while (n4 < n3) {
            int n5 = stringBuffer.charAt(n2) << 16 | stringBuffer.charAt(n2 + 1);
            n2 += 2;
            sArray[n4] = new short[n5];
            int n6 = 0;
            while (n6 < n5) {
                sArray[n4][n6] = (short)(stringBuffer.charAt(n2++) - 2);
                ++n6;
            }
            ++n4;
        }
        return sArray;
    }

    public void unrecovered_syntax_error(Symbol symbol) throws Exception {
        this.report_fatal_error("Couldn't repair and continue parse", symbol);
    }

    public void user_init() throws Exception {
    }
}

