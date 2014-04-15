package av.expr;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by anton on 4/15/14.
 */
public class ExpressionTest {
    private ExpParser expParser = new ExpParser();

    public void infixToPostFix() {

    }

    @Test
    public void unitTest(String expressionString) {
        SerializeTokenVisitor printer = new SerializeTokenVisitor();
        for (ExpParser.Token token : expParser.toPostfixForm(expressionString)) {
            token.accept(printer);
        }

        String result = printer.getResult();
        Assert.assertEquals("1.04.0+7.0*9.02.0/+", result);
    }

}
