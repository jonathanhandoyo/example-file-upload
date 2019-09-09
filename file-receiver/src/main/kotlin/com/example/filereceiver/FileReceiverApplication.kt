package com.example.filereceiver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

@RestController
@RequestMapping
@SpringBootApplication
class FileReceiverApplication {

  @PostMapping(value = ["/upload"], consumes = [MediaType.APPLICATION_PDF_VALUE])
  fun upload(@RequestBody pdf: ByteArrayResource): Mono<Boolean> {
    println("Content-Length: ${pdf.contentLength()}")
    return true.toMono()
  }
}

fun main(args: Array<String>) {
  runApplication<FileReceiverApplication>(*args)
}
