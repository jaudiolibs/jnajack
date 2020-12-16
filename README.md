# JNAJack

Java bindings to JACK Audio Connection Kit (http://jackaudio.org/)

## Installation

Gradle (Groovy):

```groovy
implementation 'org.jaudiolibs:jnajack:1.4.0'
```

Gradle (Kotlin DSL):

```kotlin
implementation("org.jaudiolibs:jnajack:1.4.0")
```

Apache Maven: 
```xml
<dependency>
  <groupId>org.jaudiolibs</groupId>
  <artifactId>jnajack</artifactId>
  <version>1.4.0</version>
</dependency>
```

## Getting Started

Get the single `Jack` instance using 
```java
Jack jack = Jack.getInstance()
```

Then, you can open a new client session using
```java

EnumSet<JackStatus> status = EnumSet.noneOf(JackStatus.class);
try {
    Jack jack = Jack.getInstance();
    JackClient client = jack.openClient(
        /*name*/ "My Jack Client",
        /*options*/ EnumSet.of(JackOptions.JackNoStartServer),
        /*status*/ status
    );
} catch (JackException ex) {
    // If something went wrong
    if (!status.isEmpty()) {
        System.out.println("JACK exception client status : " + status);
    }
    throw ex;
}
```

You can register ports using the newly opened client
```java
JackPort midiInPort = client.registerPort("My MIDI in", JackPortType.MIDI, JackPortFlags.JackPortIsInput);
JackPort audioOutPort = client.registerPort("My Audio out", JackPortType.MIDI, JackPortFlags.JackPortIsOutput);
```

For more detailed examples, check out [the examples repository](https://github.com/jaudiolibs/examples)

## License

```
This code is free software; you can redistribute it and/or modify it
under the terms of the GNU Lesser General Public License as published
by the Free Software Foundation; either version 2.1 of the License,
or (at your option) any later version.

This code is distributed in the hope that it will be useful, but WITHOUT
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
for more details.

You should have received a copy of the GNU Lesser General Public License
along with this work; if not, see http://www.gnu.org/licenses/

Please visit http://neilcsmith.net if you need additional information or
have any questions.
```