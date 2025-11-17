# GitHub Workflows

This directory contains GitHub Actions workflows for CI/CD of the wizard Maven project.

## Workflows

### 1. Verify on Branch (`verify-branch.yml`)
- **Trigger**: Push to any branch except `master`
- **Purpose**: Verifies the Maven package by running `mvn verify`
- **Requirements**: None (uses standard Maven build)

### 2. Verify on Pull Request (`verify-pr.yml`)
- **Trigger**: When a pull request is opened, synchronized, or reopened
- **Purpose**: Verifies the Maven package with the `release` profile
- **Features**:
  - Runs `mvn clean verify` with the `release` profile
  - Attaches sources and javadoc
  - Signs artifacts with GPG
- **Required Secrets**:
  - `GPG_SECRET_KEY`: The GPG private key for signing artifacts
  - `GPG_SECRET_KEY_PASSWORD`: The passphrase for the GPG private key

### 3. Publish Release (`publish-release.yml`)
- **Trigger**: When a pull request is merged to `main` or `master`
- **Purpose**: Prepares and performs Maven release to Maven repository
- **Features**:
  - Runs `mvn release:prepare` and `mvn release:perform` with the `release` profile
  - Attaches sources and javadoc
  - Signs artifacts with GPG
  - Publishes to Maven Central (OSSRH)
- **Required Secrets**:
  - `GPG_SECRET_KEY`: The GPG private key for signing artifacts
  - `GPG_SECRET_KEY_PASSWORD`: The passphrase for the GPG private key
  - `OSSRH_USERNAME`: Username for Maven repository (Sonatype user token)
  - `OSSRH_TOKEN`: Password/token for Maven repository (Sonatype user token)

## Setting Up Secrets

To configure the required secrets, go to your repository's Settings > Secrets and variables > Actions, and add:

1. **GPG_SECRET_KEY**: Export your GPG private key in ASCII format:
   ```bash
      gpg --list-secret-keys --keyid-format=long
      gpg --export-secret-keys -a <key-id> > secret.txt
   ```

2. **GPG_SECRET_KEY_PASSWORD**: The passphrase used to protect your GPG private key

3. **OSSRH_USERNAME**: Your Sonatype user token username

4. **OSSRH_TOKEN**: Your Sonatype user token

## Maven Release Profile

The workflows expect a Maven `release` profile to be configured in your `pom.xml` that:
- Attaches source jars (`maven-source-plugin`)
- Attaches javadoc jars (`maven-javadoc-plugin`)
- Signs artifacts with GPG (`maven-gpg-plugin`)
- Configures deployment to Maven Central/OSSRH

Example profile configuration:
```xml
<profiles>
  <profile>
    <id>release</id>
    <build>
      <plugins>
        <plugin>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-source-plugin</artifactId>
          <executions>
            <execution>
              <id>attach-sources</id>
              <goals>
                <goal>jar-no-fork</goal>
              </goals>
            </execution>
          </executions>
        </plugin>
        <plugin>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-javadoc-plugin</artifactId>
          <executions>
            <execution>
              <id>attach-javadocs</id>
              <goals>
                <goal>jar</goal>
              </goals>
            </execution>
          </executions>
        </plugin>
        <plugin>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-gpg-plugin</artifactId>
          <executions>
            <execution>
              <id>sign-artifacts</id>
              <phase>verify</phase>
              <goals>
                <goal>sign</goal>
              </goals>
            </execution>
          </executions>
        </plugin>
      </plugins>
    </build>
  </profile>
</profiles>
```
