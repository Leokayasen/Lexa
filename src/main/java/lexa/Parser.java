package lexa;

import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Stmt parse() {
        if (match(TokenType.SAY)) {
            Expr value = expression();
            consume(TokenType.SEMICOLON, "Expect ';' after value.");
            return new Stmt.Say(value);

        } else if (match(TokenType.LET)) {
            Token name = consume(TokenType.IDENTIFIER, "Expect variable name.");
            consume(TokenType.EQUAL, "Expect '=' after variable name.");
            Expr initialiser = expression();
            consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.");
            return new Stmt.Let(name, initialiser);

        } else if (match(TokenType.HELP)) {
            consume(TokenType.SEMICOLON, "Expect ';' after help.");
            return new Stmt.Help();

        } else if (match(TokenType.IDENTIFIER)) {
            Token name = previous();
            if (match(TokenType.EQUAL)) {
                Expr value = expression();
                consume(TokenType.SEMICOLON, "Expect ';' after assignment.");
                return new Stmt.Expression( new Expr.Assign(name, value) );
            } else {
                throw error(peek(), "Expect '=' after identifier.");
            }
        } else {
            throw error(peek(), "Expect statement.");
        }
    }

    private Expr expression() {
        return addition();
    }

    private Expr addition() {
        Expr expr = multiplication();
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = previous();
            Expr right = multiplication();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr multiplication() {
        Expr expr = primary();
        while (match(TokenType.STAR, TokenType.SLASH)) {
            Token operator = previous();
            Expr right = primary();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr primary() {
        if (match(TokenType.NUMBER)) {
            return new Expr.Literal(previous().literal);
        }
        if (match(TokenType.STRING)) {
            return new Expr.Literal(previous().literal);
        }
        if (match(TokenType.IDENTIFIER)) {
            return new Expr.Variable(previous());
        }
        throw error(peek(), "Expect expression.");
    }

    // --- Utility Methods below --- \\

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw error(peek(), message);
    }

    private ParseError error(Token token, String message) {
        System.err.println("[line " + token.line + "] Error at '" +
                token.lexeme + "': " + message);
        return new ParseError();
    }

    private static class ParseError extends RuntimeException {}
}
