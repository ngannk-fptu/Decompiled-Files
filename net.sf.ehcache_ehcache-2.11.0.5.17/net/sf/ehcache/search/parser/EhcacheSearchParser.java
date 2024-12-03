/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.parser;

import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import net.sf.ehcache.search.parser.EhcacheSearchParserConstants;
import net.sf.ehcache.search.parser.EhcacheSearchParserTokenManager;
import net.sf.ehcache.search.parser.InteractiveCmd;
import net.sf.ehcache.search.parser.MAggregate;
import net.sf.ehcache.search.parser.MAttribute;
import net.sf.ehcache.search.parser.MCriteria;
import net.sf.ehcache.search.parser.MValue;
import net.sf.ehcache.search.parser.ParseException;
import net.sf.ehcache.search.parser.ParseModel;
import net.sf.ehcache.search.parser.ParserSupport;
import net.sf.ehcache.search.parser.SimpleCharStream;
import net.sf.ehcache.search.parser.Token;

public class EhcacheSearchParser
implements EhcacheSearchParserConstants {
    private ParseModel qmodel;
    public EhcacheSearchParserTokenManager token_source;
    SimpleCharStream jj_input_stream;
    public Token token;
    public Token jj_nt;
    private int jj_ntk;
    private int jj_gen;
    private final int[] jj_la1 = new int[29];
    private static int[] jj_la1_0;
    private static int[] jj_la1_1;
    private List<int[]> jj_expentries = new ArrayList<int[]>();
    private int[] jj_expentry;
    private int jj_kind = -1;

    public ParseModel getModel() {
        return this.qmodel;
    }

    public final InteractiveCmd InteractiveCommand() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 51: {
                String s = this.UseCache();
                this.jj_consume_token(0);
                InteractiveCmd cmd = new InteractiveCmd(InteractiveCmd.Cmd.UseCache, s);
                break;
            }
            case 52: {
                String s = this.UseCacheManager();
                this.jj_consume_token(0);
                InteractiveCmd cmd = new InteractiveCmd(InteractiveCmd.Cmd.UseCacheManager, s);
                break;
            }
            case 32: {
                ParseModel qm = this.QueryStatement();
                InteractiveCmd cmd = new InteractiveCmd(qm);
                return cmd;
            }
            default: {
                this.jj_la1[0] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        throw new Error("Missing return statement in function");
    }

    public final String UseCache() throws ParseException {
        this.jj_consume_token(51);
        StringAndToken s = this.SingleQuotedString();
        return s.string;
    }

    public final String UseCacheManager() throws ParseException {
        this.jj_consume_token(52);
        StringAndToken s = this.SingleQuotedString();
        return s.string;
    }

    public final ParseModel QueryStatement() throws ParseException {
        this.qmodel = new ParseModel();
        MCriteria crit = null;
        this.jj_consume_token(32);
        this.TargetList();
        this.jj_consume_token(33);
        String cacheName = this.CacheName();
        this.qmodel.setCacheName(cacheName);
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 18: {
                this.jj_consume_token(18);
                crit = this.Criteria();
                this.qmodel.setCriteria(crit);
                break;
            }
            default: {
                this.jj_la1[1] = this.jj_gen;
            }
        }
        this.PostScript();
        this.jj_consume_token(0);
        return this.qmodel;
    }

    public final void PostScript() throws ParseException {
        block11: {
            block10: while (true) {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 50: 
                    case 53: {
                        break;
                    }
                    default: {
                        this.jj_la1[2] = this.jj_gen;
                        break block11;
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 53: {
                        this.GroupBy();
                        continue block10;
                    }
                    case 50: {
                        this.OrderBy();
                        continue block10;
                    }
                }
                break;
            }
            this.jj_la1[3] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
        }
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 34: {
                this.Limit();
                break;
            }
            default: {
                this.jj_la1[4] = this.jj_gen;
            }
        }
    }

    public final void GroupBy() throws ParseException {
        this.jj_consume_token(53);
        MAttribute attr1 = this.Attribute();
        this.qmodel.addGroupBy(attr1);
        block3: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 3: {
                    break;
                }
                default: {
                    this.jj_la1[5] = this.jj_gen;
                    break block3;
                }
            }
            this.jj_consume_token(3);
            MAttribute attr2 = this.Attribute();
            this.qmodel.addGroupBy(attr2);
        }
    }

    public final void OrderBy() throws ParseException {
        boolean asc1 = true;
        boolean asc2 = true;
        this.jj_consume_token(50);
        MAttribute attr1 = this.Attribute();
        block0 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 46: 
            case 47: 
            case 48: 
            case 49: {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 49: {
                        this.jj_consume_token(49);
                        break block0;
                    }
                    case 48: {
                        this.jj_consume_token(48);
                        asc1 = false;
                        break block0;
                    }
                    case 47: {
                        this.jj_consume_token(47);
                        break block0;
                    }
                    case 46: {
                        this.jj_consume_token(46);
                        asc1 = false;
                        break block0;
                    }
                }
                this.jj_la1[6] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
            default: {
                this.jj_la1[7] = this.jj_gen;
            }
        }
        this.qmodel.addOrderBy(attr1, asc1);
        block21: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 3: {
                    break;
                }
                default: {
                    this.jj_la1[8] = this.jj_gen;
                    break block21;
                }
            }
            this.jj_consume_token(3);
            MAttribute attr2 = this.Attribute();
            block12 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 46: 
                case 47: 
                case 48: 
                case 49: {
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 49: {
                            this.jj_consume_token(49);
                            break block12;
                        }
                        case 48: {
                            this.jj_consume_token(48);
                            asc2 = false;
                            break block12;
                        }
                        case 47: {
                            this.jj_consume_token(47);
                            break block12;
                        }
                        case 46: {
                            this.jj_consume_token(46);
                            asc2 = false;
                            break block12;
                        }
                    }
                    this.jj_la1[9] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
                default: {
                    this.jj_la1[10] = this.jj_gen;
                }
            }
            this.qmodel.addOrderBy(attr2, asc2);
        }
    }

    public final void Limit() throws ParseException {
        this.jj_consume_token(34);
        Token t = this.jj_consume_token(54);
        int lim = Integer.parseInt(t.image);
        this.qmodel.setLimit(lim);
    }

    public final MCriteria Criteria() throws ParseException {
        MCriteria crit;
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 4: {
                crit = this.PCriteria();
                break;
            }
            case 1: 
            case 2: 
            case 16: 
            case 17: 
            case 56: {
                crit = this.SimpleCriteria();
                break;
            }
            default: {
                this.jj_la1[11] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        return crit;
    }

    public final String CacheName() throws ParseException {
        String name = this.UnQuotedString();
        return name;
    }

    public final MCriteria PCriteria() throws ParseException {
        ArrayList<MCriteria> crits = new ArrayList<MCriteria>(10);
        boolean isNot = false;
        boolean isAnd = false;
        this.jj_consume_token(4);
        block0 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 19: {
                this.jj_consume_token(19);
                MCriteria crit = this.PCriteria();
                crits.add(crit);
                isNot = true;
                break;
            }
            case 1: 
            case 2: 
            case 4: 
            case 16: 
            case 17: 
            case 56: {
                MCriteria crit = this.Criteria();
                crits.add(crit);
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 24: 
                    case 25: 
                    case 26: {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 26: {
                                block21: while (true) {
                                    this.jj_consume_token(26);
                                    crit = this.Criteria();
                                    crits.add(crit);
                                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                                        case 26: {
                                            continue block21;
                                        }
                                    }
                                    break;
                                }
                                this.jj_la1[12] = this.jj_gen;
                                break block0;
                            }
                            case 25: {
                                block22: while (true) {
                                    this.jj_consume_token(25);
                                    crit = this.Criteria();
                                    crits.add(crit);
                                    isAnd = true;
                                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                                        case 25: {
                                            continue block22;
                                        }
                                    }
                                    break;
                                }
                                this.jj_la1[13] = this.jj_gen;
                                break block0;
                            }
                            case 24: {
                                block23: while (true) {
                                    this.jj_consume_token(24);
                                    crit = this.Criteria();
                                    crits.add(crit);
                                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                                        case 24: {
                                            continue block23;
                                        }
                                    }
                                    break;
                                }
                                this.jj_la1[14] = this.jj_gen;
                                break block0;
                            }
                        }
                        this.jj_la1[15] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                this.jj_la1[16] = this.jj_gen;
                break;
            }
            default: {
                this.jj_la1[17] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        this.jj_consume_token(5);
        MCriteria ret = crits.size() == 1 ? (MCriteria)crits.get(0) : (isAnd ? new MCriteria.And(crits.toArray(new MCriteria[0])) : new MCriteria.Or(crits.toArray(new MCriteria[0])));
        if (isNot) {
            ret = new MCriteria.Not(ret);
        }
        return ret;
    }

    public final MCriteria SimpleCriteria() throws ParseException {
        MCriteria crit;
        MValue v1 = null;
        MAttribute attr = this.Attribute();
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 8: 
            case 9: 
            case 10: 
            case 11: 
            case 12: 
            case 13: 
            case 14: 
            case 15: {
                MCriteria.SimpleOp op = this.SimpleCriteriaOp();
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 4: 
                    case 35: 
                    case 36: 
                    case 37: 
                    case 38: 
                    case 39: 
                    case 40: 
                    case 41: 
                    case 42: 
                    case 44: 
                    case 45: 
                    case 54: 
                    case 55: {
                        v1 = this.Value();
                        break;
                    }
                    default: {
                        this.jj_la1[18] = this.jj_gen;
                    }
                }
                crit = new MCriteria.Simple(attr, op, v1);
                break;
            }
            case 22: {
                crit = this.ILikeCriteria(attr);
                break;
            }
            case 23: {
                crit = this.LikeCriteria(attr);
                break;
            }
            case 24: {
                crit = this.InCriteria(attr);
                break;
            }
            case 21: {
                crit = this.SQLBetweenCriteria(attr);
                break;
            }
            case 20: {
                crit = this.IsBetweenCriteria(attr);
                break;
            }
            default: {
                this.jj_la1[19] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        return crit;
    }

    public final MCriteria IsBetweenCriteria(MAttribute attr) throws ParseException {
        boolean includeLower = false;
        boolean includeUpper = false;
        this.jj_consume_token(20);
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 6: {
                this.jj_consume_token(6);
                includeLower = true;
                break;
            }
            default: {
                this.jj_la1[20] = this.jj_gen;
            }
        }
        MValue v1 = this.Value();
        MValue v2 = this.Value();
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 7: {
                this.jj_consume_token(7);
                includeUpper = true;
                break;
            }
            default: {
                this.jj_la1[21] = this.jj_gen;
            }
        }
        return new MCriteria.Between(attr, v1, includeLower, v2, includeUpper);
    }

    public final MCriteria SQLBetweenCriteria(MAttribute attr) throws ParseException {
        boolean includeLower = true;
        boolean includeUpper = true;
        this.jj_consume_token(21);
        MValue v1 = this.Value();
        this.jj_consume_token(25);
        MValue v2 = this.Value();
        return new MCriteria.Between(attr, v1, includeLower, v2, includeUpper);
    }

    public final MCriteria InCriteria(MAttribute attr) throws ParseException {
        ArrayList<MCriteria.Simple> crits = new ArrayList<MCriteria.Simple>(10);
        this.jj_consume_token(24);
        this.jj_consume_token(4);
        MValue val = this.Value();
        crits.add(new MCriteria.Simple(attr, MCriteria.SimpleOp.EQ, val));
        block3: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 3: {
                    break;
                }
                default: {
                    this.jj_la1[22] = this.jj_gen;
                    break block3;
                }
            }
            this.jj_consume_token(3);
            val = this.Value();
            crits.add(new MCriteria.Simple(attr, MCriteria.SimpleOp.EQ, val));
        }
        this.jj_consume_token(5);
        return new MCriteria.Or(crits.toArray(new MCriteria[crits.size()]));
    }

    public final MCriteria ILikeCriteria(MAttribute attr) throws ParseException {
        this.jj_consume_token(22);
        StringAndToken s = this.SingleQuotedString();
        return new MCriteria.ILike(attr, s.string);
    }

    public final MCriteria LikeCriteria(MAttribute attr) throws ParseException {
        this.jj_consume_token(23);
        StringAndToken s = this.SingleQuotedString();
        return new MCriteria.Like(attr, s.string);
    }

    public final MCriteria.SimpleOp SimpleCriteriaOp() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 8: {
                Token t = this.jj_consume_token(8);
                return MCriteria.SimpleOp.EQ;
            }
            case 9: {
                Token t = this.jj_consume_token(9);
                return MCriteria.SimpleOp.NE;
            }
            case 10: {
                Token t = this.jj_consume_token(10);
                return MCriteria.SimpleOp.NULL;
            }
            case 11: {
                Token t = this.jj_consume_token(11);
                return MCriteria.SimpleOp.NOT_NULL;
            }
            case 15: {
                Token t = this.jj_consume_token(15);
                return MCriteria.SimpleOp.LT;
            }
            case 13: {
                Token t = this.jj_consume_token(13);
                return MCriteria.SimpleOp.GT;
            }
            case 14: {
                Token t = this.jj_consume_token(14);
                return MCriteria.SimpleOp.LE;
            }
            case 12: {
                Token t = this.jj_consume_token(12);
                return MCriteria.SimpleOp.GE;
            }
        }
        this.jj_la1[23] = this.jj_gen;
        this.jj_consume_token(-1);
        throw new ParseException();
    }

    public final MValue Value() throws ParseException {
        Token t = null;
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 55: {
                StringAndToken s = this.SingleQuotedString();
                return new MValue.MString(s.token, s.string);
            }
            case 45: {
                t = this.jj_consume_token(45);
                StringAndToken s = this.SingleQuotedString();
                return new MValue.MChar(s.token, s.string);
            }
            case 54: {
                t = this.jj_consume_token(54);
                return new MValue.MInt(t, t.image);
            }
            case 35: {
                t = this.jj_consume_token(35);
                StringAndToken s = this.SingleQuotedString();
                return new MValue.MBool(s.token, s.string);
            }
            case 36: {
                this.jj_consume_token(36);
                t = this.jj_consume_token(54);
                return new MValue.MByte(t, t.image);
            }
            case 37: {
                this.jj_consume_token(37);
                t = this.jj_consume_token(54);
                return new MValue.MInt(t, t.image);
            }
            case 38: {
                this.jj_consume_token(38);
                t = this.jj_consume_token(54);
                return new MValue.MShort(t, t.image);
            }
            case 39: {
                this.jj_consume_token(39);
                t = this.jj_consume_token(54);
                return new MValue.MLong(t, t.image);
            }
            case 40: {
                t = this.jj_consume_token(40);
                StringAndToken s = this.SingleQuotedString();
                return new MValue.MFloat(s.token, s.string);
            }
            case 41: {
                t = this.jj_consume_token(41);
                StringAndToken s = this.SingleQuotedString();
                return new MValue.MDouble(s.token, s.string);
            }
            case 44: {
                t = this.jj_consume_token(44);
                StringAndToken s = this.SingleQuotedString();
                return new MValue.MSqlDate(s.token, s.string);
            }
            case 42: {
                t = this.jj_consume_token(42);
                StringAndToken s = this.SingleQuotedString();
                return new MValue.MJavaDate(s.token, s.string);
            }
            case 4: {
                this.jj_consume_token(4);
                t = this.jj_consume_token(57);
                this.jj_consume_token(5);
                StringAndToken s = this.SingleQuotedString();
                return new MValue.MEnum(t, t.image.substring("enum".length()).trim(), s.string);
            }
        }
        this.jj_la1[24] = this.jj_gen;
        this.jj_consume_token(-1);
        throw new ParseException();
    }

    public final void TargetList() throws ParseException {
        this.SingleTarget();
        block3: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 3: {
                    break;
                }
                default: {
                    this.jj_la1[25] = this.jj_gen;
                    break block3;
                }
            }
            this.jj_consume_token(3);
            this.SingleTarget();
        }
    }

    public final void SingleTarget() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 1: {
                this.jj_consume_token(1);
                this.qmodel.includeTargetStar();
                break;
            }
            case 2: {
                this.jj_consume_token(2);
                this.qmodel.includeTargetStar();
                break;
            }
            case 16: 
            case 17: 
            case 56: {
                MAttribute m = this.Attribute();
                this.qmodel.includeTargetAttribute(m);
                break;
            }
            case 27: 
            case 28: 
            case 29: 
            case 30: 
            case 31: {
                MAggregate agg = this.Aggregate();
                this.qmodel.includeTargetAggregator(agg);
                break;
            }
            default: {
                this.jj_la1[26] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }

    public final MAggregate Aggregate() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 27: {
                this.jj_consume_token(27);
                this.jj_consume_token(4);
                MAttribute ma = this.Attribute();
                this.jj_consume_token(5);
                return new MAggregate(MAggregate.AggOp.Sum, ma);
            }
            case 29: {
                this.jj_consume_token(29);
                this.jj_consume_token(4);
                MAttribute ma = this.Attribute();
                this.jj_consume_token(5);
                return new MAggregate(MAggregate.AggOp.Min, ma);
            }
            case 28: {
                this.jj_consume_token(28);
                this.jj_consume_token(4);
                MAttribute ma = this.Attribute();
                this.jj_consume_token(5);
                return new MAggregate(MAggregate.AggOp.Max, ma);
            }
            case 30: {
                this.jj_consume_token(30);
                this.jj_consume_token(4);
                MAttribute ma = this.Attribute();
                this.jj_consume_token(5);
                return new MAggregate(MAggregate.AggOp.Average, ma);
            }
            case 31: {
                this.jj_consume_token(31);
                this.jj_consume_token(4);
                MAttribute ma = this.Attribute();
                this.jj_consume_token(5);
                return new MAggregate(MAggregate.AggOp.Count, ma);
            }
        }
        this.jj_la1[27] = this.jj_gen;
        this.jj_consume_token(-1);
        throw new ParseException();
    }

    public final MAttribute Attribute() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 16: {
                this.jj_consume_token(16);
                return MAttribute.KEY;
            }
            case 17: {
                this.jj_consume_token(17);
                return MAttribute.VALUE;
            }
            case 1: {
                this.jj_consume_token(1);
                return MAttribute.STAR;
            }
            case 2: {
                this.jj_consume_token(2);
                return MAttribute.STAR;
            }
            case 56: {
                String t = this.UnQuotedString();
                return new MAttribute(t);
            }
        }
        this.jj_la1[28] = this.jj_gen;
        this.jj_consume_token(-1);
        throw new ParseException();
    }

    public final StringAndToken SingleQuotedString() throws ParseException {
        Token t = this.jj_consume_token(55);
        return new StringAndToken(t, ParserSupport.processQuotedString(t, t.image));
    }

    public final String UnQuotedString() throws ParseException {
        Token t = this.jj_consume_token(56);
        return t.image;
    }

    private static void jj_la1_init_0() {
        jj_la1_0 = new int[]{0, 262144, 0, 0, 0, 8, 0, 0, 8, 0, 0, 196630, 0x4000000, 0x2000000, 0x1000000, 0x7000000, 0x7000000, 720918, 16, 0x1F0FF00, 64, 128, 8, 65280, 16, 8, -134021114, -134217728, 196614};
    }

    private static void jj_la1_init_1() {
        jj_la1_1 = new int[]{0x180001, 0, 0x240000, 0x240000, 4, 0, 245760, 245760, 0, 245760, 245760, 0x1000000, 0, 0, 0, 0, 0, 0x1000000, 12597240, 0, 0, 0, 0, 0, 12597240, 0, 0x1000000, 0, 0x1000000};
    }

    public EhcacheSearchParser(InputStream stream) {
        this(stream, null);
    }

    public EhcacheSearchParser(InputStream stream, String encoding) {
        try {
            this.jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        this.token_source = new EhcacheSearchParserTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 29; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    public void ReInit(InputStream stream) {
        this.ReInit(stream, null);
    }

    public void ReInit(InputStream stream, String encoding) {
        try {
            this.jj_input_stream.ReInit(stream, encoding, 1, 1);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        this.token_source.ReInit(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 29; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    public EhcacheSearchParser(Reader stream) {
        this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
        this.token_source = new EhcacheSearchParserTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 29; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    public void ReInit(Reader stream) {
        this.jj_input_stream.ReInit(stream, 1, 1);
        this.token_source.ReInit(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 29; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    public EhcacheSearchParser(EhcacheSearchParserTokenManager tm) {
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 29; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    public void ReInit(EhcacheSearchParserTokenManager tm) {
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 29; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    private Token jj_consume_token(int kind) throws ParseException {
        Token oldToken = this.token;
        this.token = oldToken.next != null ? this.token.next : (this.token.next = this.token_source.getNextToken());
        this.jj_ntk = -1;
        if (this.token.kind == kind) {
            ++this.jj_gen;
            return this.token;
        }
        this.token = oldToken;
        this.jj_kind = kind;
        throw this.generateParseException();
    }

    public final Token getNextToken() {
        this.token = this.token.next != null ? this.token.next : (this.token.next = this.token_source.getNextToken());
        this.jj_ntk = -1;
        ++this.jj_gen;
        return this.token;
    }

    public final Token getToken(int index) {
        Token t = this.token;
        for (int i = 0; i < index; ++i) {
            t = t.next != null ? t.next : (t.next = this.token_source.getNextToken());
        }
        return t;
    }

    private int jj_ntk() {
        this.jj_nt = this.token.next;
        if (this.jj_nt == null) {
            this.token.next = this.token_source.getNextToken();
            this.jj_ntk = this.token.next.kind;
            return this.jj_ntk;
        }
        this.jj_ntk = this.jj_nt.kind;
        return this.jj_ntk;
    }

    public ParseException generateParseException() {
        int i;
        this.jj_expentries.clear();
        boolean[] la1tokens = new boolean[62];
        if (this.jj_kind >= 0) {
            la1tokens[this.jj_kind] = true;
            this.jj_kind = -1;
        }
        for (i = 0; i < 29; ++i) {
            if (this.jj_la1[i] != this.jj_gen) continue;
            for (int j = 0; j < 32; ++j) {
                if ((jj_la1_0[i] & 1 << j) != 0) {
                    la1tokens[j] = true;
                }
                if ((jj_la1_1[i] & 1 << j) == 0) continue;
                la1tokens[32 + j] = true;
            }
        }
        for (i = 0; i < 62; ++i) {
            if (!la1tokens[i]) continue;
            this.jj_expentry = new int[1];
            this.jj_expentry[0] = i;
            this.jj_expentries.add(this.jj_expentry);
        }
        int[][] exptokseq = new int[this.jj_expentries.size()][];
        for (int i2 = 0; i2 < this.jj_expentries.size(); ++i2) {
            exptokseq[i2] = this.jj_expentries.get(i2);
        }
        return new ParseException(this.token, exptokseq, tokenImage);
    }

    public final void enable_tracing() {
    }

    public final void disable_tracing() {
    }

    static {
        EhcacheSearchParser.jj_la1_init_0();
        EhcacheSearchParser.jj_la1_init_1();
    }

    static final class StringAndToken {
        Token token;
        String string;

        StringAndToken(Token tok, String s) {
            this.token = tok;
            this.string = s;
        }
    }
}

