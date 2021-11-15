package net.troja;

import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SourceAnalyzerTest {

    public static final Path TEST_PATH = Paths.get("src/test/resources/project-to-test");
    @Mock
    private Log log;

    private SourceAnalyzer classToTest;

    @BeforeEach
    void setUp() {
        classToTest = new SourceAnalyzer(log);
    }

    @Test
    void isClean() {
        assertThat(classToTest.isClean("All fine /* OK? */")).isTrue();
        assertThat(classToTest.isClean("/*\u202E } \u2066if (isAdmin)\u2069 \u2066 begin admins only */")).isFalse();
    }

    @Test
    void findFilesByFileExtension() throws IOException {
        List<Path> files = classToTest.findFilesByFileExtension(TEST_PATH, "txt");

        assertThat(files).containsOnly(Paths.get("src/test/resources/project-to-test/src/main/java/project/test/SomeOtherFile.txt"));
    }

    @Test
    void findFilesWithBidi() {
        List<String> filesWithBidi = classToTest.findFilesWithBidi(TEST_PATH, "java");

        assertThat(filesWithBidi).containsExactly("src/test/resources/project-to-test/src/main/java/project/test/CommentingOut.java");
    }
}
