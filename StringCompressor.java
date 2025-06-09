import java.util.*;
import java.util.stream.Collectors;

public class StringCompressor extends Compression {
    private final Map<String, Integer> histogramMap = new HashMap<>();
    private String inputUnCleared;
    private String inputCleared;

    public void input(String input) {
        histogramMap.clear();
        inputCleared = "";
        inputUnCleared = "";
        this.inputUnCleared = input;
        String charsToRemove = ".,:;!()";
        StringBuilder cleaned = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);

            if (charsToRemove.indexOf(ch) == -1) {
                cleaned.append(ch);
            }
        }
        inputCleared = cleaned.toString();
        String[] words = cleaned.toString().split("\\s+");
        for (String word : words) {
            String cleanedWord = word.trim();
            histogramMap.put(cleanedWord, histogramMap.getOrDefault(cleanedWord, 0) + 1);
        }
        // to nic nie zmienia (na wypadek w testach)
        Map<String, Integer> sortedHistogram = histogramMap.entrySet()
                .stream()
                .sorted((entry1, entry2) -> {
                    int score1 = entry1.getKey().length() * entry1.getValue();
                    int score2 = entry2.getKey().length() * entry2.getValue();
                    return Integer.compare(score2, score1); // Sort malejący
                })
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        histogramMap.clear();
        histogramMap.putAll(sortedHistogram);
    }

    public Map<String, Integer> histogram() {
        return histogramMap;
    }

    public List<String> code() {
        if (histogramMap.isEmpty()) {
            return Arrays.asList(inputUnCleared.split("\\s+"));
        }

        int numWords = histogramMap.size();
        if (numWords == 1) {
            return new ArrayList<>();
        }

        Map<Integer, Map.Entry<List<String>, Integer>> kosztslownika = new HashMap<>();

        List<String> ListaSlownik = new ArrayList<>();
        int kosztzmiany = 0;

        kosztslownika.put(0, new AbstractMap.SimpleEntry<>(ListaSlownik, kosztzmiany));

        for (int rozmiarSlownika = 2; rozmiarSlownika <= 64; rozmiarSlownika *= 2) {
            int liczba_bitów = Integer.toBinaryString(rozmiarSlownika - 1).length();

            List<String> sortedWords = histogramMap.keySet().stream()
                    .sorted((word1, word2) -> {
                        int word1Length = word1.length();
                        int word1Frequency = histogramMap.get(word1);
                        int word1Cost = (word1Length - liczba_bitów) * word1Frequency - word1Length;

                        int word2Length = word2.length();
                        int word2Frequency = histogramMap.get(word2);
                        int word2Cost = (word2Length - liczba_bitów) * word2Frequency - word2Length;

                        return Integer.compare(word2Cost, word1Cost);
                    })
                    .collect(Collectors.toList());

            ListaSlownik = new ArrayList<>();
            kosztzmiany = 0;

            for (int j = 0; j < rozmiarSlownika && j < sortedWords.size(); ++j) {
                String word = sortedWords.get(j);
                ListaSlownik.add(word);
            }
            for (int k = 0; k < ListaSlownik.size(); k++) {
                String word = ListaSlownik.get(k);
                int wordLength = word.length();
                int wordFrequency = histogramMap.get(word);
                kosztzmiany += (wordLength - liczba_bitów) * wordFrequency - wordLength;
            }

            kosztslownika.put(rozmiarSlownika, new AbstractMap.SimpleEntry<>(ListaSlownik, kosztzmiany));
        }

        Map.Entry<Integer, Map.Entry<List<String>, Integer>> bestEntry = null;
        for (Map.Entry<Integer, Map.Entry<List<String>, Integer>> entry : kosztslownika.entrySet()) {
            if (bestEntry == null || entry.getValue().getValue() > bestEntry.getValue().getValue()) {
                bestEntry = entry;
            }

        }
        List<String> bestDictionary = bestEntry != null ? bestEntry.getValue().getKey() : new ArrayList<>();

        return bestDictionary;
    }

    public String output() {
        List<String> slownik = code();
        int rozmiarSlownika = slownik.size();
        int liczba_bitów = Integer.toBinaryString(rozmiarSlownika - 1).length();
        Map<String, String> wordToBinary = new HashMap<>();
        for (int i = 0; i < rozmiarSlownika; i++) {
            String binaryCode = String.format("%" + liczba_bitów + "s", Integer.toBinaryString(i)).replace(' ', '0');
            wordToBinary.put(slownik.get(i), binaryCode);
        }

        String[] tokens = inputUnCleared.split("(?<=\\s)|(?=\\s)");
        StringBuilder outputText = new StringBuilder();

        for (String token : tokens) {
            if (!token.trim().isEmpty() && wordToBinary.containsKey(token)) {
                outputText.append(wordToBinary.get(token));
            } else {
                outputText.append(token);
            }
        }

        return outputText.toString();
    }

    public String decode(String input, List<String> code) {
        int rozmiarSlownika = code.size();
        int liczba_bitów = Integer.toBinaryString(rozmiarSlownika - 1).length();
        for (int i = 0; i < code.size(); i++) {
            String slowo = code.get(i);
            String bity = Integer.toBinaryString(i);

            while (bity.length() < liczba_bitów) {
                bity = "0" + bity;
            }
            input = input.replaceAll( bity, slowo);
        }
        return input;
    }
}