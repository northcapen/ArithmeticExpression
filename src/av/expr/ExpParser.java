package av.expr;

import org.junit.Assert;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.regex.Pattern;

/**
 * Created by anton on 4/15/14.
 */
public class ExpParser {

    public static void main(String[] args) {
        //Pattern p = Pattern.compile("\\d*|\\+\\-\\*\\/");

        String s = "1+4*7+9-2"; //147*+9+2-
        StringBuffer out = new StringBuffer();
        Deque<Character> stack = new ArrayDeque<Character>();


        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isDigit(c)) {
                out.append(c);
            } else if (isOperator(c)) {
                if (isLowPriorityOperator(c)) {
                    while (!stack.isEmpty() && isOperator(stack.peek())) {
                        out.append(stack.pop());
                    }
                }
                stack.push(c);
            } else {
               throw new UnsupportedOperationException("Character " + c + " is not supported");
            }
        }
        while (!stack.isEmpty()) {
              out.append(stack.pop());
        }
        Assert.assertEquals("147*+9+2-", out.toString());
    }

    private static boolean isOperator(char c) {
        return isLowPriorityOperator(c) || isHighPriorityOperator(c);
    }

    private static boolean isLowPriorityOperator(char c) {
        return c == '+' || c == '-';
    }

    private static boolean isHighPriorityOperator(char c) {
        return c == '*' || c == '/';
    }
}