/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import org.aspectj.weaver.Shadow;
import org.aspectj.weaver.patterns.AndPointcut;
import org.aspectj.weaver.patterns.IfPointcut;
import org.aspectj.weaver.patterns.NotPointcut;
import org.aspectj.weaver.patterns.OrPointcut;
import org.aspectj.weaver.patterns.Pointcut;
import org.aspectj.weaver.patterns.PointcutEvaluationExpenseComparator;

public class PointcutRewriter {
    private static final boolean WATCH_PROGRESS = false;

    public Pointcut rewrite(Pointcut pc, boolean forceRewrite) {
        Pointcut result = pc;
        if (forceRewrite || !this.isDNF(pc)) {
            result = this.distributeNot(result);
            result = this.pullUpDisjunctions(result);
        }
        result = this.simplifyAnds(result);
        result = this.removeNothings(result);
        result = this.sortOrs(result);
        return result;
    }

    public Pointcut rewrite(Pointcut pc) {
        return this.rewrite(pc, false);
    }

    private boolean isDNF(Pointcut pc) {
        return this.isDNFHelper(pc, true);
    }

    private boolean isDNFHelper(Pointcut pc, boolean canStillHaveOrs) {
        if (this.isAnd(pc)) {
            AndPointcut ap = (AndPointcut)pc;
            return this.isDNFHelper(ap.getLeft(), false) && this.isDNFHelper(ap.getRight(), false);
        }
        if (this.isOr(pc)) {
            if (!canStillHaveOrs) {
                return false;
            }
            OrPointcut op = (OrPointcut)pc;
            return this.isDNFHelper(op.getLeft(), true) && this.isDNFHelper(op.getRight(), true);
        }
        if (this.isNot(pc)) {
            return this.isDNFHelper(((NotPointcut)pc).getNegatedPointcut(), canStillHaveOrs);
        }
        return true;
    }

    public static String format(Pointcut p) {
        String s = p.toString();
        return s;
    }

    private Pointcut distributeNot(Pointcut pc) {
        if (this.isNot(pc)) {
            NotPointcut npc = (NotPointcut)pc;
            Pointcut notBody = this.distributeNot(npc.getNegatedPointcut());
            if (this.isNot(notBody)) {
                return ((NotPointcut)notBody).getNegatedPointcut();
            }
            if (this.isAnd(notBody)) {
                AndPointcut apc = (AndPointcut)notBody;
                Pointcut newLeft = this.distributeNot(new NotPointcut(apc.getLeft(), npc.getStart()));
                Pointcut newRight = this.distributeNot(new NotPointcut(apc.getRight(), npc.getStart()));
                return new OrPointcut(newLeft, newRight);
            }
            if (this.isOr(notBody)) {
                OrPointcut opc = (OrPointcut)notBody;
                Pointcut newLeft = this.distributeNot(new NotPointcut(opc.getLeft(), npc.getStart()));
                Pointcut newRight = this.distributeNot(new NotPointcut(opc.getRight(), npc.getStart()));
                return new AndPointcut(newLeft, newRight);
            }
            return new NotPointcut(notBody, npc.getStart());
        }
        if (this.isAnd(pc)) {
            AndPointcut apc = (AndPointcut)pc;
            Pointcut left = this.distributeNot(apc.getLeft());
            Pointcut right = this.distributeNot(apc.getRight());
            return new AndPointcut(left, right);
        }
        if (this.isOr(pc)) {
            OrPointcut opc = (OrPointcut)pc;
            Pointcut left = this.distributeNot(opc.getLeft());
            Pointcut right = this.distributeNot(opc.getRight());
            return new OrPointcut(left, right);
        }
        return pc;
    }

