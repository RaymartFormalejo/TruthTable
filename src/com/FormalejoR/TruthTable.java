package com.FormalejoR;

import java.util.*;

public class TruthTable {

    private static final String VALID_SYMBOLS = "^[\\(\\)~&v≡:>⊃(A-Z)]+$";

    public static void main(String[] args) {
        prompt();
    }
    public static void prompt() {
        Scanner stdin = new Scanner(System.in);
        char menuNumber = '1';
        menu();
        while (true) {
            System.out.print("[" + menuNumber + "] > ");
            String expr = stdin.next();
            if (expr.equalsIgnoreCase("3")) {
                break;
            } else if (expr.equalsIgnoreCase("1")) {
                printHelp(menuNumber);
            } else if (expr.equalsIgnoreCase("2")) {
                menuNumber = '2';
                printHelp(menuNumber);
            }  else {
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

            Map<Character, Boolean> proportionMap = new LinkedHashMap<>();
            for (Character c: expr.toCharArray()) {
                if (((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) && c != 'v') {
                    proportionMap.put(c, true);
                }
            }

            LogicNode rootNode;
            try {
                rootNode = buildTree(expr);
                if (rootNode == null) {
                    return "Error: invalid expression. Please try again.";
                }
            } catch (Exception e) {
                return "Error: " + e.getMessage();
            }

            String[] args = Arrays.copyOf(String.valueOf(proportionMap.keySet()).split(""), proportionMap.size()+1);
            args[proportionMap.size()] = expr;

            String result = "";
            final int[] dividers = new int[]{proportionMap.size()-1};
            result += getLine('╔', '╗', '═', '╦', args, dividers);
            result += "║";
            for (Character c: proportionMap.keySet()) {
                result += " " + c + " ║";
            }
            result += "║ " + expr + " ║\n";
            result += getLine('║','║','═','╬', args, dividers);
            for (int i=0; i<Math.pow(2,proportionMap.size()); i++) {
                int k = proportionMap.size()-1;
                result += "║";
                for (Character key: proportionMap.keySet()) {
                    proportionMap.put(key, (i & (1 << k)) == 0);
                    result += " " + (proportionMap.get(key) ? "T" : "F") + " ║";
                    k--;
                }
                result += "║";
                if (expr.length() > 1) {
                    result += String.format(" %" + (expr.length()+1)/2 + "s%" + (expr.length()/2) + "s ║", (rootNode.evaluate(proportionMap) ? "T" : "F"), "") + "\n";
                } else {
                    result += String.format(" %s ║", (rootNode.evaluate(proportionMap) ? "T" : "F")) + "\n";
                }
                if (i<Math.pow(2,proportionMap.size())-1) { //Middle loops
                    result += getLine('║','║','═','╬', args, dividers);
                } else { //Last loop
                    result += getLine('╚','╝','═','╩', args, dividers);
                }
            }
            return result;
        } else {
            return "Command/expression not recognized. Please check the syntax or use $h for help.";
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
                System.out.println("\n\tEnter valid expression, for example 'Av(B&C)':");
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
                        "\tbiconditional, ⊃ or > for conditional, and ~ for\n" +
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
                    throw new IllegalStateException("Couldn't parse expression fully. Remaining part: " + raw + ". Please check your syntax.");
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
                throw new IllegalStateException("Unable to correctly parse expression. Please check the syntax.");
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
            } else { //Otherwise, this is a proposition
                if (!propMap.containsKey(this.val)) {
                    throw new IllegalArgumentException("Proposition " + this.val + " is not defined");
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
                    throw new UnsupportedOperationException("Logical operator " + this.val + " is not supported");
            }
        }
    }
}