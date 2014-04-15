package av.expr;

/**
 * Created by anton on 4/15/14.
 */
class SerializeTokenVisitor implements ExpParser.TokenVisitor {
    private StringBuffer sb = new StringBuffer();

    @Override
    public void visit(ExpParser.Plus plus) {
        sb.append("+");
    }

    @Override
    public void visit(ExpParser.Minus minus) {
        sb.append("-");
    }

    @Override
    public void visit(ExpParser.MultipleSign multipleSign) {
        sb.append("*");
    }

    @Override
    public void visit(ExpParser.DivisionSign divisionSign) {
        sb.append("/");
    }

    @Override
    public void visit(ExpParser.OpenBracket openBracket) {
        sb.append("(");
    }

    @Override
    public void visit(ExpParser.CloseBracket closeBracket) {
        sb.append(")");
    }

    @Override
    public void visit(ExpParser.NumberToken numberToken) {
        sb.append(numberToken.toString());
    }

    @Override
    public void visit(ExpParser.VarToken varToken) {
        sb.append(varToken.getVar());
    }

    String getResult() {
        return sb.toString();
    }
}
