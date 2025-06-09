package org.example;
import java.util.List;
import java.util.Map;
/**
 * Kasa bazowa narzędzia do kompresji ciągów znaków.
 */
public abstract class Compression {
    /**
     * Dostarcza dane do przetworzenia. Cały tekst dostarczany jest w postaci
     * pojedynczego wywołania tej metody. Ciąg zawiera słowa i dodatkowe znaki. Ciąg
     * nie zawiera cyfr.
     *
     * @param input cięg do kompresji
     */
    abstract public void input(String input);

    /**
     * Histogram wystąpień słów. Klucz to słowo, wartość to liczba jego wystąpień w
     * ciągu przekazanym za pomocą input.
     *
     * @return mapa z histogramem liczby wystąpień słowa
     */
    abstract public Map<String, Integer> histogram();

    /**
     * Lista słów (słownik) użytych do zmniejszenia rozmiaru oryginalnej wiadomości.
     * Liczba słów wskazuje na liczbę bitów użytych do ich zakodowania. Kolejność
     * słów na liście odpowiada wartości kodu. Np. dla czterech słów pierwsze
     * kodowane jest za pomocą 00. Brak rozwiązania (wynik kodowania dłuższy od
     * oryginału) sygnalizowany jest za pomocą słownika o rozmiarze 0.
     *
     * @return słownik
     */
    abstract public List<String> code();

    /**
     * Zakodowany ciąg wejściowy, część słów zamieniona na liczby binarne.
     *
     * @return ciąg wynikowy
     */
    abstract public String output();

    /**
     * Metoda dekoduje ciąg z liczbami binarnymi zamiast słów na podstawie
     * dostarczonego kodu (słownika).
     *
     * @param input cięg do zdekodowania
     * @param code  lista słów wchodzących w skład słownika
     * @return zdekodowany ciąg
     */
    abstract public String decode(String input, List<String> code);
}
