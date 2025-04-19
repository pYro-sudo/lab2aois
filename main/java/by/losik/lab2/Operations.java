package by.losik.lab2;

import java.util.HashMap;

public interface Operations {
    default boolean and(boolean first, boolean second){
        return first && second;
    }
    default boolean or(boolean first, boolean second){
        return first || second;
    }
    default boolean not(boolean first){
        return !first;
    }
    default boolean implies(boolean first, boolean second){
        return or(not(first), second);
    }
    default boolean equivalent(boolean first, boolean second){
        return first == second;
    }
    by.losik.lab2.BinaryTree prepareFunction(String function);

    boolean[][] truthTable(BinaryTree binaryTree);

    int amountOfOperations(BinaryTree binaryTree);
    boolean evaluate(BinaryTree binaryTree, boolean[] truthRow, int index);
    String ccnf(boolean[][] truthTable);
    String cdnf(boolean[][] truthTable);
    String ccnfInNumbers(boolean[][] truthTable);
    String cdnfInNumbers(boolean[][] truthTable);
    HashMap<Integer, StringBuffer> indexForm(boolean[][] truthTable);

}
