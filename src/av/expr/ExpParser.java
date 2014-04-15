package av.expr;

import org.junit.Assert;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpParser {
    static Pattern p = Pattern.compile("\\d|\\+|\\-|\\*|\\/|\\(|\\)");

    private static class Token {
        private String value;

        private Token(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public static void main(String[] args) {
        String s = "(1+4)*7+9-2"; //147*+9+2-
        StringBuffer out = toInfixForm(p, s);
        Assert.assertEquals("14+7*9+2-", out.toString());
    }

    private static StringBuffer toInfixForm(Pattern p, String s) {
        Matcher m = p.matcher(s);
        StringBuffer out = new StringBuffer();
        Deque<Token> stack = new ArrayDeque<Token>();

        List<Token> tokens = new ArrayList<Token>();
        while (m.find()) {
            String substring = s.substring(m.start(), m.end());
            System.out.print(substring + " ");
            tokens.add(new Token(substring));
        }

        for (Token token : tokens) {
            if (isNumber(token)) {
                out.append(token);
            } else if (isOperator(token)) {
                if (isLowPriorityOperator(token)) {
                    while (!stack.isEmpty() && isOperator(stack.peek())) {
                        out.append(stack.pop());
                    }
                }
                stack.push(token);
            } else if(isOpeningBracket(token)) {
                stack.push(token);
            } else if(isClosingBracket(token)) {
                while (!stack.isEmpty() && !isOpeningBracket(stack.peek())) {
                    Token pop = stack.pop();
                    out.append(pop);
                }
            } else {
                throw new UnsupportedOperationException("Character " + token + " is not supported");
            }
        }
        while (!stack.isEmpty()) {
            Token pop = stack.pop();
            if (!isOpeningBracket(pop)) {
                out.append(pop);
            }
        }
        return out;
    }

    private static boolean isOpeningBracket(Token token) {
        return "(".equals(token.value);
    }

    private static boolean isClosingBracket(Token token) {
        return ")".equals(token.value);
    }

    private static boolean isNumber(Token c) {
        try {
            Integer.parseInt(c.value);
            return true;
        } catch (NumberFormatException nf) {
            return false;
        }
    }

    private static boolean isOperator(Token c) {
        return isLowPriorityOperator(c) || isHighPriorityOperator(c);
    }

    private static boolean isLowPriorityOperator(Token t) {
        String c = t.value;
        return "+".equals(c) || "-".equals(c);
    }

    private static boolean isHighPriorityOperator(Token t) {
        String c = t.value;
        return "*".equals(c) || "/".equals(c);
    }
}