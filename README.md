# LaunchDarkly Java SDK Demo

A Java Swing application demonstrating feature flag functionality using the LaunchDarkly SDK.

## Prerequisites

- Java 11 or higher (built with Java 23)
- Maven
- LaunchDarkly account and SDK key
- Terraform (for feature flag setup)
- LaunchDarkly API access token

## Setup

1. Clone the repository
2. Copy `.env.example` to `.env` and add your LaunchDarkly SDK key:
   ```
   LAUNCHDARKLY_SDK_KEY=your-sdk-key
   ```

### Feature Flag Setup with Terraform

1. Create `terraform.tfvars`:
   ```hcl
   user_name = "your-name"
   launchdarkly_access_token = "api-key-here"
   ```

2. Initialize and apply Terraform:
   ```bash
   terraform init
   terraform plan
   terraform apply
   ```

This creates:
- Project "LaunchDarkly Java Demo - {user_name}"
- Production, test, and development environments
- Feature flags with prerequisites:
  - Form 1 (`form-1`)
  - Form 1 Bar Chart (`form-1-bar-chart`)
  - Form 1 Line Chart (`form-1-line-chart`)
  - Form 1 Progress Meters (`form-1-progress-meters`)

## Building

To build the executable jar:
```bash
mvn clean package
```

This will create `launchdarkly-java-demo.jar` in the `dist` directory.

## Running

There are two ways to run the application:

1. Using Maven:
   ```bash
   mvn clean compile exec:java
   ```

2. Using the jar file:
   ```bash
   java -jar dist/launchdarkly-java-demo.jar
   ```

## Feature Flags

The application demonstrates three feature flags:

- `form-1-progress-meters` - Controls the visibility of progress meters
- `form-1-line-chart` - Controls the line chart display
- `form-1-chart` - Controls the main chart display

## Development

The project uses:
- Java Swing for the UI
- LaunchDarkly Java Server SDK for feature flags
- Maven for build management
- dotenv-java for environment variable management
- Terraform for feature flag management

### Project Structure

```
src/main/java/dev/bradbunce/ - Source code
├── chart/     - Chart components and utilities
├── component/ - UI components
├── config/    - LaunchDarkly configuration
├── form/      - Application forms
├── main/      - Application entry point
└── swing/     - Custom Swing components
```

## License

MIT