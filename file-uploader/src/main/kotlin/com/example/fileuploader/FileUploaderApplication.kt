package com.example.fileuploader

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

@RestController
@RequestMapping
@SpringBootApplication
class FileUploaderApplication(val resourceLoader: ResourceLoader) {

  val client = WebClient.create("http://localhost:8081")

  @GetMapping("/test") fun test(): Mono<Response> {
    return resourceLoader.getResource("classpath:test4.exe").toMono()
      .zipWhen { resource -> callScan(resource) }
      .flatMap { tuple ->
        val resource = tuple.t1
        val response = tuple.t2

        when (response.status) {
          Response.Status.PENDING -> callUpload(response, resource)
          else -> response.toMono()
        }
      }
      .log()
  }

  private fun callScan(resource: Resource): Mono<Response> {
    return client
      .post()
      .uri("/scan")
      .syncBody(
        Request(
          Request.Sender("First Last", "first.last@gmail.com", "m123456"),
          "application/pdf" //TODO: how to get the content type from `Resource` that can default to `text/plain`
        )
      )
      .retrieve()
      .bodyToMono()
  }

  private fun callUpload(response: Response, resource: Resource): Mono<Response> {
    return client
      .post()
      .uri("/scan/{0}/upload", response.id)
      .body(BodyInserters.fromResource(resource))
      .retrieve()
      .bodyToMono()
  }
}

data class Request(
  val sender: Sender,
  val contentType: String,
  val text: String? = null,
  val meta: Map<String, Any>? = null
) {
  data class Sender(
    val name: String,
    val email: String,
    val pid: String
  )
}

data class Response(
  val id: String,
  val status: Status
) {
  enum class Status { OK, INFO, BLOCK, PENDING, UNSUPPORTED }
}

fun main(args: Array<String>) {
  runApplication<FileUploaderApplication>(*args)
}
