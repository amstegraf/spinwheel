package com.spinwheel;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SpinWheelApplication {
    private static final Logger log = Logger.getLogger(SpinWheelApplication.class);

    private final static boolean SLEEP_ENABLED;
    private final static int SLEEP_PERIOD;
    private final static int MIN_SHUFFLES;
    private final static int MAX_SHUFFLES;
    private final static int MIN_WINNING_DRAWS;
    private final static int MAX_WINNING_DRAWS;

    private final static String FILE_PATH;

    static {
        SLEEP_ENABLED = Boolean.valueOf(System.getProperty("SLEEP_ENABLED", "false"));
        SLEEP_PERIOD = (int) TimeUnit.SECONDS.toMillis(Long.valueOf(System.getProperty("SLEEP_PERIOD", "1")));
        MIN_SHUFFLES = Integer.valueOf(System.getProperty("MIN_SHUFFLES", "1"));
        MAX_SHUFFLES = Integer.valueOf(System.getProperty("MAX_SHUFFLES", "10"));
        MIN_WINNING_DRAWS = Integer.valueOf(System.getProperty("MIN_WINNING_DRAWS", "1"));
        MAX_WINNING_DRAWS = Integer.valueOf(System.getProperty("MAX_WINNING_DRAWS", "10"));

        FILE_PATH = System.getProperty("FILE_PATH");
    }

    public static void main(String[] args) throws Exception {
        List<String> entries = Arrays.asList(args).stream().map(e -> e.toUpperCase()).collect(Collectors.toList());

        if (entries.isEmpty()) {

            if (FILE_PATH == null || FILE_PATH.isEmpty()) {
                throw new RuntimeException("File path of the names to be drawn must be provided");
            }

            File initialFile = new File(FILE_PATH);

            try(
                    InputStream inputStream = new FileInputStream(initialFile);
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))
            ) {
                String line;

                while ((line = br.readLine()) != null)
                {
                    entries.add(line.trim().toUpperCase());
                }
            } catch (IOException e) {
                log.error(e);
                throw e;
            }
        }

        Map<String, Integer> picks = new TreeMap<>();

        for (String entry : entries) {
            picks.put(entry, 0);
        }

        log.info(String.format("Initial entries %s", entries));
        int shuffles = getARandom(MIN_SHUFFLES, MAX_SHUFFLES);
        printLineBreak();

        log.info(String.format("Shuffles to do %d", shuffles));
        trySleep();
        shuffle(entries, shuffles);
        printLineBreak();

        //todo pick another random on number of draws & get user
        int numberOfDrawsToWin = getARandom(MIN_WINNING_DRAWS, MAX_WINNING_DRAWS);
        log.info(String.format("Number of occurrences to win %d", numberOfDrawsToWin));
        trySleep();
        printLineBreak();

        int draw = 0;
        while (getCurrentMaxValue(picks) < numberOfDrawsToWin) {
            draw++;

            int shuffleAgain = getARandom(MIN_SHUFFLES, MAX_SHUFFLES);
            shuffle(entries, shuffleAgain);
            String currentPickedEntry = entries.get(getARandom(0, entries.size()-1));
            log.info(String.format("Pick no %d done (%s) & shuffle %d more times", draw, currentPickedEntry, shuffleAgain));
            picks.put(currentPickedEntry, picks.get(currentPickedEntry) + 1);
            printLineBreak();
        }

        trySleep();
        log.info("LADIES AND GENTLEMAN WE HAVE A WINNER....");
        trySleep();
        log.info("AND THE WINNER IS....");
        trySleep();
        log.info("ALMOST THERE....");
        trySleep();
        log.info("THE ONE AND ONLY....");
        trySleep();
        log.info("HERE HE IS....");
        trySleep();

        log.info(String.format("THE WINNER IS: %s", getEntryKeyWithMax(picks)));

        printLineBreak();
        trySleep();
        log.info(String.format("And the picks were: %s", sortByValue(picks)));
        printParticipantsPlaces(picks, draw);
    }

    private static void shuffle(List entries, int shuffles) throws InterruptedException {
        for (int i = 1; i <= shuffles; i++) {
            Collections.shuffle(entries);
            //log.info(String.format("Shuffle %d %s", i, entries)); //todo re-enable
        }
        trySleep(SLEEP_PERIOD);
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

    private static void trySleep() throws InterruptedException {
        trySleep(SLEEP_PERIOD);
    }

    private static void trySleep(int period) throws InterruptedException {
        if (SLEEP_ENABLED) Thread.sleep(period);
    }

    private static void printParticipantsPlaces(Map<String, Integer> picksInput, int totalDraws) {
        Map<String, Integer> picks = new HashMap<>(picksInput);

        for (int i = 1; i <= picksInput.size(); i++) {
            String currentMaxKey = getEntryKeyWithMax(picks);
            int hits = getCurrentMaxValue(picks);
            int hitPercentage = (int) (((double) hits / totalDraws) * 100);

            if (hits < 1) {
                break;
            }

            log.info(String.format("%s was on %s place with a %d%% hit rate and %s hits", currentMaxKey, i, hitPercentage, hits));
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
