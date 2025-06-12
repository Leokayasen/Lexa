package lexa;

public enum TokenType {
    //Single Character Tokens
    LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR, EQUAL,

    //Data Structures
    ARRAY, LIST, STACK,
    LEFT_PAREN, RIGHT_PAREN, RIGHT_BRACKET, LEFT_BRACKET,


    //Literals
    IDENTIFIER, STRING, NUMBER,

    //Keywords
    HELP, SAY, LET, IF, ELSE, WHILE, FOR, FN, RETURN,

    //Comments
    COMMENT, COMMENT_BLOCK,

    //EoF
    EOF
}
