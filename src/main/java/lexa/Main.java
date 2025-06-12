package lexa;

import java.util.List;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        String versionNum = "0.19.5"; //REPL version number
        Interpreter interpreter = new Interpreter();
        java.util.Scanner inputScanner = new java.util.Scanner(System.in);

        Debug.trace("DEBUG | Main | Starting REPL");
        System.out.println(" ");
        System.out.println("==={ Lexa REPL " + versionNum + " }===");
        System.out.println("Lexa REPL. Type 'help' to see manual. Type 'exit' to quit.");

        while (true) {
            System.out.print("> ");
            String line = inputScanner.nextLine();
            if (line.trim().equalsIgnoreCase("exit")) break;
            if (line.trim().isEmpty()) continue;

            //Flag Parsing
            boolean debug = false;
            String code = line;
            int flagIdx = line.indexOf("##");
            if (flagIdx != -1) {
                code = line.substring(0, flagIdx).trim();
                String flagsPart = line.substring(flagIdx + 2).trim();
                String[] flags = flagsPart.split("\\s+");
                for (String flag : flags) {
                    if (flag.equalsIgnoreCase("debug=true")) debug = true;
                }
            }

            //Splitting input into statements using ',' as a delimiter
            List<String> statements = splitStatements(code);

            for (String stmtCode : statements) {
                stmtCode = stmtCode.trim();
                if (!stmtCode.endsWith(";")) {
                    stmtCode = stmtCode + ";";
                }

                //Lexical analysis
                lexa.Scanner scanner = new lexa.Scanner(stmtCode);
                List<Token> tokens = scanner.scanTokens();

                //Optional token dump
                if (debug) {
                    System.out.println("[DEBUG] Statements: " + stmtCode);
                    System.out.println("[DEBUG] Tokens: " + tokens);
                }

                //Parsing
                Parser parser = new Parser(tokens);
                Stmt stmt;
                try {
                    stmt = parser.parse();
                } catch (Exception e) {
                    Debug.error("DEBUG | Main | Parse Error: " + e.getMessage()); //Only prints when debug is enabled in Debug.java
                    System.err.println("Parse Error: " + e.getMessage());
                    continue;
                }

                //Optional AST dump
                if (debug) {
                    System.out.println("[DEBUG] AST: " + stmt);
                }

                //Interpretation
                try {
                    interpreter.interpret(stmt, debug);
                } catch (Exception e) {
                    Debug.error("DEBUG | Main | Runtime Error: " + e.getMessage()); //Only prints when debug is enabled in Debug.java
                    System.err.println("Runtime Error: " + e.getMessage());
                }
            }
        }
        System.out.println("Lexa REPL v0.1 closing...");
    }

    public static List<String> splitStatements(String code) {
        List<String> stmts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inString = false;
        for (int i = 0; i < code.length(); i++) {
            char c = code.charAt(i);
            if (c == '"') {
                inString = !inString;
                current.append(c);
            } else if (!inString && c == ',') {
                if (!current.toString().trim().isEmpty())
                    stmts.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        if (!current.toString().trim().isEmpty())
            stmts.add(current.toString().trim());
        return stmts;
    }
}
