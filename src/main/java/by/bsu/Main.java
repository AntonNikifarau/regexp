package by.bsu;

import by.bsu.dsm.DSM;
import by.bsu.nsm.NSM;
import by.bsu.regexptree.RegExpTree;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static java.lang.String.valueOf;
import static java.util.stream.Collectors.joining;

public class Main {
    public static void main(String[] args) throws IOException {
        String DELIMITER = " ";
        String testFile = "test.txt";
        if (args.length > 0) {
            testFile = args[0];
        }
        Files.lines(Paths.get(testFile)).map(str -> str.split(DELIMITER)).peek(strings -> {
            if (strings.length > 1) {
                RegExpTree regExpTree = RegExpTree.parse(strings[0]);
                NSM nsm = regExpTree.toNSM();
                DSM dsm = nsm.toDSM();
                for (int i = 1; i < strings.length; ++i) {
                    strings[i] = valueOf(dsm.match(strings[i]));
                }
            }
        }).map(strings -> Stream.of(strings).collect(joining(DELIMITER)))
          .forEach(System.out::println);
    }
}
