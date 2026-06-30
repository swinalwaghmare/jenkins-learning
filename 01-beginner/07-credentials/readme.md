## 🚨 Errors Encountered

During the implementation of this pipeline, I ran into the following issues and resolved them:

1. **Jenkins user did not have permission to access Docker**

   * The pipeline failed while executing Docker commands with the following error:

     ```text
     permission denied while trying to connect to the docker API at unix:///var/run/docker.sock
     ```

   * The Jenkins service was running as the `jenkins` user, which was not a member of the `docker` group.

   * **Fix:** Added the `jenkins` user to the `docker` group and restarted the Jenkins service.

     ```bash
     sudo usermod -aG docker jenkins
     sudo systemctl restart jenkins
     ```

2. **Attempted to push the image to Docker Hub's `library` repository**

   * Initially used:

     ```bash
     docker push myapp:latest
     ```

   * Docker interpreted this as:

     ```text
     docker.io/library/myapp:latest
     ```

   * Since the `library` namespace is reserved for official Docker images, the push failed with:

     ```text
     push access denied, repository does not exist or may require authorization
     ```

   * **Fix:** Tagged the image using my Docker Hub username before pushing.

     ```bash
     docker tag myapp:latest <dockerhub-username>/myapp:latest
     docker push <dockerhub-username>/myapp:latest
     ```

3. **Mismatch between the image tag and the image being pushed**

   * While tagging the image, I accidentally used:

     ```bash
     docker tag myapp:latest swinalwaghmare/my_app:latest
     ```

   * But attempted to push:

     ```bash
     docker push swinalwaghmare/myapp:latest
     ```

   * Since `my_app` and `myapp` are different repository names, Docker returned:

     ```text
     tag does not exist
     ```

   * **Fix:** Ensured that both the `docker tag` and `docker push` commands used the exact same repository name.

---

## 📚 What This Pipeline Does

This pipeline authenticates with Docker Hub using Jenkins credentials and pushes a Docker image to a Docker Hub repository.

### Docker Authentication

The pipeline securely retrieves the Docker Hub username and password from Jenkins Credentials using the `withCredentials` step.

Instead of storing credentials directly in the pipeline, Jenkins injects them as temporary environment variables during execution.

Authentication is performed using:

```bash
echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
```

Using `--password-stdin` prevents the password from appearing in the command history or process list.

### Tagging the Image

Before pushing, the local Docker image is tagged with the Docker Hub repository name.

```bash
docker tag myapp:latest <dockerhub-username>/myapp:latest
```

This creates a new image tag that points to the same image ID but associates it with the Docker Hub repository.

### Pushing the Image

After authentication and tagging, the pipeline uploads the image to Docker Hub.

```bash
docker push <dockerhub-username>/myapp:latest
```

Docker uploads only the image layers that do not already exist in the remote repository, making subsequent pushes faster.

---

## 💡 Key Learnings

* Jenkins should never store Docker Hub credentials directly in the pipeline. Use the **Credentials** plugin together with `withCredentials`.

* The Jenkins user must have permission to access the Docker daemon before running Docker commands.

* Always tag Docker images using the format:

  ```text
  <dockerhub-username>/<repository>:<tag>
  ```

* `docker push myapp:latest` attempts to push to Docker Hub's `library` namespace, which is reserved for official images.

* The repository name used in `docker tag` and `docker push` must match exactly.

* Docker only uploads image layers that are not already present in the remote repository, making image pushes efficient.
