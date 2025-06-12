package lexa;

import java.util.HashMap;
import java.util.Map;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
    private boolean debugMode = false;
    private final Map<String, Object> environment = new HashMap<>();

    public void interpret(Stmt statement, boolean debug) {
        this.debugMode = debug;
        try {
            execute(statement);
        } catch (RuntimeException e) {
            Debug.error("DEBUG | ERROR | Runtime error: " + e.getMessage()); //Only prints when debug is enabled in Debug.java
            System.err.println("[ERROR] Runtime error: " + e.getMessage());
        } finally {
            this.debugMode = false;
        }
    }

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    // Statement visitor
    @Override
    public Void visitSayStmt(Stmt.Say stmt) {
        Object value = evaluate(stmt.expression);
        if (debugMode) {
            Debug.trace("DEBUG | Evaluated Value: " + value); //Only prints when debug is enabled in Debug.java
            System.out.println("[DEBUG] Evaluated Value: " + value);
        }
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Void visitLetStmt(Stmt.Let stmt) {
        Object value = evaluate(stmt.initialiser);
        environment.put(stmt.name.lexeme, value);
        if (debugMode) {
            Debug.trace("DEBUG | Variable Declaration: " + stmt.name.lexeme + " = " + value); //Only prints when debug is enabled in Debug.java
            System.out.println("[DEBUG] Variable Declaration: " + stmt.name.lexeme + " = " + value);
        }
        return null;
    }

    @Override
    public Void visitHelpStmt(Stmt.Help stmt) {
        System.out.println(
                "Lexa Syntax Help:\n" +
                        "  say <expr>;            // Print a value\n" +
                        "  let <id> = <expr>;     // Declare a variable\n" +
                        "  <id> = <expr>;         // Assign to a variable\n" +
                        "  Strings: \"hello\"\n" +
                        "  Comments: // text\n" +
                        "  Debug: Add ##debug=true at line end\n" +
                        "  Syntax Example: Say \"Hello, \" + name + \"!\";\n"
        );
        return null;
    }

    @Override
    public Object visitListLiteralExpr(Expr.ListLiteral expr) {
        java.util.List<Object> values = new java.util.ArrayList<>();
        for (Expr element : expr.elements) {
            values.add(evaluate(element));
        }
        return values; // Return the list of evaluated elements
    }

    @Override
    public Object visitIndexExpr(Expr.Index expr) {
        Object list = evaluate(expr.list);
        Object idx = evaluate(expr.index);
        if (!(list instanceof java.util.List)) throw new RuntimeException("Target is not a list.");
        if (!(idx instanceof Integer)) throw new RuntimeException("Index is not an integer.");
        return ((java.util.List<?>)list).get((Integer)idx);
    }

    // Control flow visitors //
    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        Object condition = evaluate(stmt.condition);
        if (isTruthy(condition)) {
            execute(stmt.thenBranch);
        } else if (stmt.thenBranch != null) {
            execute(stmt.elseBranch);
        }
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        while (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.body);
        }
        return null;
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        for (Stmt s : stmt.statements) {
            execute(s);
        }
        return null;
    }

    // Expression visitors //
    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);
        switch (expr.operator.type) {
            case PLUS:
                if (left instanceof String || right instanceof String) {
                    return stringify(left) + stringify(right);
                }
                return ((int) left) + ((int) right);
            case MINUS:
                return ((int) left) - ((int) right);
            case STAR:
                return ((int) left) * ((int) right);
            case SLASH:
                if ((int) right == 0) throw new RuntimeException("Division by zero.");
                return ((int) left) / ((int) right);
            default:
                throw new RuntimeException("Unknown operator: " + expr.operator.lexeme);
        }
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        if (!environment.containsKey(expr.name.lexeme)) {
            throw new RuntimeException("Undefined variable: " + expr.name.lexeme);
        }
        return environment.get(expr.name.lexeme);
    }

    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr.value);
        if (!environment.containsKey(expr.name.lexeme)) {
            throw new RuntimeException("Undefined variable: " + expr.name.lexeme);
        }
        environment.put(expr.name.lexeme, value);
        if (debugMode) {
            Debug.trace("DEBUG | Variable Assignment: " + expr.name.lexeme + " = " + value); //Only prints when debug is enabled in Debug.java
            System.out.println("[DEBUG] Variable Assignment: " + expr.name.lexeme + " = " + value);
        }
        return value;
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    private String stringify(Object obj) {
        if (obj == null) return "nil";
        return obj.toString();
    }

    private boolean isTruthy(Object obj) {
        if (obj == null) return false;
        if (obj instanceof Boolean) return (Boolean) obj;
        if (obj instanceof Integer) return ((Integer) obj) != 0; //Non-zero integers are truthy
        return true; // All other objects are truthy
    }
}
