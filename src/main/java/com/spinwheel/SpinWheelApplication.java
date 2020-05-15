package com.spinwheel;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class SpinWheelApplication {
    private static final Logger log = Logger.getLogger(SpinWheelApplication.class);
    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(1000);
        //todo step one read entries, count & store
        List<String> entries = Arrays.asList(args).stream().map(e -> e.toUpperCase()).collect(Collectors.toList());

        if (entries.isEmpty()) {
            try(
                InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("names.txt");
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))
            ) {
                String line;

                while ((line = br.readLine()) != null)
                {
                    entries.add(line.trim().toUpperCase());
                }
            } catch (IOException e) {
                log.error(e);
            }
        }

        Map<String, Integer> picks = new TreeMap<>();

        for (String entry : entries) {
            picks.put(entry, 0);
        }

        log.info(String.format("Initial entries %s", entries));
        int shuffles = getARandom(5, 20);
        printLineBreak();

        log.info(String.format("Shuffles to do %d", shuffles));
        Thread.sleep(2000);
        shuffle(entries, shuffles);
        printLineBreak();

        //todo pick another random on number of draws & get user
        int numberOfDrawsToWin = getARandom(3, 10);
        log.info(String.format("Number of occurrences to win %d", numberOfDrawsToWin));
        Thread.sleep(2000);
        printLineBreak();

        int draw = 0;
        while (getCurrentMaxValue(picks) < numberOfDrawsToWin) {
            draw++;
            Thread.sleep(200);
            int shuffleAgain = getARandom(1, 5);
            log.info(String.format("Pick no %d done & shuffle %d times", draw, shuffleAgain));
            shuffle(entries, shuffleAgain);
            String currentPickedEntry = entries.get(getARandom(0, entries.size()-1));
            picks.put(currentPickedEntry, picks.get(currentPickedEntry) + 1);
            printLineBreak();
        }

        Thread.sleep(500);
        log.info("LADIES AND GENTLEMAN WE HAVE A WINNER....");
        Thread.sleep(500);
        log.info("AND THE WINNER IS....");
        Thread.sleep(1000);
        log.info("ALMOST THERE....");
        Thread.sleep(1000);
        log.info("THE ONE AND ONLY....");
        Thread.sleep(1000);
        log.info("HERE HE IS....");
        Thread.sleep(1000);

        log.info(String.format("THE WINNER IS: %s", getEntryKeyWithMax(picks)));

        printLineBreak();
        Thread.sleep(3000);
        log.info(String.format("And the picks were: %s", sortByValue(picks)));
        printParticipantsPlaces(picks, draw);
    }

    private static void shuffle(List entries, int shuffles) throws InterruptedException {
        for (int i = 1; i <= shuffles; i++) {
            Collections.shuffle(entries);
            Thread.sleep(200);
            //log.info(String.format("Shuffle %d %s", i, entries)); //todo re-enable
        }
    }

    public static <K, V extends Comparable<V>> V getCurrentMaxValue(Map<K, V> map) {
        Optional<Map.Entry<K, V>> maxEntry = map.entrySet()
                .stream()
                .max(Comparator.comparing(Map.Entry::getValue));

        return maxEntry.get().getValue();
    }

    public static <K, V extends Comparable<V>> K getEntryKeyWithMax(Map<K, V> map) {
        Optional<Map.Entry<K, V>> maxEntry = map.entrySet()
                .stream()
                .max(Comparator.comparing(Map.Entry::getValue));

        return maxEntry.get().getKey();
    }

    private static int getARandom(int min, int max) {
        return (int) (Math.random() * ((max - min) + 1)) + min;
    }

    private static void printLineBreak() {
        log.info("--------------------------------------------");
    }

    private static void printParticipantsPlaces(Map<String, Integer> picksInput, int totalDraws) {
        Map<String, Integer> picks = new HashMap<>(picksInput);

        for (int i = 1; i <= picksInput.size(); i++) {
            String currentMaxKey = getEntryKeyWithMax(picks);
            int hits = getCurrentMaxValue(picks);
            log.info(String.format("%s was on %s place with a %d%% hit rate and %s hits", currentMaxKey, i, (int)(((double)hits/totalDraws)*100), hits));
            picks.remove(currentMaxKey);

            if (i >= 10) {
                break;
            }
        }
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());
        Collections.reverse(list);

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}
