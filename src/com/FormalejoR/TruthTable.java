package com.FormalejoR;

import java.util.*;

public class TruthTable {

    private static final String VALID_SYMBOLS = "^[\\(\\)~&v≡:>⊃(A-Z)]+$";
    public static final String RED = "\u001B[31m";
    public static final String YELLOW = "\u001B[33m";
    public static final String RESET = "\033[0m";
    public static final String CYAN = "\033[0;36m";
    public static final String GREEN = "\033[0;32m";

    public static void main(String[] args) {
        prompt();
    }
    public static void prompt() {
        Scanner stdin = new Scanner(System.in);
        char menuNumber = '1';
        menu();
        while (true) {
            System.out.print(YELLOW + "[" + menuNumber + "] " + RESET);
            String expr = stdin.next();
            if (expr.equalsIgnoreCase("3")) {
                break;
            } else if (expr.equalsIgnoreCase("1")) {
                printHelp(menuNumber);
            } else if (expr.equalsIgnoreCase("2")) {
                menuNumber = '2';
                printHelp(menuNumber);
            }   else {
                switch (menuNumber) {
                    case '2':
                        System.out.println(truthTable(expr));
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public static String truthTable(String expr) {
        if (expr.matches(VALID_SYMBOLS)) {
            Map<Character, Boolean> propMap = new LinkedHashMap<>();
            for (Character c : expr.toCharArray()) {
                if (((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) && c != 'v') {
                    propMap.put(c, true);
                }
            }

            LogicNode rootNode;
            try {
                rootNode = buildTree(expr);
                if (rootNode == null) {
                    return RED + "Error: invalid expression. Please try again." + RESET;
                }
            } catch (Exception e) {
                return RED + "Error: " + e.getMessage() + RESET;
            }

            String[] args = Arrays.copyOf(String.valueOf(propMap.keySet()).split(""), propMap.size() + 1);
            args[propMap.size()] = expr;

            String res = "";
            final int[] dividers = new int[]{propMap.size() - 1}; //Dividers for table
            res += getLine('┍', '┑', '─', '┬', args, dividers);
            res += "│";
            for (Character c : propMap.keySet()) {
                res += " " + c + " │";
            }
            res += "│ " + expr + " │\n";
            res += getLine('│', '│', '─', '┼', args, dividers);
            for (int i = 0; i < Math.pow(2, propMap.size()); i++) {
                int k = propMap.size() - 1;
                res += "│";
                for (Character key : propMap.keySet()) {
                    propMap.put(key, (i & (1 << k)) == 0);
                    res += " " + (propMap.get(key) ? "T" : "F") + " │";
                    k--;
                }
                res += "│";
                if (expr.length() > 1) {
                    res += String.format(" %" + (expr.length() + 1) / 2 + "s%" + (expr.length() / 2) + "s │", (rootNode.evaluate(propMap) ? "T" : "F"), "") + "\n";
                } else {
                    res += String.format(" %s │", (rootNode.evaluate(propMap) ? "T" : "F")) + "\n";
                }
                if (i < Math.pow(2, propMap.size()) - 1) { //Middle loops
                    res += getLine('│', '│', '─', '┼', args, dividers);
                } else { //Last loop
                    res += getLine('┕', '┙', '─', '┴', args, dividers);
                }
            }
            return res;
        } else {
            return RED + "Command/expression not recognized. Please check the syntax or use $h for help." + RESET;
        }
    }
    public static String getLine(char start, char end, char filler, char divider, String[] props, int[] specialDivider) {
        String result = "" + start;
        int j = 0;
        for (String prop: props) {
            result += filler;
            for (int i=0; i<prop.length(); i++) {
                result += filler;
            }
            result += filler + "" + divider;
            for (int dividerVal: specialDivider) {
                if (dividerVal == j) {
                    result += divider;
                }
            }
            j++;
        }
        result = result.substring(0,result.length()-1);
        result += end + "\n";
        return result;
    }
    public static void printHelp(char mode) {
        switch (mode) {
            case '2':
                System.out.println("──────────────────────────────────────────────────────\n" +
                        "\t\t\t\t\tTruth Table \n" +
                        "──────────────────────────────────────────────────────\n" +
                        "\tDisplay truth table of expression entered.\n" +
                        "\tEnter expressions only containing proposition \n" +
                        "\tletters and valid symbols, for example 'Pv(Q&R)'");
                break;
            default:
                System.out.println(
                        "══════════════════════════════════════════════════════\n" +
                                "\t\t\t\t\tINSTRUCTIONS:\n\n" +
                                "\tEnter 1 for help\n" +
                                "\tEnter 2 for truth table\n" +
                                "\tEnter 3 to exit program\n\n" +
                                "\t\tExpressions must only contain defined \n" +
                                "\tpropositions and valid logical symbols. Valid \n" +
                                "\tlogical symbols are limited to ( and ) for \n" +
                                "\tgrouping, v for OR, & for AND, ≡ or : for \n" +
                                "\tbi-conditional, ⊃ or > for conditional, and ~ for\n" +
                                "\tNOT. Grouping symbols ( and ) must be used so that\n" +
                                "\teach operator (excluding ~) has no more and no less\n" +
                                "\tthan two operands (for example, (P&Q)&R is valid,\n" +
                                "\tbut P&Q&R is not). Negation ~ may be used before\n" +
                                "\tgroups or propositions, but never before another \n" +
                                "\toperator (for example, ~P&Q and ~(PvQ)&R are valid,\n" +
                                "\tbut P~&Q is not) formulas.\n\n" +
                                "══════════════════════════════════════════════════════\n"

                );
        }
    }
    public static void menu() {
        System.out.println(
                "╔════════════════════════════════════════════════════╗\n" +
                        "║\t\t\t\tTRUTH TABLE GENERATOR\t\t\t\t ║\n" +
                        "╚════════════════════════════════════════════════════╝\n" +
                        "\tThis tool generates truth table for proportional\n" +
                        "\t\t\t\t\t\tlogic.\n\n"+
                        "\tMENU\n" +
                        "[1] Instructions (default)\n" +
                        "[2] Truth table\n" +
                        "[3] Quit\n"


        );
    }
    public static LogicNode buildTree(String raw) {
        if (raw.length() > 0) {

            LogicNode left = new LogicNode();
            raw = getNode(raw, left);

            if (raw.length() > 0) {

                char op = raw.charAt(0);
                raw = raw.substring(1);


                LogicNode right = new LogicNode();
                raw = getNode(raw, right);

                if (raw.length() != 0) {
                    throw new IllegalStateException(RED + "Couldn't parse expression fully. Remaining part: " + raw + ". Please check your syntax." + RESET);
                }
                return new LogicNode(op, left, right, NodeType.OPERATOR, false);
            } else {
                return left;
            }
        } else {
            return null;
        }
    }
    public static String getNode(String raw, LogicNode newNode) {
        boolean inverted = false;
        if (raw.charAt(0) == '~') {
            inverted = true;
            raw = raw.substring(1);
        }
        if (raw.charAt(0) == '(') {
            LogicNode temp = buildTree(raw.substring(1, getGroupEnd(raw)));
            if (temp == null) {
                throw new IllegalStateException(RED + "Unable to correctly parse expression. Please check the syntax." + RESET);
            }
            newNode.copyFrom(temp);
            newNode.inverted = inverted != newNode.inverted;
            raw = raw.substring(getGroupEnd(raw)+1);
        } else {
            newNode.initialize(raw.charAt(0), null, null, NodeType.PROPOSITION, inverted);
            raw = raw.substring(1);
        }
        return raw;
    }
    public static int getGroupEnd(String raw) {
        Stack<Character> groups = new Stack<>();
        int i = 0;
        do {
            if (raw.charAt(i) == '(') {
                groups.push('(');
            } else if (raw.charAt(i) == ')') {
                groups.pop();
            }
            i++;
        } while (groups.size() > 0);
        return i-1;
    }

    public enum NodeType {
        OPERATOR,
        PROPOSITION
    }

    static class LogicNode {
        LogicNode left;
        LogicNode right;
        Character val;
        NodeType type;
        boolean inverted;

        public LogicNode(Character val, LogicNode left, LogicNode right, NodeType type, boolean inverted) {
            initialize(val, left, right, type, inverted);
        }

        public LogicNode() {
        }

        public void initialize(Character val, LogicNode left, LogicNode right, NodeType type, boolean inverted) {
            this.left = left;
            this.right = right;
            this.val = val;
            this.type = type;
            this.inverted = inverted;
        }

        public void copyFrom(LogicNode node) {
            this.left = node.left;
            this.right = node.right;
            this.val = node.val;
            this.type = node.type;
            this.inverted = node.inverted;
        }

        public boolean evaluate(Map<Character, Boolean> propMap) {
            if (this.type == NodeType.OPERATOR) {
                return this.inverted != this.runOperator(propMap);
            } else {
                if (!propMap.containsKey(this.val)) {
                    throw new IllegalArgumentException(RED + "Proposition " + this.val + " is not defined" + RESET);
                }
                return this.inverted != propMap.get(this.val);
            }
        }

        private boolean runOperator(Map<Character, Boolean> propMap) {
            boolean propA = this.left.evaluate(propMap);
            boolean propB = this.right.evaluate(propMap);
            switch (this.val) {
                case 'v':
                    return propA || propB;
                case '&':
                    return propA && propB;
                case '>':
                case '⊃':
                    return !propA || propB;
                case ':':
                case '≡':
                    return propA == propB;
                default:
                    throw new UnsupportedOperationException(RED + "Logical operator " + this.val + " is not supported" + RESET);
            }
        }
    }
}