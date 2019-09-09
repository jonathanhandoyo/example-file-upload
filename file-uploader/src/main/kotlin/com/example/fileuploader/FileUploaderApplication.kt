package com.example.fileuploader

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.event.EventListener
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Component
@SpringBootApplication
class FileUploaderApplication(val resourceLoader: ResourceLoader) {

  @EventListener
  fun onApplicationReady(event: ApplicationReadyEvent) {
    val resource = resourceLoader.getResource("classpath:test.pdf")
    println("Content-Length: ${resource.contentLength()}")
    WebClient.create("http://localhost:8081")
      .post()
      .uri("/upload")
      .headers { headers ->
        headers.set("Content-Type", "application/pdf")
      }
      .body(BodyInserters.fromResource(resource))
      .exchange()
      .flatMap { response -> response.bodyToMono<Boolean>() }
      .doOnNext { println(it) }
      .subscribe()
  }
}

fun main(args: Array<String>) {
  runApplication<FileUploaderApplication>(*args)
}
