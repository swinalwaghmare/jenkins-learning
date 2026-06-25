## 🚨 Errors Encountered

During the implementation of this pipeline, I ran into the following issues and resolved them:

1. **Incorrect project path in the `dir()` block**

   * Initially used an absolute path (`/sample-apps/java-app`) instead of a workspace-relative path.
   * Jenkins attempted to access the root directory, resulting in an `AccessDeniedException`.
   * **Fix:** Changed the path to `sample-apps/java-app`, which is relative to the Jenkins workspace.

2. **Maven was not installed**

   * The build failed because the `mvn` command was unavailable.
   * **Fix:** Installed Apache Maven and verified the installation using:

     ```bash
     mvn -version
     ```

3. **Java Runtime (JRE) installed instead of Java Development Kit (JDK)**

   * Although `java` was available, `javac` was missing.
   * Maven requires the Java compiler (`javac`) to compile source code.
   * This resulted in errors such as:

     * `release version 17 not supported`
     * `release version 21 not supported`
     * `javac: command not found`
   * **Fix:** Installed the OpenJDK 21 JDK package and verified:

     ```bash
     java -version
     javac -version
     mvn -version
     ```


## 📚 What This Pipeline Does

* Uses Maven to build the Java application by executing:

  ```bash
  mvn clean package
  ```
* The `clean` goal removes any previous build artifacts from the `target/` directory.
* The `package` goal compiles the source code, runs the tests (unless skipped), and generates a JAR file inside the `target/` directory.

### Post Actions

The `post` block is executed after the pipeline completes, regardless of the build result. It is commonly used for:

* Cleaning the workspace
* Sending notifications (Email, Slack, Teams, etc.)
* Publishing reports
* Archiving build artifacts
* Performing other cleanup tasks

### `always`

The `always` block executes after every pipeline run, irrespective of whether the build:

* Succeeds
* Fails
* Becomes unstable
* Is aborted

This makes it the ideal place for cleanup activities.

### `cleanWs()`

The `cleanWs()` step deletes the Jenkins workspace after the build finishes.

Benefits include:

* Prevents leftover files from affecting future builds
* Frees up disk space
* Ensures every build starts with a clean workspace

### `success`

The `success` block runs only when the pipeline completes successfully. A successful build generally means:

* Source code compiled successfully
* Tests passed
* Packaging completed without errors

This block is typically used to send success notifications or trigger downstream jobs.

### `failure`

The `failure` block executes only if the pipeline fails. Common reasons include:

* Compilation errors
* Missing dependencies
* Test failures (depending on configuration)
* Script or pipeline errors

It is commonly used to send failure notifications or collect diagnostic information.

### `unstable`

The `unstable` block executes when the build finishes but is marked as **UNSTABLE** rather than **FAILED**.

For example:

* The project compiled successfully.
* One or more tests failed.
* The build artifact was still generated.

This status is useful for distinguishing between a completely failed build and one with non-critical issues.

## 💡 Key Learnings

- `dir()` paths in Jenkins are relative to the workspace unless an absolute path is specified.
- Maven requires the Java Development Kit (JDK), not just the Java Runtime Environment (JRE).
- Always verify the build environment using:
  - `java -version`
  - `javac -version`
  - `mvn -version`
- The `post` block executes after the pipeline finishes and is useful for cleanup and notifications.