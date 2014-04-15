package av.expr;

import java.util.*;
import java.util.regex.Matcher;

public class ExpResolver {

    ExpParser expParser = new ExpParser();

    public String simplifyStatement(String statement, Map<Main.Var, Main.Exp> context) {
        return resolveStatement(statement, context, false);
    }

    public String evaluateStatement(String statement, Map<Main.Var, Main.Exp> context) {
        return resolveStatement(statement, context, true);
    }

    private String resolveStatement(String statement, Map<Main.Var, Main.Exp> context, boolean strict) {
        Matcher matcher = ExpParser.ASSIGNMENT_PATTERN.matcher(statement);
        if (matcher.matches()) {
            String var = matcher.group(1);
            String expStr = matcher.group(2);
            Main.Exp expression = resolveExpression(expStr, new HashMap<Main.Var, Main.Exp>(), strict);
            context.put(new Main.Var(var.charAt(0)), expression);
            return printExpression(expression);
        } else {
            return printExpression(resolveExpression(statement, context, strict));
        }
    }

    private Main.Exp resolveExpression(String expressionStr, Map<Main.Var, Main.Exp> context, boolean strict) {
        Main.Exp fullExpTree = expParser.parseExpression(expressionStr);

        Main.Evaluator evaluator = new Main.Evaluator(context, strict);
        fullExpTree.accept(evaluator);

        return evaluator.getResult();
    }

    private String printExpression(Main.Exp exp) {
        Main.PrettyPrinter prettyPrinter = new Main.PrettyPrinter();
        exp.accept(prettyPrinter);
        return prettyPrinter.getResult();
    }
}