package av.expr;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by anton on 4/15/14.
 */
public class ExpParser {
    static Pattern EXPRESSION_PATTERN = Pattern.compile("\\d+|\\+|\\-|\\*|\\/|\\(|\\)|\\w");
    static Pattern ASSIGNMENT_PATTERN = Pattern.compile("(\\w)\\s*=\\s*(.*)");

    static Token toToken(String str) {
        switch (str) {
            case "+" : return new Plus();
            case "-" : return new Minus();
            case "*" : return new MultipleSign();
            case "/" : return new DivisionSign();
            case "(" : return new OpenBracket();
            case ")" : return new CloseBracket();
        }
        if(isNumber(str)) {
           return new NumberToken(str);
        } else  {
           return new VarToken(str);
        }
    }

    private static boolean isNumber(String c) {
        try {
            Integer.parseInt(c);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    Main.Exp parseExpression(String expressionStr) {
        List<Token> infixTokens = toPostfixForm(expressionStr);

        Token2ExpressionVisitor ast2Expression = new Token2ExpressionVisitor();
        for (Token token : infixTokens) {
            token.accept(ast2Expression);
        }
        return ast2Expression.getResult();
    }

    public List<Token> toPostfixForm(String expressionString) {
        Matcher m = EXPRESSION_PATTERN.matcher(expressionString);

        List<Token> tokens = new ArrayList<>();
        while (m.find()) {
            tokens.add(toToken(expressionString.substring(m.start(), m.end())));
        }

        InfixFormParser infixFormParser = new InfixFormParser();
        for (Token token : tokens) {
           token.accept(infixFormParser);
        }

        return infixFormParser.getResults();
    }

    public static interface TokenVisitor {
       public void visit(Plus plus);
       public void visit(Minus minus);
       public void visit(MultipleSign multipleSign);
       public void visit(DivisionSign divisionSign);
       public void visit(OpenBracket openBracket);
       public void visit(CloseBracket closeBracket);
       public void visit(NumberToken numberToken);
       public void visit(VarToken varToken);
    }

    public static class NumberToken extends Token {
        private double num;

        public NumberToken(String num) {
            this.num = Double.parseDouble(num);
        }

        double getNum() {
            return num;
        }

        @Override
        public Main.Exp accept(TokenVisitor tokenVisitor) {
            tokenVisitor.visit(this);
            return null;
        }

        @Override
        public String toString() {
            return Double.toString(num);
        }
    }

    public static class Plus extends Token {
        @Override
        public Main.Exp accept(TokenVisitor tokenVisitor) {
            tokenVisitor.visit(this);
            return null;
        }
    }

    public static class Minus extends Token {
        @Override
        public Main.Exp accept(TokenVisitor tokenVisitor) {
            tokenVisitor.visit(this);
            return null;
        }
    }

    public static class MultipleSign extends Token {
        @Override
        public Main.Exp accept(TokenVisitor tokenVisitor) {
            tokenVisitor.visit(this);
            return null;
        }
    }

    public static class DivisionSign extends Token {
        @Override
        public Main.Exp accept(TokenVisitor tokenVisitor) {
            tokenVisitor.visit(this);
            return null;
        }
    }

    public static class OpenBracket extends Token {
        @Override
        public Main.Exp accept(TokenVisitor tokenVisitor) {
            tokenVisitor.visit(this);
            return null;
        }
    }

    public static class CloseBracket extends Token {
        @Override
        public Main.Exp accept(TokenVisitor tokenVisitor) {
            tokenVisitor.visit(this);
            return null;
        }
    }

    public static class VarToken extends Token {
        private String var;
        public VarToken(String var) {
            this.var = var;
        }

        public String getVar() {
            return var;
        }

        @Override
        public Main.Exp accept(TokenVisitor tokenVisitor) {
            tokenVisitor.visit(this);
            return null;
        }
    }

    public static class InfixFormParser implements TokenVisitor {
        List<Token> results = new ArrayList<>();
        Deque<Token> stack = new ArrayDeque<>();


        public void visit(Plus plus) {
            processLowPrio(plus);
        }

        private void processLowPrio(Token plus) {
            while (!stack.isEmpty() && isOperator(stack.peek())) {
                results.add(stack.pop());
            }
            stack.push(plus);
        }

        public void visit(Minus minus) {
            processLowPrio(minus);
        }

        public void visit(MultipleSign multipleSign) {
            stack.push(multipleSign);
        }

        public void visit(DivisionSign divisionSign) {
            stack.push(divisionSign);
        }

        public void visit(OpenBracket openBracket) {
            stack.push(openBracket);
        }

        public void visit(CloseBracket closeBracket) {
            while (!stack.isEmpty() && !isOpeningBracket(stack.peek())) {
                Token pop = stack.pop();
                results.add(pop);
            }
        }

        public void visit(NumberToken numberToken) {
            results.add(numberToken);
        }

        @Override
        public void visit(VarToken varToken) {
            results.add(varToken);
        }

        public List<Token> getResults() {
            while (!stack.isEmpty()) {
                Token pop = stack.pop();
                if (!isOpeningBracket(pop)) {
                    results.add(pop);
                }
            }
            return results;
        }

        private static boolean isOpeningBracket(Token token) {
            return token instanceof OpenBracket;
        }

        private static boolean isOperator(Token c) {
            return isLowPriorityOperator(c) || isHighPriorityOperator(c);
        }

        private static boolean isLowPriorityOperator(Token t) {
            return t instanceof Plus || t instanceof Minus;
        }

        private static boolean isHighPriorityOperator(Token t) {
            return t instanceof MultipleSign || t instanceof DivisionSign;
        }

    }

    abstract static class Token { public abstract Main.Exp accept(TokenVisitor tokenVisitor); }
}
