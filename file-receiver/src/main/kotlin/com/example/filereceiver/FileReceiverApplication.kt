package com.example.filereceiver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.util.*

@RestController
@RequestMapping
@SpringBootApplication
class FileReceiverApplication {

  @PostMapping(value = ["/scan"], consumes = [MediaType.APPLICATION_JSON_VALUE])
  fun scan(@RequestBody request: Request): Mono<Response> {
    return when (request.contentType) {
      MediaType.TEXT_PLAIN_VALUE -> Response(uuid(), Response.Status.OK).toMono()
      MediaType.APPLICATION_PDF_VALUE -> Response(uuid(), Response.Status.PENDING).toMono()
      else -> Response(uuid(), Response.Status.UNSUPPORTED).toMono()
    }
  }

  @PostMapping(value = ["/scan/{id}/upload"], consumes = [MediaType.ALL_VALUE])
  fun upload(@RequestHeader headers: HttpHeaders, @PathVariable id: String, @RequestBody bytes: ByteArrayResource): Mono<Response> {
    println(headers.toString())
    return Response(uuid(), Response.Status.BLOCK).toMono()
  }
}

fun uuid() = UUID.randomUUID().toString()

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
  runApplication<FileReceiverApplication>(*args)
}
