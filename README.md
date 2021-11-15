# Trojan Source Maven Plugin
This Maven plugin scans your source code for occurrences of trojan source as described
on the following page: https://trojansource.codes

Trojan source attacks use unicode control characters to make evil source code look valid by
reordering parts of it. For a deeper explanation have a look on the above-mentioned page
or even read the paper of Nicholas Boucher and Ross Anderson, linked there.

The attack is also tracked by [CVE-2021-42574](https://nvd.nist.gov/vuln/detail/CVE-2021-42574).

## Add plugin to your project
To activate the plugin and let it scan the source code before compiling it,
just add the following section to your _pom.xml_ file:
```
<build>
  <plugins>
    <plugin>
      <groupId>net.troja</groupId>
      <artifactId>trojan-source-maven-plugin</artifactId>
      <version>1.0-SNAPSHOT</version>
      <executions>
        <execution>
          <phase>process-sources</phase>
          <goals>
            <goal>scan</goal>
          </goals>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
```

## References
* Regular expression to scan for bidis was taken from https://github.com/nickboucher/bidi-viewer
* Example bidi file was taken from https://github.com/nickboucher/trojan-source
