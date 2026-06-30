# Jenkins Pipeline - Parallel Stages

## 🚨 Errors Encountered

During the implementation of this pipeline, I encountered the following issues and resolved them:

### 1. Security Scan stage failed

* The `Security Scan` stage executed the following command:

  ```bash
  mvn dependency-check:check
  ```

* Maven failed with the error:

  ```text
  No plugin found for prefix 'dependency-check'
  ```

* This happened because the **OWASP Dependency Check Maven Plugin** was not configured in the project's `pom.xml`.

* **Fix:** Either:

  * Add the `dependency-check-maven` plugin to the `pom.xml`, or
  * Remove/replace the Security Scan stage if the project does not require dependency scanning.

---

### 2. Parallel stage failed even though tests passed

* The pipeline was configured to run **Unit Tests** and **Security Scan** in parallel.

* The **Unit Tests** completed successfully.

* However, because the **Security Scan** branch failed, Jenkins marked the entire parallel stage and pipeline as **FAILED**.

* **Learning:** In a `parallel` block, if any branch fails, the overall pipeline is considered failed unless additional error handling (`catchError`, `try/catch`, etc.) is implemented.

---

## 📚 What This Pipeline Does

This pipeline demonstrates how Jenkins can execute multiple stages simultaneously using the `parallel` directive.

### Build Stage

The pipeline first builds the application by executing:

```bash
mvn clean package -DskipTests
```

This command:

* Cleans previous build artifacts from the `target/` directory.
* Compiles the application.
* Packages the application into a JAR file.
* Skips unit tests during the build for faster execution.

---

### Parallel Execution

After the build completes successfully, Jenkins executes two stages at the same time.

### Unit Tests

Runs:

```bash
mvn test
```

This stage:

* Compiles test classes (if required).
* Executes all unit tests.
* Reports the test results.

---

### Security Scan

Runs:

```bash
mvn dependency-check:check
```

This stage is intended to:

* Scan project dependencies.
* Detect known security vulnerabilities (CVEs).
* Generate a dependency vulnerability report.

> **Note:** This stage requires the OWASP Dependency Check Maven Plugin to be configured before it can run successfully.

---

## Why Use Parallel Stages?

Running independent tasks simultaneously provides several benefits:

* Reduces overall pipeline execution time.
* Makes better use of Jenkins executors.
* Separates independent responsibilities (testing, scanning, analysis).
* Improves CI/CD efficiency.

Instead of waiting for one stage to finish before starting the next, Jenkins executes both branches concurrently.

---

## 💡 Key Learnings

* The `parallel` directive allows multiple stages to execute simultaneously.
* Independent tasks such as testing and security scanning are good candidates for parallel execution.
* If **any parallel branch fails**, Jenkins marks the entire pipeline as failed.
* Maven plugins must be configured before their goals can be executed.
* `mvn clean package -DskipTests` is useful for creating build artifacts before running tests separately.
* Parallel execution can significantly reduce the overall CI pipeline duration.
