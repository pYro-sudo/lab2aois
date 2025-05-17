package by.losik.lab2;

import java.util.*;

public class TruthTableGenerator {
    private static List<String> variables = new ArrayList<>();
    private static List<int[]> truthTable = new ArrayList<>();
    private static String expression;
    private static String indexForm = "";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите логическое выражение (используйте &, |, !, ->, ~): ");
        expression = scanner.nextLine().replaceAll("\\s+", "");

        extractVariables();
        generateTruthTable();
        evaluateExpression();
        printTruthTable();

        String sdnf = buildSDNF();
        String sknf = buildSKNF();
        String indexSDNF = buildIndexSDNF();
        String indexSKNF = buildIndexSKNF();
        String functionVector = getFunctionVector();
        int decimalRepresentation = getDecimalRepresentation();

        System.out.println("\nСовершенная дизъюнктивная нормальная форма (СДНФ):");
        System.out.println(sdnf);
        System.out.println("\nСовершенная конъюнктивная нормальная форма (СКНФ):");
        System.out.println(sknf);
        System.out.println("\nСДНФ в индексной форме:");
        System.out.println(indexSDNF);
        System.out.println("\nСКНФ в индексной форме:");
        System.out.println(indexSKNF);
        System.out.println("\nВектор функции:");
        System.out.println(functionVector);
        System.out.println("\nДесятичное представление вектора функции:");
        System.out.println(decimalRepresentation);
    }

    private static void extractVariables() {
        Set<String> vars = new HashSet<>();
        for (char c : expression.toCharArray()) {
            if (Character.isLetter(c) && Character.isUpperCase(c)) {
                vars.add(String.valueOf(c));
            }
        }
        variables = new ArrayList<>(vars);
        Collections.sort(variables);
    }

    private static void generateTruthTable() {
        int varCount = variables.size();
        int rows = (int) Math.pow(2, varCount);

        for (int i = 0; i < rows; i++) {
            int[] row = new int[varCount + 1];
            for (int j = 0; j < varCount; j++) {
                row[j] = (i >> (varCount - 1 - j)) & 1;
            }
            truthTable.add(row);
        }
    }

    private static void evaluateExpression() {
        StringBuilder indexBuilder = new StringBuilder();
        for (int[] row : truthTable) {
            Map<String, Boolean> values = new HashMap<>();
            for (int i = 0; i < variables.size(); i++) {
                values.put(variables.get(i), row[i] == 1);
            }
            boolean result = evaluate(expression, values);
            row[variables.size()] = result ? 1 : 0;
            indexBuilder.append(row[variables.size()]);
        }
        indexForm = indexBuilder.toString();
    }

    private static boolean evaluate(String expr, Map<String, Boolean> values) {
        expr = expr.replaceAll("\\s+", "");
        return evaluateExpression(expr, values);
    }

    private static boolean evaluateExpression(String expr, Map<String, Boolean> values) {
        if (expr.startsWith("(") && expr.endsWith(")")) {
            return evaluateExpression(expr.substring(1, expr.length() - 1), values);
        }

        int lowestPriority = Integer.MAX_VALUE;
        int splitPos = -1;
        String operator = "";
        int parenCount = 0;

        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (c == '(') parenCount++;
            else if (c == ')') parenCount--;

            if (parenCount == 0) {
                int priority = getOperatorPriority(expr, i);
                if (priority != -1 && priority <= lowestPriority) {
                    lowestPriority = priority;
                    splitPos = i;
                    operator = getOperator(expr, i);
                }
            }
        }

        if (splitPos == -1) {
            if (expr.startsWith("!")) {
                return !evaluateExpression(expr.substring(1), values);
            }
            if (values.containsKey(expr)) {
                return values.get(expr);
            }
            if (expr.equals("1")) return true;
            if (expr.equals("0")) return false;
            throw new IllegalArgumentException("Неизвестный символ: " + expr);
        }

        String left = expr.substring(0, splitPos);
        String right = expr.substring(splitPos + operator.length());

        boolean leftVal = evaluateExpression(left, values);
        boolean rightVal = evaluateExpression(right, values);

        return switch (operator) {
            case "&" -> leftVal && rightVal;
            case "|" -> leftVal || rightVal;
            case "->" -> !leftVal || rightVal;
            case "~" -> leftVal == rightVal;
            default -> throw new IllegalArgumentException("Неизвестный оператор: " + operator);
        };
    }

    private static int getOperatorPriority(String expr, int pos) {
        if (pos + 1 < expr.length()) {
            String twoCharOp = expr.substring(pos, pos + 2);
            if (twoCharOp.equals("->")) return 1;
        }

        char c = expr.charAt(pos);
        if (c == '~') return 1;
        if (c == '&') return 2;
        if (c == '|') return 3;

        return -1;
    }

    private static String getOperator(String expr, int pos) {
        if (pos + 1 < expr.length()) {
            String twoCharOp = expr.substring(pos, pos + 2);
            if (twoCharOp.equals("->")) {
                return twoCharOp;
            }
        }
        return String.valueOf(expr.charAt(pos));
    }

    private static void printTruthTable() {
        System.out.println("\nТаблица истинности:");
        for (String var : variables) {
            System.out.print(var + " ");
        }
        System.out.println("Res");

        for (int[] row : truthTable) {
            for (int j : row) {
                System.out.print(j + " ");
            }
            System.out.println();
        }
    }

    private static String buildSDNF() {
        StringBuilder sdnf = new StringBuilder();
        boolean firstTerm = true;

        for (int[] row : truthTable) {
            if (row[variables.size()] == 1) {
                if (!firstTerm) {
                    sdnf.append(" | ");
                }
                sdnf.append("(");
                for (int i = 0; i < variables.size(); i++) {
                    if (i > 0) {
                        sdnf.append(" & ");
                    }
                    if (row[i] == 0) {
                        sdnf.append("!").append(variables.get(i));
                    } else {
                        sdnf.append(variables.get(i));
                    }
                }
                sdnf.append(")");
                firstTerm = false;
            }
        }

        return sdnf.toString().isEmpty() ? "0" : sdnf.toString();
    }

    private static String buildSKNF() {
        StringBuilder sknf = new StringBuilder();
        boolean firstTerm = true;

        for (int[] row : truthTable) {
            if (row[variables.size()] == 0) {
                if (!firstTerm) {
                    sknf.append(" & ");
                }
                sknf.append("(");
                for (int i = 0; i < variables.size(); i++) {
                    if (i > 0) {
                        sknf.append(" | ");
                    }
                    if (row[i] == 1) {
                        sknf.append("!").append(variables.get(i));
                    } else {
                        sknf.append(variables.get(i));
                    }
                }
                sknf.append(")");
                firstTerm = false;
            }
        }

        return sknf.toString().isEmpty() ? "1" : sknf.toString();
    }

    private static String getFunctionVector() {
        StringBuilder vector = new StringBuilder();
        for (int[] row : truthTable) {
            vector.append(row[variables.size()]);
        }
        return vector.toString();
    }

    private static int getDecimalRepresentation() {
        String vector = getFunctionVector();
        return Integer.parseInt(vector, 2);
    }

    private static String buildIndexSDNF() {
        List<Integer> indices = new ArrayList<>();

        for (int i = 0; i < indexForm.length(); i++) {
            if (indexForm.charAt(i) == '1') {
                indices.add(i);
            }
        }

        if (indices.isEmpty()) {
            return "0";
        }

        return "|(" + joinIndices(indices) + ")";
    }

    private static String buildIndexSKNF() {
        List<Integer> indices = new ArrayList<>();

        for (int i = 0; i < indexForm.length(); i++) {
            if (indexForm.charAt(i) == '0') {
                indices.add(i);
            }
        }

        if (indices.isEmpty()) {
            return "1";
        }

        return "&(" + joinIndices(indices) + ")";
    }

    private static String joinIndices(List<Integer> indices) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indices.size(); i++) {
            if (i > 0) {
                sb.append(" ");
            }
            sb.append(indices.get(i));
        }
        return sb.toString();
    }
}
