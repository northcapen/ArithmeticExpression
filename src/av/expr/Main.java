package av.expr;

import java.util.*;

public class Main {
    public static interface ExpVisitor {
        Exp visit(Num num);

        Exp visit(Sum sum);

        Exp visit(Mul mul);

        Exp visit(Dev dev);

        Exp visit(Var var);

        void visit(Assign assign);
    }

    public static interface Exp {
        void accept(ExpVisitor visitor);

        void traverse(ExpVisitor visitor);

        Iterator<Exp> iterator();
    }

    public static class Num implements Exp {
        public final Number number;

        public Num(Number number) {
            this.number = number;
        }

        public void accept(ExpVisitor visitor) {
            visitor.visit(this);
        }

        public void traverse(ExpVisitor visitor) {
            visitor.visit(this);
        }

        @Override
        public Iterator<Exp> iterator() {
            return new Iterator<Exp>() {
                boolean hasNext = true;

                @Override
                public boolean hasNext() {
                    return hasNext;
                }

                @Override
                public Exp next() {
                    try {
                        return Num.this;
                    } finally {
                        hasNext = false;
                    }
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }

    public static class Var implements Exp {
        private char c;

        public Var(char c) {
            this.c = c;
        }

        @Override
        public void accept(ExpVisitor visitor) {
            visitor.visit(this);
        }

        @Override
        public void traverse(ExpVisitor visitor) {
            visitor.visit(this);
        }

        @Override
        public Iterator<Exp> iterator() {
            return null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Var var = (Var) o;

            if (c != var.c) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return (int) c;
        }
    }

    public static abstract class BiExp implements Exp {
        public final Exp left;
        public final Exp right;

        public BiExp(Exp left, Exp right) {
            this.left = left;
            this.right = right;
        }

        public Iterator<Exp> iterator() {
            return new Iterator<Exp>() {
                private final Iterator<Exp> leftIter = left.iterator();
                private final Iterator<Exp> rightIter = right.iterator();
                private boolean doneThis = false;

                public boolean hasNext() {
                    return !doneThis || leftIter.hasNext() || rightIter.hasNext();
                }

                public Exp next() {
                    if (!doneThis) {
                        try {
                            return BiExp.this;
                        } finally {
                            doneThis = true;
                        }
                    }
                    if (leftIter.hasNext()) return leftIter.next();
                    if (rightIter.hasNext()) return rightIter.next();
                    throw new IllegalStateException();
                }

                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }

    public static class Sum extends BiExp {
        public Sum(Exp left, Exp right) {
            super(left, right);
        }

        public void accept(ExpVisitor matcher) {
            matcher.visit(this);
        }

        public void traverse(ExpVisitor visitor) {
            left.traverse(visitor);
            visitor.visit(this);
            right.traverse(visitor);
        }
    }

    public static class Mul extends BiExp {
        public Mul(Exp left, Exp right) {
            super(left, right);
        }

        public void accept(ExpVisitor matcher) {
            matcher.visit(this);
        }

        public void traverse(ExpVisitor visitor) {
            left.traverse(visitor);
            visitor.visit(this);
            right.traverse(visitor);
        }
    }

    public static class Dev extends BiExp {
        public Dev(Exp left, Exp right) {
            super(left, right);
        }

        public void accept(ExpVisitor matcher) {
            matcher.visit(this);
        }

        public void traverse(ExpVisitor visitor) {
            left.traverse(visitor);
            visitor.visit(this);
            right.traverse(visitor);
        }
    }

    public static class Assign extends BiExp {

        public Assign(Exp left, Exp right) {
            super(left, right);
        }

        @Override
        public void accept(ExpVisitor visitor) {
            visitor.visit(this);
        }

        public void traverse(ExpVisitor visitor) {
            left.traverse(visitor);
            visitor.visit(this);
            right.traverse(visitor);
        }

    }

    /*
        private static void prettyPrint(Exp exp) {
            if (exp instanceof Num) {
                prettyPrint((Num) exp);
            } else if (exp instanceof Sum) {
                prettyPrint((Sum) exp);
            } else if (exp instanceof Mul) {
                prettyPrint((Mul) exp);
            } else if (exp instanceof Dev) {
                prettyPrint((Dev) exp);
            }
        }
    */
    private static void prettyPrint(Num exp) {
        System.out.print(exp.number);
    }

    private static void prettyPrint(Dev exp) {
    /*
            prettyPrint(exp.left);
    */
        System.out.print(" / ");
    /*
            prettyPrint(exp.right);
    */
    }

    private static void prettyPrint(Mul exp) {
    /*
            prettyPrint(exp.left);
    */
        System.out.print(" * ");
    /*
            prettyPrint(exp.right);
    */
    }

    private static void prettyPrint(Sum exp) {
        System.out.print("(");
    /*
            prettyPrint(exp.left);
    */
        System.out.print(" + ");
    /*
            prettyPrint(exp.right);
    */
        System.out.print(")");
    }

    public static void main(String[] args) {
        Map<Var, Exp> context = new HashMap<>();
        Evaluator evaluator = new Evaluator(context);

        Exp exp1 = new Assign(new Var('x'), new Num(18));
        exp1.accept(evaluator);
        System.out.println(context.get(new Var('x')));

        Exp exp2 = new Sum(
                new Mul(
                        new Sum(new Num(10.0), new Num(21.0)),
                        new Sum(new Num(22.0), new Num(14.0))
                ),
                new Dev(
                        new Sum(new Num(15), new Num(88)),
                        new Mul(new Num(11), new Num(18))
                        //new Num(0)
                )
        );
        PrettyPrinter prettyPrinter = new PrettyPrinter();
        exp2.accept(evaluator);
        Exp resolvedResult = evaluator.getResult();
        resolvedResult.accept(prettyPrinter);
        System.out.println(prettyPrinter.getResult());

    }

     static class Evaluator implements ExpVisitor {
        private final Deque<Exp> queue = new ArrayDeque<>();
         private final Map<Var, Exp> context;
         private boolean strict;

         public Evaluator(Map<Var, Exp> context) {
            this.context = context;
        }

         Evaluator(Map<Var, Exp> context, boolean strict) {
             this.context = context;
             this.strict = strict;
         }

         @Override
        public Exp visit(Num num) {
            queue.addLast(num);
            return null;
        }

        @Override
        public Exp visit(Sum sum) {
            sum.left.accept(this);
            sum.right.accept(this);
            Exp e1 = queue.pollLast();
            Exp e2 = queue.pollLast();
            if(e1 instanceof Num && e2 instanceof Num) {
                Num n1 = (Num) e1;
                Num n2 = (Num) e2;
                queue.addLast(new Num(n1.number.doubleValue() + n2.number.doubleValue()));
            } else {
                queue.push(new Sum(e1, e2));
            }
            return null;
        }

        @Override
        public Exp visit(Mul mul) {
            mul.left.accept(this);
            mul.right.accept(this);
            Exp e1 = queue.pollLast();
            Exp e2 = queue.pollLast();
            if(e1 instanceof Num && e2 instanceof Num) {
                Num n1 = (Num) e1;
                Num n2 = (Num) e2;
                queue.addLast(new Num(n1.number.doubleValue() * n2.number.doubleValue()));
            } else {
                queue.push(new Mul(e1, e2));
            }
            return null;
        }

        @Override
        public Exp visit(Dev dev) {
            dev.left.accept(this);
            dev.right.accept(this);
            Exp e1 = queue.pollLast();
            Exp e2 = queue.pollLast();
            if(e1 instanceof Num && e2 instanceof Num) {
                Num n1 = (Num) e1;
                Num n2 = (Num) e2;
                queue.addLast(new Num(n2.number.doubleValue() / n1.number.doubleValue()));
            } else {
                queue.push(new Dev(e1, e2));
            }
            return null;
        }

        @Override
        public Exp visit(Var var) {
            Exp exp = context.get(var);
            if(exp != null) {
                exp.accept(this);
            } else {
                if(strict) throw new RuntimeException("dddd");
                queue.addLast(var);
            }
            return null;
        }

         @Override
         public void visit(final Assign assign) {
            assign.left.accept(new ExpVisitor() {
                @Override
                public Exp visit(Num num) {
                    return null;
                }

                @Override
                public Exp visit(Sum sum) {
                    return null;
                }

                @Override
                public Exp visit(Mul mul) {
                    return null;
                }

                @Override
                public Exp visit(Dev dev) {
                    return null;
                }

                @Override
                public Exp visit(Var var) {
                    context.put(var, assign.right);
                    return assign.right;
                }

                @Override
                public void visit(Assign assign) {

                }
            });
         }

         public Exp getResult() {
            return queue.getLast();
        }
    }

     static class PrettyPrinter implements ExpVisitor {
        private StringBuffer sb = new StringBuffer();

        public Exp visit(Num num) {
            sb.append(num.number);
            return null;
        }

        public Exp visit(Sum sum) {
            sb.append("(");
            sum.left.accept(this);
            sb.append(" + ");
            sum.right.accept(this);
            sb.append(")");
            return null;
        }

        public Exp visit(Mul mul) {
            mul.left.accept(this);
            sb.append(" * ");
            mul.right.accept(this);
            return null;
        }

        public Exp visit(Dev dev) {
            dev.left.accept(this);
            sb.append(" / ");
            dev.right.accept(this);
            return null;
        }

        @Override
        public Exp visit(Var var) {
            sb.append(var.c);
            return null;
        }

         @Override
         public void visit(Assign assign) {
             assign.left.accept(this);
             sb.append(" = ");
             assign.right.accept(this);
         }

         public String getResult() {
          return sb.toString();
        }
    }
}
