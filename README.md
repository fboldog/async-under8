# async-under8
Library built upon Kotlin coroutines.
Forked from [kotlinx.coroutines](https://github.com/Kotlin/kotlinx.coroutines) 
([kotlinx-coroutines-async](https://github.com/Kotlin/kotlinx.coroutines/tree/master/kotlinx-coroutines-async)), but drops java 8 requirement.

This modification allows libraries to use *asnyc* and runs under most of Android devices.

Thanks to [tikurahul](https://github.com/tikurahul/) - [kotlin-futures](https://github.com/tikurahul/kotlin-futures) implementation.

## Example
```kotlin
import async_under8.async
import com.rahulrav.futures.Future
 
private fun startLongAsyncOperation(v: Int) =
        Future.submit {
            Thread.sleep(1000)
            "Result: $v"
        }
        
fun main(args: Array<String>) {
    
    val future = async<String> {
        (1..5).map {
            await(startLongAsyncOperation(it))
        }.joinToString("\n")
    }
    
    future.onSuccess {
        println(it)
    }
}
```
## Status
[![Build Status](https://api.travis-ci.org/fboldog/async-under8.svg?branch=master)](https://travis-ci.org/fboldog/async-under8)

## Download

[![Download](https://api.bintray.com/packages/fboldog/maven/async-under8/images/download.svg)](https://bintray.com/fboldog/maven/async-under8/_latestVersion) or depend via Gradle:

```groovy
compile 'com.fboldog.async_under8:async_under8:0.0.1'
```

or Maven:
```xml
<dependency>
  <groupId>com.fboldog.async_under8</groupId>
  <artifactId>async_under8</artifactId>
  <version>0.0.1</version>
</dependency>
```
