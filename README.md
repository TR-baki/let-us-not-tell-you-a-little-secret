# No more secrets in GitHub

## Infrastructure
In the case that you have configured an AWS Secret Manager within your Kubernetes Cluster (EKS), the full benefits can be taken advantage of:
- the secrets are managed centrally with fine-grained (IAM) policies 
- the secrets can easily be (automatically) rotated
- the pods always have the most up-to-date secrets

However, regardless of where the secrets come from, as long as they are mounted as volumes in the pod's filesystem, the application would work in the same way.

## Application
This application uses the Spring Boot's [ConfigTree](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config.files.configtree) support to load secrets into the application environment from a ConfigTree (Kubernetes [secret] volume mount).


If running locally, set the environment variable `SECRETS_BASE_PATH` to the path under which the ConfigTree secrets are mounted. 

If not provided, it will default do an empty string, which in Kubernetes would be the execution path of the application.

It additionally provides a small wrapper class `Secret`, which prevents the clearText secrets being logged by overriding their `toString()` method in line with Spring Security recommendations.

## Usage
If using the AWS Secrets Manager integration with Kubernetes, the following file needs to be provided:
```yaml
apiVersion: secrets-store.csi.x-k8s.io/v1alpha1
kind: SecretProviderClass
metadata:
  name: aws-secrets
spec:
  provider: aws
  parameters:
    objects: |
        - objectName: "arn:aws:secretsmanager:eu-central-1:[111122223333]:secret:test0-00AABB"
          objectAlias: "/app/config/secrets/testsecret0"
        - objectName: "arn:aws:secretsmanager:eu-central-1:[333322221111]:secret:test1-00AABB"
          objectAlias: "/app/config/secrets/testsecret1"

```
This will ensure that the secrets from AWS Secrets manager are mounted in the Kubernetes (EKS) pod's filesystem under the paths specified by the key `objectAlias`.

Once the application has been containerized, we can have a deployment similar to this: 
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: secrets-app
spec:
  selector:
    matchLabels:
      app: secrets-app
  replicas: 3
  template:
    metadata:
      labels:
        app: secrets-app
    spec:
      securityContext:
        # run the pod as a non-root user
        runAsUser: 54321 
        runAsGroup: 54321
      containers:
        - name: secrets-app 
          image: <your_image_url>
          imagePullPolicy: IfNotPresent
          ports:
            - containerPort: 8080
            - containerPort: 8443
          securityContext:
            runAsUser: 12345
            runAsGroup: 12345
            # run the container as a non-root user different to the pod user
      initContainers:
      - name: isolate-secrets-filesystem
        image: alpine:3
        command: [ "/bin/bash", "-c" ]
        args: 
          - chown -R 12345:12345 /app/config/secrets;
          - chmod -R 600 /app/config/secrets;
        # only the user running the container has R+W access to the secrets

```

## Future improvements:

### 1. Help Spring Boot implement a refreshable ConfigTree. 
At this point of time, their implementation only allows caching the contents of the ConfigTree when it is created. 

If the files change, which is the case when a secret is updated, only reinitialization of the `Application Context` will reload the changes in secrets. 

While this is necessary if the embedded server is running in TLS and its certificate and key pair is updated, for many of the secrets this is not the case. 

There is an option `ALWAYS_READ` in `ConfigTreePropertySource.Option`, however, this would read the secret from the filesystem each time it is requested, which in most cases will degrade performance. 