package by.losik.lab2;

import java.util.Arrays;
import java.util.Scanner;


public class App {
    public static void main(String[] args) {
        try {
            System.out.println("Enter the function");
            Scanner scanner = new Scanner(System.in);
            String function = scanner.nextLine();
            OperationsImpl operations = new OperationsImpl();
            System.out.println("Table:");

            boolean[][] table = operations.truthTable(operations.prepareFunction(function));
            for (boolean[] booleans : table) {
                System.out.println(Arrays.toString(booleans));
            }

            operations = new OperationsImpl();
            System.out.println("CCNF:\n" + operations.ccnf(operations.truthTable(operations.prepareFunction(function))));
            operations = new OperationsImpl();
            System.out.println("CCNF in numbers:\n" + operations.ccnfInNumbers(operations.truthTable(operations.prepareFunction(function))));
            operations = new OperationsImpl();
            System.out.println("CDNF:\n" + operations.cdnf(operations.truthTable(operations.prepareFunction(function))));
            operations = new OperationsImpl();
            System.out.println("CDNF in numbers:\n" + operations.cdnfInNumbers(operations.truthTable(operations.prepareFunction(function))));
            operations = new OperationsImpl();
            System.out.println("Index form:\n" + operations.indexForm(operations.truthTable(operations.prepareFunction(function))));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
