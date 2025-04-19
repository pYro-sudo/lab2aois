package by.losik.lab2;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class OperationsImpl implements Operations {
    List<BinaryTree> operandList = new ArrayList<>();
    private List<BinaryTree> subFormulaList = new ArrayList<>();

    public List<BinaryTree> getSubFormulaList(){
        return subFormulaList;
    }
    public int operandSize(){
        return operandList.size();
    }
    public List<BinaryTree> getOperandList(){
        return operandList;
    }
    @Override
    public by.losik.lab2.BinaryTree prepareFunction(String function) {
        BinaryTree binaryTreeToPush = new BinaryTree();
        binaryTreeToPush.setLiteral(function);
        subFormulaList.add(binaryTreeToPush);
        System.out.println("The entrance function: " + function);
        if(function.equals("")){
            return null;
        }
        BinaryTree root = new BinaryTree();
        int bracketCount = 0;

        for(int i = 0; i< function.length();++i){
            if(function.charAt(i) == '('){
                ++bracketCount;
            } else if (function.charAt(i) == ')') {
                --bracketCount;
                if(bracketCount < 0){
                    throw new RuntimeException("Incorrect brackets placement");
                }
            }
        }
        if(bracketCount != 0){
            throw new RuntimeException("Wrong Input of braces");
        }

        for(int i = 0; i < function.length()-1;++i){
            if((function.charAt(i) == '('
                    && (function.charAt(i+1) == '-'
                    || function.charAt(i+1) == '&'
                    || function.charAt(i+1) == '>'
                    || function.charAt(i+1) == '|'))
               || ((function.charAt(i) == '-'
                    || function.charAt(i) == '>'
                    || function.charAt(i) == '&'
                    || function.charAt(i) == '|'
                    || function.charAt(i) == '!')
                    && function.charAt(i+1) == ')')){
                throw new RuntimeException("Wrong Input 1");
            }
        }

        boolean foundOperators = false;
        for(int i=0;i<function.length();++i){
            foundOperators = foundOperators || function.charAt(i) == '~' || function.charAt(i) == ')' || function.charAt(i) == '!'
                    || function.charAt(i) == '(' || function.charAt(i) == '-'  || function.charAt(i) == '>'
                    || function.charAt(i) == '&'  || function.charAt(i) == '|';
        }

        if (!foundOperators) {
            BinaryTree existingOperand = null;
            for (BinaryTree operand : operandList) {
                if (operand.getLiteral().equals(function)) {
                    existingOperand = operand;
                    break;
                }
            }
            if (existingOperand != null) {
                return existingOperand;
            } else {
                BinaryTree newNode = new BinaryTree();
                newNode.setLiteral(function);
                newNode.setLeftNode(null);
                newNode.setRightNode(null);
                operandList.add(newNode);
                return newNode;
            }
        }

        for(int i = 0; i< function.length(); ++i){
            switch (function.charAt(i)){
                case '~', '|', '&' ->{
                    if(function.charAt(i-1) == ')'){
                        root.setLiteral(String.valueOf(function.charAt(i)));
                        int closedBraces = 1;
                        StringBuilder leftSide = new StringBuilder();
                        for(int j = i-1; j>= 0 && closedBraces != 0 &&
                                (function.charAt(j) != '>' || function.charAt(j) != '~' ||
                                        function.charAt(j) != '|' || function.charAt(j) != '&'); --j){
                            if(function.charAt(j) == '('){
                                --closedBraces;
                            } else if (function.charAt(j) == ')') {
                                ++closedBraces;
                            }
                            leftSide.append(function.charAt(j));
                        }
                        root.setLeftNode(prepareFunction(String.valueOf(leftSide.reverse())));
                    }
                    else{
                        root.setLiteral(String.valueOf(function.charAt(i)));
                        StringBuilder leftSide = new StringBuilder();
                        for(int j = i-1; j>= 0 &&
                                (function.charAt(j) != '>' || function.charAt(j) != '~' ||
                                        function.charAt(j) != '|' || function.charAt(j) != '&'); --j){
                            leftSide.append(function.charAt(j));
                        }
                        root.setLeftNode(prepareFunction(String.valueOf(leftSide.reverse())));
                    }

                    if(function.charAt(1+i) == '('){
                        root.setLiteral(String.valueOf(function.charAt(i)));
                        int openBraces = 1;
                        StringBuilder leftSide = new StringBuilder();
                        int j;
                        for(j = i+1; j < function.length() && openBraces != 0 &&
                                (function.charAt(j) != '-' || function.charAt(j) != '~' ||
                                        function.charAt(j) != '|' || function.charAt(j) != '&'); ++j){
                            if(function.charAt(j) == '('){
                                ++openBraces;
                            } else if (function.charAt(j) == ')') {
                                --openBraces;
                            }
                            leftSide.append(function.charAt(j));
                        }
                        i=j-1;
                        root.setRightNode(prepareFunction(String.valueOf(leftSide)));
                    }
                    else{
                        root.setLiteral(String.valueOf(function.charAt(i)));
                        StringBuilder leftSide = new StringBuilder();
                        int j;
                        for(j = i+1; j < function.length() &&
                            (function.charAt(j) != '-' || function.charAt(j) != '~' ||
                                    function.charAt(j) != '|' || function.charAt(j) != '&'); ++j){
                            leftSide.append(function.charAt(j));
                        }
                        i=j-1;
                        root.setRightNode(prepareFunction(String.valueOf(leftSide)));
                    }
                }
                case '-' -> {
                    if(function.charAt(i+1) != '>'){
                        throw new RuntimeException("Wrong Input");
                    }
                    else{
                        root.setLiteral("->");
                        if(function.charAt(i-1) == ')'){
                            root.setLiteral(String.valueOf(function.charAt(i)));
                            int closedBraces = 1;
                            StringBuilder leftSide = new StringBuilder();
                            for(int j = i-1; j>= 0 && closedBraces != 0 &&
                                    (function.charAt(j) != '>' || function.charAt(j) != '~' ||
                                            function.charAt(j) != '|' || function.charAt(j) != '&'); --j){
                                if(function.charAt(j) == '('){
                                    --closedBraces;
                                } else if (function.charAt(j) == ')') {
                                    ++closedBraces;
                                }
                                leftSide.append(function.charAt(j));
                            }
                            root.setLeftNode(prepareFunction(String.valueOf(leftSide.reverse())));
                        }
                        else{
                            root.setLiteral(String.valueOf(function.charAt(i)));
                            StringBuilder leftSide = new StringBuilder();
                            for(int j = i-1; j >= 0 &&
                                (function.charAt(j) != '>' || function.charAt(j) != '~' ||
                                        function.charAt(j) != '|' || function.charAt(j) != '&'); --j){
                                leftSide.append(function.charAt(j));
                            }
                            root.setLeftNode(prepareFunction(String.valueOf(leftSide.reverse())));
                        }

                        if(function.charAt(2+i) == '('){
                            root.setLiteral(String.valueOf(function.charAt(i)));
                            int openBraces = 1;
                            StringBuilder rightSide = new StringBuilder();
                            int j;
                            for(j = i+2; j < function.length() && openBraces != 0 &&
                                    (function.charAt(j) != '-' || function.charAt(j) != '~' ||
                                            function.charAt(j) != '|' || function.charAt(j) != '&'); ++j){
                                if(function.charAt(j) == '('){
                                    ++openBraces;
                                } else if (function.charAt(j) == ')') {
                                    --openBraces;
                                }
                                rightSide.append(function.charAt(j));
                            }
                            i=j-1;
                            root.setRightNode(prepareFunction(String.valueOf(rightSide)));
                        }
                        else{
                            root.setLiteral(String.valueOf(function.charAt(i)));
                            StringBuilder rightSide = new StringBuilder();
                            int j;
                            for(j = i+2; j < function.length() &&
                                (function.charAt(j) != '-' || function.charAt(j) != '~' ||
                                        function.charAt(j) != '|' || function.charAt(j) != '&'); ++j){
                                rightSide.append(function.charAt(j));
                            }
                            i=j-1;
                            root.setRightNode(prepareFunction(String.valueOf(rightSide)));
                        }
                    }
                }
                case '(' -> {
                    int brackets = 1;
                    int j = i;
                    while(brackets != 0 && j < function.length() && (
                            function.charAt(j) != '|' || function.charAt(j) != '-'
                                    || function.charAt(j) != '&' || function.charAt(j) != '~'
                            )){
                        if (function.charAt(j) == '(' && i != j) {
                            ++brackets;
                        } else if (function.charAt(j) == ')') {
                            --brackets;
                        }
                        j++;
                    }
                    char c = function.substring(i + 1, j - 1).charAt(function.substring(i + 1, j - 1).length() - 1);
                    if(c != '|'
                            && c != '-'
                            && c != '&'
                            && c != '~'){
                        root = prepareFunction(function.substring(i+1,j-1));
                    }
                    else{
                        root = prepareFunction(function.substring(i+1,j-3));
                    }
                    i=j-1;
                }
                case '!' -> {
                    root.setLiteral("!");
                    root.setRightNode(null);
                    StringBuilder stringBuilder = new StringBuilder();
                    if(function.charAt(i+1) == '('){
                        int openBraces = 1;
                        StringBuilder leftSide = new StringBuilder();
                        int j;
                        for(j = i+1; j < function.length() && openBraces != 0; ++j){
                            if(function.charAt(j) == '(' && j != i+1){
                                ++openBraces;
                            } else if (function.charAt(j) == ')') {
                                --openBraces;
                            }
                            leftSide.append(function.charAt(j));
                        }
                        i = j-1;
                        root.setLeftNode(prepareFunction(leftSide.toString()));
                    } else {
                        int j;
                        for(j = i+1; j < function.length(); ++j){
                            if(function.charAt(j) == '(' || function.charAt(j) == '!' ||
                                    function.charAt(j) == '~' || function.charAt(j) == '&' ||
                                    function.charAt(j) == '|' || function.charAt(j) == '-') {
                                break;
                            }
                            stringBuilder.append(function.charAt(j));
                        }
                        i = j-1;
                        root.setLeftNode(prepareFunction(stringBuilder.toString()));
                    }
                }
            }
        }
        return root;
    }

    @Override
    public boolean[][] truthTable(BinaryTree binaryTree) {
        int numOperands = operandList.size();
        int amountOfOps = amountOfOperations(binaryTree);
        boolean[][] truthTable = new boolean[(int) Math.pow(2, numOperands)][numOperands + amountOfOps];

        for (int i = 0; i < Math.pow(2, numOperands); i++) {
            for (int j = 0; j < numOperands; j++) {
                boolean value = ((i >> (numOperands - 1 - j)) & 1) == 1;
                operandList.get(j).setValue(value);
                truthTable[i][j] = value;
            }
            truthTable[i][numOperands + amountOfOps - 1] = evaluate(binaryTree, truthTable[i], numOperands + amountOfOps - 1);
        }
        return truthTable;
    }

    @Override
    public int amountOfOperations(BinaryTree binaryTree) {
        if (binaryTree == null) {
            return 0;
        }
        int count = isOperator(binaryTree.getLiteral()) ? 1 : 0;
        count += amountOfOperations(binaryTree.getLeftNode());
        count += amountOfOperations(binaryTree.getRightNode());
        return count;
    }

    private boolean isOperator(String literal) {
        return literal.equals("|") || literal.equals("&") || literal.equals("!") || literal.equals("->") || literal.equals("~");
    }

    @Override
    public boolean evaluate(BinaryTree binaryTree, boolean[] truthRow, int index) {
         switch(String.valueOf(binaryTree.getLiteral())){
            case "~" -> {
                if(binaryTree.getLeftNode() == null || binaryTree.getRightNode() == null){
                    throw new RuntimeException();
                }
                else{
                    truthRow[index] = equivalent(evaluate(binaryTree.getLeftNode(), truthRow, index),evaluate(binaryTree.getRightNode(), truthRow, index));
                    --index;
                    return equivalent(evaluate(binaryTree.getLeftNode(), truthRow, index),evaluate(binaryTree.getRightNode(), truthRow, index));
                }
            }
            case "|" -> {
                if(binaryTree.getLeftNode() == null || binaryTree.getRightNode() == null){
                    throw new RuntimeException();
                }
                else{
                    truthRow[index] = or(evaluate(binaryTree.getLeftNode(), truthRow, index),evaluate(binaryTree.getRightNode(), truthRow, index));
                    --index;
                    return or(evaluate(binaryTree.getLeftNode(), truthRow, index),evaluate(binaryTree.getRightNode(), truthRow, index));
                }
            }
            case "&" -> {
                if(binaryTree.getLeftNode() == null || binaryTree.getRightNode() == null){
                    throw new RuntimeException();
                }
                else{
                    truthRow[index] = and(evaluate(binaryTree.getLeftNode(), truthRow, index),evaluate(binaryTree.getRightNode(), truthRow, index));
                    --index;
                    return and(evaluate(binaryTree.getLeftNode(), truthRow, index),evaluate(binaryTree.getRightNode(), truthRow, index));
                }
            }
            case "!" -> {
                if(binaryTree.getLeftNode() == null && binaryTree.getRightNode() == null){
                    throw new RuntimeException();
                }
                else{
                    truthRow[index] = not(evaluate(binaryTree.getLeftNode(), truthRow, index));
                    --index;
                    return not(evaluate(binaryTree.getLeftNode(), truthRow, index));
                }
            }
            case "-" -> {
                if(binaryTree.getLeftNode() == null || binaryTree.getRightNode() == null){
                    throw new RuntimeException();
                }
                else{
                    truthRow[index] = implies(evaluate(binaryTree.getLeftNode(), truthRow, index),evaluate(binaryTree.getRightNode(), truthRow, index));
                    --index;
                    return implies(evaluate(binaryTree.getLeftNode(), truthRow, index),evaluate(binaryTree.getRightNode(), truthRow, index));
                }
            }
            default -> {
                return binaryTree.getValue();
            }
        }
    }

    @Override
    public String cdnf(boolean[][] truthTable) {
        StringBuilder stringBuilder = new StringBuilder();
        for (boolean[] booleans : truthTable) {
            if (booleans[booleans.length - 1]) {
                if (stringBuilder.isEmpty()) {
                    stringBuilder.append("(");
                } else {
                    stringBuilder.append("|(");
                }
                for (BinaryTree operand : operandList) {
                    if (operandList.indexOf(operand) != operandList.size() - 1) {
                        if (booleans[operandList.indexOf(operand)]) {
                            stringBuilder.append(operand.getLiteral()).append("&");
                        } else {
                            stringBuilder.append("!").append(operand.getLiteral()).append("&");
                        }
                    } else {
                        if (booleans[operandList.indexOf(operand)]) {
                            stringBuilder.append(operand.getLiteral());
                        } else {
                            stringBuilder.append("!").append(operand.getLiteral());
                        }
                    }

                }
                stringBuilder.append(")");
            }
        }
        return String.valueOf(stringBuilder);
    }

    @Override
    public String ccnf(boolean[][] truthTable) {
        StringBuilder stringBuilder = new StringBuilder();
        for (boolean[] booleans : truthTable) {
            if (!booleans[booleans.length - 1]) {
                if (stringBuilder.isEmpty()) {
                    stringBuilder.append("(");
                } else {
                    stringBuilder.append("&(");
                }
                for (BinaryTree operand : operandList) {
                    if (operandList.indexOf(operand) != operandList.size() - 1) {
                        if (booleans[operandList.indexOf(operand)]) {
                            stringBuilder.append(operand.getLiteral()).append("|");
                        } else {
                            stringBuilder.append("!").append(operand.getLiteral()).append("|");
                        }
                    } else {
                        if (booleans[operandList.indexOf(operand)]) {
                            stringBuilder.append(operand.getLiteral());
                        } else {
                            stringBuilder.append("!").append(operand.getLiteral());
                        }
                    }

                }
                stringBuilder.append(")");
            }
        }
        return String.valueOf(stringBuilder);
    }

    @Override
    public String ccnfInNumbers(boolean[][] truthTable) {
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("( ");
        for (int i = 0; i < truthTable.length; i++) {
            if(!truthTable[i][truthTable[i].length-1]){
                stringBuffer.append(i).append(" ");
            }
        }
        stringBuffer.append(")&");
        return String.valueOf(stringBuffer);
    }

    @Override
    public String cdnfInNumbers(boolean[][] truthTable) {
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("( ");
        for (int i = 0; i < truthTable.length; i++) {
            if(truthTable[i][truthTable[i].length-1]){
                stringBuffer.append(i).append(" ");
            }
        }
        stringBuffer.append(")|");
        return String.valueOf(stringBuffer);
    }

    @Override
    public HashMap<Integer, StringBuffer> indexForm(boolean[][] truthTable) {
        int index = 0;
        StringBuffer binary = new StringBuffer();
        for(int i =truthTable.length-1;i >= 0;i--) {
            index += (truthTable[i][truthTable[i].length - 1] ? 1 : 0)*Math.pow(2, truthTable.length-i-1);
            binary.append(truthTable[truthTable.length - i - 1][truthTable[i].length - 1] ? "1" : "0");
        }
        HashMap<Integer, StringBuffer> indexFormAnswer = new HashMap<>();
        indexFormAnswer.put(index,binary);
        return indexFormAnswer;
    }
}
