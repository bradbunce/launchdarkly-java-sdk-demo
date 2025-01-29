package dev.bradbunce.config;
import com.launchdarkly.sdk.LDContext;
import com.launchdarkly.sdk.server.LDClient;
import com.launchdarkly.sdk.server.LDConfig;
import io.github.cdimascio.dotenv.Dotenv;
import java.io.File;

public class LD {
    private static String userEmail = "";
    private static String userName = "";
    private static LDContext context;
    private static LDClient client;
    private static String SDK_KEY = "";

    // Default values from .env
    private static String defaultEmail = "";
    private static String defaultName = "";
    
    // Feature flag keys
    public static final String FEATURE_FLAG_1_KEY = "dashboard-progress-meters";
    public static final String FEATURE_FLAG_2_KEY = "dashboard-line-chart";
    public static final String FEATURE_FLAG_3_KEY = "dashboard-bar-chart";
    
    static {
        // Try to load values from environment or .env file
        loadEnvValues();
    }
    
    private static void loadEnvValues() {
        // First try system environment variables
        String key = System.getenv("LAUNCHDARKLY_SDK_KEY");
        String email = System.getenv("LAUNCHDARKLY_USER_EMAIL");
        String name = System.getenv("LAUNCHDARKLY_USER_NAME");
        
        if (key != null && !key.isEmpty()) {
            showMessage("Found SDK key in environment variables");
            SDK_KEY = key;
        }
        if (email != null && !email.isEmpty()) {
            showMessage("Found user email in environment variables");
            defaultEmail = email;
        }
        if (name != null && !name.isEmpty()) {
            showMessage("Found user name in environment variables");
            defaultName = name;
        }
        
        // Try loading from .env file in project root
        try {
            File envFile = new File(".env");
            if (envFile.exists()) {
                showMessage("Found .env file at: " + envFile.getPath());
                Dotenv dotenv = Dotenv.configure()
                    .directory(".")
                    .ignoreIfMalformed()
                    .ignoreIfMissing()
                    .load();
                
                // Only override if not already set from environment variables
                if (SDK_KEY.isEmpty()) {
                    key = dotenv.get("LAUNCHDARKLY_SDK_KEY");
                    if (key != null && !key.isEmpty()) {
                        showMessage("Found SDK key in .env file");
                        SDK_KEY = key;
                    }
                }
                
                if (defaultEmail.isEmpty()) {
                    email = dotenv.get("LAUNCHDARKLY_USER_EMAIL");
                    if (email != null && !email.isEmpty()) {
                        showMessage("Found user email in .env file");
                        defaultEmail = email;
                    }
                }
                
                if (defaultName.isEmpty()) {
                    name = dotenv.get("LAUNCHDARKLY_USER_NAME");
                    if (name != null && !name.isEmpty()) {
                        showMessage("Found user name in .env file");
                        defaultName = name;
                    }
                }
            }
        } catch (Exception e) {
            showMessage("Warning: Failed to load .env file: " + e.getMessage());
        }
        
        if (SDK_KEY.isEmpty()) {
            showMessage("No SDK key found in environment or .env file");
        }
    }
    
    public static String getSdkKey() {
        return SDK_KEY;
    }
    
    public static String getDefaultEmail() {
        return defaultEmail;
    }
    
    public static String getDefaultName() {
        return defaultName;
    }
    
    public static void initialize(String sdkKey, String email, String name) throws Exception {
        // Use provided values or fall back to defaults from .env
        String finalSdkKey = (sdkKey != null && !sdkKey.isEmpty()) ? sdkKey : SDK_KEY;
        String finalEmail = (email != null && !email.isEmpty()) ? email : defaultEmail;
        String finalName = (name != null && !name.isEmpty()) ? name : defaultName;
        try {
            SDK_KEY = finalSdkKey;
            userEmail = finalEmail;
            userName = finalName;
            
            showMessage("Creating context for user: " + userName + " with email: " + userEmail);
            
            // Create context
            context = LDContext.builder(userEmail)
                             .name(userName)
                             .build();
            
            // Initialize SDK with offline mode for testing if no valid key
            showMessage("Initializing LaunchDarkly client...");
            LDConfig config;
            if (SDK_KEY == null || SDK_KEY.isEmpty()) {
                showMessage("No SDK key provided, initializing in offline mode");
                config = new LDConfig.Builder()
                    .offline(true)
                    .build();
            } else {
                config = new LDConfig.Builder()
                    .build();
            }
            
            client = new LDClient(SDK_KEY, config);
            
            if (client.isInitialized()) {
                showMessage("LaunchDarkly client initialized successfully");
                
                // Test flag evaluation
                boolean testFlag = client.boolVariation(FEATURE_FLAG_1_KEY, context, false);
                showMessage("Test flag evaluation for " + FEATURE_FLAG_1_KEY + ": " + testFlag);
            } else {
                showMessage("SDK failed to initialize");
                throw new Exception("SDK failed to initialize");
            }
        } catch (Exception e) {
            showMessage("Error during initialization: " + e.getMessage());
            throw e;
        }
    }
    
    // Getter for the context
    public static LDContext getContext() {
        if (context == null) {
            showMessage("Warning: Context is null!");
        }
        return context;
    }
    
    // Getter for the client
    public static LDClient getClient() {
        if (client == null) {
            showMessage("Warning: Client is null!");
            // Return a dummy client that always returns false for flags
            return new LDClient("dummy-key", new LDConfig.Builder().offline(true).build());
        }
        return client;
    }
  
    public static void showMessage(String s) {
        System.out.println("*** " + s + " ***\n");
    }
}
