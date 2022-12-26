# LegacyClientCommands
 Small Library providing a client-command API, ported from Forge.

## Using it in your project

### Include it in your mod
```groovy
repositories {
  maven {
    url "https://moehreag.duckdns.org/maven/releases"
  }
}

dependencies {
  modImplementation include('io.github.axolotlclient:LegacyClientCommands:1.0.0')
}
```

### Registering a Command

```java
public class YourMod implements ClientModInitializer {
    public void onIntitialize() {
        ClientCommandRegistry.getInstance().registerCommandnew ClientCommand(){
            ...
        };
    }
}
```

There's also a helper method available which accepts Lambdas as parameters.
It is also possible to register Listeners which will be called every time
a command is executed.

