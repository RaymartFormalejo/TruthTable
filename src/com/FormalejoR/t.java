package com.FormalejoR;

import java.util.*;

public class t {

    private static final String VALID_SYMBOL_REGEX = "^[\\(\\)~&v≡⊃(A-Z)]+$";
    public static final String RED = "\u001B[31m";
    public static final String YELLOW = "\u001B[33m";
    public static final String RESET = "\033[0m";
    public static final String CYAN = "\033[0;36m";
    public static final String GREEN = "\033[0;32m";

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
                    throw new IllegalArgumentException(RED+"Proposition " + this.val + " is not defined" + RESET);
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
                    return !propA || propB;
                case '≡':
                    return propA == propB;
                default:
                    throw new UnsupportedOperationException(RED + "Logical operator " + this.val + " is not supported" + RESET);
            }
        }
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            arguments(args);
        } else {
            prompt();
        }
    }

    public static void arguments(String[] args) {
        char collecting = '0';
        ArrayList<String> premises = new ArrayList<>();
        String conclusion = null;
        String expression = null;
        for (String arg : args) {
            if (arg.charAt(0) == '-' && arg.length() > 2) {
                switch (arg.charAt(1)) {
                    case 'I':
                        menu();
                        break;
                    case 'p':
                        collecting = 'p';
                        break;
                    case 'c':
                        collecting = 'c';
                        break;
                    case 'e':
                        collecting = 'e';
                        break;
                    default:
                        throw new IllegalArgumentException(RED + "Invalid parameter: '" + arg + "'" + RESET);
                }
            } else {
                switch (collecting) {
                    case 'p':
                        premises.addAll(Arrays.asList(arg.split(",")));
                        break;
                    case 'c':
                        conclusion = arg;
                        break;
                    case 'e':
                        expression = arg;
                        break;
                    default:
                        throw new IllegalArgumentException(RED + "Please specify a parameter first: '" + arg + "'" + RESET);
                }
            }
        }
        if (premises.size() > 0 && conclusion != null) {
            System.out.println(evaluateArgument(premises, conclusion));
        } else if (expression != null) {
            System.out.println(evaluateExpression(expression));
        }
    }

    public static String evaluateArgument(ArrayList<String> premises, String conclusion) {
        String res = "";
        return res;
    }

    public static String evaluateExpression(String expression) {
        String res = "";


        return res;
    }

    public static void prompt() {
        Scanner stdin = new Scanner(System.in);
        Map<Character, Boolean> propMap = new HashMap<>();
        char mode = 'I';
        menu();
        instructions(mode);
        while (true) {
            System.out.print(YELLOW + "[" + mode + "] " + RESET);
            String expr = stdin.next();
            if (expr.equalsIgnoreCase("Q")) {
                break;
            } else if (expr.equalsIgnoreCase("I")) {
                instructions(mode);
                instructions('\0');
            } else if (expr.equalsIgnoreCase("V")) {
                mode = 'V';
                instructions(mode);
                propMap = new HashMap<>();
            } else if (expr.equalsIgnoreCase("T")) {
                mode = 'T';
                instructions(mode);
            } else if (expr.equalsIgnoreCase("A")) {
                mode = 'A';
                instructions(mode);
            } else {
                switch (mode) {
                    case 'A':
                        System.out.println(argumentsValidity(expr));
                        break;
                    case 'T':
                        System.out.println(truthTable(expr));
                        break;
                    case 'V':
                        System.out.println(truthValue(expr, propMap));
                        break;
                }
            }
        }
    }

    public static String truthTable(String expr) {
        if (expr.matches(VALID_SYMBOL_REGEX)) {

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

            String res = "\t";
            final int[] dividers = new int[]{propMap.size() - 1};
            res += getLine('┍', '┑', '─', '┬', args, dividers);
            res += "\t│";
            for (Character c : propMap.keySet()) {
                res += " " + c + " │";
            }
            res += "│ " + expr + " │\n";
            res += "\t"+getLine('│', '│', '─', '┼', args, dividers);
            for (int i = 0; i < Math.pow(2, propMap.size()); i++) {
                int k = propMap.size() - 1;
                res += "\t│";
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
                    res += "\t"+getLine('│', '│', '─', '┼', args, dividers);
                } else { //Last loop
                    res += "\t"+getLine('┕', '┙', '─', '┴', args, dividers);
                }
            }
            return res;
        } else {
            return RED + "Invalid input. Please check the syntax or enter 'I' for instructions." + RESET;
        }
    }

    public static String argumentsValidity(String expr) {
        if (expr.matches("^[\\(\\)\\~\\&v≡>(A-Z),]+$")) {
            String[] argument = expr.split(",");
            Map<Character, Boolean> propMap = new LinkedHashMap<>();
            for (Character c : expr.toCharArray()) {
                if (((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) && c != 'v') {
                    propMap.put(c, true);
                }
            }
            LogicNode[] rootNodes = new LogicNode[argument.length];
            for (int i = 0; i < argument.length; i++) {
                try {
                    rootNodes[i] = buildTree(argument[i]);
                    if (rootNodes[i] == null) {
                        return RED + "Error: invalid expression: " + argument[i] + ". Please try again." + RESET;
                    }
                } catch (Exception e) {
                    return RED + "Error: (at expression: " + argument[i] + ")" + e.getMessage() + RESET;
                }
            }
            boolean[] argumentResults = new boolean[(int) Math.pow(2, propMap.size())];
            String[] args = Arrays.copyOf(String.valueOf(propMap.keySet()).split(""), propMap.size() + argument.length);
            for (int i = 0; i < argument.length; i++) {
                args[i + propMap.size()] = argument[i];
            }

            String res = "\t";
            final int[] dividers = new int[]{propMap.size() - 1, propMap.size() + argument.length - 2};
            res += getLine('┍', '┑', '─', '┬', args, dividers);
            res += "\t│";
            for (Character c : propMap.keySet()) {
                res += " " + c + " │";
            }
            res += "│";
            for (int i = 0; i < argument.length; i++) {
                res += " " + argument[i];
                if (i == argument.length - 2) {
                    res += " ││";
                } else {
                    res += " │";
                }
            }
            res += "\n";
            res += "\t"+getLine('│', '│', '─', '┼', args, dividers);
            for (int i = 0; i < Math.pow(2, propMap.size()); i++) {
                int k = propMap.size() - 1;
                res += "\t|";
                for (Character key : propMap.keySet()) {
                    propMap.put(key, (i & (1 << k)) == 0);
                    res += " " + (propMap.get(key) ? "T" : "F") + " │";
                    k--;
                }
                argumentResults[i] = true;
                for (int j = 0; j < argument.length; j++) {
                    boolean argumentVal = rootNodes[j].evaluate(propMap);
                    if (j == 0 || j == argument.length - 1) {
                        res += "│";
                    }
                    if (j == argument.length - 1) {
                        argumentResults[i] = !argumentResults[i] || argumentVal;
                    } else {
                        argumentResults[i] &= argumentVal;
                    }
                    if (argument[j].length() > 1) {
                        res += String.format(" %" + (argument[j].length() + 1) / 2 + "s%" + (argument[j].length() / 2) + "s │", (argumentVal ? "T" : "F"), "");
                    } else {
                        res += String.format(" %s │", (argumentVal ? "T" : "F"));
                    }
                }
                res += "\n";
                if (i < Math.pow(2, propMap.size()) - 1) {
                    res += "\t"+getLine('│', '│', '─', '┼', args, dividers);
                } else {
                    res += "\t"+getLine('┕', '┙', '─', '┴', args, dividers);
                }
            }
            boolean validity = true;
            for (boolean arg : argumentResults) {
                validity &= arg;
            }
            res += "\t\t\t\t" + (validity ?  GREEN + "Valid".toUpperCase() + RESET : RED + "Invalid".toUpperCase() + RESET) + "\n";
            return res;
        } else {
            return RED + "Invalid input. Please check the syntax or enter 'I' for instructions.\nNote that the premises and the conclusion must each be separated by commas and the conclusion is the last item." + RESET;
        }
    }

    public static String getLine(char start, char end, char filler, char divider, String[] props, int[] specialDivider) {
        String res = "" + start;
        int j = 0;
        for (String prop : props) {
            res += filler;
            for (int i = 0; i < prop.length(); i++) {
                res += filler;
            }
            res += filler + "" + divider;
            for (int dividerVal : specialDivider) {
                if (dividerVal == j) {
                    res += divider;
                }
            }
            j++;
        }
        res = res.substring(0, res.length() - 1);
        res += end + "\n";
        return res;
    }

    public static String truthValue(String expr, Map<Character, Boolean> propMap) {
        if (expr.length() == 3 && expr.charAt(1) == '=') {
            if (expr.toLowerCase().charAt(2) != 't' && expr.toLowerCase().charAt(2) != 'f') {
                return RED + "Error: propositions must be either true or false (T/F)" + RESET;
            }
            propMap.put(expr.charAt(0), expr.toLowerCase().charAt(2) == 't');
            return "";
        } else if (expr.matches(VALID_SYMBOL_REGEX)) {
            try {
                LogicNode rootNode = buildTree(expr);
                if (rootNode == null) {
                    return RED + "Error: invalid expression. Please try again." + RESET;
                }
                return "\t=" + (rootNode.evaluate(propMap) ?  GREEN + "true".toUpperCase() + RESET : RED + "false".toUpperCase() + RESET);
            } catch (Exception e) {
                return RED + "Error: " + e.getMessage() + RESET;
            }
        } else {
            return RED + "Invalid input. Please check the syntax or use $h for help." + RESET;
        }
    }

    public static void instructions(char mode) {
        switch (mode) {
            case 'V':
                System.out.println("──────────────────────────────────────────────────────\n" +
                        "\t\t\t\t  TRUTH VALUE \n" +
                        "──────────────────────────────────────────────────────\n" +
                        "\tDetermine the truth value of each propositional.\n" +
                        "\tEnter a propositions using the A-Z and assign it\n" +
                        "\tusing '=' to 'T' as true or 'F' as false. Then,\n" +
                        "\tenter your expressions that follows out valid\n" +
                        "\tsymbols, for example P&(QvR)");
                break;
            case 'A':
                System.out.println("──────────────────────────────────────────────────────\n" +
                        "\t\t\t\t  ARGUMENTS VALIDITY \n" +
                        "──────────────────────────────────────────────────────\n" +
                        "\tDetermine whether the arguments are valid or\n" +
                        "\tinvalid. Enter premises and conclusion and\n" +
                        "\tuse a comma to separation, for example 'PvQ,R>Q,R&P'");
                break;
            case 'T':
                System.out.println("──────────────────────────────────────────────────────\n" +
                        "\t\t\t\t  TRUTH TABLE \n" +
                        "──────────────────────────────────────────────────────\n" +
                        "\tDisplay truth table of expression entered.\n" +
                        "\tEnter expressions only containing proposition \n" +
                        "\tletters and valid symbols, for example 'Pv(Q&R)'");
                break;
            default: //Default help
                System.out.println(
                        "──────────────────────────────────────────────────────\n" +
                        "\t\t\t\t\tINSTRUCTIONS:\n\n" +
                        "\tEnter "+ YELLOW +"I"+RESET+" for instructions\n" +
                        "\tEnter "+YELLOW+"T" + RESET+" for truth table\n" +
                        "\tEnter "+YELLOW+"V"+RESET+" for truth value\n" +
                        "\tEnter "+YELLOW+"A"+RESET+" for arguments validity\n" +
                        "\tEnter "+ YELLOW+"Q"+RESET+" to exit program\n\n" +
                        "\tYou must enter a valid expression by following\n" +
                        "\tthe list of valid symbols.\n\n" +
                        "\tValid logical symbols\n" +
                        YELLOW + "\t\t*" + RESET + " '(' and ')' for groupings\n" +
                        YELLOW + "\t\t*" + RESET + " 'v' for OR\n" +
                        YELLOW + "\t\t*" + RESET + " '&' for AND\n" +
                        YELLOW + "\t\t*" + RESET + " '≡' or ':' for bi-conditional\n" +
                        YELLOW + "\t\t*" + RESET + " '>' for conditional\n" +
                        YELLOW + "\t\t*" + RESET + " '~' for NOT\n\n" +
                        "\tExample valid expressions:\n" +
                        YELLOW + "\t\t* " + RESET + " PvQ\n" +
                        YELLOW + "\t\t*" + RESET + " (P&Q)&R\n" +
                        YELLOW + "\t\t*" + RESET + " ~(PvQ)&(Q>R)\n" +
                        YELLOW + "\t\t*" + RESET + " ~P&Q\n" +
                        YELLOW + "\t\t*" + RESET + " P&~(QvR)\n\n" +
                        "\tExample of invalid expression: \n" +
                        YELLOW + "\t\t*" + RESET + " P~vQ\n" +
                        YELLOW + "\t\t*" + RESET + " PQ\n" +
                        YELLOW + "\t\t*" + RESET + " (PvQ\n" +
                        YELLOW + "\t\t*" + RESET + " PvQ)\n\n" +
                        "\tFor "+ YELLOW +"argument validity " + RESET +"; enter your premises sepa-\n\trated by comma." +
                        "The last proportion you enter is\n\tconsidered as " +
                        "conclusion,for example 'PvQ,R>Q,R&P'\n\n" +
                        "\tFor " + YELLOW +"truth value" + RESET + ", you must use '=' to assign your \n" +
                        "\tproportion to 'T' for true and 'T' for false.\n" +
                        "\tThe declaration of proportion must be single\n" +
                        "\tletter(A-Z), for example;\n" +
                        YELLOW + "\t\t>"+RESET+" A=F \n" +
                        YELLOW + "\t\t>"+RESET+" B=T \n" +
                        "\t\t\t(you can declare proportion as long as you like,\n" +
                        "\t\t\tthen, enter the expression, in our case)\n" +
                        YELLOW + "\t\t>"+RESET+" AvB\n" +
                        "\t\t\tand the output will be \n" +
                        YELLOW + "\t\t>"+RESET+" Expression is: true\n" +
                        "──────────────────────────────────────────────────────\n"
                );
        }
    }

    public static void menu() {
        System.out.println(
                "╔════════════════════════════════════════════════════╗\n" +
                "║\t\tPROPORTIONS and TRUTH TABLE GENERATOR\t\t ║\n" +
                "╚════════════════════════════════════════════════════╝\n" +
                "\tThis software generates truth table for propor-\n" +
                "\t\t\t\t\ttional logic.\n\n"+
                "\tMENU\n" +
                YELLOW + "[I]"+ RESET +" Instructions (default)\n" +
                YELLOW +"[T]"+ RESET +" Truth table\n" +
                YELLOW +"[V]"+ RESET +" Truth Value\n" +
                YELLOW +"[A]"+ RESET +" Arguments Validity\n" +
                YELLOW +"[Q]"+ RESET +" Quit\n"
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
                    throw new IllegalStateException(RED + "Couldn't analyze expression fully. Remaining part: " + raw + ". Please check your syntax." + RESET);
                }
                return new LogicNode(op, left, right, NodeType.OPERATOR, false);
            } else {
                return left;
            }
        } else {
            return null;
        }
    }

    public static int getGroupEndIndex(String raw) {
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
        return i - 1;
    }

    public static String getNode(String raw, LogicNode newNode) {
        boolean inverted = false;
        if (raw.charAt(0) == '~') {
            inverted = true;
            raw = raw.substring(1);
        }
        if (raw.charAt(0) == '(') {
            LogicNode temp = buildTree(raw.substring(1, getGroupEndIndex(raw)));
            if (temp == null) {
                throw new IllegalStateException(RED + "Unable to correctly analyze expression. Please check the syntax." + RESET);
            }
            newNode.copyFrom(temp);
            newNode.inverted = inverted != newNode.inverted;
            raw = raw.substring(getGroupEndIndex(raw) + 1);
        } else {
            newNode.initialize(raw.charAt(0), null, null, NodeType.PROPOSITION, inverted);
            raw = raw.substring(1);
        }
        return raw;
    }
}
