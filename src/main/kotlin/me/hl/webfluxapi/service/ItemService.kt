package me.hl.webfluxapi.service

import me.hl.webfluxapi.domain.Item
import me.hl.webfluxapi.exception.ErrorCode
import me.hl.webfluxapi.exception.ItemNotFoundException
import me.hl.webfluxapi.repository.ItemRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ItemService( private val itemRepository: ItemRepository) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun create(item: Item): Mono<Item> = itemRepository.save(item).also{
        logger.debug("Processed create(${item}).")
    }

    fun update(id: Long, item: Item): Mono<Item> = findById(id)
        .flatMap {
            it.name = item.name
            itemRepository.save(it)
        }.also{
            logger.debug("Processed update($id, ${item}).")
        }

    fun delete(id: Long) = findById(id)
        .flatMap {
            itemRepository.deleteById(it.id)
        }.also{
            logger.debug("Processed delete($id).")
        }

    fun findAll(): Flux<Item> = itemRepository.findAll()
        .also {
            logger.debug("Processed findAll().")
        }

    fun findById(id: Long): Mono<Item> = itemRepository.findById(id)
        .also {
            logger.debug("Processed findById($id).")
        }
        .switchIfEmpty(
            Mono.error(ItemNotFoundException(ErrorCode.ITEM_NOT_FOUND))
        )
}