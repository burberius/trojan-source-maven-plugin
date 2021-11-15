package net.troja;


import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TrojanSourceScanMojoTest extends BaseMojoTest {

    @Test
    void testScan() throws Exception {
        File pom = new File("target/test-classes/project-to-test/");
        assertThat(pom).exists();

        TrojanSourceScanMojo trojanSourceScanMojo = (TrojanSourceScanMojo) lookupConfiguredMojo(pom, "scan");
        assertThatThrownBy(trojanSourceScanMojo::execute)
                .isInstanceOf(MojoExecutionException.class)
                .hasMessage("Possible trojan source found");
    }
}
