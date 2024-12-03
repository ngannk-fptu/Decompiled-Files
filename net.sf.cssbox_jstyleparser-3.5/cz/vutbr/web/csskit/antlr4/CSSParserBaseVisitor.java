/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.antlr.v4.runtime.tree.AbstractParseTreeVisitor
 *  org.antlr.v4.runtime.tree.RuleNode
 */
package cz.vutbr.web.csskit.antlr4;

import cz.vutbr.web.csskit.antlr4.CSSParser;
import cz.vutbr.web.csskit.antlr4.CSSParserVisitor;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.RuleNode;

public class CSSParserBaseVisitor<T>
extends AbstractParseTreeVisitor<T>
implements CSSParserVisitor<T> {
    @Override
    public T visitInlinestyle(CSSParser.InlinestyleContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }

    @Override
    public T visitStylesheet(CSSParser.StylesheetContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }

    @Override
    public T visitStatement(CSSParser.StatementContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }

    @Override
    public T visitAtstatement(CSSParser.AtstatementContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }

    @Override
    public T visitImport_uri(CSSParser.Import_uriContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }

    @Override
    public T visitPage(CSSParser.PageContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }

    @Override
    public T visitMargin_rule(CSSParser.Margin_ruleContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }

    @Override
    public T visitInlineset(CSSParser.InlinesetContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }

    @Override
    public T visitMedia(CSSParser.MediaContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }

    @Override
    public T visitMedia_query(CSSParser.Media_queryContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }

    @Override
    public T visitMedia_term(CSSParser.Media_termContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }

    @Override
    public T visitMedia_expression(CSSParser.Media_expressionContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }

    @Override
    public T visitMedia_rule(CSSParser.Media_ruleContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }

    @Override
    public T visitKeyframes_name(CSSParser.Keyframes_nameContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }

    @Override
    public T visitKeyframe_block(CSSParser.Keyframe_blockContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }

    @Override
    public T visitKeyframe_selector(CSSParser.Keyframe_selectorContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }

    @Override
    public T visitUnknown_atrule(CSSParser.Unknown_atruleContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }

    @Override
    public T visitUnknown_atrule_body(CSSParser.Unknown_atrule_bodyContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }

    @Override
    public T visitRuleset(CSSParser.RulesetContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }

    @Override
    public T visitDeclarations(CSSParser.DeclarationsContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }

    @Override
    public T visitDeclaration(CSSParser.DeclarationContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }

    @Override
    public T visitImportant(CSSParser.ImportantContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }

    @Override
    public T visitProperty(CSSParser.PropertyContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }

    @Override
    public T visitTerms(CSSParser.TermsContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }

    @Override
    public T visitTermValuePart(CSSParser.TermValuePartContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }

    @Override
    public T visitTermInvalid(CSSParser.TermInvalidContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }

    @Override
    public T visitFunct(CSSParser.FunctContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }

    @Override
    public T visitValuepart(CSSParser.ValuepartContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }

    @Override
    public T visitFunct_args(CSSParser.Funct_argsContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }

    @Override
    public T visitFunct_argument(CSSParser.Funct_argumentContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }

    @Override
    public T visitCombined_selector(CSSParser.Combined_selectorContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }

    @Override
    public T visitCombinator(CSSParser.CombinatorContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }

    @Override
    public T visitSelector(CSSParser.SelectorContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }

    @Override
    public T visitSelpart(CSSParser.SelpartContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }

    @Override
    public T visitAttribute(CSSParser.AttributeContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }

    @Override
    public T visitPseudo(CSSParser.PseudoContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }

    @Override
    public T visitString(CSSParser.StringContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }

    @Override
    public T visitBracketed_idents(CSSParser.Bracketed_identsContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }

    @Override
    public T visitIdent_list_item(CSSParser.Ident_list_itemContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }

    @Override
    public T visitAny(CSSParser.AnyContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }

    @Override
    public T visitNostatement(CSSParser.NostatementContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }

    @Override
    public T visitNoprop(CSSParser.NopropContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }

    @Override
    public T visitNorule(CSSParser.NoruleContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }

    @Override
    public T visitNomediaquery(CSSParser.NomediaqueryContext ctx) {
        return (T)this.visitChildren((RuleNode)ctx);
    }
}