    private Pointcut pullUpDisjunctions(Pointcut pc) {
        if (this.isNot(pc)) {
            NotPointcut npc = (NotPointcut)pc;
            return new NotPointcut(this.pullUpDisjunctions(npc.getNegatedPointcut()));
        }
        if (this.isAnd(pc)) {
            AndPointcut apc = (AndPointcut)pc;
            Pointcut left = this.pullUpDisjunctions(apc.getLeft());
            Pointcut right = this.pullUpDisjunctions(apc.getRight());
            if (this.isOr(left) && !this.isOr(right)) {
                Pointcut leftLeft = ((OrPointcut)left).getLeft();
                Pointcut leftRight = ((OrPointcut)left).getRight();
                return this.pullUpDisjunctions(new OrPointcut(new AndPointcut(leftLeft, right), new AndPointcut(leftRight, right)));
            }
            if (this.isOr(right) && !this.isOr(left)) {
                Pointcut rightLeft = ((OrPointcut)right).getLeft();
                Pointcut rightRight = ((OrPointcut)right).getRight();
                return this.pullUpDisjunctions(new OrPointcut(new AndPointcut(left, rightLeft), new AndPointcut(left, rightRight)));
            }
            if (this.isOr(right) && this.isOr(left)) {
                Pointcut A = this.pullUpDisjunctions(((OrPointcut)left).getLeft());
                Pointcut B = this.pullUpDisjunctions(((OrPointcut)left).getRight());
                Pointcut C = this.pullUpDisjunctions(((OrPointcut)right).getLeft());
                Pointcut D = this.pullUpDisjunctions(((OrPointcut)right).getRight());
                OrPointcut newLeft = new OrPointcut(new AndPointcut(A, C), new AndPointcut(A, D));
                OrPointcut newRight = new OrPointcut(new AndPointcut(B, C), new AndPointcut(B, D));
                return this.pullUpDisjunctions(new OrPointcut(newLeft, newRight));
            }
            return new AndPointcut(left, right);
        }
        if (this.isOr(pc)) {
            OrPointcut opc = (OrPointcut)pc;
            return new OrPointcut(this.pullUpDisjunctions(opc.getLeft()), this.pullUpDisjunctions(opc.getRight()));
        }
        return pc;
    }

    public Pointcut not(Pointcut p) {
        if (this.isNot(p)) {
            return ((NotPointcut)p).getNegatedPointcut();
        }
        return new NotPointcut(p);
    }

    public Pointcut createAndsFor(Pointcut[] ps) {
        if (ps.length == 1) {
            return ps[0];
        }
        if (ps.length == 2) {
            return new AndPointcut(ps[0], ps[1]);
        }
        Pointcut[] subset = new Pointcut[ps.length - 1];
        for (int i = 1; i < ps.length; ++i) {
            subset[i - 1] = ps[i];
        }
        return new AndPointcut(ps[0], this.createAndsFor(subset));
    }

    private Pointcut simplifyAnds(Pointcut pc) {
        if (this.isNot(pc)) {
            NotPointcut npc = (NotPointcut)pc;
            Pointcut notBody = npc.getNegatedPointcut();
            if (this.isNot(notBody)) {
                return this.simplifyAnds(((NotPointcut)notBody).getNegatedPointcut());
            }
            return new NotPointcut(this.simplifyAnds(npc.getNegatedPointcut()));
        }
        if (this.isOr(pc)) {
            OrPointcut opc = (OrPointcut)pc;
            return new OrPointcut(this.simplifyAnds(opc.getLeft()), this.simplifyAnds(opc.getRight()));
        }
        if (this.isAnd(pc)) {
            return this.simplifyAnd((AndPointcut)pc);
        }
        return pc;
    }

