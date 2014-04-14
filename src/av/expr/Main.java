package av.expr;

import java.util.Iterator;

public class Main {
    public static interface ExpVisitor {
        Exp visit(Num num);

        Exp visit(Sum sum);

        Exp visit(Mul mul);

        Exp visit(Dev dev);
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
        Exp exp = new Sum(
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
        ExpVisitor prettyPrinter = new ExpVisitor() {
            public Exp visit(Num num) {
                System.out.print(num.number);
                return null;
            }

            public Exp visit(Sum sum) {
                System.out.print("(");
                sum.left.accept(this);
                System.out.print(" + ");
                sum.right.accept(this);
                System.out.print(")");
                return null;
            }

            public Exp visit(Mul mul) {
                mul.left.accept(this);
                System.out.print(" * ");
                mul.right.accept(this);
                return null;
            }

            public Exp visit(Dev dev) {
                dev.left.accept(this);
                System.out.print(" / ");
                dev.right.accept(this);
                return null;
            }
        };
        exp.traverse(new ExpVisitor() {
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
                dev.right.accept(new ExpVisitor() {
                    @Override
                    public Exp visit(Num num) {
                        if (num.number.intValue() == 0) {
                            throw new RuntimeException("StaticCheck failed: Devide by zero");
                        }
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
                });
                return null;
            }
        });
        Iterator<Exp> i = exp.iterator();
        while (i.hasNext()) {
            Exp e = i.next();
            System.out.print("FROM " + e + ": ");
            e.accept(prettyPrinter);
            System.out.println();
        }
    }
}
