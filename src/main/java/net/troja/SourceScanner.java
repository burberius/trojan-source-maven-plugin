package net.troja;

import org.apache.maven.plugin.logging.Log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SourceScanner {
    private static final Pattern BIDI_PATTERN = Pattern.compile("(^[^\n]*'[^\n]*[\u2066\u2067\u2068][^'\u2069\n]*'[^\n]*$|^[^\n]*'[^\n]*[\u202A\u202B\u202D\u202E][^'\u202C\n]*'[^\n]*$|^[^\n]*\"[^\n]*[\u2066\u2067\u2068][^\"\u2069\n]*\"[^\n]*$|^[^\n]*\"[^\n]*[\u202A\u202B\u202D\u202E][^\"\u202C\n]*\"[^\n]*$|^[^\n]*\\/\\*[^\n]*[\u2066\u2067\u2068][^\u2069\n]*\\*\\/[^\n]*$|^[^\n]*\\/\\*[^\n]*[\u202A\u202B\u202D\u202E][^\u202C\n]*\\*\\/[^\n]*$|^[^\n]*\\/\\/[^\n]*[\u2066\u2067\u2068][^'\u2069\n]*$|^[^\n]*\\/\\/[^\n]*[\u202A\u202B\u202D\u202E][^'\u202C\n]*$|^[^\n]*#[^\n]*[\u2066\u2067\u2068][^'\u2069\n]*$|^[^\n]*#[^\n]*[\u202A\u202B\u202D\u202E][^'\u202C\n]*$)");

    private final Log log;

    public SourceScanner(final Log log) {
        this.log = log;
    }

    public List<String> findFilesWithBidi(Collection<Path> paths, Collection<String> extensions) {
        List<String> filesWithBidi = new ArrayList<>();
        for (Path currentPath : paths) {
            if(!Files.isDirectory(currentPath)) {
                log.warn("Path '" + currentPath + "' is not valid");
                continue;
            }
            try {
                List<Path> files = findFilesByFileExtension(currentPath, extensions);
                for (Path file : files) {
                    log.debug("Checking file " + file.toString());
                    boolean isBidi = Files.readAllLines(file).stream().map(this::isClean).anyMatch(aBoolean -> !aBoolean);
                    if (isBidi) {
                        filesWithBidi.add(file.toString());
                    }
                }
            } catch (IOException e) {
                log.error("Could not process files below " + paths.toString(), e);
            }
        }
        return filesWithBidi;
    }

    List<Path> findFilesByFileExtension(Path path, Collection<String> fileExtensions)
            throws IOException {

        List<Path> result;
        try (Stream<Path> pathStream = Files.find(path,
                Integer.MAX_VALUE,
                matchFileExtensions(fileExtensions)
        )) {
            result = pathStream.collect(Collectors.toList());
        }
        return result;

    }

    private BiPredicate<Path, BasicFileAttributes> matchFileExtensions(final Collection<String> fileExtensions) {
        return (p, basicFileAttributes) ->
                fileExtensions.stream().anyMatch(ext -> p.getFileName().toString().endsWith(ext))
                        && Files.isReadable(p)
                        && Files.isRegularFile(p);
    }

    boolean isClean(String text) {
        return !BIDI_PATTERN.matcher(text).matches();
    }
}
