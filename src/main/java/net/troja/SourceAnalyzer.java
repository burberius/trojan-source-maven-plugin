package net.troja;

import org.apache.maven.plugin.logging.Log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SourceAnalyzer {
    private static final Pattern BIDI_PATTERN = Pattern.compile("(^[^\n]*'[^\n]*[\u2066\u2067\u2068][^'\u2069\n]*'[^\n]*$|^[^\n]*'[^\n]*[\u202A\u202B\u202D\u202E][^'\u202C\n]*'[^\n]*$|^[^\n]*\"[^\n]*[\u2066\u2067\u2068][^\"\u2069\n]*\"[^\n]*$|^[^\n]*\"[^\n]*[\u202A\u202B\u202D\u202E][^\"\u202C\n]*\"[^\n]*$|^[^\n]*\\/\\*[^\n]*[\u2066\u2067\u2068][^\u2069\n]*\\*\\/[^\n]*$|^[^\n]*\\/\\*[^\n]*[\u202A\u202B\u202D\u202E][^\u202C\n]*\\*\\/[^\n]*$|^[^\n]*\\/\\/[^\n]*[\u2066\u2067\u2068][^'\u2069\n]*$|^[^\n]*\\/\\/[^\n]*[\u202A\u202B\u202D\u202E][^'\u202C\n]*$|^[^\n]*#[^\n]*[\u2066\u2067\u2068][^'\u2069\n]*$|^[^\n]*#[^\n]*[\u202A\u202B\u202D\u202E][^'\u202C\n]*$)");

    private final Log log;

    public SourceAnalyzer(final Log log) {
        this.log = log;
    }

    public List<String> findFilesWithBidi(Path sourcePath, String extension) {
        List<String> filesWithBidi = new ArrayList<>();
        try {
            List<Path> files = findFilesByFileExtension(sourcePath, extension);
            for (Path file : files) {
                log.debug("Checking file " + file.toString());
                boolean isBidi = Files.readAllLines(file).stream().map(this::isClean).anyMatch(aBoolean -> !aBoolean);
                if(isBidi) {
                    filesWithBidi.add(file.toString());
                }
            }
        } catch (IOException e) {
            log.error("Could not process files below " + sourcePath.toString(), e);
        }
        return filesWithBidi;
    }

    List<Path> findFilesByFileExtension(Path path, String fileExtension)
            throws IOException {

        List<Path> result;
        try (Stream<Path> pathStream = Files.find(path,
                Integer.MAX_VALUE,
                (p, basicFileAttributes) ->
                        p.getFileName().toString().endsWith(fileExtension)
                                && Files.isReadable(p)
                                && Files.isRegularFile(p)
        )) {
            result = pathStream.collect(Collectors.toList());
        }
        return result;

    }

    boolean isClean(String text) {
        return !BIDI_PATTERN.matcher(text).matches();
    }
}
