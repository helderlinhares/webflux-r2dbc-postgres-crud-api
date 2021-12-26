package me.hl.webfluxapi.rest

import me.hl.webfluxapi.Commons.Companion.ITEM_DEFAULT_ID
import me.hl.webfluxapi.buildAlternativeItemRequest
import me.hl.webfluxapi.buildItem
import me.hl.webfluxapi.buildItemRequest
import me.hl.webfluxapi.buildModifiedItem
import me.hl.webfluxapi.buildNotFoundResponse
import me.hl.webfluxapi.domain.Item
import me.hl.webfluxapi.exception.ErrorCode
import me.hl.webfluxapi.exception.ErrorResponse
import me.hl.webfluxapi.exception.ItemNotFoundException
import me.hl.webfluxapi.domain.ItemService
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.MessageSource
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.web.reactive.function.BodyInserters
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RunWith(SpringRunner::class)
@WebFluxTest
class ItemControllerTest {
    @MockBean
    private lateinit var itemService: ItemService

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var messageSource: MessageSource

    @Test
    fun `Should return Item When Item exists`() {
        val item = buildItem()
        given(itemService.findById(ITEM_DEFAULT_ID)).willReturn(Mono.just(item))

        webTestClient.get()
            .uri("/items/$ITEM_DEFAULT_ID")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<Item>()
            .isEqualTo(item)
    }

    @Test
    fun `Should return Item list When at least one Item exists`() {
        val item = buildItem()
        given(itemService.findAll()).willReturn(Flux.just(item))

        webTestClient.get()
            .uri("/items")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<List<Item>>()
            .isEqualTo(listOf(item))
    }

    @Test
    fun `Should return empty list When no Item exists`() {
        given(itemService.findAll()).willReturn(Flux.empty())

        webTestClient.get()
            .uri("/items")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<List<Item>>()
            .isEqualTo(listOf())
    }

    @Test
    fun `Should return NotFound When search for an Item that does not exists`() {
        given(itemService.findById(ITEM_DEFAULT_ID)).willReturn(Mono.error(ItemNotFoundException(ErrorCode.ITEM_NOT_FOUND)))

        webTestClient.get()
            .uri("/items/$ITEM_DEFAULT_ID")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound
            .expectBody<ErrorResponse>()
            .isEqualTo(buildNotFoundResponse(messageSource))
    }

    @Test
    fun `Should create Item`() {
        val item = buildItem()
        val itemRequest = buildItemRequest().toDomain()
        given(itemService.create(itemRequest)).willReturn(Mono.just(item))

        webTestClient.post()
            .uri("/items")
            .body(BodyInserters.fromValue(itemRequest))
            .exchange()
            .expectStatus().isCreated
            .expectBody<Item>()
            .isEqualTo(item)
    }

    @Test
    fun `Should update Item When Item exists`() {
        val item = buildModifiedItem()
        val itemRequest = buildAlternativeItemRequest().toDomain()
        given(itemService.update(ITEM_DEFAULT_ID, itemRequest)).willReturn(Mono.just(item))

        webTestClient.put()
            .uri("/items/$ITEM_DEFAULT_ID")
            .body(BodyInserters.fromValue(itemRequest))
            .exchange()
            .expectStatus().isOk
            .expectBody<Item>()
            .isEqualTo(item)
    }

    @Test
    fun `Should return NotFound When try to update an Item that does not exists`() {
        val itemRequest = buildAlternativeItemRequest()
        given(itemService.update(ITEM_DEFAULT_ID, itemRequest.toDomain())).willReturn(
            Mono.error(
                ItemNotFoundException(
                    ErrorCode.ITEM_NOT_FOUND
                )
            )
        )

        webTestClient.put()
            .uri("/items/$ITEM_DEFAULT_ID")
            .body(BodyInserters.fromValue(itemRequest))
            .exchange()
            .expectStatus().isNotFound
            .expectBody<ErrorResponse>()
            .isEqualTo(buildNotFoundResponse(messageSource))
    }

    @Test
    fun `Should delete Item When Item exists`() {
        given(itemService.delete(ITEM_DEFAULT_ID)).willReturn(Mono.empty())

        webTestClient.delete()
            .uri("/items/$ITEM_DEFAULT_ID")
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun `Should return NotFound When try to delete an Item that does not exists`() {
        given(itemService.delete(ITEM_DEFAULT_ID)).willReturn(Mono.error(ItemNotFoundException(ErrorCode.ITEM_NOT_FOUND)))

        webTestClient.delete()
            .uri("/items/$ITEM_DEFAULT_ID")
            .exchange()
            .expectStatus().isNotFound
            .expectBody<ErrorResponse>()
            .isEqualTo(buildNotFoundResponse(messageSource))
    }

}