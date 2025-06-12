package lexa;

public abstract class Expr {
    public interface Visitor<R> {
        R visitLiteralExpr(Literal expr); //For literal values
        R visitBinaryExpr(Binary expr); //For binary operations
        R visitVariableExpr(Variable expr); //For variable access
        R visitAssignExpr(Assign expr); //For variable assignment
        R visitListLiteralExpr(ListLiteral expr); //For list literals
        R visitIndexExpr(Index expr); //For indexing into lists
    }

    public abstract <R> R accept(Visitor<R> visitor);

    public static class Literal extends Expr {
        public final Object value;
        public Literal(Object value) { this.value = value; }
        @Override
        public <R> R accept(Visitor<R> visitor) { return visitor.visitLiteralExpr(this); }
    }

    public static class Binary extends Expr {
        public final Expr left;
        public final Token operator;
        public final Expr right;
        public Binary(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;

            // Only prints when debug is enabled in Debug.java
            Debug.trace("DEBUG | Expr | Creating binary expression: " + left + " " + operator.lexeme + " " + right);
        }
        @Override
        public <R> R accept(Visitor<R> visitor) { return visitor.visitBinaryExpr(this); }
    }

    public static class Variable extends Expr {
        public final Token name;
        public Variable(Token name) {
            this.name = name;

            // Only prints when debug is enabled in Debug.java
            Debug.trace("DEBUG | Expr | Accessing variable: " + name.lexeme);
        }
        @Override
        public <R> R accept(Visitor<R> visitor) { return visitor.visitVariableExpr(this); }
    }

    public static class Assign extends Expr {
        public final Token name;
        public final Expr value;
        public Assign(Token name, Expr value) {
            this.name = name;
            this.value = value;

            // Only prints when debug is enabled in Debug.java
            Debug.trace("DEBUG | Expr | Assigning value to variable: " + name.lexeme + " = " + value);
        }
        @Override
        public <R> R accept(Visitor<R> visitor) { return visitor.visitAssignExpr(this); }
    }

    public static class ListLiteral extends Expr {
        public final java.util.List<Expr> elements;
        public ListLiteral(java.util.List<Expr> elements) { this.elements = elements; }
        @Override
        public <R> R accept(Visitor<R> visitor) { return visitor.visitListLiteralExpr(this); }
    }

    public static class Index extends Expr {
        public final Expr list;
        public final Expr index;
        public Index(Expr list, Expr index) {
            this.list = list;
            this.index = index;
        }
        @Override
        public <R> R accept(Visitor<R> visitor) { return visitor.visitIndexExpr(this); }
    }
}
