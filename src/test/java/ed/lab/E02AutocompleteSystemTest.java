package ed.lab;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class E02AutocompleteSystemTest {

    @ParameterizedTest
    @MethodSource("testArguments")
    void test(List<AutocompleteOperations> operations) {
        List<AutocompleteOperations> previousOps = new LinkedList<>();

        AutocompleteOperations initialOperation = operations.removeFirst();

        final String[] initialSentences = Arrays.stream(initialOperation.arguments().split(","))
                .toArray(String[]::new);


        int[] array = Arrays.stream(initialOperation.expected().trim().split(","))
                .mapToInt(Integer::parseInt)
                .toArray();

        var e02 = new E02AutocompleteSystem(initialSentences, array);
        previousOps.add(initialOperation);

        for (AutocompleteOperations operation : operations) {

            List<String> expected = Arrays.stream(operation.expected().trim().split(","))
                    .filter(s -> !s.isEmpty())
                    .toList();

            List<String> actual = e02.input(operation.arguments().charAt(0));

            previousOps.add(operation);

            assertNotNull(actual,
                    () -> String.format("Error al llamar a %s(). Se esperaba una lista que no fuera null.\nLista de operaciones previas:\n%s",
                            operation.operationName(),
                            previousOps.stream()
                                    .map(String::valueOf)
                                    .collect(Collectors.joining("\n"))));

            assertEquals(expected.size(), actual.size(),
                    () -> String.format("Error al llamar a %s(). Se esperaba una lista de tamaño %s pero se obtuvo una lista de tamaño %s.\nLista de operaciones previas:\n%s",
                            operation.operationName(),
                            expected.size(),
                            actual.size(),
                            previousOps.stream()
                                    .map(String::valueOf)
                                    .collect(Collectors.joining("\n"))));

            Iterator<String> expectedIterator = expected.iterator();
            Iterator<String> actualIterator = actual.iterator();

            while (expectedIterator.hasNext() && actualIterator.hasNext()) {
                String expectedValue = expectedIterator.next();
                String actualValue = actualIterator.next();

                assertEquals(expectedValue, actualValue,
                        () -> String.format("Error al llamar a %s(). Se esperaba %s pero se obtuvo %s.\nLista de operaciones previas:\n%s",
                                operation.operationName(),
                                expected,
                                actual,
                                previousOps.stream()
                                        .map(String::valueOf)
                                        .collect(Collectors.joining("\n"))));
            }
        }
    }

    private static Stream<List<AutocompleteOperations>> testArguments() {
        try {
            final String fileContent = new String(
                    Objects.requireNonNull(E02AutocompleteSystemTest.class.getClassLoader()
                                    .getResourceAsStream("ed/lab/E02.csv"))
                            .readAllBytes());

            final String[] lines = fileContent.split("\n");

            Stream.Builder<List<AutocompleteOperations>> builder = Stream.builder();

            List<AutocompleteOperations> operations = new LinkedList<>();

            for (String line : lines) {
                if (line.isBlank()) {
                    builder.add(operations);
                    operations = new LinkedList<>();
                    continue;
                }

                String[] commands = line.split("\\|");

                final String args = Optional.ofNullable(commands[1])
                        .filter(s -> !s.isBlank())
                        .orElse(" ");

                final String expected = commands.length > 2 ?
                        Optional.ofNullable(commands[2])
                                .filter(s -> !s.isBlank())
                                .orElse("")
                        : "";

                final AutocompleteOperations autocompleteOperations = new AutocompleteOperations(commands[0], args, expected);

                operations.add(autocompleteOperations);
            }

            if (!operations.isEmpty())
                builder.add(operations);

            return builder.build();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private record AutocompleteOperations(String operationName, String arguments, String expected) {
        @Override
        public String toString() {
            return String.format("%s(%s)%s",
                    operationName,
                    arguments != null ? arguments : "",
                    expected != null ? " -> " + expected : "");
        }
    }

}