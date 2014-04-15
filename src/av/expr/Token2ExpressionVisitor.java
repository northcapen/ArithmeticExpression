package av.expr;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Created by anton on 4/15/14.
 */
class Token2ExpressionVisitor implements ExpParser.TokenVisitor {
    private Deque<Main.Exp> stack = new ArrayDeque<>();

    @Override
    public void visit(ExpParser.NumberToken numberToken) {
        stack.push(new Main.Num(numberToken.getNum()));
    }

    @Override
    public void visit(ExpParser.Plus plus) {
        Main.Exp op1 = stack.pop();
        Main.Exp op2 = stack.pop();
        stack.push(new Main.Sum(op2, op1));
    }

    @Override
    public void visit(ExpParser.Minus minus) {

    }

    @Override
    public void visit(ExpParser.MultipleSign multipleSign) {
        Main.Exp op1 = stack.pop();
        Main.Exp op2 = stack.pop();
        stack.push(new Main.Mul(op2, op1));

    }

    @Override
    public void visit(ExpParser.DivisionSign divisionSign) {
        Main.Exp op1 = stack.pop();
        Main.Exp op2 = stack.pop();
        stack.push(new Main.Dev(op2, op1));
    }

    @Override
    public void visit(ExpParser.OpenBracket openBracket) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(ExpParser.CloseBracket closeBracket) {
        throw new UnsupportedOperationException();
    }

    public Main.Exp getResult() {
        return stack.pop();
    }

    @Override
    public void visit(ExpParser.VarToken varToken) {
         stack.push(new Main.Var(varToken.getVar().charAt(0)));
    }
}
