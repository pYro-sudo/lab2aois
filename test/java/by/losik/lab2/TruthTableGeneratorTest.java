package by.losik.lab2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

class TruthTableGeneratorTest {

    @BeforeEach
    void resetSingleton() throws Exception {
        setPrivateStaticField("variables", new ArrayList<>());
        setPrivateStaticField("truthTable", new ArrayList<>());
        setPrivateStaticField("expression", "");
        setPrivateStaticField("indexForm", "");
    }

    private static void setPrivateStaticField(String fieldName, Object value) throws Exception {
        Field field = TruthTableGenerator.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }

    private static Object getPrivateStaticField(String fieldName) throws Exception {
        Field field = TruthTableGenerator.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(null);
    }

    private static Object invokePrivateMethod(String methodName, Object... args) throws Exception {
        Class<?>[] argTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            argTypes[i] = args[i].getClass();
        }

        Method method = TruthTableGenerator.class.getDeclaredMethod(methodName, argTypes);
        method.setAccessible(true);
        return method.invoke(null, args);
    }

    @Test
    void testExtractVariables() throws Exception {
        setPrivateStaticField("expression", "A & B | !C");
        invokePrivateMethod("extractVariables");

        @SuppressWarnings("unchecked")
        List<String> variables = (List<String>) getPrivateStaticField("variables");

        assertEquals(3, variables.size());
        assertEquals("A", variables.get(0));
        assertEquals("B", variables.get(1));
        assertEquals("C", variables.get(2));
    }

    @Test
    void testGenerateTruthTable() throws Exception {
        setPrivateStaticField("variables", List.of("A", "B"));
        invokePrivateMethod("generateTruthTable");

        @SuppressWarnings("unchecked")
        List<int[]> truthTable = (List<int[]>) getPrivateStaticField("truthTable");

        assertEquals(4, truthTable.size());
        assertArrayEquals(new int[]{0, 0, 0}, truthTable.get(0));
        assertArrayEquals(new int[]{0, 1, 0}, truthTable.get(1));
        assertArrayEquals(new int[]{1, 0, 0}, truthTable.get(2));
        assertArrayEquals(new int[]{1, 1, 0}, truthTable.get(3));
    }

    @Test
    void testEvaluateExpression() throws Exception {
        setPrivateStaticField("variables", List.of("A", "B"));
        setPrivateStaticField("expression", "A | B");
        invokePrivateMethod("generateTruthTable");
        invokePrivateMethod("evaluateExpression");

        @SuppressWarnings("unchecked")
        List<int[]> truthTable = (List<int[]>) getPrivateStaticField("truthTable");
        String indexForm = (String) getPrivateStaticField("indexForm");

        assertEquals(4, truthTable.size());
        assertEquals(0, truthTable.get(0)[2]); // 0 | 0 = 0
        assertEquals(1, truthTable.get(1)[2]); // 0 | 1 = 1
        assertEquals(1, truthTable.get(2)[2]); // 1 | 0 = 1
        assertEquals(1, truthTable.get(3)[2]); // 1 | 1 = 1
        assertEquals("0111", indexForm);
    }

    @Test
    void testBuildSDNF() throws Exception {
        setPrivateStaticField("variables", List.of("A", "B"));
        List<int[]> testTable = new ArrayList<>();
        testTable.add(new int[]{0, 0, 0});
        testTable.add(new int[]{0, 1, 1});
        testTable.add(new int[]{1, 0, 1});
        testTable.add(new int[]{1, 1, 1});
        setPrivateStaticField("truthTable", testTable);

        String sdnf = (String) invokePrivateMethod("buildSDNF");

        assertTrue(sdnf.contains("(!A & B)") || sdnf.contains("(B & !A)"));
        assertTrue(sdnf.contains("(A & !B)") || sdnf.contains("(!B & A)"));
        assertTrue(sdnf.contains("(A & B)"));
    }

    @Test
    void testBuildSKNF() throws Exception {
        setPrivateStaticField("variables", List.of("A", "B"));

        List<int[]> testTable = new ArrayList<>();
        testTable.add(new int[]{0, 0, 0}); // (!A | !B)
        testTable.add(new int[]{0, 1, 0}); // (!A | B)
        testTable.add(new int[]{1, 0, 0}); // (A | !B)
        testTable.add(new int[]{1, 1, 1}); // не входит в СКНФ
        setPrivateStaticField("truthTable", testTable);

        String sknf = (String) invokePrivateMethod("buildSKNF");

        assertFalse(sknf.isEmpty());
        assertNotEquals("1", sknf);
    }

    @Test
    void testGetFunctionVector() throws Exception {
        setPrivateStaticField("variables", List.of("A"));
        List<int[]> testTable = new ArrayList<>();
        testTable.add(new int[]{0, 1});
        testTable.add(new int[]{1, 0});
        setPrivateStaticField("truthTable", testTable);

        String vector = (String) invokePrivateMethod("getFunctionVector");
        assertEquals("10", vector);
    }

    @Test
    void testGetDecimalRepresentation() throws Exception {
        setPrivateStaticField("variables", List.of("A", "B"));
        List<int[]> testTable = new ArrayList<>();
        testTable.add(new int[]{0, 0, 0});
        testTable.add(new int[]{0, 1, 1});
        testTable.add(new int[]{1, 0, 1});
        testTable.add(new int[]{1, 1, 0});
        setPrivateStaticField("truthTable", testTable);

        int decimal = (int) invokePrivateMethod("getDecimalRepresentation");
        assertEquals(6, decimal);
    }

    @Test
    void testBuildIndexForms() throws Exception {
        setPrivateStaticField("indexForm", "0101");

        String indexSDNF = (String) invokePrivateMethod("buildIndexSDNF");
        String indexSKNF = (String) invokePrivateMethod("buildIndexSKNF");

        assertEquals("|(1 3)", indexSDNF);
        assertEquals("&(0 2)", indexSKNF);
    }
}