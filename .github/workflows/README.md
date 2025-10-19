# GitHub Workflows

This directory contains GitHub Actions workflows for CI/CD of the wizard Maven project.

## Workflows

### 1. Verify on Branch (`verify-branch.yml`)
- **Trigger**: Push to any branch except `main` and `master`
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
  - `GPG_PRIVATE_KEY`: The GPG private key for signing artifacts
  - `GPG_PASSPHRASE`: The passphrase for the GPG private key

### 3. Publish Release (`publish-release.yml`)
- **Trigger**: When a pull request is merged to `main` or `master`
- **Purpose**: Prepares and performs Maven release to Maven repository
- **Features**:
  - Runs `mvn release:prepare` and `mvn release:perform` with the `release` profile
  - Attaches sources and javadoc
  - Signs artifacts with GPG
  - Publishes to Maven Central (OSSRH)
- **Required Secrets**:
  - `GPG_PRIVATE_KEY`: The GPG private key for signing artifacts
  - `GPG_PASSPHRASE`: The passphrase for the GPG private key
  - `MAVEN_USERNAME`: Username for Maven repository (OSSRH)
  - `MAVEN_PASSWORD`: Password/token for Maven repository (OSSRH)

## Setting Up Secrets

To configure the required secrets, go to your repository's Settings > Secrets and variables > Actions, and add:

1. **GPG_PRIVATE_KEY**: Export your GPG private key in ASCII format:
   ```bash
   gpg --armor --export-secret-keys YOUR_KEY_ID
   ```

2. **GPG_PASSPHRASE**: The passphrase used to protect your GPG private key

3. **MAVEN_USERNAME**: Your Sonatype OSSRH username

4. **MAVEN_PASSWORD**: Your Sonatype OSSRH password or token

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