    private Pointcut simplifyAnd(AndPointcut apc) {
        TreeSet<Pointcut> nodes = new TreeSet<Pointcut>(new PointcutEvaluationExpenseComparator());
        this.collectAndNodes(apc, nodes);
        for (Pointcut element : nodes) {
            Pointcut body;
            if (element instanceof NotPointcut && nodes.contains(body = ((NotPointcut)element).getNegatedPointcut())) {
                return Pointcut.makeMatchesNothing(body.state);
            }
            if (element instanceof IfPointcut && ((IfPointcut)element).alwaysFalse()) {
                return Pointcut.makeMatchesNothing(element.state);
            }
            if (element.couldMatchKinds() != Shadow.NO_SHADOW_KINDS_BITS) continue;
            return element;
        }
        if (apc.couldMatchKinds() == Shadow.NO_SHADOW_KINDS_BITS) {
            return Pointcut.makeMatchesNothing(apc.state);
        }
        Iterator iter = nodes.iterator();
        Pointcut result = (Pointcut)iter.next();
        while (iter.hasNext()) {
            Pointcut right = (Pointcut)iter.next();
            result = new AndPointcut(result, right);
        }
        return result;
    }

    private Pointcut sortOrs(Pointcut pc) {
        TreeSet<Pointcut> nodes = new TreeSet<Pointcut>(new PointcutEvaluationExpenseComparator());
        this.collectOrNodes(pc, nodes);
        Iterator iter = nodes.iterator();
        Pointcut result = (Pointcut)iter.next();
        while (iter.hasNext()) {
            Pointcut right = (Pointcut)iter.next();
            result = new OrPointcut(result, right);
        }
        return result;
    }

    private Pointcut removeNothings(Pointcut pc) {
        if (this.isAnd(pc)) {
            AndPointcut apc = (AndPointcut)pc;
            Pointcut right = this.removeNothings(apc.getRight());
            Pointcut left = this.removeNothings(apc.getLeft());
            if (left instanceof Pointcut.MatchesNothingPointcut || right instanceof Pointcut.MatchesNothingPointcut) {
                return new Pointcut.MatchesNothingPointcut();
            }
            return new AndPointcut(left, right);
        }
        if (this.isOr(pc)) {
            OrPointcut opc = (OrPointcut)pc;
            Pointcut right = this.removeNothings(opc.getRight());
            Pointcut left = this.removeNothings(opc.getLeft());
            if (left instanceof Pointcut.MatchesNothingPointcut && !(right instanceof Pointcut.MatchesNothingPointcut)) {
                return right;
            }
            if (right instanceof Pointcut.MatchesNothingPointcut && !(left instanceof Pointcut.MatchesNothingPointcut)) {
                return left;
            }
            if (!(left instanceof Pointcut.MatchesNothingPointcut) && !(right instanceof Pointcut.MatchesNothingPointcut)) {
                return new OrPointcut(left, right);
            }
            if (left instanceof Pointcut.MatchesNothingPointcut && right instanceof Pointcut.MatchesNothingPointcut) {
                return new Pointcut.MatchesNothingPointcut();
            }
        }
        return pc;
    }

    private void collectAndNodes(AndPointcut apc, Set<Pointcut> nodesSoFar) {
        Pointcut left = apc.getLeft();
        Pointcut right = apc.getRight();
        if (this.isAnd(left)) {
            this.collectAndNodes((AndPointcut)left, nodesSoFar);
        } else {
            nodesSoFar.add(left);
        }
        if (this.isAnd(right)) {
            this.collectAndNodes((AndPointcut)right, nodesSoFar);
        } else {
            nodesSoFar.add(right);
        }
    }

    private void collectOrNodes(Pointcut pc, Set<Pointcut> nodesSoFar) {
        if (this.isOr(pc)) {
            OrPointcut opc = (OrPointcut)pc;
            this.collectOrNodes(opc.getLeft(), nodesSoFar);
            this.collectOrNodes(opc.getRight(), nodesSoFar);
        } else {
            nodesSoFar.add(pc);
        }
    }

    private boolean isNot(Pointcut pc) {
        return pc instanceof NotPointcut;
    }

    private boolean isAnd(Pointcut pc) {
        return pc instanceof AndPointcut;
    }

    private boolean isOr(Pointcut pc) {
        return pc instanceof OrPointcut;
    }
}

