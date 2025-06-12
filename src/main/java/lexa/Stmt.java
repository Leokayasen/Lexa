package lexa;

public abstract class Stmt {
    public interface Visitor<R> {
        R visitSayStmt(Say stmt); //For 'say' statements
        R visitLetStmt(Let stmt); //For 'let' statements
        R visitExpressionStmt(Expression stmt); //For expression statements

        //Help command
        R visitHelpStmt(Help stmt); //For 'help' statement

        //Control Flow
        R visitIfStmt(If stmt); //For 'if' statements
        R visitWhileStmt(While stmt); //For 'while' statements
        R visitBlockStmt(Block stmt); //For block statements
    }

    public abstract <R> R accept(Visitor<R> visitor);

    public static class Say extends Stmt {
        public final Expr expression;
        public Say(Expr expression) { this.expression = expression; }
        @Override
        public <R> R accept(Visitor<R> visitor) { return visitor.visitSayStmt(this); }
    }

    public static class Let extends Stmt {
        public final Token name;
        public final Expr initialiser;
        public Let(Token name, Expr initialiser) {
            this.name = name;
            this.initialiser = initialiser;
        }
        @Override
        public <R> R accept(Visitor<R> visitor) { return visitor.visitLetStmt(this); }
    }

    public static class Expression extends Stmt {
        public final Expr expression;
        public Expression(Expr expression) { this.expression = expression; }
        @Override
        public <R> R accept(Visitor<R> visitor) { return visitor.visitExpressionStmt(this); }
    }

    public static class Help extends Stmt {
        @Override
        public <R> R accept(Visitor<R> visitor) { return visitor.visitHelpStmt(this); }
    }

    public static class If extends Stmt {
        public final Expr condition;
        public final Stmt thenBranch;
        public final Stmt elseBranch;

        public If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) { return visitor.visitIfStmt(this); }
    }

    public static class While extends Stmt {
        public final Expr condition;
        public final Stmt body;

        public While(Expr condition, Stmt body) {
            this.condition = condition;
            this.body = body;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) { return visitor.visitWhileStmt(this); }
    }

    public static class Block extends Stmt {
        public final java.util.List<Stmt> statements;
        public Block(java.util.List<Stmt> statements) { this.statements = statements; }
        @Override
        public <R> R accept(Visitor<R> visitor) { return visitor.visitBlockStmt(this); }
    }
}
